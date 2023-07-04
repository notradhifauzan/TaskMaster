package com.example.taskmaster.model;
import com.google.gson.annotations.SerializedName;

public class Root{
    @SerializedName("to")
    public String myTo;
    public Data data;
    public Notification notification;

    public Root(String myTo, Data data, Notification notification) {
        this.myTo = myTo;
        this.data = data;
        this.notification = notification;
    }
}