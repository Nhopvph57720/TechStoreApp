package com.example.techstoreapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.R;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private DatabaseReference productsRef;

    public AdminProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productsRef = FireBaseHelper.getProductsRef();
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

        // Xử lý nút Sửa
        holder.btnEdit.setOnClickListener(v -> showEditDialog(product, position));

        // Xử lý nút Xóa
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(product, position));
    }

    private void showEditDialog(Product product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa sản phẩm");

        // Tạo layout cho dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_product, null);

        EditText edtName = dialogView.findViewById(R.id.edt_product_name);
        EditText edtPrice = dialogView.findViewById(R.id.edt_product_price);
        EditText edtImageUrl = dialogView.findViewById(R.id.edt_product_image);
        EditText edtCategory = dialogView.findViewById(R.id.edt_product_category);
        EditText edtDescription = dialogView.findViewById(R.id.edt_product_description);

        // Điền dữ liệu hiện tại
        edtName.setText(product.getName());
        edtPrice.setText(String.valueOf(product.getPrice()));
        edtImageUrl.setText(product.getImageUrl());
        edtCategory.setText(product.getCategory());
        edtDescription.setText(product.getDescription());

        builder.setView(dialogView);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();
            String category = edtCategory.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            // Kiểm tra dữ liệu
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(context, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(priceStr)) {
                Toast.makeText(context, "Vui lòng nhập giá sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceStr);
                if (price <= 0) {
                    Toast.makeText(context, "Giá sản phẩm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Giá sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(category)) {
                Toast.makeText(context, "Vui lòng nhập danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật sản phẩm
            updateProduct(product.getId(), name, price, imageUrl, category, description, position);
        });

        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateProduct(String productId, String name, int price, String imageUrl, String category, String description, int position) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("price", price);
        updates.put("imageUrl", imageUrl);
        updates.put("category", category);
        updates.put("description", description);

        productsRef.child(productId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();

                    // Cập nhật dữ liệu local
                    Product updatedProduct = productList.get(position);
                    updatedProduct.setName(name);
                    updatedProduct.setPrice(price);
                    updatedProduct.setImageUrl(imageUrl);
                    updatedProduct.setCategory(category);
                    updatedProduct.setDescription(description);

                    notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteDialog(Product product, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product.getId(), position))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteProduct(String productId, int position) {
        productsRef.child(productId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();

                    // Xóa khỏi danh sách local và cập nhật RecyclerView
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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