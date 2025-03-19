package com.example.shopii;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.shopii.adapters.ImageSliderAdapter;
import com.example.shopii.models.CartItem;
import com.example.shopii.models.Product;
import com.example.shopii.repos.CartRepository;
import me.relex.circleindicator.CircleIndicator3;

public class ProductDetailActivity extends AppCompatActivity {

    private CartRepository cartRepository;
    private TextView quantityDisplay;
    private int quantity = 1;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize CartRepository
        cartRepository = new CartRepository(this);

        // Nhận sản phẩm từ Intent
        Product product = (Product) getIntent().getSerializableExtra("product");

        // Initialize views
        ViewPager2 imageSlider = findViewById(R.id.image_slider);
        CircleIndicator3 indicator = findViewById(R.id.indicator);
        TextView productName = findViewById(R.id.detail_product_name);
        TextView productBrand = findViewById(R.id.detail_product_brand);
        TextView productPrice = findViewById(R.id.detail_product_price);
        TextView productRating = findViewById(R.id.detail_product_rating);
        TextView productStock = findViewById(R.id.detail_product_stock);
        TextView productDescription = findViewById(R.id.detail_product_description);

        // Initialize Add to Cart button and quantity input
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        quantityDisplay = findViewById(R.id.quantity_display);
        TextView decreaseQuantity = findViewById(R.id.decrease_quantity);
        TextView increaseQuantity = findViewById(R.id.increase_quantity);

        // Set initial quantity
        quantityDisplay.setText(String.valueOf(quantity));

        // Set click listeners
        decreaseQuantity.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityDisplay.setText(String.valueOf(quantity));
            }
        });

        increaseQuantity.setOnClickListener(v -> {
            quantity++;
            quantityDisplay.setText(String.valueOf(quantity));
        });

        // Update Add to Cart button click listener
        addToCartButton.setOnClickListener(v -> {
            CartItem cartItem = new CartItem(product, quantity);
            cartRepository.addToCart(cartItem);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Set product data
        if (product != null) {
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                ImageSliderAdapter adapter = new ImageSliderAdapter(product.getImageUrls());
                imageSlider.setAdapter(adapter);
                indicator.setViewPager(imageSlider);
            }
            productName.setText(product.getName());
            productBrand.setText(product.getBrand());
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            productRating.setText(String.format("Rating: %.1f/5", product.getRating()));
            productStock.setText(String.format("In Stock: %d", product.getStockQuantity()));
            productDescription.setText(product.getDescription());
        }
    }
} 