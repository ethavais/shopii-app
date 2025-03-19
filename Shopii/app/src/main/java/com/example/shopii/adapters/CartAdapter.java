package com.example.shopii.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import com.example.shopii.CartActivity;
import com.example.shopii.ProductDetailActivity;
import com.example.shopii.R;
import com.example.shopii.models.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final CartActivity cartActivity;

    public CartAdapter(List<CartItem> cartItems, CartActivity cartActivity) {
        this.cartItems = cartItems;
        this.cartActivity = cartActivity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.productName.setText(cartItem.getProduct().getName());
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));

        double totalPrice = cartItem.getProduct().getPrice() * cartItem.getQuantity();
        holder.productPrice.setText(String.format("$%.2f", totalPrice));

        if (cartItem.getProduct().getImageUrls() != null && !cartItem.getProduct().getImageUrls().isEmpty()) {
            Picasso.get()
                .load(cartItem.getProduct().getImageUrls().get(0))
                .fit()
                .centerInside()
                .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.increaseQuantity.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            notifyItemChanged(position);
            cartActivity.updateTotalPrice();
            cartActivity.saveCartItems();
        });

        holder.decreaseQuantity.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                notifyItemChanged(position);
                cartActivity.updateTotalPrice();
                cartActivity.saveCartItems();
            }
        });

        holder.removeItem.setOnClickListener(v -> {
            cartItems.remove(cartItem);
            notifyDataSetChanged();
            cartActivity.updateTotalPrice();
            cartActivity.saveCartItems();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(cartActivity, ProductDetailActivity.class);
            intent.putExtra("product", cartItem.getProduct());
            cartActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantity;
        ImageButton increaseQuantity, decreaseQuantity, removeItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.product_quantity);
            increaseQuantity = itemView.findViewById(R.id.increase_quantity);
            decreaseQuantity = itemView.findViewById(R.id.decrease_quantity);
            removeItem = itemView.findViewById(R.id.remove_item);
        }
    }
}