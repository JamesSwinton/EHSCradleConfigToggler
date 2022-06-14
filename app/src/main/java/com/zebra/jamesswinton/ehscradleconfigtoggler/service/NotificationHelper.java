package com.zebra.jamesswinton.ehscradleconfigtoggler.service;

import static com.zebra.jamesswinton.ehscradleconfigtoggler.MonitorCradleStateService.StopServiceAction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.zebra.jamesswinton.ehscradleconfigtoggler.MonitorCradleStateService;
import com.zebra.jamesswinton.ehscradleconfigtoggler.R;

public class NotificationHelper {

    // Constants
    public static final int NOTIFICATION_ID = 1;

    public static Notification createNotification(Context cx) {
        // Create Variables
        String channelId = "com.zebra.ehscradleconfig";
        String channelName = "Foreground service channel";

        // Create Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    channelName, android.app.NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set Channel
            android.app.NotificationManager manager = (android.app.NotificationManager)
                    cx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        // Build Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(cx,
                channelId);

        // Set Notification Options
        notificationBuilder.setContentTitle("Monitoring Cradle State")
                .setSmallIcon(R.drawable.ic_logo)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true);

        // Add Buttons
        Intent stopIntent = new Intent(cx, MonitorCradleStateService.class);
        stopIntent.setAction(StopServiceAction);
        PendingIntent stopPendingIntent = PendingIntent.getService(cx,
                0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action stopServiceAction = new NotificationCompat.Action(
                R.drawable.ic_stop,
                "Stop Service",
                stopPendingIntent
        );

        notificationBuilder.addAction(stopServiceAction);

        // Build & Return Notification
        return notificationBuilder.build();
    }
}
