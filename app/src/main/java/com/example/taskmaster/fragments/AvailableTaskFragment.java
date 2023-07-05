package com.example.taskmaster.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

import com.example.taskmaster.Util.LoadingAlert;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.agents.TaskDetailsActivity;
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

public class AvailableTaskFragment extends Fragment {

    LoadingAlert loadingAlert;
    private TaskService taskService;
    private Context context;
    private RecyclerView taskList;
    private TaskAdapter adapter;

    public AvailableTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_available_task, container, false);

        context = requireContext(); // get current fragment's context

        // get reference to the RecyclerView task-list
        taskList = view.findViewById(R.id.rvAvailableTask);

        // register the taskList recycler view for context menu
        registerForContextMenu(taskList);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(requireContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        loadingAlert = new LoadingAlert(getActivity());
        loadingAlert.startAlertDialog();
        // execute the call. send the user token when sending the query
        taskService.getUnassignedTask(user.getToken()).enqueue(new Callback<List<Task>>() {

            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                loadingAlert.closeAlertDialog();
                // for debug purpose
                Log.d("MyApp", "Response: " + response.raw().toString());

                if (response.code() == 401) {
                    // authorization problem, go to login
                    logoutAlert("Session expired");
                } else {
                    if (response.isSuccessful()) {
                        // Check the response structure
                        if (response.body() instanceof List) {
                            // Response is a list of tasks
                            List<Task> tasks = response.body();

                            // initialize adapter
                            adapter = new TaskAdapter(context, (ArrayList<Task>) tasks);

                            // set adapter to the recyclerview
                            taskList.setAdapter(adapter);

                            // set layout to recycler view
                            taskList.setLayoutManager(new LinearLayoutManager(context));
                        } else {
                            // Handle unexpected response structure here
                            Log.e("availableTaskFragment","unexpected response structure");
                        }
                    } else {
                        // Handle unsuccessful response here
                        Log.e("availableTaskFragment","unsuccessful response");
                    }
                }
            }


            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                loadingAlert.closeAlertDialog();
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("availableTaskFragment:", t.getMessage() + t.getCause() + t.getStackTrace());
                Log.e("availableTaskFragment",t.getSuppressed().toString());
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @NonNull ContextMenu.ContextMenuInfo menuInfo) {
        // call the original method in superclass
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.task_context_menu, menu);

        // set menu title - optional
        menu.setHeaderTitle("Options");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Task selectedTask = adapter.getSelectedItem();
        if(item.getItemId() == R.id.details){

            doShowDetails(selectedTask);
        }
        return super.onContextItemSelected(item);

    }

    private void doShowDetails(Task selectedTask) {
        Intent intent = new Intent(requireContext(), TaskDetailsActivity.class);
        Toast.makeText(requireContext(), "Showing Details", Toast.LENGTH_SHORT).show();
        intent.putExtra("taskID", selectedTask.getJobid());
        startActivity(intent);
    }

    private void logoutAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doLogout();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void doLogout() {
        // clear the shared preferences
        SharedPrefManager.getInstance(requireContext()).logout();

        // display message
        Toast.makeText(requireContext(), "You have successfully logged out", Toast.LENGTH_LONG).show();

        // forward to MainActivity
        requireActivity().finish();
        startActivity(new Intent(requireContext(), MainActivity.class));
    }
}
