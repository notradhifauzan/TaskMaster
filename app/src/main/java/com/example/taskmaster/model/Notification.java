package com.example.taskmaster.model;

public class Notification {
    private String title;
    private String text;
    private String click_action;

    public Notification(String title, String text, String click_action) {
        this.title = title;
        this.text = text;
        this.click_action = click_action;
    }
    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", click_action='" + click_action + '\'' +
                '}';
    }
}
