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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillActivity extends AppCompatActivity {

    private TextView tvBillId, tvName, tvPhone, tvAddress, tvTotal, tvTime;
    private RecyclerView rcvBillItems;
    private List<CartItem> billItems = new ArrayList<>();
    private CartAdapter adapter;
    private Button btnBackToHome;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        // Ánh xạ view
        tvBillId = findViewById(R.id.tvBillId);
        tvName = findViewById(R.id.tvBillName);
        tvPhone = findViewById(R.id.tvBillPhone);
        tvAddress = findViewById(R.id.tvBillAddress);
        tvTotal = findViewById(R.id.tvBillTotal);
        tvTime = findViewById(R.id.tvBillTime);
        rcvBillItems = findViewById(R.id.rcvBillItems);
        btnBackToHome = findViewById(R.id.btnBackHome);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup RecyclerView
        adapter = new CartAdapter(this, billItems);
        rcvBillItems.setLayoutManager(new LinearLayoutManager(this));
        rcvBillItems.setAdapter(adapter);

        // Chọn icon Đơn hàng
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
                return true; // đang ở đây rồi
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserProfile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // Load đơn mới nhất
        loadLatestBill();

        // Nút về Home
        btnBackToHome.setOnClickListener(v -> {
            startActivity(new Intent(BillActivity.this, HomeActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void loadLatestBill() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FireBaseHelper.getBillsRef()
                .orderByChild("userId").equalTo(uid)
                .limitToLast(1) // lấy 1 đơn mới nhất
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(BillActivity.this, "Bạn chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DataSnapshot billSnap : snapshot.getChildren()) {
                            Bill bill = billSnap.getValue(Bill.class);
                            if (bill != null) {
                                showBillDetails(bill);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BillActivity.this, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBillDetails(Bill bill) {
        tvBillId.setText("Mã hoá đơn: " + bill.getId());
        tvName.setText("👤 " + bill.getName());
        tvPhone.setText("📞 " + bill.getPhone());
        tvAddress.setText("📍 " + bill.getAddress());
        tvTotal.setText("Tổng tiền: " + String.format("%,d VNĐ", bill.getTotalAmount()));
        tvTime.setText("Ngày đặt: " +
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(bill.getTimestamp())));

        billItems.clear();
        billItems.addAll(bill.getItems());
        adapter.notifyDataSetChanged();
    }
}


