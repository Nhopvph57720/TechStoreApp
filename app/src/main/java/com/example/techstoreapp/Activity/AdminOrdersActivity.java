package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstoreapp.Adapter.AdminOrdersAdapter;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.Bill;
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

public class AdminOrdersActivity extends AppCompatActivity {

    private RecyclerView rcvOrders;
    private AdminOrdersAdapter adapter;
    private List<Bill> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        rcvOrders = findViewById(R.id.rcvOrders);
        rcvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrdersAdapter(this, orderList);
        rcvOrders.setAdapter(adapter);

        // Load tất cả đơn hàng
        loadAllOrders();

        // Xử lý Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_ordersadmin); // chọn tab hiện tại

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            else if (id == R.id.nav_ordersadmin) {
                return true; // đang ở màn này
            }
            else if (id == R.id.nav_logout) {
                new AlertDialog.Builder(this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(this, LogInActivity.class));
                            finish();
                        })
                        .setNegativeButton("Không", null)
                        .show();
                return true;
            }
            return false;
        });
    }


    private void loadAllOrders() {
        FireBaseHelper.getBillsRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Bill bill = item.getValue(Bill.class);
                            if (bill != null) {
                                bill.setId(item.getKey()); // Gán id để theo dõi
                                orderList.add(bill);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminOrdersActivity.this,
                                "Lỗi tải đơn hàng: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

