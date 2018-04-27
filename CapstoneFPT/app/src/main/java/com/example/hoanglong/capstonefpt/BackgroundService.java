package com.example.hoanglong.capstonefpt;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleResponse;
import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleUserInfo;
import com.example.hoanglong.capstonefpt.POJOs.EmailInfo;
import com.example.hoanglong.capstonefpt.POJOs.Schedule;
import com.example.hoanglong.capstonefpt.api.RetrofitUtils;
import com.example.hoanglong.capstonefpt.api.ServerAPI;
import com.example.hoanglong.capstonefpt.ormlite.DatabaseManager;
import com.example.hoanglong.capstonefpt.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hoanglong on 24-Feb-18.
 */

public class BackgroundService extends Service {
    private Handler mHandler;
    private int mInterval = 30000;
    private ServerAPI serverAPI;

    public BackgroundService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStatusChecker.run();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    public void runBackground() {
        mStatusChecker.run();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                serverAPI = RetrofitUtils.get().create(ServerAPI.class);

                DatabaseManager.init(getBaseContext());
                SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
                String userEmailJson = sharedPref.getString(getString(R.string.user_email_account), "");
                Gson gson = new Gson();
                final EmailInfo userEmail = gson.fromJson(userEmailJson, EmailInfo.class);

                JsonParser parser = new JsonParser();
//              JsonObject obj = parser.parse("{\"email\": \"" + userEmail.getEmail() + "\"}").getAsJsonObject();
                String testMail = "khanhkt@fpt.edu.vn";
                final JsonObject obj = parser.parse("{\"email\": \"" + testMail + "\"}").getAsJsonObject();
                boolean isLecture = sharedPref.getBoolean("isLecture", false);

                if (isLecture) {
                    serverAPI.getScheduleEmployeeInfo(obj).enqueue(new Callback<ScheduleUserInfo>() {
                        @Override
                        public void onResponse(Call<ScheduleUserInfo> call, Response<ScheduleUserInfo> response) {
                            ScheduleUserInfo empInfo = response.body();
                            if (empInfo != null && response.code() == 200) {
                                DatabaseManager.getInstance().deleteAllSchedules();

                                String date = "";
                                for (ScheduleResponse schedule : empInfo.getScheduleList()) {
                                    Schedule aSchedule = new Schedule();
                                    aSchedule.setCourse(schedule.getCourseName());
                                    aSchedule.setStartTime(schedule.getStartTime());
                                    aSchedule.setEndTime(schedule.getEndTime());
                                    aSchedule.setLecture(schedule.getLecture());
                                    aSchedule.setRoom(schedule.getRoom());
                                    aSchedule.setSlot(schedule.getSlot());
                                    aSchedule.setDate(schedule.getDate());

                                    if (schedule.getDate().equals(date)) {
                                        aSchedule.setIsFirstSlot("false");
                                    } else {
                                        date = schedule.getDate();
                                        aSchedule.setIsFirstSlot("true");
                                    }

                                    aSchedule.setIsNew("false");

                                    DatabaseManager.getInstance().addSchedule(aSchedule);
                                }
//                                Toast.makeText(getBaseContext(), "lecture",Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<ScheduleUserInfo> call, Throwable t) {
//                            Toast.makeText(getBaseContext(), "lecture failed",Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
//                    JsonObject obj2 = parser.parse("{\"email\": \"" + userEmail.getEmail() + "\"}").getAsJsonObject();
                    String testMail2 = "longphse62094@fpt.edu.vn";
                    final JsonObject obj2 = parser.parse("{\"email\": \"" + testMail2 + "\"}").getAsJsonObject();
                    serverAPI.getScheduleStudent(obj2).enqueue(new Callback<ScheduleUserInfo>() {
                        @Override
                        public void onResponse(Call<ScheduleUserInfo> call, Response<ScheduleUserInfo> response) {
                            ScheduleUserInfo empInfo = response.body();
                            if (empInfo != null && response.code() == 200) {
                                DatabaseManager.getInstance().deleteAllSchedules();

                                String date = "";
                                for (ScheduleResponse schedule : empInfo.getScheduleList()) {
                                    Schedule aSchedule = new Schedule();
                                    aSchedule.setCourse(schedule.getCourseName());
                                    aSchedule.setStartTime(schedule.getStartTime());
                                    aSchedule.setEndTime(schedule.getEndTime());
                                    aSchedule.setLecture(schedule.getLecture());
                                    aSchedule.setRoom(schedule.getRoom());
                                    aSchedule.setSlot(schedule.getSlot());
                                    aSchedule.setDate(schedule.getDate());

                                    if (schedule.getDate().equals(date)) {
                                        aSchedule.setIsFirstSlot("false");
                                    } else {
                                        date = schedule.getDate();
                                        aSchedule.setIsFirstSlot("true");
                                    }

                                    aSchedule.setIsNew("false");

                                    DatabaseManager.getInstance().addSchedule(aSchedule);
                                }
//                                Toast.makeText(getBaseContext(), "student",Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<ScheduleUserInfo> call, Throwable t) {
//                            Toast.makeText(getBaseContext(), "student failed",Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                int timeInterval = sharedPref.getInt("background_scan", 30);
                int timeIntervalChoice = 30;
                if (timeInterval == 0) {
                    timeIntervalChoice = 0;
                } else {
                    timeIntervalChoice = timeInterval;
                }

                switch (timeIntervalChoice) {
                    case 0:
                        mInterval = 30000;
                        break;
                    default:
                        mInterval = timeIntervalChoice * 60 * 1000;
                        break;

                }

                mHandler.postDelayed(mStatusChecker, mInterval);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
}
