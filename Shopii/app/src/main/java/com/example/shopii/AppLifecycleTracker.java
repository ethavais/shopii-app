package com.example.shopii;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.example.shopii.notifications.CartNotificationManager;
import com.example.shopii.repos.CartRepository;

public class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks {

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // Ứng dụng đã vào foreground
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // Ứng dụng đã vào background (đóng)
            showNotification(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
    
    private void showNotification(Activity activity) {
        // Khởi tạo CartRepository để lấy danh sách sản phẩm
        CartRepository cartRepository = new CartRepository(activity);
        int itemCount = cartRepository.getCartItems().size();
        
        // Khởi tạo CartNotificationManager để hiển thị thông báo
        CartNotificationManager notificationManager = new CartNotificationManager(activity);
        notificationManager.showCartNotification(itemCount);
    }
} 