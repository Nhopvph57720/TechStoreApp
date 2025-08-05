package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.Model.CartItem;
import com.example.techstoreapp.Model.Product;
import com.example.techstoreapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView imgDetail;
    private TextView tvNameDetail, tvPriceDetail, tvQuantity, tvDescription;
    private ImageButton btnMinus, btnPlus;
    private Button btnAddToCart;

    private Product product;
    private int quantity = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgDetail = findViewById(R.id.imgDetail);
        tvNameDetail = findViewById(R.id.tvNameDetail);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvDescription = findViewById(R.id.tvDescription);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            tvNameDetail.setText(product.getName());
            tvPriceDetail.setText(String.format("%,d VNĐ", product.getPrice()));
            Glide.with(this).load(product.getImageUrl()).into(imgDetail); // nếu có Glide

            tvDescription.setText(product.getDescription());

        }

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnAddToCart.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String uid = auth.getCurrentUser().getUid();

            String productId = product.getId();
            DatabaseReference cartRef = FireBaseHelper.getCartRef(uid).child(productId);

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int finalQuantity = quantity;

                    if (snapshot.exists()) {
                        CartItem existingItem = snapshot.getValue(CartItem.class);
                        if (existingItem != null) {
                            finalQuantity += existingItem.getQuantity(); // ✅ cộng dồn tại đây
                        }
                    }

                    // ✅ Tạo CartItem mới với số lượng cập nhật
                    CartItem updatedItem = new CartItem(
                            productId,
                            product.getName(),
                            product.getPrice(),
                            finalQuantity,
                            product.getImageUrl()
                    );

                    cartRef.setValue(updatedItem)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CART_DEBUG", "Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
                                Toast.makeText(ProductDetailActivity.this, "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            });
        });






        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Không chọn mục nào khi mở màn hình
        bottomNav.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            bottomNav.getMenu().getItem(i).setChecked(false);
        }
        bottomNav.getMenu().setGroupCheckable(0, true, true);

        // Xử lý điều hướng
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, Cart.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserProfile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });


    }
}