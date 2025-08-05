package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstoreapp.Adapter.CartAdapter;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.CartItem;
import com.example.techstoreapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {
    private RecyclerView rcvCartItems;
    private CartAdapter cartAdapter;
    private List<CartItem> cartList = new ArrayList<>();
    private TextView tvTotalPrice, tvNameUser, tvPhoneUser, tvDeliveryAddress;
    private DatabaseReference cartRef, userRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserProfile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // Setup RecyclerView
        rcvCartItems = findViewById(R.id.rcvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rcvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartList);
        rcvCartItems.setAdapter(cartAdapter);

        // Ánh xạ các TextView thông tin người dùng
        tvNameUser = findViewById(R.id.tvNameUser);
        tvPhoneUser = findViewById(R.id.tvPhoneUser);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish(); // hoặc chuyển về LoginActivity
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        cartRef = FireBaseHelper.getCartRef(uid); // Sẽ dùng đúng region
        userRef = FirebaseDatabase.getInstance("https://techstoreapp-de25c-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(uid);

        loadCartItems();
        loadUserInfo(); // <--- Thêm dòng này để tải thông tin user
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                int total = 0;
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    CartItem item = itemSnap.getValue(CartItem.class);
                    if (item != null) {
                        cartList.add(item);
                        total += item.getTotalPrice(); // Dùng hàm getTotalPrice() trong CartItem
                    }
                }
                cartAdapter.notifyDataSetChanged();
                tvTotalPrice.setText("Số tiền cần thanh toán : " + String.format("%,d VNĐ", total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Cart.this, "Lỗi khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    tvNameUser.setText("👤 " + (name != null ? name : ""));
                    tvPhoneUser.setText("📞 " + (phone != null ? phone : ""));
                    tvDeliveryAddress.setText("📍 Địa chỉ giao hàng: " + (address != null ? address : ""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Cart.this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

