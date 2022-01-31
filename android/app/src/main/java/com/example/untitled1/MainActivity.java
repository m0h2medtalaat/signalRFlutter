package com.example.untitled1;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.os.Build;

import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


public class MainActivity extends FlutterActivity implements EventChannel.StreamHandler {
    private static final String CHANNEL = "flutterNative";
    private EventChannel eventChannel =  null;
    public static EventChannel.EventSink eventSink = null;
    private static final  String EVENT_CHANNEL = "com.unit1.beacon.entered.beacons";
    String channelID = "channel1";
    SignalR mySignalR ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySignalR  = SignalR.getInstance(this);
        mySignalR.getLiveData().observe(this, new Observer<String>() {
        @Override
        public void onChanged(String s) {
            Log.e("Observer", s);
            if(eventSink != null)
            eventSink.success(s);
        }
    });
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if(call.method.equals("signalR")) {
                                try {
                                    String userId = call.argument("userId");
                                    String token = call.argument("token");

                                    createNotificationChannel();

                                    if(!foregroundServiceRunning()) {



                                        Intent foregroundServiceIntent = new Intent(this, MyForegroundService.class);
                                        foregroundServiceIntent.putExtra("userId",userId);
                                        foregroundServiceIntent.putExtra("token",token);
                                        foregroundServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        foregroundServiceIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                startForegroundService(foregroundServiceIntent);
                                            }
                                        }
                                    }
                                    Log.e("Exception", "temp");

                                    result.success("temp");
                                } catch ( Exception e) {
                                    result.error("error","errorMessage","any");
                                }
                            }
                            else {
                                result.notImplemented();
                            }
                        }
                );

        eventChannel =new  EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), EVENT_CHANNEL);
        eventChannel.setStreamHandler(this);


    }

    private void createNotificationChannel()
    {
        String name = "Notif Channel";
        String desc = "A Description of the Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelID, name, importance);
            channel.setShowBadge(true);
            channel.setDescription(desc);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for ( int i = 0 ; i <activityManager.getRunningServices(Integer.MAX_VALUE).size() ; i++) {
            if (MyForegroundService.class.getName().equals(activityManager.getRunningServices(Integer.MAX_VALUE).get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {

        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
        eventChannel = null;
    }

}

