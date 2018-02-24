package com.example.hoanglong.capstonefpt.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleResponse;
import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleUserInfo;
import com.example.hoanglong.capstonefpt.POJOs.EmailInfo;
import com.example.hoanglong.capstonefpt.POJOs.Schedule;
import com.example.hoanglong.capstonefpt.R;
import com.example.hoanglong.capstonefpt.api.RetrofitUtils;
import com.example.hoanglong.capstonefpt.api.ServerAPI;
import com.example.hoanglong.capstonefpt.ormlite.DatabaseManager;
import com.example.hoanglong.capstonefpt.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hoanglong.capstonefpt.MainActivity.materialSheetFab;

public class ScheduleFragment extends Fragment {
    @BindView(R.id.rvSchedule)
    RecyclerView rvSchedule;

    @BindView(R.id.empty_view)
    TextView empty;

    private Handler handler;
    @BindView(R.id.srlSchedule)
    SwipeRefreshLayout srlHistory;
    private ServerAPI serverAPI;

    private Handler mHandler;
    //refresh time of screen
    private int mInterval = 90000;

    FastItemAdapter<Schedule> fastAdapter = new FastItemAdapter<>();


    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(String title) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                //set the items to your ItemAdapter
                List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                String title = ((Toolbar) getActivity().findViewById(R.id.toolbar)).getTitle().toString();
                resetAdapter(scheduleList, title);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    private void resetAdapter(List<Schedule> scheduleList, String title) {
        List<Schedule> normalizeList = new ArrayList<>();
        switch (title) {
            case "Today Schedule": {
                normalizeList = normalizeList(scheduleList, "today", null);
            }
            break;
            case "Week Schedule": {
                normalizeList = normalizeList(scheduleList, "week", null);
            }
            break;
            case "All Schedule": {
                normalizeList = normalizeList(scheduleList, "all", null);
            }
            break;
            default: {
                normalizeList = normalizeList(scheduleList, "specific", title);
            }
            break;

        }

        fastAdapter.clear();
        fastAdapter.add(normalizeList);
        fastAdapter.notifyAdapterDataSetChanged();
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, rootView);
        serverAPI = RetrofitUtils.get().create(ServerAPI.class);
        DatabaseManager.init(getActivity().getBaseContext());

        mHandler = new Handler();
        mStatusChecker.run();

        rvSchedule.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set our adapters to the RecyclerView
        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        rvSchedule.setAdapter(fastAdapter);

        //set the items to your ItemAdapter
        List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
        resetAdapter(scheduleList, getResources().getString(R.string.week_schedule));

        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<Schedule>() {
            @Override
            public boolean onClick(View v, IAdapter<Schedule> adapter, Schedule item, int position) {
                EventBus.getDefault().post(new FragmentAdapter.OpenEvent(position, item.getLecture(), item.getRoom()));
                return true;
            }
        });

        SharedPreferences sharedPref = getActivity().getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
        String userEmailJson = sharedPref.getString(getString(R.string.user_email_account), "");
        Gson gson = new Gson();
        final EmailInfo userEmail = gson.fromJson(userEmailJson, EmailInfo.class);

        JsonParser parser = new JsonParser();
