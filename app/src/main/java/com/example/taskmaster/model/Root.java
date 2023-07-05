package com.example.taskmaster.model;
import com.google.gson.annotations.SerializedName;

public class Root{
    public String to;
    public Data data;
    public Notification notification;

    public Root(String myTo, Data data, Notification notification) {
        this.to = myTo;
        this.data = data;
        this.notification = notification;
    }


    @Override
    public String toString() {
        return "Root{" +
                "to='" + to + '\'' +
                ", data=" + data.toString() +
                ", notification=" + notification.toString() +
                '}';
    }
}