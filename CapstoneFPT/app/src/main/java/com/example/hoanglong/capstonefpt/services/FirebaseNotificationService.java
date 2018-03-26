package com.example.hoanglong.capstonefpt.services;

import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleResponse;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.hoanglong.capstonefpt.utils.Utils.sendNotificationForFireBase;

/**
 * Created by hoanglong on 08-Feb-17.
 */

public class FirebaseNotificationService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        try {
            List<ScheduleResponse> newScheduleList = new ArrayList<>();
            if (remoteMessage.getData().get("newScheduleList") != null || !remoteMessage.getData().get("newScheduleList").equals("")) {
                Gson gson = new Gson();
                Type scheduleType = new TypeToken<List<ScheduleResponse>>() {
                }.getType();
                newScheduleList = gson.fromJson(remoteMessage.getData().get("newScheduleList"), scheduleType);

                String type = remoteMessage.getData().get("type");

                List<ScheduleResponse> newScheduleForCreate = new ArrayList<>();

                for (ScheduleResponse schedule : newScheduleList) {
                    List<ScheduleResponse> newSchedule = new ArrayList<>();
                    newSchedule.add(schedule);

                    newScheduleForCreate.add(schedule);

                    if (type.equals("edit")) {
                        String msg = "Your schedule on day " + schedule.getDate() + ", " + schedule.getSlot() + " has been changed.";
                        sendNotificationForFireBase(getBaseContext(), msg, gson.toJson(newSchedule));
                    }
                }

                if (type.equals("create")) {
                    String msg = "Your subject " + newScheduleForCreate.get(0).getCourseName() + " has new schedules.";
                    sendNotificationForFireBase(getBaseContext(), msg, gson.toJson(newScheduleForCreate));
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}