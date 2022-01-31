package com.example.untitled1.models;
import com.google.gson.Gson;

public class NotificationModel {
    private int id;
    private int notificationId;
    private String title;
    private String body;
    private String orderNumber;

    public NotificationModel fromJson(String json){
        Gson gson = new Gson();
        NotificationModel notificationModel = gson.fromJson(json, NotificationModel.class);
        return  notificationModel;
    }

    public int getId() {
        return id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getBody() {
        return body;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getTitle() {
        return title;
    }
}
