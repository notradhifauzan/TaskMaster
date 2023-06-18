package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.TaskService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskviewActivity extends AppCompatActivity {

    TaskService taskService;
    Context context;
    RecyclerView taskList;
    TaskAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskview);

        context = this; // get current activity context

        // get reference to the RecyclerView tasklist
        taskList = findViewById(R.id.rvAvailableTask);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        // execute the call. send the user token when sending the query
        taskService.getAllTask(user.getToken()).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                // for debug purpose
                Log.d("MyApp","Response: " + response.raw().toString());

                if(response.code() == 401) {
                    // authorization problem, go to login
                    finish();
                    goToActivity(MainActivity.class);
                    return;
                }

                // get list of task object from response
                List<Task> tasks = response.body();

                // initialize adapter
                adapter = new TaskAdapter(context, (ArrayList<Task>) tasks);

                // set adapter to the recyclerview
                taskList.setAdapter(adapter);

                // set layout to recycler view
                taskList.setLayoutManager(new LinearLayoutManager(context));

            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(context,"Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:",t.getMessage());
            }
        });
    }

    private void goToActivity(Class<MainActivity> mainActivityClass) {
        startActivity(new Intent(this,MainActivity.class));
    }
}