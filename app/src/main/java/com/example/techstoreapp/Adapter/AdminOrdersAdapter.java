package com.example.techstoreapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstoreapp.Activity.AdminOrderDetailActivity;
import com.example.techstoreapp.Model.Bill;
import com.example.techstoreapp.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Bill> orderList;

    public AdminOrdersAdapter(Context context, List<Bill> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Bill bill = orderList.get(position);
        if (bill == null) return;

        holder.tvUserId.setText("🆔 Tài khoản: " + bill.getUserId());
        holder.tvCustomerName.setText("👤 " + bill.getName());
        holder.tvPhone.setText("📞 " + bill.getPhone());
        holder.tvAddress.setText("📍 " + bill.getAddress());
        holder.tvTotal.setText("💰 " + String.format("%,d VNĐ", bill.getTotalAmount()));

        // Convert timestamp to date string
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText("🗓 " + sdf.format(new Date(bill.getTimestamp())));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetailActivity.class);
            intent.putExtra("billId", bill.getId()); // Gửi billId sang
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvCustomerName, tvPhone, tvAddress, tvTotal, tvDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}







