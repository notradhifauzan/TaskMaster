package com.example.taskmaster.model;

public class Data {
    private String extra_information;

    public Data(String extra_information) {
        this.extra_information = extra_information;
    }

    @Override
    public String toString() {
        return "Data{" +
                "extra_information='" + extra_information + '\'' +
                '}';
    }
}
