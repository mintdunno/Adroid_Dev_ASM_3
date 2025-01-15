package com.minh.payday.utils;

import static android.content.ContentValues.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.minh.payday.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 123; // Unique ID for the notification
    private static final String CHANNEL_ID = "network_channel"; // Channel ID

    // Interface for callbacks to the Activity
    public interface NetworkChangeListener {
        void onNetworkChanged(boolean isConnected);
    }

    private NetworkChangeListener listener; // Listener instance

    // Constructor to set the listener (optional)
    public NetworkChangeReceiver(NetworkChangeListener listener) {
        this.listener = listener;
    }

    // Default constructor
    public NetworkChangeReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() called ASDASDASDASDASDASDSA");
        Toast.makeText(context, "Connectivity Change Detected", Toast.LENGTH_SHORT).show();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                Toast.makeText(context, "Internet Connection Restored", Toast.LENGTH_SHORT).show();
                dismissNoInternetNotification(context);

                // Notify the listener (if set)
                if (listener != null) {
                    listener.onNetworkChanged(true);
                }
            } else {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
                showNoInternetNotification(context);

                // Notify the listener (if set)
                if (listener != null) {
                    listener.onNetworkChanged(false);
                }
            }
        }
    }

    private void showNoInternetNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Network Status", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_no_internet) // Replace with your icon
                .setContentTitle("No Internet Connection")
                .setContentText("Please connect to the internet to use the app.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true); // Make it persistent (user can't swipe it away)

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void dismissNoInternetNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}