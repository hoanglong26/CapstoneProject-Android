package com.example.hoanglong.capstonefpt.utils;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.hoanglong.capstonefpt.LoginActivity;
import com.example.hoanglong.capstonefpt.MainActivity;
import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleResponse;
import com.example.hoanglong.capstonefpt.POJOs.EmailInfo;
import com.example.hoanglong.capstonefpt.POJOs.UserInfo;
import com.example.hoanglong.capstonefpt.R;
import com.example.hoanglong.capstonefpt.fragments.ScheduleFragment;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sonata on 10/26/2016.
 */

public class Utils {

    // tags for Shared Utils to store and retrieve some piece of data from local
    public static final String SharedPreferencesTag = "FPT_UNiversity";
    public static final int SharedPreferences_ModeTag = Context.MODE_PRIVATE;

    public static void showNotificationDialog(final Activity activity, String title, String message) {
        new android.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                })
                .show();
    }

    public static String getMac(Context context) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static void setUserInfo(UserInfo userInfo, Application app, boolean isLecture) {
        SharedPreferences pref = app.getSharedPreferences(SharedPreferencesTag, SharedPreferences_ModeTag);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("isLogin", "true");
        editor.putBoolean("isLecture", isLecture);
        editor.putString("user_id", userInfo.getId()+"");
        editor.putString("user_code", userInfo.getCode());
        editor.putString("user_name", userInfo.getName());
        editor.putString("user_role", userInfo.getRole());

        editor.apply();
    }

    public static void clearUserInfo(Application app) {
        SharedPreferences pref = app.getSharedPreferences(SharedPreferencesTag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

    }

    public static void notify(Context context, String title, String content) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.checklist)
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.fpt))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent(context, LoginActivity.class);

        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(9696, builder.build());
    }

    public static void studentNotify(Context context, String title, String content, int id) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.checklist)
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.fpt))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent(context, LoginActivity.class);

        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    public static void studentNotifyWithLongText(Context context, String title, String content, int id) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.checklist)
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.fpt))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent(context, LoginActivity.class);

        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }


    public static boolean checkInternetOn(Activity activity) {
        ConnectivityManager conMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    static int x = 999;
    public static void sendNotificationForFireBase(Context context, String msg, String newScheduleJson, String type) {

        SharedPreferences sharedPref = context.getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
        String userEmailJson = sharedPref.getString(context.getString(R.string.user_email_account), "");

        if (!userEmailJson.equals("")) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("FPT University")
                            .setContentText(msg)
                            .setSmallIcon(R.drawable.logo)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.logo))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                            .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("newSchedule", newScheduleJson);
            intent.putExtra("type", type);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(x++, builder.build());
        }
    }
}
