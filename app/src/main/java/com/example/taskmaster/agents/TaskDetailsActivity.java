package com.example.taskmaster.agents;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.taskmaster.R;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        //Start Pull Data
        String  id = getIntent().getStringExtra("taskID");
        TextView tvID = findViewById(R.id.tvID);
        tvID.setText(id);

        String  title = getIntent().getStringExtra("taskTitle");
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        String  domain = getIntent().getStringExtra("taskDomain");
        TextView tvDomain = findViewById(R.id.tvDomain);
        tvDomain.setText(domain);

        String  requirement = getIntent().getStringExtra("taskRequirement");
        TextView tvRequirement = findViewById(R.id.tvRequirement);
        tvRequirement.setText(requirement);

        String  created_at = getIntent().getStringExtra("taskCreated_at");
        TextView tvCreated_at = findViewById(R.id.tvTitle);
        tvCreated_at.setText(created_at);

        String  price = getIntent().getStringExtra("taskPrice");
        TextView tvPrice = findViewById(R.id.tvPrice);
        tvPrice.setText(price);

        String  date = getIntent().getStringExtra("taskDate");
        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText("Due: "+date);

        String  time = getIntent().getStringExtra("taskTime");
        TextView tvTime = findViewById(R.id.tvTime);
        tvTime.setText(time);

        String  status = getIntent().getStringExtra("taskStatus");
        TextView tvStatus = findViewById(R.id.tvTitle);
        tvStatus.setText(status);

        // end pull data


    }
}