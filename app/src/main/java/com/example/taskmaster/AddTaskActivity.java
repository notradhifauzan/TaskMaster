package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskmaster.model.Task;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    EditText edtJobTitle, edtJobDomain, edtJobRequirements,
                edtBudget;
    DatePicker dueDateInput;
    TimePicker dueTimeInput;
    Button taskSubmit;

    Task task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        findViews();

        taskSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

    }

    private boolean validateInput() {
        if(edtJobTitle == null || edtJobTitle.getText().toString().trim().length() == 0) {
            displayToast("Job Title is required");
            return false;
        } else {
            task.setJob_title(edtJobTitle.getText().toString());
        }
        if(edtJobDomain == null || edtJobDomain.getText().toString().trim().length() == 0) {
            displayToast("Job Domain is required");
            return false;
        } else {
            task.setJob_domain(edtJobDomain.getText().toString());
        }
        if(edtJobRequirements == null || edtJobRequirements.getText().toString().trim().length() == 0) {
            displayToast("Job Requirements is required");
            return false;
        } else {
            task.setRequirements(edtJobRequirements.getText().toString());
        }
        if(edtBudget == null || edtBudget.getText().toString().trim().length() == 0) {
            displayToast("Job Budget is required");
            return false;
        } else {
            task.setBudget(Double.parseDouble(edtBudget.toString()));
        }

        dueDateInput.init(year, month, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            }
        });

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

        dueDateInput = findViewById(R.id.dueDateInput);
        dueTimeInput = findViewById(R.id.dueTimeInput);

        taskSubmit = findViewById(R.id.taskSubmitButton);
    }
}