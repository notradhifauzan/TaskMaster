package com.example.taskmaster.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.Util.LoadingAlert;
import com.example.taskmaster.model.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class BottomNavBar extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav_bar);

        FirebaseMessaging.getInstance().subscribeToTopic("NEW_TASK");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navAvailableTask) {
                loadFragment(new AvailableTaskFragment());
                return true;
            } else if (item.getItemId() == R.id.navMyTask) {
                loadFragment(new MyTaskFragment());
                return true;
            } else if (item.getItemId() == R.id.navPreference) {
                loadFragment(new PreferenceFragment());
                return true;
            }
            // Add more conditions for each item in your bottom navigation bar
            return false;
        });

        // Set the initial fragment (AvailableTaskFragment) when the activity starts
        loadFragment(new AvailableTaskFragment());
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
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutContainer, fragment);
        transaction.commit();
    }


}