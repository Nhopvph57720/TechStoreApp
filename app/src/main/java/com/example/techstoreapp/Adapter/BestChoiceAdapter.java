package com.example.techstoreapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.Activity.ProductDetailActivity;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.R;

import java.text.DecimalFormat;
import java.util.List;

public class BestChoiceAdapter extends RecyclerView.Adapter<BestChoiceAdapter.BestChoiceViewHolder> {
    private Context context;
    private List<Product> productList;

    public BestChoiceAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public BestChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_best_choice, parent, false);
        return new BestChoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestChoiceViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedPrice = formatter.format(product.price);
        holder.tvPrice.setText("Giá: " + formattedPrice + " VNĐ");


        Glide.with(context)
                .load(product.getImageUrl()) // URL ảnh
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product); // p phải implements Serializable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class BestChoiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;

        public BestChoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}

