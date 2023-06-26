package com.example.taskmaster.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.taskmaster.R;

public class LoadingAlert {

     Activity activity;
     AlertDialog dialog;

    public LoadingAlert(Activity myActivity){
        activity = myActivity;
    }

    public void startAlertDialog() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_layout,null));

        builder.setCancelable(true);


        dialog = builder.create();
        dialog.show();
    }

    public void closeAlertDialog() {
        dialog.dismiss();
    }
}
