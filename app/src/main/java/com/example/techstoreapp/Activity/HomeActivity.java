package com.example.techstoreapp.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
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

import com.example.techstoreapp.Adapter.ProductAdapter;
import com.example.techstoreapp.Adapter.TrendingAdapter;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.Model.Trending;
import com.example.techstoreapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rcvSanPham, rcvTrenDing;
    private ProductAdapter adapter;
    private TrendingAdapter trendingAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Trending> trendingList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                // String selected = category.getText().toString();
                // filterByCategory(selected);
            });
        }

        rcvSanPham = findViewById(R.id.rcv_sanpham);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcvSanPham.setLayoutManager(layoutManager);

        adapter = new ProductAdapter(productList);
        rcvSanPham.setAdapter(adapter);


        /////
        rcvTrenDing = findViewById(R.id.rcv_trending);
        trendingAdapter = new TrendingAdapter(trendingList);
        rcvTrenDing.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvTrenDing.setAdapter(trendingAdapter);



        //pushSampleData();

        loadProductsFromFirebase();
        loadTrendingFromFirebase();

    }

    private void loadProductsFromFirebase() {
        DatabaseReference dbRef = FireBaseHelper.getProductsRef();

        dbRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(HomeActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTrendingFromFirebase() {
        DatabaseReference dbRef = FireBaseHelper.getTrendingRef();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trendingList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Trending trending = item.getValue(Trending.class);
                    if (trending != null) {
                        trendingList.add(trending);
                    }
                }
                trendingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Lỗi tải trending: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




//    private void pushSampleData() {
//        DatabaseReference dbRef = FireBaseHelper.getProductsRef();
//
//        for (int i = 1; i <= 5; i++) {
//            String id = dbRef.push().getKey();
//            Product p = new Product(
//                    id,
//                    "Sản phẩm " + i,
//                    10000000 * i,
//                    "https://via.placeholder.com/150x150.png?text=SP+" + i
//            );
//            dbRef.child(id).setValue(p);
//        }
//
//        Toast.makeText(this, "Đã thêm dữ liệu mẫu!", Toast.LENGTH_SHORT).show();
//    }
}