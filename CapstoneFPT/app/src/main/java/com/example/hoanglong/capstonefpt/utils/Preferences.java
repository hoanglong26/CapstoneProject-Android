package com.example.hoanglong.capstonefpt.utils;

import android.app.Activity;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.hoanglong.capstonefpt.LoginActivity;
import com.example.hoanglong.capstonefpt.POJOs.UserInfo;
import com.example.hoanglong.capstonefpt.R;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sonata on 10/26/2016.
 */

public class Preferences {

    // tags for Shared Preferences to store and retrieve some piece of data from local
    public static final String SharedPreferencesTag = "FPT_UNiversity";
    public static final int SharedPreferences_ModeTag = Context.MODE_PRIVATE;


    public static ProgressDialog loading;
    public static boolean isShownLoading = false;

    public static void showLoading(final Activity activity, final String title, final String message) {
        try {
            if (!isShownLoading) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading = ProgressDialog.show(activity, title, message, false, false);
                        isShownLoading = true;
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissLoading() {
        try {
            if (isShownLoading) {

                loading.dismiss();
                isShownLoading = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void showBadRequestNotificationDialog(final Activity activity, int badRequestCode, int title) {
//        int message;
//
//        switch (badRequestCode) {
//            case CODE_INCORRECT_USERNAME:
//
//                break;
//            case CODE_INCORRECT_PASSWORD:
//
//                break;
//            case CODE_INCORRECT_DEVICE:
//
//                break;
//            case CODE_UNVERIFIED_EMAIL:
//
//                break;
//            case CODE_UNVERIFIED_DEVICE:
//
//                break;
//            case CODE_UNVERIFIED_EMAIL_DEVICE:
//
//                break;
//            case CODE_INVALID_ACCOUNT:
//
//                break;
//        }
//    }

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

    public static void setUserInfo(UserInfo userInfo, Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences(SharedPreferencesTag, SharedPreferences_ModeTag);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("isLogin", "true");
        editor.putString("isLecture", "true");
        editor.putString("user_id", userInfo.getId()+"");
        editor.putString("user_code", userInfo.getCode());
        editor.putString("user_name", userInfo.getName());
        editor.putString("user_role", userInfo.getRole());

        editor.apply();
    }

    public static void clearUserInfo(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences(SharedPreferencesTag, Context.MODE_PRIVATE);
        String auCode = pref.getString("authorizationCode", null);

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
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
}
