package com.example.techstoreapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.Model.CartItem;
import com.example.techstoreapp.R;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private Context context;
    private List<CartItem> billItems;

    public BillAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.billItems = cartItems;
    }
    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        CartItem item = billItems.get(position);
        holder.tvProductName.setText(item.getName());
        holder.tvUnitPrice.setText("Giá: " + String.format("%,d VNĐ", item.getPrice()));
        holder.tvQuantity.setText("Số lượng: " + item.getQuantity());
        holder.tvItemTotal.setText("Tổng tiền: " + String.format("%,d VNĐ", item.getTotalPrice()));

        Glide.with(context).load(item.getImageUrl()).into(holder.imgProductThumb);
    }

    @Override
    public int getItemCount() {
        return billItems.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductThumb;
        TextView tvProductName, tvUnitPrice, tvQuantity, tvItemTotal;


        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductThumb = itemView.findViewById(R.id.imgProductThumb);
            tvProductName = itemView.findViewById(R.id.tvProductNameCart);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantityCart);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
        }
    }
}
