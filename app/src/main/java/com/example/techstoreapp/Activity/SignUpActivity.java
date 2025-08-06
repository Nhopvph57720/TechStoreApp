package com.example.techstoreapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.example.techstoreapp.Model.User;
import com.example.techstoreapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword;
    private TextView btn_LogIn;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtName = findViewById(R.id.name);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.sign_up);
        btn_LogIn = findViewById(R.id.btn_goToLogin);
        ImageView imageView = findViewById(R.id.imageview_logo);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FireBaseHelper.getUsersRef();

        Glide.with(this)
                .load(R.drawable.logoda1)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(60)))
                .into(imageView);

        btn_LogIn.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            finish();
        });

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Nhập tên");
            edtName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }
        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        // Disable button during registration
        btnSignUp.setEnabled(false);
        btnSignUp.setText("Đang đăng ký...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        User userModel = new User(name, email);
                        databaseRef.child(uid).setValue(userModel)
                                .addOnSuccessListener(unused -> {
                                    // Sign out để user phải đăng nhập lại
                                    mAuth.signOut();
                                    Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi ghi DB: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    resetSignUpButton();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email đã được sử dụng!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    resetSignUpButton();
                });
    }

    private void resetSignUpButton() {
        btnSignUp.setEnabled(true);
        btnSignUp.setText("Đăng ký");
    }


}