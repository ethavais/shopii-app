package com.example.shopii.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.PendingIntent;
import androidx.core.content.ContextCompat;
import androidx.core.app.TaskStackBuilder;
import com.example.shopii.R;
import com.example.shopii.CartActivity;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Lớp quản lý thông báo và huy hiệu cho giỏ hàng
 */
public class CartNotificationManager {
    private static final String CHANNEL_ID = "cart_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    
    private final Context context;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    
    /**
     * Khởi tạo CartNotificationManager
     * @param context Context của ứng dụng
     */
    public CartNotificationManager(Context context) {
        this.context = context;
        
        // Tạo kênh thông báo (cho Android 8.0+)
        createNotificationChannel();
        
        // Thiết lập requestPermissionLauncher nếu context là AppCompatActivity
        if (context instanceof AppCompatActivity) {
            setupPermissionLauncher((AppCompatActivity) context);
        }
    }
    
    /**
     * Thiết lập launcher để yêu cầu quyền thông báo
     * @param activity Activity đang hiển thị
     */
    private void setupPermissionLauncher(AppCompatActivity activity) {
        requestPermissionLauncher = activity.registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // Quyền được cấp, có thể đăng thông báo
                } else {
                    // Quyền bị từ chối
                }
            }
        );
    }
    
    /**
     * Tạo kênh thông báo cho Android 8.0+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Cart Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    /**
     * Kiểm tra và yêu cầu quyền thông báo nếu cần
     * @return true nếu có quyền, false nếu không có quyền
     */
    public boolean checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != 
                    PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền nếu có requestPermissionLauncher
                if (requestPermissionLauncher != null) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
                return false;
            }
        }
        return true;
    }
    
    /**
     * Tạo và hiển thị thông báo về giỏ hàng
     * @param itemCount Số lượng sản phẩm trong giỏ hàng
     */
    public void showCartNotification(int itemCount) {
        if (itemCount <= 0) return; // Không thông báo nếu giỏ hàng trống
        
        // Kiểm tra quyền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != 
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        // Tạo Intent để mở CartActivity khi nhấn vào thông báo
        Intent resultIntent = new Intent(context, CartActivity.class);
        
        // Tạo TaskStackBuilder để duy trì back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        
        // Tạo PendingIntent
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart)
            .setContentTitle("Giỏ hàng của bạn")
            .setContentText("Bạn có " + itemCount + " sản phẩm trong giỏ hàng")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent) // Thiết lập PendingIntent
            .setAutoCancel(true); // Tự động đóng thông báo khi nhấn vào
        
        // Hiển thị thông báo
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    
    /**
     * Cập nhật huy hiệu (badge) cho biểu tượng ứng dụng
     * @param itemCount Số lượng sản phẩm trong giỏ hàng
     */
    public void updateBadge(int itemCount) {
        if (itemCount <= 0) {
            ShortcutBadger.removeCount(context); // Xóa huy hiệu nếu giỏ hàng trống
        } else {
            ShortcutBadger.applyCount(context, itemCount); // Hiển thị huy hiệu
        }
    }
} 