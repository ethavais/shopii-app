package com.example.shopii;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shopii.utils.DatabaseHelper;
import com.example.shopii.repos.UserRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.shopii.models.user.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnSignUp, btnBackToSignIn;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private final UserRepository userRepository = new UserRepository(dbHelper);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUI();
        setupListeners();
    }

    private void initUI() {
        edtUsername = findViewById(R.id.edt_username_signup);
        edtEmail = findViewById(R.id.edt_email_signup);
        edtPassword = findViewById(R.id.edt_password_signup);
        edtConfirmPassword = findViewById(R.id.edt_cf_password_signup);
        btnSignUp = findViewById(R.id.btn_signup);
        btnBackToSignIn = findViewById(R.id.btn_back_to_signin);
    }

    private void setupListeners() {
        btnSignUp.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSignUp.setEnabled(false);

            executor.execute(() -> {
                String hashedPassword = hashPassword(password);
                User user = new User(username, email, hashedPassword);
                boolean success = userRepository.createUser(user);
                runOnUiThread(() -> {
                    btnSignUp.setEnabled(true);
                    
                    if (success) {
                        Log.i("SignUpActivity", "User registered successfully: " + user.getEmail());
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        finish(); // Close the sign-up activity after successful registration
                    } else {
                        Log.w("SignUpActivity", "Registration failed for email: " + email);
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        btnBackToSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish(); // Đóng SignUpActivity sau khi chuyển
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte[] byteArray = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("SignUpActivity", "Error hashing password: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
