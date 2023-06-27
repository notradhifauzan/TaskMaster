package com.example.taskmaster.remote;

import com.example.taskmaster.model.DeleteResponse;
import com.example.taskmaster.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TaskService {

    @GET("jobs/?order=created_at&orderType=desc")
    Call<List<Task>> getAllTask(@Header("api-key") String api_key);

    @GET("jobs/unassigned")
    Call<List<Task>> getUnassignedTask(@Header("api-key") String api_key);

    @GET("jobs/{id}")
    Call<Task> getTask(@Header("api-key") String api_key, @Path("id") int id);

    @FormUrlEncoded
    @POST("jobs/update/{id}")
    Call<Task> updateTask(@Header("api-key") String api_key, @Path("id") int id,
                          @Field("job_title") String title, @Field("job_domain") String job_domain,
                          @Field("requirements") String requirements, @Field("budget") double budget,
                          @Field("created_at") String created_at, @Field("due_date") String due_date,
                          @Field("due_time") String due_time, @Field("status") String status);

    @POST("jobs")
    Call<Task> createTask(@Header("api-key") String api_key, @Body Task task);

    // delete a task record
    @POST("jobs/delete/{id}")
    Call<DeleteResponse> deleteTask(@Header("api-key") String api_key, @Path("id") int id);
}
