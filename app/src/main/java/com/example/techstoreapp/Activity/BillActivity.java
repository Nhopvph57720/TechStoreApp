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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        tvBillId = findViewById(R.id.tvBillId);
        tvName = findViewById(R.id.tvBillName);
        tvPhone = findViewById(R.id.tvBillPhone);
        tvAddress = findViewById(R.id.tvBillAddress);
        tvTotal = findViewById(R.id.tvBillTotal);
        tvTime = findViewById(R.id.tvBillTime);
        rcvBillItems = findViewById(R.id.rcvBillItems);
        btnBackToHome = findViewById(R.id.btnBackHome);

        adapter = new CartAdapter(this, billItems);
        rcvBillItems.setLayoutManager(new LinearLayoutManager(this));
        rcvBillItems.setAdapter(adapter);

        String billId = getIntent().getStringExtra("billId");
        if (billId == null) {
            finish();
            return;
        }

        FireBaseHelper.getBillsRef().child(billId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Bill bill = snapshot.getValue(Bill.class);
                if (bill != null) {
                    tvBillId.setText("Mã hoá đơn: " + bill.getId());
                    tvName.setText("👤 " + bill.getName());
                    tvPhone.setText("📞 " + bill.getPhone());
                    tvAddress.setText("📍 " + bill.getAddress());
                    tvTotal.setText("Tổng tiền: " + String.format("%,d VNĐ", bill.getTotalAmount()));
                    tvTime.setText("Ngày đặt: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(bill.getTimestamp())));

                    billItems.clear();
                    billItems.addAll(bill.getItems());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BillActivity.this, "Lỗi tải hóa đơn", Toast.LENGTH_SHORT).show();
            }
        });


        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BillActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
