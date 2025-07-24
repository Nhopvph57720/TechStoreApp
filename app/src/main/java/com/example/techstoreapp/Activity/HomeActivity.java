package com.example.techstoreapp.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techstoreapp.Adapter.ProductAdapter;
import com.example.techstoreapp.Adapter.SearchAdapter;
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
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView rcvSanPham, rcvTrenDing;
    private ProductAdapter adapter;
    private TrendingAdapter trendingAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> searchList  = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();

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
                 String selected = category.getText().toString();
                 filterByCategory(selected);
            });
        }
        ///
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

        ///

        EditText edtSearch = findViewById(R.id.edt_search);
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_search_result, null);

        ListView lvSearch = popupView.findViewById(R.id.lv_search_result);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
        );

        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        SearchAdapter popupAdapter = new SearchAdapter(this, searchList);
        lvSearch.setAdapter(popupAdapter);


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchList.clear();
                    for (Product product : allProducts) {
                        if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                            searchList.add(product);
                        }
                    }
                    popupAdapter.notifyDataSetChanged();

                    if (!popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(edtSearch);
                    }
                } else {
                    popupWindow.dismiss();
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        lvSearch.setOnItemClickListener((parent, view, position, id) -> {
            Product selected = searchList.get(position);
            Toast.makeText(HomeActivity.this, "Đã chọn: " + selected.getName(), Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();

            // Bạn có thể xử lý thêm: ví dụ mở chi tiết sản phẩm hoặc gán vào form
        });



        //pushSampleDataByCategory();

        loadProductsFromFirebase();
        loadTrendingFromFirebase();

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
                        Toast.makeText(HomeActivity.this, "Lỗi lọc: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



//    private void pushSampleDataByCategory() {
//        DatabaseReference dbRef = FireBaseHelper.getProductsRef();
//
//        List<Product> sampleList = Arrays.asList(
//                new Product(null, "Laptop Dell XPS", 25000000, "https://via.placeholder.com/150", "Laptop"),
//                new Product(null, "Tai nghe Sony", 7000000, "https://via.placeholder.com/150", "Tai nghe"),
//                new Product(null, "iPad Air", 18000000, "https://via.placeholder.com/150", "Tablet"),
//                new Product(null, "Bàn phím cơ Razer", 2500000, "https://via.placeholder.com/150", "Bàn phím"),
//                new Product(null, "Điện thoại Samsung", 21000000, "https://via.placeholder.com/150", "Điện thoại")
//        );
//
//        for (Product p : sampleList) {
//            String id = dbRef.push().getKey(); // Tạo ID tự động
//            if (id != null) {
//                p.setId(id); // Đặt ID vào sản phẩm
//                dbRef.child(id).setValue(p); // Lưu vào Firebase
//            }
//        }
//
//        Toast.makeText(this, "Đã thêm dữ liệu theo danh mục!", Toast.LENGTH_SHORT).show();
//    }


}