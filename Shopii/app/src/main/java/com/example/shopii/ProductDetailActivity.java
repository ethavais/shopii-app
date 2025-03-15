package com.example.shopii;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shopii.models.CartItem;
import com.example.shopii.models.Product;
import com.example.shopii.repos.CartRepository;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {

    private CartRepository cartRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize CartRepository
        cartRepository = new CartRepository(this);

        // Get the passed product
        Product product = (Product) getIntent().getSerializableExtra("product");

        // Initialize views
        ImageView productImage = findViewById(R.id.detail_product_image);
        TextView productName = findViewById(R.id.detail_product_name);
        TextView productBrand = findViewById(R.id.detail_product_brand);
        TextView productPrice = findViewById(R.id.detail_product_price);
        TextView productRating = findViewById(R.id.detail_product_rating);
        TextView productStock = findViewById(R.id.detail_product_stock);
        TextView productDescription = findViewById(R.id.detail_product_description);

        // Initialize Add to Cart button and quantity input
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        EditText quantityInput = findViewById(R.id.quantity_input);

        // Set click listener for Add to Cart button
        addToCartButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(quantityInput.getText().toString());
            if (quantity > 0) {
                CartItem cartItem = new CartItem(product, quantity);
                cartRepository.addToCart(cartItem);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            }
        });

        // Set product data
        if (product != null) {
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                Picasso.get().load(product.getImageUrls().get(0)).into(productImage);
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