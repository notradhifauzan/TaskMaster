package com.example.taskmaster.agents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.R;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.TaskService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailsActivity extends AppCompatActivity {

    TaskService taskService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent intent = getIntent();
        int id = intent.getIntExtra("taskID", -1);

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser(); //token related

        taskService = ApiUtils.getTaskService();

        // execute the API query. send the token and book id
        taskService.getTask(user.getToken(), id).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Log.d("TaskMaster:", "Response: " + response.raw().toString());

                Task task = response.body();

                // get references to the view elements
                TextView tvID = findViewById(R.id.tvID);
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvDomain = findViewById(R.id.tvDomain);
                TextView tvRequirement = findViewById(R.id.tvRequirement);
                TextView tvCreated_at = findViewById(R.id.tvCreated_at);
                TextView tvPrice = findViewById(R.id.tvPrice);
                TextView tvDate = findViewById(R.id.tvDate);
                TextView tvTime = findViewById(R.id.tvTime);
                //TextView tvStatus = findViewById(R.id.tvStatus);

                //set values
                tvID.setText("ID: "+task.getJobid());
                tvTitle.setText(String.valueOf(task.getJob_title()));
                tvDomain.setText(task.getJob_domain());
                tvRequirement.setText(task.getRequirements());
                tvCreated_at.setText(task.getCreated_at());
                tvPrice.setText(String.valueOf(task.getBudget()));
                tvDate.setText("Due: " + task.getDue_date());
                tvTime.setText(task.getDue_time());
                //tvStatus.setText(task.getStatus());
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    }
}