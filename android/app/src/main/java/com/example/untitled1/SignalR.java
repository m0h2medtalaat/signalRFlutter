package com.example.untitled1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.untitled1.models.NotificationModel;
import com.google.gson.Gson;
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection;
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnectionListener;
import com.smartarmenia.dotnetcoresignalrclientjava.HubEventListener;
import com.smartarmenia.dotnetcoresignalrclientjava.HubMessage;
import com.smartarmenia.dotnetcoresignalrclientjava.WebSocketHubConnectionP2;

public class SignalR implements   HubConnectionListener, HubEventListener {
   private static SignalR signalR ;

    private String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Ijc5NzhjMjI3LWViMGItNGMwOS1iYWEyLTEwYmE0MjI4YWE4OSIsImNlcnRzZXJpYWxudW1iZXIiOiJtYWNfYWRkcmVzc19vZl9waG9uZSIsInNlY3VyaXR5U3RhbXAiOiJlMTAxOWNiYy1jMjM2LTQ0ZTEtYjdjYy0zNjMxYTYxYzMxYmIiLCJuYmYiOjE1MDYyODQ4NzMsImV4cCI6NDY2MTk1ODQ3MywiaWF0IjoxNTA2Mjg0ODczLCJpc3MiOiJCbGVuZCIsImF1ZCI6IkJsZW5kIn0.QUh241IB7g3axLcfmKR2899Kt1xrTInwT6BBszf6aP4";
    private HubConnection connection = new WebSocketHubConnectionP2("http://197.168.1.248:1592/NotificationHub/negotiate", authHeader);
    private MutableLiveData <String> mutableLiveData = new MutableLiveData<>();
    public  static Context context;
    int notificationID = 1;
    String channelID = "channel1";

    public static SignalR getInstance(Context context) {
        if(signalR == null) {
            signalR = new SignalR(context);
        }
        return signalR;
    }
    public SignalR(Context context){
        this.context = context;
    }

    @Override
    public void onConnected() {
        Log.e("Connected", "connected");
        connection.invoke("AddUserToGroup",  "b427fd9b-9abb-410e-bc50-041c5bd4dcbb");
        Log.e("AddUserToGroup", "Added");

    }

    @Override
    public void onDisconnected() {
        Log.e("Connected", "disconnected");
    }

    @Override
        public void onMessage(HubMessage message) {
        Gson gson = new Gson();

//        pushNotification("tilte", gson.toJson(message.getArguments()));
        pushNotification(message.getArguments()[0].toString(), context);
        String value = gson.toJson(message.getArguments());
        mutableLiveData.postValue(value);
        Log.e("ObserverOnMessage", value);

    }

    @Override
    public void onError(Exception exception) {
        Log.e("exption", exception.getMessage());
    }

    @Override
    public void onEventMessage(HubMessage message) {
        Log.e("exption", message.getArguments().toString());
    }

    public LiveData<String> getLiveData(){
        return mutableLiveData;
    }
    public void connectSocket(String userId){


            try {
                connection.addListener(this);
                connection.subscribeToEvent("ReceiveMessage", this);
                connection.connect();
                Log.e("Exception", "Connected");

            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
            }

    }
    public void destroySocket(){
        connection.removeListener(this);
        connection.unSubscribeFromEvent("Send", this);
        connection.disconnect();
    }
    public void pushNotification(String message , Context context) {

        NotificationModel notificationModel = new NotificationModel();
        if(message != null){
            notificationModel = new NotificationModel().fromJson(message);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("pushedNotificationIntent",message);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);

        builder.setContentTitle(notificationModel.getTitle());
        builder.setContentText(notificationModel.getBody());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, builder.build());
    }

}