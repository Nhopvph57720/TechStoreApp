package com.example.techstoreapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public AdminProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%,d₫", product.getPrice()));
        Glide.with(context).load(product.getImageUrl()).into(holder.imgProduct);

        // Xóa sản phẩm
//        holder.btnDelete.setOnClickListener(v -> {
//            new AlertDialog.Builder(context)
//                    .setTitle("Xác nhận xóa")
//                    .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
//                    .setPositiveButton("Xóa", (dialog, which) -> {
//                        FirebaseDatabase.getInstance().getReference("products")
//                                .child(product.getId())
//                                .removeValue()
//                                .addOnSuccessListener(aVoid ->
//                                        Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show()
//                                ).addOnFailureListener(e ->
//                                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                                );
//                    })
//                    .setNegativeButton("Hủy", null)
//                    .show();
//        });

        // Sửa sản phẩm (ví dụ mở màn hình sửa)
//        holder.btnEdit.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EditProductActivity.class);
//            intent.putExtra("product", product); // Product implements Serializable
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgProduct;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameproductAdm);
            tvPrice = itemView.findViewById(R.id.tvPriceProductAdm);
            imgProduct = itemView.findViewById(R.id.imgProductProductAdm);
            btnEdit = itemView.findViewById(R.id.btnEditProductAdm);
            btnDelete = itemView.findViewById(R.id.btnDeleteProductAdm);
        }
    }
}



