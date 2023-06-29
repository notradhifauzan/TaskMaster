package com.example.taskmaster.agents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.admins.AddTaskActivity;
import com.example.taskmaster.admins.AdminTaskView;
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
    Task task;
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

                task = response.body();

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

        Button acceptButton = findViewById(R.id.button);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptTaskDialogBox(id);
            }
        });
    }

    private void acceptTaskDialogBox(int id) {
        // prepare a dialog box with yes and no
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(task.getJob_title());
        builder.setMessage("Accept this task?");

        // prepare action listener for each button
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // accept task function
                        doAcceptTask(task.getJobid());
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        // create the alert dialog and show to the user
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void displayToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    private void doAcceptTask(int jobid) {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser(); //token related

        taskService = ApiUtils.getTaskService();
        taskService.acceptTask(user.getToken(),task.getJobid(),task.getJob_title(),
                task.getJob_domain(),task.getRequirements(),task.getBudget(),
                task.getCreated_at(),task.getDue_date(),
                task.getDue_time(),"assigned",user.getId()).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Log.d("myapp: ","Trying to update job id: " + task.getJobid());
                Log.d("MyApp:","Response: " + response.raw().toString());

                if(response.code() == 401)
                {
                    // authorization problem, go to login
                    displayToast("Something went wrong: " + response.code());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    if(response.code() == 200)
                    {
                        displayToast("Task accepted successfully");
                    } else {
                        displayToast("Failed to accept task: " + response.code());
                        Log.e("accept task error",response.raw().toString());
                    }
                    startActivity(new Intent(getApplicationContext(), TaskviewActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                displayToast("Couldn't connect to server");
            }
        });
    }
}