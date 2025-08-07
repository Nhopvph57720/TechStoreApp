package com.example.techstoreapp.Activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstoreapp.Adapter.AdminProductAdapter;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private RecyclerView rcvSanPham;
    private AdminProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private Button btnAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast.makeText(this, "Xin chào Admin!", Toast.LENGTH_SHORT).show();

        // Ánh xạ
        btnAddProduct = findViewById(R.id.btn_add_product);
        rcvSanPham = findViewById(R.id.rcv_sanpham);
        rcvSanPham.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(this, productList);
        rcvSanPham.setAdapter(adapter);

        // Gọi API load sản phẩm
        loadProductsFromFirebase();

        // Bắt sự kiện nút thêm
        btnAddProduct.setOnClickListener(v -> showAddProductDialog());

        // Bắt sự kiện chọn danh mục
        TextView tvLaptop = findViewById(R.id.tvLaptop);
        TextView tvDienThoai = findViewById(R.id.tvDienThoai);
        TextView tvTaiNghe = findViewById(R.id.tvTaiNghe);
        TextView tvTablet = findViewById(R.id.tvTablet);
        TextView tvBanPhim = findViewById(R.id.tvBanPhim);

        TextView[] categories = {tvLaptop, tvDienThoai, tvTaiNghe, tvTablet, tvBanPhim};

        for (TextView category : categories) {
            category.setOnClickListener(v -> {
                // Reset tất cả
                for (TextView c : categories) {
                    c.setBackgroundResource(R.drawable.bg_category_chip);
                    c.setTextColor(ContextCompat.getColor(this, R.color.red_E00));
                }

                // Gán màu đỏ cho mục được chọn
                category.setBackgroundResource(R.drawable.bg_category_chip_selected);
                category.setTextColor(Color.WHITE);

                // Lọc theo danh mục
                String selected = category.getText().toString();
                filterByCategory(selected);
            });
        }
    }

    private void loadProductsFromFirebase() {
        DatabaseReference dbRef = FireBaseHelper.getProductsRef();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                productList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    if (product != null) {
                        allProducts.add(product);
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByCategory(String categoryName) {
        DatabaseReference dbRef = FireBaseHelper.getProductsRef();

        dbRef.orderByChild("category").equalTo(categoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Product product = item.getValue(Product.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminDashboardActivity.this, "Lỗi lọc: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtPrice = view.findViewById(R.id.edtPrice);
        EditText edtImageUrl = view.findViewById(R.id.edtImageUrl);
        Spinner spnCategory = view.findViewById(R.id.spnCategory);
        EditText edtDescription = view.findViewById(R.id.edtDescription);

        String[] categories = {"Laptop", "Điện thoại", "Tai nghe", "Tablet", "Bàn phím"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);

        builder.setTitle("Thêm sản phẩm mới");

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();
            String category = spnCategory.getSelectedItem().toString();
            String description = edtDescription.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            DatabaseReference dbRef = FireBaseHelper.getProductsRef();
            String id = dbRef.push().getKey();

            Product product = new Product(id, name, price, imageUrl, category, description);

            dbRef.child(id).setValue(product)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Đã thêm sản phẩm!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
