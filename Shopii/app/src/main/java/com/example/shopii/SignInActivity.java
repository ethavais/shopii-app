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

public class SignInActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSignIn, btnSignUp, btnProductList;
    //private com.github.ybq.android.spinkit.SpinKitView progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private final UserRepository userRepository = new UserRepository(dbHelper);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initUI();
        setupListeners();
    }

    private void initUI() {
        edtEmail = findViewById(R.id.edt_email_signin);
        edtPassword = findViewById(R.id.edt_password_signin);
        btnSignIn = findViewById(R.id.btn_signin);
        btnSignUp = findViewById(R.id.btn_signup);
        btnProductList = findViewById(R.id.btn_product_list);
        //progressBar = findViewById(R.id.processBar_signin);
    }

    private void setupListeners() {
        btnSignIn.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            //progressBar.setVisibility(View.VISIBLE);
            btnSignIn.setEnabled(false);

            executor.execute(() -> {
                User user = userRepository.authenticate(email, password);
                runOnUiThread(() -> {
                    //progressBar.setVisibility(View.GONE);
                    btnSignIn.setEnabled(true);
                    
                    if (user != null) {
                        Log.i("SignInActivity", "User logged in successfully: " + user.getEmail());
                        // Navigate to main activity with user object
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("SignInActivity", "Login failed for email: " + email);
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        btnProductList.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ProductListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
