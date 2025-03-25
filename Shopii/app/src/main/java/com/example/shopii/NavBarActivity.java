package com.example.shopii;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shopii.utils.SharedPreferencesManager;

public class NavBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupNavBar();
    }
    
    private void setupNavBar() {
        Button productListButton = findViewById(R.id.nav_product_list);
        Button cartButton = findViewById(R.id.nav_cart);
        Button profileButton = findViewById(R.id.nav_profile);
        
        if (productListButton != null) {
            productListButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductListActivity.class);
                startActivity(intent);
            });
        }
        
        if (cartButton != null) {
            cartButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            });
        }
        
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
                SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
                
                if (prefsManager.isUserLoggedIn()) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please sign in to view your profile", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
} 