package com.example.taskmaster.admins;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.TaskService;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTaskActivity extends AppCompatActivity {

    // DatePickerDialog variables
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    //TimePickerDialog variables
    int hours = calendar.get(Calendar.HOUR);
    int minutes = calendar.get(Calendar.MINUTE);

    String selectedDueDate;
    String selectedDueTime;

    // ---------------views variable-----------------------
    EditText edtJobTitle, edtJobDomain, edtJobRequirements,
            edtBudget, edtDueDate, edtDueTime;
    Button taskSubmit;
    // ----------------------------------------------------
    Task task;
    TaskService taskService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        findViews();

        // enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Due date listener
        edtDueDate.setFocusable(false);
        edtDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        //Due time listener
        edtDueTime.setFocusable(false);
        edtDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog();
            }
        });

        // get task id sent by previous activity, -1 if not found
        Intent intent = getIntent();
        int id = intent.getIntExtra("task_id", -1);

        // load the task details
        loadTaskDetails(id);

    }

    private void openTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                String hourStr="", minuteStr = "";
                if(hours<10)
                {
                    hourStr = "0" + hours;
                } else{
                    hourStr = String.valueOf(hours);
                }

                if(minutes<10)
                {
                    minuteStr = "0" + minutes;
                } else {
                    minuteStr = String.valueOf(minutes);
                }

                edtDueTime.setText(hourStr+":"+minuteStr+":"+"00");
                selectedDueTime = hourStr+":"+minuteStr+":"+"00";
            }
        }, hours, minutes, true);

        dialog.show();
    }

    private void openDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month+1;
                String dayStr = "", monthStr="";

                if(dayOfMonth<10)
                {
                    dayStr = "0" + dayOfMonth;
                } else {
                    dayStr = String.valueOf(dayOfMonth);
                }

                if(month<10)
                {
                    monthStr = "0" + month;
                } else {
                    monthStr = String.valueOf(month);
                }

                String dateBuilder = year + "-" + monthStr + "-" + dayStr;
                edtDueDate.setText(dateBuilder);
                selectedDueDate = dateBuilder;

            }
        }, year, month, dayOfMonth);

        dialog.show();
    }

    private void loadTaskDetails(int id) {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        // execute the API query. send the token and task id
        taskService.getTask(user.getToken(), id).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                // for debug purpose
                Log.d("myapp", "response: " + response.raw().toString());

                // get task object from response
                task = response.body();

                setViews(task);
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setViews(Task task) {
        edtJobTitle.setText(task.getJob_title());
        edtJobDomain.setText(task.getJob_domain());
        edtJobRequirements.setText(task.getRequirements());
        edtBudget.setText(String.valueOf(task.getBudget()));
        edtDueDate.setText(task.getDue_date());
        edtDueTime.setText(task.getDue_time());
    }

    private void findViews() {
        edtJobTitle = findViewById(R.id.edtJobTitle);
        edtJobDomain = findViewById(R.id.edtJobDomain);
        edtJobRequirements = findViewById(R.id.edtJobRequirements);
        edtBudget = findViewById(R.id.edtBudget);
        edtDueDate = findViewById(R.id.edtDueDate);
        edtDueTime = findViewById(R.id.edtDueTime);


        taskSubmit = findViewById(R.id.taskSubmitButton);
    }
}