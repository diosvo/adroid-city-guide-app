package com.vtmn.audioplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 = "Channel 1";
    public static final String CHANNEL_ID_2 = "Channel 2";
    public static final String ACTION_PREV = "Action Previous";
    public static final String ACTION_NEXT = "Action Next";
    public static final String ACTION_PLAY = "Action Play";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel_1 =
                    new NotificationChannel(CHANNEL_ID_1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel_1.setDescription("Channel 1 Desc...");

            NotificationChannel channel_2 =
                    new NotificationChannel(CHANNEL_ID_2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel_1.setDescription("Channel 2 Desc...");

            NotificationManager notificationManager
                    = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel_1);
            notificationManager.createNotificationChannel(channel_2);
        }
    }
}
