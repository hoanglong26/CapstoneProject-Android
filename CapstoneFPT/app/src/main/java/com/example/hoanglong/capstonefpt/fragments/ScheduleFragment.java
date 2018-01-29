package com.example.hoanglong.capstonefpt.fragments;

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

        rvSchedule.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set our adapters to the RecyclerView
        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        rvSchedule.setAdapter(fastAdapter);

        //set the items to your ItemAdapter
        List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();

        fastAdapter.clear();
        fastAdapter.add(normalizeList(scheduleList, "week"));
        fastAdapter.notifyAdapterDataSetChanged();

        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<Schedule>() {
            @Override
            public boolean onClick(View v, IAdapter<Schedule> adapter, Schedule item, int position) {
                EventBus.getDefault().post(new FragmentAdapter.OpenEvent(position, item.getLecture(), item.getRoom()));
                return true;
            }
        });

        SharedPreferences sharedPref = getActivity().getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
        String userEmailJson = sharedPref.getString("user_email_account", "");
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
                                            aSchedule.setLecture(empInfo.getEmp().getFullName());
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
                                        List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();

                                        fastAdapter.clear();
                                        fastAdapter.add(normalizeList(scheduleList, "week"));
                                        fastAdapter.notifyAdapterDataSetChanged();
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
                }, 2000);
            }
        });

        getActivity().findViewById(R.id.tvToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                    List<Schedule> todayList = normalizeList(scheduleList, "today");
                    ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("Today Schedule");

                    fastAdapter.clear();
                    fastAdapter.add(todayList);
                    fastAdapter.notifyAdapterDataSetChanged();
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
                    List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                    List<Schedule> todayList = normalizeList(scheduleList, "week");
                    ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("Week Schedule");

                    fastAdapter.clear();
                    fastAdapter.add(todayList);
                    fastAdapter.notifyAdapterDataSetChanged();
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
                    List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();
                    List<Schedule> todayList = normalizeList(scheduleList, "all");
                    ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("All Schedule");

                    fastAdapter.clear();
                    fastAdapter.add(todayList);
                    fastAdapter.notifyAdapterDataSetChanged();
                    srlHistory.setRefreshing(false);
                    materialSheetFab.hideSheet();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return rootView;
    }

    private List<Schedule> normalizeList(List<Schedule> scheduleList, String type) {
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

                List<Schedule> oldScheduleList = DatabaseManager.getInstance().getAllSchedules();
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

//                    for (Schedule oldSchedule : oldScheduleList) {
//                        if (oldSchedule.getDate().equals(aSchedule.getDate())
//                                && oldSchedule.getSlot().equals(aSchedule.getSlot())) {
//                            DatabaseManager.getInstance().deleteSchedule(oldSchedule);
////                            aSchedule.setId(oldSchedule.getId());
//                        }

                    DatabaseManager.getInstance().deleteScheduleByDateAndSlot(aSchedule);
                    DatabaseManager.getInstance().createOrUpdateSchedule(aSchedule);
//                        }

                }

//                oldScheduleList.addAll(scheduleList);
                List<Schedule> updatedScheduleList = DatabaseManager.getInstance().getAllSchedules();

//                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//                Schedule tmpModel;
//                for (int i = 0; i < updatedScheduleList.size() - 1; i++) {
//                    for (int j = 0; j < updatedScheduleList.size() - i - 1; j++) {
//                        Date aDate = df.parse(updatedScheduleList.get(j).getDate());
//                        Date aDate2 = df.parse(updatedScheduleList.get(j + 1).getDate());
//
//                        if (aDate.compareTo(aDate2) > 0) {
//                            tmpModel = updatedScheduleList.get(j + 1);
//                            updatedScheduleList.set(j + 1, updatedScheduleList.get(j));
//                            updatedScheduleList.set(j, tmpModel);
//                        }
//
//                        if (aDate.compareTo(aDate2) == 0) {
//                            String slot1 = updatedScheduleList.get(j).getSlot();
//                            String slot2 = updatedScheduleList.get(j + 1).getSlot();
//
//                            if (slot1.compareTo(slot2) > 0) {
//                                tmpModel = updatedScheduleList.get(j + 1);
//                                updatedScheduleList.set(j + 1, updatedScheduleList.get(j));
//                                updatedScheduleList.set(j, tmpModel);
//                            }
//                        }
//                    }
//
//                    if (updatedScheduleList.get(i).getDate().equals(date)) {
//                        updatedScheduleList.get(i).setIsFirstSlot("false");
//                    } else {
//                        date = updatedScheduleList.get(i).getDate();
//                        updatedScheduleList.get(i).setIsFirstSlot("true");
//                    }
//                }



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
