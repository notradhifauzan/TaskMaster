package com.example.taskmaster.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.taskmaster.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavBar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav_bar);

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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutContainer, fragment);
        transaction.commit();
    }


}