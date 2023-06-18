package com.example.taskmaster.remote;

import com.example.taskmaster.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface TaskService {

    @GET("task")
    Call<List<Task>> getAllTask(@Header("api-key") String api_key);
}
