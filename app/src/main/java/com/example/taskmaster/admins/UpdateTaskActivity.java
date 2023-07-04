package com.example.taskmaster.admins;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.Util.LoadingAlert;
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
    LoadingAlert loadingAlert;

    // DatePickerDialog variables
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    //TimePickerDialog variables
    int hours = calendar.get(Calendar.HOUR);
    int minutes = calendar.get(Calendar.MINUTE);

    /*variables for job details input*/
    String selectedDueDate;
    String selectedDueTime;

    String inputJobTitle,inputJobDomain,inputJobRequirements;
    double inputJobBudget;

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

        Log.d("myapp","current task id " + id);

        // load the task details
        loadTaskDetails(id);

        // on submit button
        taskSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get all field data
                initializeVariables();

                if(validateInput())
                {
                    Log.d("myApp:","input is validated!");
                    initializeTaskObject(task);

                    // debugging purpose
                    Log.d("myApp: ",task.toString());

                    // send POST request
                    doUpdateTask(task);
                }
            }
        });

    }

    private void doUpdateTask(Task task) {
        loadingAlert = new LoadingAlert(this);
        loadingAlert.startAlertDialog();
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        taskService = ApiUtils.getTaskService();

        taskService.updateTask(user.getToken(),task.getJobid(),task.getJob_title(),
                task.getJob_domain(),task.getRequirements(),task.getBudget(),
                task.getCreated_at(),task.getDue_date(),
                task.getDue_time(),task.getStatus()).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                loadingAlert.closeAlertDialog();
                Log.d("myapp: ","Trying to update job id: " + task.getJobid());
                Log.d("MyApp:","Response: " + response.raw().toString());

                if(response.code() == 401)
                {
                    // authorization problem, go to login
                    displayToast("Something went wrong: " + response.code());
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                } else {
                    if(response.code() == 200)
                    {
                        displayToast("Task updated successfully");
                    } else {
                        displayToast("Failed to update task: " + response.code());
                        Log.e("update task error",response.raw().toString());
                    }
                    startActivity(new Intent(getApplicationContext(), AdminTaskView.class));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
        loadingAlert = new LoadingAlert(this);
        loadingAlert.startAlertDialog();
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        // execute the API query. send the token and task id
        taskService.getTask(user.getToken(), id).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                loadingAlert.closeAlertDialog();
                // for debug purpose
                Log.d("myapp", "response: " + response.raw().toString());

                // get task object from response
                task = response.body();

                selectedDueDate = task.getDue_date();
                selectedDueTime = task.getDue_time();

                Log.d("myapp","viewing task: " + task.toString());

                setViews(task);
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                loadingAlert.closeAlertDialog();
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

    private void initializeTaskObject(Task task) {
        task.setJob_title(inputJobTitle);
        task.setJob_domain(inputJobDomain);
        task.setRequirements(inputJobRequirements);
        task.setBudget(inputJobBudget);
        task.setDue_date(selectedDueDate);
        task.setDue_time(selectedDueTime);
    }

    private void initializeVariables() {
        inputJobTitle = edtJobTitle.getText().toString();
        inputJobDomain = edtJobDomain.getText().toString();
        inputJobRequirements = edtJobRequirements.getText().toString();

        try {
            inputJobBudget = Double.parseDouble(edtBudget.getText().toString());
        } catch(NumberFormatException e) {
            inputJobBudget = 0.00;
        }
    }

    private boolean validateInput() {
        if(inputJobTitle == null || inputJobTitle.trim().length() == 0)
        {
            displayToast("Job Title is required");
            return false;
        }

        if(inputJobDomain == null || inputJobDomain.trim().length()==0)
        {
            displayToast("Job Domain is required");
            return false;
        }

        if(inputJobRequirements == null || inputJobRequirements.trim().length()==0)
        {
            displayToast("Job Requirements is required");
            return false;
        }

        if(inputJobBudget <=0 ){
            displayToast("Job Budget is required");
            return false;
        }

        if(selectedDueDate == null || selectedDueDate.length() == 0)
        {
            displayToast("Job due date is required");
            return false;
        }

        if(selectedDueTime == null || selectedDueTime.length() == 0) {
            displayToast("Job due time is required");
            return false;
        }

        return true;
    }

    private void displayToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}