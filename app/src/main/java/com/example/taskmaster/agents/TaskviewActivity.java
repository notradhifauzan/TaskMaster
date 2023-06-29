package com.example.taskmaster.agents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.admins.UpdateTaskActivity;
import com.example.taskmaster.fragments.BottomNavBar;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.TaskService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskviewActivity extends AppCompatActivity {

    TaskService taskService;
    Context context;
    RecyclerView taskList;
    TaskAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskview);

        context = this; // get current activity context

        // get reference to the RecyclerView task-list
        taskList = findViewById(R.id.rvAvailableTask);

        // register the taskList recycler view for context menu
        super.registerForContextMenu(taskList);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        // execute the call. send the user token when sending the query
        taskService.getUnassignedTask(user.getToken()).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                // for debug purpose
                Log.d("MyApp","Response: " + response.raw().toString());

                if(response.code() == 401) {
                    // authorization problem, go to login
                    logoutAlert("Session expired");
                } else {
                    // get list of task object from response
                    List<Task> tasks = response.body();

                    // initialize adapter
                    adapter = new TaskAdapter(context, (ArrayList<Task>) tasks);

                    // set adapter to the recyclerview
                    taskList.setAdapter(adapter);

                    // set layout to recycler view
                    taskList.setLayoutManager(new LinearLayoutManager(context));
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(context,"Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:",t.getMessage());
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
        if(item.getItemId() == R.id.logout) {
            logoutDialogbox();
            return true;
        }
        else if(item.getItemId() == R.id.mytask) {
            startActivity(new Intent(getApplicationContext(),MyTaskActivity.class));
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

    public boolean onContextItemSelected(@NonNull MenuItem item) {

        Task selectedTask = adapter.getSelectedItem();

        if(item.getItemId() == R.id.details){

            doShowDetails(selectedTask);
        } else if (item.getItemId() == R.id.botNavBar) {

            showBottomNavBar();

        }

        return super.onContextItemSelected(item);
    }

    public void showBottomNavBar(){
        Intent intentsuccess = new Intent(this, BottomNavBar.class);
        startActivity(intentsuccess);

    }

    private void doShowDetails(Task selectedTask) {
        Intent intentsuccess = new Intent(this, TaskDetailsActivity.class);
        Toast.makeText(getApplicationContext(),"Showing Details", Toast.LENGTH_SHORT).show();
        intentsuccess.putExtra("taskID", selectedTask.getJobid());


        startActivity(intentsuccess);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // call the original method in superclass
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        // use our XML file for the menu items
        inflater.inflate(R.menu.task_context_menu, menu);

        // set menu title - optional
        menu.setHeaderTitle("Options");
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

    public void logoutAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        doLogout();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}