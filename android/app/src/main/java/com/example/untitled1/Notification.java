package com.example.untitled1;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;


public class Notification extends BroadcastReceiver {

    int notificationID = 1;
    String channelID = "channel1";
    String titleExtra = "titleExtra";
    String messageExtra = "messageExtra";

    @Override
    public void onReceive(Context context, Intent onRintent) {
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,channelID);
        notificationBuilder.setContentTitle(titleExtra);
        notificationBuilder.setContentText(messageExtra);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID,notificationBuilder.build());
    }
}

