package com.example.taskmaster;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.taskmaster.agents.TaskDetailsActivity;
import com.example.taskmaster.fragments.BottomNavBar;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String THIS_TAG = "firebaseMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String jsonMessage = "";
        // check if message contains a data payload
        if(remoteMessage.getData().size() > 0) {
            Log.d(THIS_TAG,"Message data payload: " + remoteMessage.getData());
            try {
                JSONObject data = new JSONObject(remoteMessage.getData());
                jsonMessage = data.getString("extra_information");
                Log.d(THIS_TAG,"onMessageReceived: \n" + "Extra Information: " + jsonMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // check if message containts a notification payload
        if(remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();

            Log.d(THIS_TAG,"Notification Title: " + title);
            Log.d(THIS_TAG,"Notification Body: " + message);
            Log.d(THIS_TAG,"Notification click_action: " + click_action);

            sendNotification(title,message,click_action);
        }
    }

    private void sendNotification(String title,String message,String click_action) {
        Intent intent = null;

        if(click_action.equals("newTask")){
          intent = new Intent(this, BottomNavBar.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.baseline_work_outline_24)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (isAppInForeground(this)) {
            // If the app is in the foreground, show the notification without the sound and vibration
            notificationBuilder.setSound(null);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } else {
            // If the app is not in the foreground, show the notification with sound and vibration
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    private boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}
