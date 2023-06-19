package com.example.taskmaster.remote;

import com.example.taskmaster.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TaskService {

    @GET("jobs")
    Call<List<Task>> getAllTask(@Header("api-key") String api_key);

    @POST("jobs")
    Call<Task> createTask(@Header("api-key") String api_key, @Body Task task);
}
