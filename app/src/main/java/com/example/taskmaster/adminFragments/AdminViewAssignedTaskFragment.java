package com.example.taskmaster.adminFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.Util.LoadingAlert;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.adapter.TaskAdapter2;
import com.example.taskmaster.agents.MyTaskDetailsActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminViewAssignedTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminViewAssignedTaskFragment extends Fragment {

    LoadingAlert loadingAlert;

    TaskService taskService;
    Context context;
    RecyclerView taskList;
    TaskAdapter2 adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminViewAssignedTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminViewAssignedTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminViewAssignedTaskFragment newInstance(String param1, String param2) {
        AdminViewAssignedTaskFragment fragment = new AdminViewAssignedTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_view_assigned_task, container, false);

        context = requireContext(); // get current activity context

        // get reference to the RecyclerView task-list
        taskList = view.findViewById(R.id.rvAvailableTask);

        User user = SharedPrefManager.getInstance(requireContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        // register the taskList recycler view for context menu
        super.registerForContextMenu(taskList);

        loadTask();

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
        Intent intent = new Intent(requireContext(), MyTaskDetailsActivity.class);
        Toast.makeText(requireContext(), "Showing Details", Toast.LENGTH_SHORT).show();
        intent.putExtra("taskID", selectedTask.getJobid());
        intent.putExtra("caller","admin");
        startActivity(intent);
    }

    private void displayToast(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
    }

    private void loadTask() {
        loadingAlert = new LoadingAlert(getActivity());
        loadingAlert.startAlertDialog();
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(requireContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getCustomTaskService();

        taskService.getAllAssignedTask(user.getToken()).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                loadingAlert.closeAlertDialog();
                // for debug purpose
                Log.d("assigned-task","Response: " + response.raw().toString());
                if(response.code() == 401) {
                    // authorization problem, go to login
                    logoutAlert("Session expired");
                } else {
                    if(response.code() == 204) {
                        // no task yet for this particular user
                        displayToast("No task yet...");
                    } else {
                        if(response.code() == 200) {
                            // get list of task object from response
                            List<Task> tasks = response.body();
                            Log.e("assigned-task",response.body().toString());
                            // initialize adapter
                            adapter = new TaskAdapter2(context, (ArrayList<Task>) tasks);

                            // set adapter to the recyclerview
                            taskList.setAdapter(adapter);

                            // set layout to recycler view
                            taskList.setLayoutManager(new LinearLayoutManager(context));
                        } else {
                            Log.e("assigned-task","code: "+String.valueOf(response.code()));
                            displayToast("Something unexpected occured");
                        }
                    }
                }
                Log.d("assigned-task","Response: " + response.raw().toString());
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("assigned-task","FAILURE: " + t.getCause());
                Log.e("assigned-task","FAILURE: " + t.getMessage());
                displayToast("response: " + t.getMessage());
                loadingAlert.closeAlertDialog();
            }
        });
    }

    private void logoutDialogbox() {
        // prepare a dialog box with yes and no
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout)
        {
            logoutDialogbox();
            return true;
        }
        return false;
    }

    public void logoutAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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

    private void doLogout() {
        // clear the shared preferences
        SharedPrefManager.getInstance(requireContext()).logout();

        // display message
        Toast.makeText(requireContext(),"You have successfully logged out",Toast.LENGTH_LONG).show();

        // forward to MainActivity
        startActivity(new Intent(requireContext(), MainActivity.class));
    }
}