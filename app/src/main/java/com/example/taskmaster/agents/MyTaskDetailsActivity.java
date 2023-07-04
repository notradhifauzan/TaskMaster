package com.example.taskmaster.agents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.Util.LoadingAlert;
import com.example.taskmaster.fragments.BottomNavBar;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.TaskService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTaskDetailsActivity extends AppCompatActivity {
    LoadingAlert loadingAlert;

    TaskService taskService;
    Task task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task_details);

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int id = intent.getIntExtra("taskID", -1);

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser(); //token related

        taskService = ApiUtils.getTaskService();

        loadingAlert = new LoadingAlert(this);
        loadingAlert.startAlertDialog();
        // execute the API query. send the token and book id
        taskService.getTask(user.getToken(), id).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                loadingAlert.closeAlertDialog();
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
                tvPrice.setText(String.format("RM %.2f", task.getBudget()));
                tvDate.setText(task.getDue_date());
                tvTime.setText(task.getDue_time());
                //tvStatus.setText(task.getStatus());
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                loadingAlert.closeAlertDialog();
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });

        Button CompleteButton = findViewById(R.id.btnCompleteTask);

        // Complete Task Button
        CompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeTaskDialogBox(task);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();

        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout) {
            logoutDialogbox();
            return true;
        } else if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void logoutDialogbox() {
        // prepare a dialog box with yes and no
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Log out from the app?");

        // prepare action listener for each button
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doLogout();
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

    private void doLogout() {
        // clear the shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).logout();

        // display message
        Toast.makeText(getApplicationContext(),"You have successfully logged out",Toast.LENGTH_LONG).show();

        // forward to MainActivity
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void completeTaskDialogBox(Task task) {
        // prepare a dialog box with yes and no
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(task.getJob_title());
        builder.setMessage("Complete this task?");

        // prepare action listener for each button
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // accept task function
                        doCompleteTask(task);
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

    private void doCompleteTask(Task task) {
        loadingAlert = new LoadingAlert(this);
        loadingAlert.startAlertDialog();
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser(); //token related

        taskService = ApiUtils.getTaskService();
        taskService.completeTask(user.getToken(),task.getJobid(),task.getJob_title(),
                task.getJob_domain(),task.getRequirements(),task.getBudget(),
                task.getCreated_at(),task.getDue_date(),
                task.getDue_time(),"completed",user.getId()).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                loadingAlert.closeAlertDialog();
                Log.d("myapp: ","Trying to complete job id: " + task.getJobid());
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
                        displayToast("Task completed successfully");
                    } else {
                        displayToast("Failed to complete task: " + response.code());
                        Log.e("complete task error",response.raw().toString());
                    }
                    startActivity(new Intent(getApplicationContext(), BottomNavBar.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                loadingAlert.closeAlertDialog();
                displayToast("Couldn't connect to server");
            }
        });
    }
}