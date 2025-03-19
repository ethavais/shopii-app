package com.example.shopii;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

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
    }
} 