package com.example.taskmaster.admins;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.taskmaster.adminFragments.AdminNavBarActivity;
import com.example.taskmaster.model.Data;
import com.example.taskmaster.model.Message;
import com.example.taskmaster.model.Notification;
import com.example.taskmaster.model.Root;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.TaskV1;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.NotificationService;
import com.example.taskmaster.remote.TaskService;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {
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

    String inputJobTitle, inputJobDomain, inputJobRequirements;
    double inputJobBudget;


    EditText edtJobTitle, edtJobDomain, edtJobRequirements,
            edtBudget, edtDueDate, edtDueTime;
    Button taskSubmit;

    TaskV1 task;

    TaskService taskService;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

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

        // on submit button
        taskSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get all field data
                initializeVariables();

                if (validateInput()) {
                    Log.d("myApp:", "input is validated!");
                    task = new TaskV1();
                    initializeTaskObject(task);

                    // debugging purpose
                    Log.d("myApp: ", task.toString());

                    // send POST request
                    doAddTask(task);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();

        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logoutDialogbox();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
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
        Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_LONG).show();

        // forward to MainActivity
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void doAddTask(TaskV1 task) {
        loadingAlert = new LoadingAlert(this);
        loadingAlert.startAlertDialog();
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        taskService = ApiUtils.getTaskService();
        taskService.createTaskV2(user.getToken(), task).enqueue(new Callback<TaskV1>() {
            @Override
            public void onResponse(Call<TaskV1> call, Response<TaskV1> response) {
                loadingAlert.closeAlertDialog();
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 401) {
                    // authorization problem, go to login
                    displayToast("Something went wrong: " + response.code());
                    goToMainActivity();
                    finish();
                } else {
                    if (response.code() == 201) {
                        displayToast("Task created successfully");
                        TaskV1 createdTask = response.body();
                        sendNotification(createdTask);
                    } else {
                        displayToast("Failed to add new task: " + response.code());
                        Log.e("add-task-error", "trying to send: " + task.toString());
                        Log.e("add-task-error", response.raw().toString());
                        Log.e("add-task-error", response.message());
                    }
                    startActivity(new Intent(getApplicationContext(), AdminNavBarActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TaskV1> call, Throwable t) {
                loadingAlert.closeAlertDialog();
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.getMessage());
            }
        });
    }


    private void sendNotification(TaskV1 createdTask) {

        final String SERVER_KEY = "key=AAAA1v9b5D4:APA91bE7z6bxQ4LWjauYKBSvLCIeza5WthFpGzx1-MJOPqiR0E7m22p_LJOGMj2XrRFxyyz_o_IVOMSEej4kqsY7JU7NgWKBqlWWfxNumzjIU6w6Wj3ftc7QJvIhbTYmm76LvbickDBG";
        final String CONTENT_TYPE = "application/json";
        Data data = new Data(String.valueOf(createdTask.getJobid()));
        Notification noti = new Notification("New task has been added!", createdTask.getJob_domain(), "newTask");
        Root root = new Root("/topics/NEW_TASK", data, noti);

        Log.d("sendNotification", root.toString());

        NotificationService notificationService = ApiUtils.getNotificationService();

        notificationService.notify(SERVER_KEY, CONTENT_TYPE, root).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d("addTaskNotification", "response code: " + response.code());
                Log.d("addTaskNotification", response.raw().toString());
                Message message = response.body();
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("addTaskNotification", t.getMessage() + "\ncause: " + t.getCause());
            }
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void openTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                String hourStr = "", minuteStr = "";
                if (hours < 10) {
                    hourStr = "0" + hours;
                } else {
                    hourStr = String.valueOf(hours);
                }

                if (minutes < 10) {
                    minuteStr = "0" + minutes;
                } else {
                    minuteStr = String.valueOf(minutes);
                }

                edtDueTime.setText(hourStr + ":" + minuteStr + ":" + "00");
                selectedDueTime = hourStr + ":" + minuteStr + ":" + "00";
            }
        }, hours, minutes, true);

        dialog.show();
    }

    private void openDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;
                String dayStr = "", monthStr = "";

                if (dayOfMonth < 10) {
                    dayStr = "0" + dayOfMonth;
                } else {
                    dayStr = String.valueOf(dayOfMonth);
                }

                if (month < 10) {
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

    private void initializeTaskObject(TaskV1 task) {
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
        } catch (NumberFormatException e) {
            inputJobBudget = 0.00;
        }
    }

    private boolean validateInput() {
        if (inputJobTitle == null || inputJobTitle.trim().length() == 0) {
            displayToast("Job Title is required");
            return false;
        }

        if (inputJobDomain == null || inputJobDomain.trim().length() == 0) {
            displayToast("Job Domain is required");
            return false;
        }

        if (inputJobRequirements == null || inputJobRequirements.trim().length() == 0) {
            displayToast("Job Requirements is required");
            return false;
        }

        if (inputJobBudget <= 0) {
            displayToast("Job Budget is required");
            return false;
        }

        if (selectedDueDate == null || selectedDueDate.length() == 0) {
            displayToast("Job due date is required");
            return false;
        }

        if (selectedDueTime == null || selectedDueTime.length() == 0) {
            displayToast("Job due time is required");
            return false;
        }

        /*
        if (!isValidDate(selectedDueDate)) {
            displayToast("Invalid due date");
            return false;
        } */

        return true;
    }

    private boolean isValidDate(String selectedDueDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date theDueDate = (Date) simpleDateFormat.parse(selectedDueDate);
        } catch(ParseException e) {
            return false;
        }
        return false;
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

