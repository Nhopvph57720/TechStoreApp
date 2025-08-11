package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstoreapp.Adapter.OrdersAdapter;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.Bill;
import com.example.techstoreapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersUserActivity extends AppCompatActivity {

    private RecyclerView rcvOrders;
    private OrdersAdapter ordersAdapter;
    private List<Bill> ordersList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_user);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = user.getUid();

        rcvOrders = findViewById(R.id.rcvOrdersUser);
        rcvOrders.setLayoutManager(new LinearLayoutManager(this));
        ordersAdapter = new OrdersAdapter(ordersList, bill -> {
            // Khi click đơn hàng → sang BillActivity
            Intent intent = new Intent(OrdersUserActivity.this, BillActivity.class);
            intent.putExtra("billId", bill.getId());
            startActivity(intent);
        });
        rcvOrders.setAdapter(ordersAdapter);

        // Load đơn hàng từ Firebase
        loadUserOrders();

        // BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_ordersuser);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, Cart.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_ordersuser) {
                return true; // Đang ở đây
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserProfile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadUserOrders() {
        FireBaseHelper.getBillsRef().orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ordersList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Bill bill = ds.getValue(Bill.class);
                            if (bill != null) {
                                ordersList.add(bill);
                            }
                        }
                        ordersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrdersUserActivity.this, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
