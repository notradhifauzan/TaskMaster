package com.example.taskmaster;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.TaskService;

import java.text.NumberFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    // DatePickerDialog variables
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    //TimePickerDialog variables
    int hours = calendar.get(Calendar.HOUR);
    int minutes = calendar.get(Calendar.MINUTE);
    int seconds = calendar.get(Calendar.SECOND);


    String inputJobTitle,inputJobDomain,inputJobRequirements;
    double inputJobBudget;

    String selectedDueDate;
    String selectedDueTime;
    EditText edtJobTitle, edtJobDomain, edtJobRequirements,
                edtBudget,edtDueDate,edtDueTime;
    Button taskSubmit;

    Task task;

    TaskService taskService;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                if(validateInput())
                {
                    Log.d("myApp:","input is validated!");
                    task = new Task();
                    initializeTaskObject(task);

                    // debugging purpose
                    Log.d("myApp: ",task.toString());

                    // send POST request
                    sendPOSTrequest(task);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();

        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout)
        {
            logoutDialogbox();
            return true;
        }

        if(item.getItemId() == android.R.id.home)
        {
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
        Toast.makeText(getApplicationContext(),"You have successfully logged out",Toast.LENGTH_LONG).show();

        // forward to MainActivity
        finish();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    private void sendPOSTrequest(Task task) {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        taskService = ApiUtils.getTaskService();
        taskService.createTask(user.getToken(),task).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Log.d("MyApp:","Response: " + response.raw().toString());

                if(response.code() == 401)
                {
                    // authorization problem, go to login
                    finish();
                    goToMainActivity();
                    return;
                } else {
                    displayToast("Successfully create new task.");
                    finish();
                    startActivity(new Intent(getApplicationContext(),AdminTaskView.class));
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(context,"Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:",t.getMessage());
            }
        });
    }

    private void goToMainActivity() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
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