package com.example.untitled1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyForegroundService extends Service  {

    SignalR signalR;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        signalR =  SignalR.getInstance(this);
        
        String token = intent.getStringExtra("token");
        String userId = intent.getStringExtra("userId");
        
        signalR.connectSocket(userId);

        Log.e("backgroundService", token);
        Log.e("backgroundService", userId);


        final String channelId = "Foreground Service Id";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, channelId, NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(intent.getStringExtra("Service enabled"))
                    .setContentText(intent.getStringExtra("Service is running"));

            startForeground(1001, notification.build());

        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    

    

    @Override
    public void onDestroy() {
        super.onDestroy();
        signalR.destroySocket();
    }

    
}