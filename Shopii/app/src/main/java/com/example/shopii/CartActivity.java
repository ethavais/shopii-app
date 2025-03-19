package com.example.shopii;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shopii.adapters.CartAdapter;
import com.example.shopii.models.CartItem;
import com.example.shopii.notifications.CartNotificationManager;
import com.example.shopii.repos.CartRepository;
import java.util.List;

public class CartActivity extends NavBarActivity {

    private CartRepository cartRepository;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceTextView;
    private CartNotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Khởi tạo CartRepository và CartNotificationManager
        cartRepository = new CartRepository(this);
        notificationManager = new CartNotificationManager(this);

        // Khởi tạo các view
        RecyclerView recyclerView = findViewById(R.id.recycler_view_cart);
        totalPriceTextView = findViewById(R.id.total_price);
        Button clearCartButton = findViewById(R.id.clear_cart_button);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = cartRepository.getCartItems(); // Lấy danh sách sản phẩm trong giỏ hàng
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(cartAdapter);

        // Cập nhật tổng giá
        updateTotalPrice();

        // Xử lý sự kiện khi nhấn nút "Clear Cart"
        clearCartButton.setOnClickListener(v -> clearCart());

        // Nhận CartItem từ Intent và thêm vào giỏ hàng
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("cartItem")) {
            CartItem cartItem = (CartItem) intent.getSerializableExtra("cartItem");
            if (cartItem != null) {
                addToCart(cartItem);
            }
        }

        // Kiểm tra và yêu cầu quyền thông báo
        notificationManager.checkAndRequestNotificationPermission();
    }

    public void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));

        // Cập nhật huy hiệu và thông báo
        int itemCount = cartItems.size();
        notificationManager.updateBadge(itemCount);
    }

    public void addToCart(CartItem newCartItem) {
        List<CartItem> cartItems = cartRepository.getCartItems();
        boolean productExists = false;

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(newCartItem.getProduct().getId())) {
                // Nếu sản phẩm đã tồn tại, cộng số lượng
                cartItem.setQuantity(cartItem.getQuantity() + newCartItem.getQuantity());
                productExists = true;
                break;
            }
        }

        // Nếu sản phẩm chưa tồn tại, thêm mới vào giỏ hàng
        if (!productExists) {
            cartItems.add(newCartItem);
        }

        // Lưu giỏ hàng và cập nhật giao diện
        saveCartItems(); // Lưu giỏ hàng sau khi thêm hoặc cập nhật
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    public void saveCartItems() {
        cartRepository.saveCartItems(cartItems); // Lưu giỏ hàng
    }

    public void clearCart() {
        cartRepository.clearCart();
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    public void removeFromCart(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hiển thị thông báo khi đóng ứng dụng
        notificationManager.showCartNotification(cartItems.size());
    }
} 