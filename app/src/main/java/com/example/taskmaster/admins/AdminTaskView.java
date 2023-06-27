package com.example.taskmaster.admins;

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
import com.example.taskmaster.Util.LoadingAlert;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.admins.AddTaskActivity;
import com.example.taskmaster.model.DeleteResponse;
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

public class AdminTaskView extends AppCompatActivity {

    LoadingAlert loadingAlert;
    TaskService taskService;
    Context context;
    RecyclerView taskList;
    TaskAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_task_view);

        context = this;

        taskList = findViewById(R.id.rv_admin_view_task);

        // register the taskList recycler view for context menu
        super.registerForContextMenu(taskList);

        // update listview
        updateListView();

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // call the original method in superclass
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        // use our XML file for the menu items
        inflater.inflate(R.menu.admin_task_context_menu, menu);

        // set menu title - optional
        menu.setHeaderTitle("Select the action:");
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();

        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.task_options_menu,menu);

        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Task selectedTask = adapter.getSelectedItem();
        Log.d("myapp","selected " + selectedTask.toString());

        if(item.getItemId() == R.id.update) {
            doViewDetails(selectedTask);
            Log.d("myApp","You clicked update context menu");
        } else if (item.getItemId() == R.id.delete) {
            loadingAlert = new LoadingAlert(this);
            loadingAlert.startAlertDialog();
            doDeleteTask(selectedTask);
            Log.d("myapp","you clicked delete task context menu");
        }

        return false;
        //return super.onContextItemSelected(item);
    }

    private void doViewDetails(Task selectedTask) {
        Log.d("myapp","viewing details " + selectedTask.toString());
        Intent intent = new Intent(getApplicationContext(),UpdateTaskActivity.class);
        intent.putExtra("task_id", selectedTask.getJobid());
        startActivity(intent);
    }

    /*
    * Delete book record. Called by contextual menu "Delete"
    * @param selectedTask = task selected by admin
    *
    * */
    private void doDeleteTask(Task selectedTask) {
        Log.d("myapp","attempting to delete task ");
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // prepare REST API call
        taskService = ApiUtils.getTaskService();
        try{
            Call<DeleteResponse> call = taskService.deleteTask(user.getToken(),selectedTask.getJobid());
            // execute the call
            call.enqueue(new Callback<DeleteResponse>() {
                @Override
                public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                    Log.d("myapp",response.message());
                    if(response.code() == 200) {
                        // 200 means ok
                        displayAlert("Task successfully deleted");

                        // update data in list view
                        updateListView();
                    }
                    loadingAlert.closeAlertDialog();
                }

                @Override
                public void onFailure(Call<DeleteResponse> call, Throwable t) {
                    displayAlert("Error [" + t.getMessage() + "]");
                    Log.e("myapp",t.getMessage());
                }
            });
        } catch(Exception e) {
            Log.e("retrofit error",e.getMessage());
            loadingAlert.closeAlertDialog();
        }
    }

    private void updateListView() {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        taskService.getAllTask(user.getToken()).enqueue(new Callback<List<Task>>() {
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

    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout)
        {
            logoutDialogbox();
            return true;
        }

        if(item.getItemId() == R.id.add_task)
        {
            addTaskDialogBox();
            return true;
        }
        return false;
    }

    private void addTaskDialogBox() {
        // prepare a dialog box with yes and no
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Task");
        builder.setMessage("Add new task?");

        // prepare action listener for each button
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), AddTaskActivity.class));
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
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}