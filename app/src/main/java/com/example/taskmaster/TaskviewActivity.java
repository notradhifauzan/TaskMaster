package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.model.Task;

import java.util.ArrayList;

public class TaskviewActivity extends AppCompatActivity {

    ArrayList<Task> taskModels = new ArrayList<>(); //recyclerView check this 4 step.. 1.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskview);
        RecyclerView recyclerView = findViewById(R.id.rvAvailableTask);

        setupTaskModels(); //recyclerView 2.


        TaskAdapter adapter = new TaskAdapter( this, taskModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void setupTaskModels(){ //recyclerView 3.
        String[] taskTitle = getResources().getStringArray(R.array.taskTitle);
        String[] taskDesc = getResources().getStringArray(R.array.taskDescription);
        String[] taskPrice = getResources().getStringArray(R.array.taskPrice);
        String[] taskDate = getResources().getStringArray(R.array.taskDate);
        String[] taskTime = getResources().getStringArray(R.array.taskTime);

        for( int i = 0; i<taskTitle.length;i++){ //recyclerView 4. >>go to  adapter
            taskModels.add( new Task(taskTitle[i], taskDesc[i], taskPrice[i],  taskDate[i], taskTime[i]));
        }
    }
}