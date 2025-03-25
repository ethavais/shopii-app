package com.example.shopii;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.shopii.models.user.User;
import com.example.shopii.repos.UserRepository;
import com.example.shopii.utils.DatabaseHelper;
import com.example.shopii.utils.SharedPreferencesManager;

public class ProfileActivity extends NavBarActivity {

    private TextView txtUsername, txtEmail, txtPhone;
    private Button btnUpdateProfile;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private UserRepository userRepository;
    private User currentUser;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userRepository = new UserRepository(new DatabaseHelper());
        
        initViews();
        
        fetchUserProfile();
        
        setupListeners();
    }

    private void initViews() {
        txtUsername = findViewById(R.id.txt_profile_username);
        txtEmail = findViewById(R.id.txt_profile_email);
        txtPhone = findViewById(R.id.txt_profile_phone);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
    }

    private void fetchUserProfile() {
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        String userEmail = prefsManager.getUserEmail();
        
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        
        executor.execute(() -> {
            currentUser = userRepository.getUserByEmail(userEmail);
            
            runOnUiThread(() -> {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                
                if (currentUser != null) {
                    txtUsername.setText(currentUser.getUsername());
                    txtEmail.setText(currentUser.getEmail());
                    
                    String phone = currentUser.getPhoneNumber();
                    if (phone != null && !phone.isEmpty()) {
                        txtPhone.setText(phone);
                    } else {
                        txtPhone.setText("Not provided");
                    }
                    
                    TextView txtStatus = findViewById(R.id.txt_profile_status);
                    if (currentUser.isActive()) {
                        txtStatus.setText("Status: Active");
                        txtStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        txtStatus.setText("Status: Inactive");
                        txtStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                } else {
                    Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "User not found: " + userEmail);
                }
            });
        });
    }

    private void setupListeners() {
        btnUpdateProfile.setOnClickListener(v -> {
            // Tạo dialog hoặc chuyển đến màn hình cập nhật thông tin
            Toast.makeText(this, "Update profile feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 