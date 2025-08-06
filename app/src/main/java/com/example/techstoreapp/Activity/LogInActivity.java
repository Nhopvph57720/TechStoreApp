package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.techstoreapp.FirebaseHelper.FireBaseHelper;
import com.example.techstoreapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {
    private TextView btn_goToSignUp;
    private Button btn_Login;
    private EditText edtEmail, edtPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FireBaseHelper.getUsersRef();

        // Initialize views
        btn_goToSignUp = findViewById(R.id.btn_goToSignUp);
        btn_Login = findViewById(R.id.sign_in);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        ////
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        // Check if user is already logged in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // User is already signed in, redirect to HomeActivity
//            startActivity(new Intent(LogInActivity.this, HomeActivity.class));
//            finish();
//            return;
//        }

        btn_goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        ImageView imageView = findViewById(R.id.imageview_logo);
        Glide.with(this)
                .load(R.drawable.logoda1)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(60)))
                .into(imageView);
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            edtEmail.setError("Nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        // Disable button during login process
        btn_Login.setEnabled(false);
        btn_Login.setText("Đang đăng nhập...");

        // Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Check if user data exists in Realtime Database
                        String uid = user.getUid();
                        databaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Boolean isAdmin = dataSnapshot.child("isAdmin").getValue(Boolean.class);
                                    Toast.makeText(LogInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                    Intent intent;
                                    if (Boolean.TRUE.equals(isAdmin)) {
                                        // 👉 Admin → mở màn hình AdminDashboardActivity
                                        intent = new Intent(LogInActivity.this, AdminDashboardActivity.class);
                                    } else {
                                        // 👉 User thường → mở HomeActivity
                                        intent = new Intent(LogInActivity.this, HomeActivity.class);
                                    }

                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    // User data doesn't exist in database, sign out and show error
                                    mAuth.signOut();
                                    Toast.makeText(LogInActivity.this, "Dữ liệu người dùng không tồn tại!", Toast.LENGTH_LONG).show();
                                    resetLoginButton();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(LogInActivity.this, "Lỗi kiểm tra dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                resetLoginButton();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LogInActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show();
                    resetLoginButton();
                });

    }

    private void resetLoginButton() {
        btn_Login.setEnabled(true);
        btn_Login.setText("Đăng nhập");
    }
}