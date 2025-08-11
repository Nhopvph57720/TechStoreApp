package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {
    private EditText edtFullName, edtEmail, edtPhone, edtAddress;
    private Button btnSave, btnOut;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String currentUserId;
    private ShapeableImageView imgAvatar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(v -> openImagePicker());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FireBaseHelper.getUsersRef();

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to login
            startActivity(new Intent(this, LogInActivity.class));
            finish();
            return;
        }
        currentUserId = currentUser.getUid();

        // Initialize views
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnSave = findViewById(R.id.btnSave);
        btnOut = findViewById(R.id.btnOut);

        // Load user data
        loadUserData();

        // Set up button listeners
        btnSave.setOnClickListener(v -> saveUserData());
        btnOut.setOnClickListener(v -> showLogoutDialog());

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_user);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
            } else if (id == R.id.nav_ordersuser) {
                startActivity(new Intent(this, OrdersUserActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_user) {
                // Đang ở UserProfile
                return true;
            }

            return false;
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgAvatar.setImageURI(selectedImageUri);  // Hiển thị ảnh mới lên avatar
        }
    }

    private void loadUserData() {
        databaseRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null) {
                        edtFullName.setText(user.name != null ? user.name : "");
                        edtEmail.setText(user.email != null ? user.email : "");
                        edtPhone.setText(user.phone != null ? user.phone : "");
                        edtAddress.setText(user.address != null ? user.address : "");
                    }
                } else {
                    Toast.makeText(UserProfile.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfile.this, "Lỗi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        String name = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            edtFullName.setError("Nhập tên");
            edtFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Nhập email");
            edtEmail.requestFocus();
            return;
        }

        // Disable save button during update
        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        // Create updated user object với các field mới
        UserModel updatedUser = new UserModel();
        updatedUser.name = name;
        updatedUser.email = email;
        updatedUser.phone = phone;
        updatedUser.address = address;

        // Save to Firebase Realtime Database
        databaseRef.child(currentUserId).setValue(updatedUser)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(UserProfile.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    resetSaveButton();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserProfile.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resetSaveButton();
                });
    }

    private void resetSaveButton() {
        btnSave.setEnabled(true);
        btnSave.setText("Lưu thay đổi");
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> logoutUser())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logoutUser() {
        // Sign out from Firebase Auth
        mAuth.signOut();

        // Clear any saved preferences if needed
        // SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        // prefs.edit().clear().apply();

        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Redirect to login screen
        Intent intent = new Intent(this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // UserModel class đơn giản như ban đầu
    public static class UserModel {
        public String name;
        public String email;
        public String phone;
        public String address;

        public UserModel() {
            // Firebase cần constructor rỗng
        }

        public UserModel(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

}