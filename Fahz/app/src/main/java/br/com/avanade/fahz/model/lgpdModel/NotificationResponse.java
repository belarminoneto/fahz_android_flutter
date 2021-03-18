package br.com.avanade.fahz.model.lgpdModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationResponse {

    @SerializedName("messageIdentifier")
    @Expose
    private String message;

    @SerializedName("Notification")
    @Expose
    private Notification notification;

    public String getMessage() {
        return message;
    }

    public boolean hasMessage(){
        return message!=null;
    }

    public Notification getNotification() {
        return notification;
    }
}