package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.taskmaster.model.ErrorResponse;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.User;
import com.example.taskmaster.remote.ApiUtils;
import com.example.taskmaster.remote.UserService;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button btnLogin;
    EditText edtUsername;
    EditText edtPassword;

    UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        // set disable progress bar by default
        progressBar.setVisibility(View.INVISIBLE);

        // if the user is already logged in we will directly start
        // the main activity
        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            if(SharedPrefManager.getInstance(this).getUser().getRole().equalsIgnoreCase("admin"))
            {
                finish();
                startActivity(new Intent(this,AddTaskActivity.class));
            } else {
                finish();
                startActivity(new Intent(this,TaskviewActivity.class));
            }

            return;
        }

        // get userService instance
        userService = ApiUtils.getUserService();

        // set onClick action to btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get username and password entered by user
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                // validate form, make sure it is not empty
                if(validateLogin(username,password))
                {
                    // do login
                    doLogin(username,password);
                }
            }
        });

    }

    /*
    * Call REST API to login
    *
    * */
    private void doLogin(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);
        Call call = userService.login(username,password);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                // received reply from REST API
                if(response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    // parse response to POJO
                    User user = (User) response.body();
                    if(user.getToken()!=null) {
                        // successful login. server replies a token value
                        displayToast("Login successful");
                        finish();

                        // store value in Shared Preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        // if role is agent, go to task view
                        if(user.getRole().equalsIgnoreCase("agent"))
                        {
                            goToTaskView();
                        } else if (user.getRole().equalsIgnoreCase("admin")) {
                            goToTaskCreator();
                        }

                    }
                } else if(response.errorBody() != null) {
                    progressBar.setVisibility(View.GONE);
                    // parse response to POJO
                    String errorResp = null;
                    try{
                        errorResp = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ErrorResponse e = new Gson().fromJson(errorResp, ErrorResponse.class);
                    displayToast(e.getError().getMessage());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                displayToast("Error connecting to server");
                displayToast(t.getMessage());
            }
        });
    }

    private void goToTaskCreator() {
        finish();
        startActivity(new Intent(this,AddTaskActivity.class));
    }

    private void displayToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    private boolean validateLogin(String username, String password) {
        if(username == null || username.trim().length() == 0)
        {
            displayToast("Username is required");
            return false;
        }
        if(password == null || password.trim().length() == 0)
        {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    private void findViews() {
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.loginButton);
        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
    }

    public void goToTaskView() {
        Intent intent = new Intent(this, TaskviewActivity.class);
        finish();
        startActivity(intent);
    }
}