//      JsonObject obj = parser.parse("{\"email\": \"" + userEmail.getEmail() + "\"}").getAsJsonObject();
        String testMail = "khanhkt@fpt.edu.vn";
        final JsonObject obj = parser.parse("{\"email\": \"" + testMail + "\"}").getAsJsonObject();

        handler = new Handler();
        srlHistory.setDistanceToTriggerSync(550);
        srlHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getContext() != null) {
                            SharedPreferences sharedPref = getActivity().getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
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

                                            //set the items to your ItemAdapter
                                            ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(R.string.week_schedule);
                                            List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                                            resetAdapter(scheduleList, getResources().getString(R.string.week_schedule));
                                            srlHistory.setRefreshing(false);

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ScheduleUserInfo> call, Throwable t) {

                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                        alertDialog.setTitle("Fetch data Failed");
                                        alertDialog.setMessage("Connection error.");
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                        srlHistory.setRefreshing(false);
                                    }
                                });
                            } else {
                                JsonObject obj = parser.parse("{\"email\": \"" + userEmail.getEmail() + "\"}").getAsJsonObject();

                                serverAPI.getScheduleStudent(obj).enqueue(new Callback<ScheduleUserInfo>() {
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

                                            //set the items to your ItemAdapter
                                            ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(R.string.week_schedule);
                                            List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                                            resetAdapter(scheduleList, getResources().getString(R.string.week_schedule));
                                            srlHistory.setRefreshing(false);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ScheduleUserInfo> call, Throwable t) {

                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                        alertDialog.setTitle("Fetch data Failed");
                                        alertDialog.setMessage("Connection error.");
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                        srlHistory.setRefreshing(false);
                                    }
                                });
                            }


                        }
                    }
                }, 2000);
            }
        });

        getActivity().findViewById(R.id.tvToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(R.string.today_schedule);

                    List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                    resetAdapter(scheduleList, getResources().getString(R.string.today_schedule));
                    srlHistory.setRefreshing(false);
                    materialSheetFab.hideSheet();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        getActivity().findViewById(R.id.tvWeek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(R.string.week_schedule);

                    List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                    resetAdapter(scheduleList, getResources().getString(R.string.week_schedule));
                    srlHistory.setRefreshing(false);
                    materialSheetFab.hideSheet();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        getActivity().findViewById(R.id.tvAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(R.string.all_schedule);

                    List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                    resetAdapter(scheduleList, getResources().getString(R.string.all_schedule));
                    srlHistory.setRefreshing(false);
                    materialSheetFab.hideSheet();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        getActivity().findViewById(R.id.tvPickDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatePickerDialog.OnDateSetListener mDateSetListener =
                            (view1, year, monthOfYear, dayOfMonth) -> {
                                Calendar c = Calendar.getInstance();
                                c.set(year, monthOfYear, dayOfMonth, 0, 0);

                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(df.format(c.getTime()));

                                List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                                resetAdapter(scheduleList, df.format(c.getTime()));

                                srlHistory.setRefreshing(false);
                                materialSheetFab.hideSheet();

                            };

                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), mDateSetListener, year, month, day);
                    datePickerDialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return rootView;
    }

    private List<Schedule> normalizeList(List<Schedule> scheduleList, String type, String specificDate) {
        List<Schedule> aList = new ArrayList<>();

        try {
            if (scheduleList == null) {
                scheduleList = new ArrayList<>();
            }
            if (scheduleList.size() == 0) {
                rvSchedule.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                return aList;
            } else {
                empty.setVisibility(View.GONE);
                rvSchedule.setVisibility(View.VISIBLE);
            }

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            if (type.equals("today")) {
                for (Schedule schedule : scheduleList) {
                    Date today = new Date();
                    if (schedule.getDate().equals(df.format(today))) {
                        aList.add(schedule);
                    }
                }
            }

            if (type.equals("specific") && specificDate != null) {
                for (Schedule schedule : scheduleList) {
                    if (schedule.getDate().equals(specificDate)) {
                        aList.add(schedule);
                    }
                }
            }


            if (type.equals("week")) {
                Date date = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
                c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);

                Date weekStart = c.getTime();
                // we do not need the same day a week after, that's why use 6, not 7
                c.add(Calendar.DAY_OF_MONTH, 6);
                Date weekEnd = c.getTime();

                for (Schedule schedule : scheduleList) {
                    if (df.parse(schedule.getDate()).after(weekStart) && df.parse(schedule.getDate()).before(weekEnd)) {
                        aList.add(schedule);
                    }
                }
            }

            if (type.equals("all")) {
                aList = scheduleList;
            }

            if (aList.size() == 0) {
                rvSchedule.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
                rvSchedule.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return aList;
    }

    private List<Schedule> loadFromNotification() {
        DatabaseManager.init(getActivity().getBaseContext());

        List<ScheduleResponse> newScheduleList = new ArrayList<>();
        List<Schedule> scheduleList = new ArrayList<>();
        try {
            String newScheduleJson = getActivity().getIntent().getStringExtra("newSchedule");

            //convert json
            if (newScheduleJson != null && !newScheduleJson.equals("")) {
                Gson gson = new Gson();
                Type scheduleType = new TypeToken<List<ScheduleResponse>>() {
                }.getType();
                newScheduleList = gson.fromJson(newScheduleJson, scheduleType);
            }

            if (newScheduleList != null && newScheduleList.size() > 0) {
                for (ScheduleResponse schedule : newScheduleList) {
                    String lecture = URLDecoder.decode(schedule.getLecture());
                    schedule.setLecture(lecture);
                }

                for (ScheduleResponse scheduleResponse : newScheduleList) {
                    Schedule aSchedule = new Schedule();
                    aSchedule.setIsNew("true");
                    aSchedule.setIsFirstSlot("false");
                    aSchedule.setDate(scheduleResponse.getDate());
                    aSchedule.setSlot(scheduleResponse.getSlot());
                    aSchedule.setRoom(scheduleResponse.getRoom());
                    aSchedule.setLecture(scheduleResponse.getLecture());
                    aSchedule.setStartTime(scheduleResponse.getStartTime());
                    aSchedule.setEndTime(scheduleResponse.getEndTime());
                    aSchedule.setCourse(scheduleResponse.getCourseName());
                    scheduleList.add(aSchedule);

                    DatabaseManager.getInstance().deleteScheduleByDateAndSlot(aSchedule);
                    DatabaseManager.getInstance().createOrUpdateSchedule(aSchedule);
                }

                List<Schedule> updatedScheduleList = DatabaseManager.getInstance().getAllSchedules();

                Collections.sort(updatedScheduleList, new Comparator<Schedule>() {
                    @Override
                    public int compare(Schedule o1, Schedule o2) {
                        try {
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            Date aDate = df.parse(o1.getDate());
                            Date aDate2 = df.parse(o2.getDate());
                            if (aDate.compareTo(aDate2) > 0) {
                                return 1;
                            }

                            if (aDate.compareTo(aDate2) == 0) {
                                String slot1 = o1.getSlot();
                                String slot2 = o2.getSlot();

                                if (slot1.compareTo(slot2) > 0) {
                                    return 1;
                                }
                            }
                            return -1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                String date = "";
                for (Schedule schedule : updatedScheduleList) {
                    if (schedule.getDate().equals(date)) {
                        schedule.setIsFirstSlot("false");
                    } else {
                        date = schedule.getDate();
                        schedule.setIsFirstSlot("true");
                    }
                }

                DatabaseManager.getInstance().deleteAllSchedules();
                DatabaseManager.getInstance().addAllSchedule(updatedScheduleList);

                rvSchedule.setAdapter(fastAdapter);
                fastAdapter.clear();

                int hasNew = -1;
                for (int i = 0; i < updatedScheduleList.size(); i++) {
                    if (updatedScheduleList.get(i).getIsNew().equals("true")) {
                        hasNew = i - 1;
                        break;
                    }
                }

                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

                if (hasNew == -1) {
                    fastAdapter.add(normalizeList(updatedScheduleList, "week", null));
                    toolbar.setTitle(R.string.week_schedule);

                } else {
                    fastAdapter.add(normalizeList(updatedScheduleList, "all", null));
                    toolbar.setTitle(R.string.all_schedule);
                    rvSchedule.scrollToPosition(hasNew);
                    hasNew = -1;
                }
                fastAdapter.notifyAdapterDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return scheduleList;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFromNotification();
    }
}
