package com.example.taskmaster.remote;

import com.example.taskmaster.model.DeleteResponse;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.TaskV1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TaskService {

    @GET("jobs/?order=created_at&orderType=desc")
    Call<List<Task>> getAllTask(@Header("api-key") String api_key);

    @GET("jobs/completed")
    Call<List<Task>> getAllCompletedTask(@Header("api-key") String api_key);

    @GET("jobs/assigned")
    Call<List<Task>> getAllAssignedTask(@Header("api-key") String api_key);

    @GET("jobs/?status[in]=completed")
    Call<List<Task>> getMyCompletedTask(@Header("api-key") String api_key,
                                        @Query("userid[in]") int userid);

    @GET("jobs/?userid[in]=0")
    Call<List<Task>> getUnassignedTask(@Header("api-key") String api_key);

    @GET("jobs/")
    Call<List<Task>> getMyTask(@Header("api-key") String api_key,@Query("userid[in]") int id,
                               @Query("status[in]") String status);

    @GET("jobs/{id}")
    Call<Task> getTask(@Header("api-key") String api_key, @Path("id") int id);

    @FormUrlEncoded
    @POST("jobs/update/{id}")
    Call<Task> updateTask(@Header("api-key") String api_key, @Path("id") int id,
                          @Field("job_title") String title, @Field("job_domain") String job_domain,
                          @Field("requirements") String requirements, @Field("budget") double budget,
                          @Field("created_at") String created_at, @Field("due_date") String due_date,
                          @Field("due_time") String due_time, @Field("status") String status);

    @FormUrlEncoded
    @POST("jobs/update/{id}")
    Call<Task> acceptTask(@Header("api-key") String api_key, @Path("id") int id,
                          @Field("job_title") String title, @Field("job_domain") String job_domain,
                          @Field("requirements") String requirements, @Field("budget") double budget,
                          @Field("created_at") String created_at, @Field("due_date") String due_date,
                          @Field("due_time") String due_time, @Field("status") String status, @Field("userid") int userid);

    @FormUrlEncoded
    @POST("jobs/update/{id}")
    Call<Task> completeTask(@Header("api-key") String api_key, @Path("id") int id,
                          @Field("job_title") String title, @Field("job_domain") String job_domain,
                          @Field("requirements") String requirements, @Field("budget") double budget,
                          @Field("created_at") String created_at, @Field("due_date") String due_date,
                          @Field("due_time") String due_time, @Field("status") String status, @Field("userid") int userid);

    @FormUrlEncoded
    @POST("jobs/add")
    Call<Task> createTask(@Header("api-key") String api_key, @Field("job_title") String job_title,
                          @Field("job_domain") String job_domain,@Field("requirements") String requirements,
                          @Field("budget") String budget, @Field("due_date") String due_date,
                          @Field("due_time") String due_time);
    @POST("jobs")
    Call<TaskV1> createTaskV2(@Header("api-key") String api_key, @Body TaskV1 task);

    // delete a task record
    @POST("jobs/delete/{id}")
    Call<DeleteResponse> deleteTask(@Header("api-key") String api_key, @Path("id") int id);
}
