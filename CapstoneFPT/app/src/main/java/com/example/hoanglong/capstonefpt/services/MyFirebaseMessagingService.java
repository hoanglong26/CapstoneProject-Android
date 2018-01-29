package com.example.hoanglong.capstonefpt.services;

import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleResponse;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static com.example.hoanglong.capstonefpt.utils.Utils.sendNotificationForFireBase;

/**
 * Created by hoanglong on 08-Feb-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        try {
//            for (Resident aResident : patientList) {
//                if (remoteMessage.getData().get("id").equals(String.valueOf(aResident.getId()))) {
//                    if (aResident.getStatus() == 1) {
//                        sendNotificationForFireBase(getBaseContext(), aResident, remoteMessage.getData().get("data"));
//                        aResident.setStatus(0);
//                    }
//                }
//            }
            List<ScheduleResponse> newScheduleList = null;
            if (remoteMessage.getData().get("newScheduleList") != null || !remoteMessage.getData().get("newScheduleList").equals("")) {
                Gson gson = new Gson();
                Type scheduleType = new TypeToken<List<ScheduleResponse>>() {
                }.getType();
                newScheduleList = gson.fromJson(remoteMessage.getData().get("newScheduleList"), scheduleType);
                for (ScheduleResponse schedule : newScheduleList) {
                    String msg = "Your schedule on day " + schedule.getDate() + ", " + schedule.getSlot() + " has been changed.";
                    sendNotificationForFireBase(getBaseContext(), msg, remoteMessage.getData().get("newScheduleList"));
                }

            }


//            Toast.makeText(getBaseContext(),remoteMessage.getMessageId(),Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}