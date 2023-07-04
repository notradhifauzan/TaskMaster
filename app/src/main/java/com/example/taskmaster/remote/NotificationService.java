package com.example.taskmaster.remote;

import com.example.taskmaster.model.Message;
import com.example.taskmaster.model.Root;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface NotificationService {

    @POST("https://fcm.googleapis.com/fcm/send")
    Call<Message> notify(@Header("Authorization") String key, @Header("Content-type") String contentType, @Body Root root);
}
