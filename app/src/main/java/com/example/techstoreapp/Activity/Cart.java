package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.techstoreapp.Model.Bill;
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

    private TextView tvTotalPrice, tvNameUser, tvPhoneUser, tvDeliveryAddress, tvEmptyCart;
    private Button btnCheckout;

    private DatabaseReference cartRef, userRef;
    private FirebaseAuth mAuth;

    private String currentUserName, currentUserPhone, currentUserAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // Ánh xạ view
        initViews();

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        cartRef = FireBaseHelper.getCartRef(uid);
        userRef = FireBaseHelper.getUsersRef().child(uid);

        // Load dữ liệu
        loadCartItems();
        loadUserInfo();

        // Sự kiện nút thanh toán
        btnCheckout.setOnClickListener(v -> checkout(uid));

        // Setup bottom navigation
        setupBottomNav();
    }

    private void initViews() {
        rcvCartItems = findViewById(R.id.rcvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvNameUser = findViewById(R.id.tvNameUser);
        tvPhoneUser = findViewById(R.id.tvPhoneUser);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        rcvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartList);
        rcvCartItems.setAdapter(cartAdapter);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                return true;
            } else if (id == R.id.nav_ordersuser) {
                startActivity(new Intent(this, OrdersUserActivity.class));
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
                        total += item.getTotalPrice();
                    }
                }

                if (cartList.isEmpty()) {
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    rcvCartItems.setVisibility(View.GONE);
                } else {
                    tvEmptyCart.setVisibility(View.GONE);
                    rcvCartItems.setVisibility(View.VISIBLE);
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
                    currentUserName = snapshot.child("name").getValue(String.class);
                    currentUserPhone = snapshot.child("phone").getValue(String.class);
                    currentUserAddress = snapshot.child("address").getValue(String.class);

                    tvNameUser.setText("👤 " + safeText(currentUserName));
                    tvPhoneUser.setText("📞 " + safeText(currentUserPhone));
                    tvDeliveryAddress.setText("📍 Địa chỉ giao hàng: " + safeText(currentUserAddress));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Cart.this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safeText(String text) {
        return text != null ? text : "";
    }

    private void checkout(String uid) {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEmptyOrNull(currentUserName) ||
                isEmptyOrNull(currentUserPhone) ||
                isEmptyOrNull(currentUserAddress)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        long total = 0;
        for (CartItem item : cartList) {
            total += item.getTotalPrice();
        }

        String billId = FireBaseHelper.getBillsRef().push().getKey();

        Bill bill = new Bill(
                billId,
                uid,
                currentUserName,
                currentUserPhone,
                currentUserAddress,
                total,
                new ArrayList<>(cartList),
                System.currentTimeMillis()
        );

        FireBaseHelper.getBillsRef()
                .child(billId)
                .setValue(bill)
                .addOnSuccessListener(unused -> {
                    FireBaseHelper.getCartRef(uid).removeValue();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi thanh toán", Toast.LENGTH_SHORT).show()
                );
    }

    private boolean isEmptyOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

}

