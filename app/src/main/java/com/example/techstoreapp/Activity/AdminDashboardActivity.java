package com.example.techstoreapp.Activity;

import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private RecyclerView rcvSanPham;
    private AdminProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();

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
        ///

        // Danh sách các danh mục
        TextView tvLaptop = findViewById(R.id.tvLaptop);
        TextView tvDienThoai = findViewById(R.id.tvDienThoai);
        TextView tvTaiNghe = findViewById(R.id.tvTaiNghe);
        TextView tvTablet = findViewById(R.id.tvTablet);
        TextView tvBanPhim = findViewById(R.id.tvBanPhim);

        TextView[] categories = {tvLaptop, tvDienThoai, tvTaiNghe, tvTablet, tvBanPhim};

        for (TextView category : categories) {
            category.setOnClickListener(v -> {
                // Reset tất cả về mặc định
                for (TextView c : categories) {
                    c.setBackgroundResource(R.drawable.bg_category_chip);
                    c.setTextColor(ContextCompat.getColor(this, R.color.red_E00));
                }

                // Gán màu đỏ cho chip đang được click
                category.setBackgroundResource(R.drawable.bg_category_chip_selected);
                category.setTextColor(Color.WHITE);

                // TODO: nếu cần lọc sản phẩm thì làm ở đây
                String selected = category.getText().toString();
                filterByCategory(selected);
            });
        }

        ///
        rcvSanPham = findViewById(R.id.rcv_sanpham);
        rcvSanPham.setLayoutManager(new LinearLayoutManager(this)); // Dọc

        adapter = new AdminProductAdapter(this, productList);
        rcvSanPham.setAdapter(adapter);

        loadProductsFromFirebase();
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
                        allProducts.add(product);   // lưu vào danh sách đầy đủ
                        productList.add(product);   // hiển thị ban đầu
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
}
