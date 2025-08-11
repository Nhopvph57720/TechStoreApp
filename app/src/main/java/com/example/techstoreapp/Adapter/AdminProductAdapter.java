package com.example.techstoreapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.R;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
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

        holder.btnEdit.setOnClickListener(v -> showEditDialog(product));
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(product));
    }

    private void showEditDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa sản phẩm");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_product, null);

        EditText edtName = dialogView.findViewById(R.id.edt_product_name);
        EditText edtPrice = dialogView.findViewById(R.id.edt_product_price);
        EditText edtImageUrl = dialogView.findViewById(R.id.edt_product_image);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_product_category); // Spinner thay vì EditText
        EditText edtDescription = dialogView.findViewById(R.id.edt_product_description);

        edtName.setText(product.getName());
        edtPrice.setText(String.valueOf(product.getPrice()));
        edtImageUrl.setText(product.getImageUrl());
        edtDescription.setText(product.getDescription());

        // Danh sách danh mục
        String[] categories = {"Laptop", "Điện thoại", "Tai nghe", "Tablet", "Bàn phím"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Chọn đúng danh mục của sản phẩm hiện tại
        int categoryPosition = Arrays.asList(categories).indexOf(product.getCategory());
        if (categoryPosition >= 0) {
            spinnerCategory.setSelection(categoryPosition);
        }

        builder.setView(dialogView);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString(); // Lấy từ Spinner
            String description = edtDescription.getText().toString().trim();

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

            updateProduct(product.getId(), name, price, imageUrl, category, description);
        });

        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void updateProduct(String productId, String name, int price, String imageUrl, String category, String description) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("price", price);
        updates.put("imageUrl", imageUrl);
        updates.put("category", category);
        updates.put("description", description);

        productsRef.child(productId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();

                    int index = findIndexById(productId);
                    if (index != -1) {
                        Product updatedProduct = productList.get(index);
                        updatedProduct.setName(name);
                        updatedProduct.setPrice(price);
                        updatedProduct.setImageUrl(imageUrl);
                        updatedProduct.setCategory(category);
                        updatedProduct.setDescription(description);

                        notifyItemChanged(index);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteDialog(Product product) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product.getId()))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteProduct(String productId) {
        productsRef.child(productId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    int index = findIndexById(productId);
                    if (index != -1) {
                        productList.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, productList.size());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int findIndexById(String productId) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(productId)) {
                return i;
            }
        }
        return -1;
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