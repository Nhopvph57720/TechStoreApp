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

import com.example.techstoreapp.Adapter.BillAdapter;
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

public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView tvBillId, tvName, tvPhone, tvAddress, tvTotal, tvTime;
    private RecyclerView rcvBillItems;
    private List<CartItem> billItems = new ArrayList<>();
    private BillAdapter adapter; // Sử dụng BillAdapter để hiện sản phẩm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        // Ánh xạ view
        tvBillId = findViewById(R.id.tvBillId);
        tvName = findViewById(R.id.tvBillName);
        tvPhone = findViewById(R.id.tvBillPhone);
        tvAddress = findViewById(R.id.tvBillAddress);
        tvTotal = findViewById(R.id.tvBillTotal);
        tvTime = findViewById(R.id.tvBillTime);
        rcvBillItems = findViewById(R.id.rcvBillItems);

        // Setup RecyclerView
        adapter = new BillAdapter(this, billItems);
        rcvBillItems.setLayoutManager(new LinearLayoutManager(this));
        rcvBillItems.setAdapter(adapter);

        // Nhận billId từ intent
        String billId = getIntent().getStringExtra("billId");
        if (billId != null) {
            loadBillDetails(billId);
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_ordersadmin); // đang ở màn đơn hàng

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_ordersadmin) {
                startActivity(new Intent(this, AdminOrdersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LogInActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });


    }

    private void loadBillDetails(String billId) {
        FireBaseHelper.getBillsRef().child(billId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Bill bill = snapshot.getValue(Bill.class);
                        if (bill != null) {
                            showBillDetails(bill);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminOrderDetailActivity.this, "Lỗi tải chi tiết", Toast.LENGTH_SHORT).show();
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
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(new Date(bill.getTimestamp())));

        billItems.clear();
        billItems.addAll(bill.getItems());
        adapter.notifyDataSetChanged();
    }
}
