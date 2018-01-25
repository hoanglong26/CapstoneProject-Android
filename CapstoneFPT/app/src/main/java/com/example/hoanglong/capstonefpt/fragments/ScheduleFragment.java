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

import com.example.hoanglong.capstonefpt.POJOs.APIresponses.EmployeeInfo;
import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleResponse;
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
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        DatabaseManager.init(getActivity().getBaseContext());
        serverAPI = RetrofitUtils.get().create(ServerAPI.class);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getActivity()));

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvSchedule.getContext(),
//                DividerItemDecoration.HORIZONTAL);
//        Drawable verticalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
//        dividerItemDecoration.setDrawable(verticalDivider);
//        rvSchedule.addItemDecoration(dividerItemDecoration);

        //set our adapters to the RecyclerView
        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        rvSchedule.setAdapter(fastAdapter);

        //set the items to your ItemAdapter
        List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();

        fastAdapter.add(normalizeList(scheduleList,"week"));

        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<Schedule>() {
            @Override
            public boolean onClick(View v, IAdapter<Schedule> adapter, Schedule item, int position) {
                EventBus.getDefault().post(new FragmentAdapter.OpenEvent(position, item.getLecture(), item.getRoom()));
                return true;
            }
        });

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
                            String userEmailJson = sharedPref.getString("user_email_account", "");
                            Gson gson = new Gson();
                            final EmailInfo userEmail = gson.fromJson(userEmailJson, EmailInfo.class);

                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse("{\"email\": \"" + userEmail.getEmail() + "\"}").getAsJsonObject();
                            serverAPI.getEmployeeInfo(obj).enqueue(new Callback<EmployeeInfo>() {
                                @Override
                                public void onResponse(Call<EmployeeInfo> call, Response<EmployeeInfo> response) {
                                    EmployeeInfo empInfo = response.body();
                                    if (empInfo != null && response.code() == 200) {
                                        DatabaseManager.getInstance().deleteAllSchedules();

                                        String date="";
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

                                            DatabaseManager.getInstance().addSchedule(aSchedule);
                                        }

                                        //set the items to your ItemAdapter
                                        List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();

                                        fastAdapter.clear();
                                        fastAdapter.add(normalizeList(scheduleList,"week"));
                                        fastAdapter.notifyAdapterDataSetChanged();
                                        srlHistory.setRefreshing(false);

                                    }
                                }

                                @Override
                                public void onFailure(Call<EmployeeInfo> call, Throwable t) {

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
                    ((Toolbar)getActivity().findViewById(R.id.toolbar)).setTitle("Today Schedule");

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
                    ((Toolbar)getActivity().findViewById(R.id.toolbar)).setTitle("Week Schedule");

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
                    ((Toolbar)getActivity().findViewById(R.id.toolbar)).setTitle("All Schedule");

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

//            String date = "";
//            for (Schedule model : aList) {
//                if (model.getDate().equals(date)) {
//                    model.setDate(model.getDate()+"!");
//                } else {
//                    date = model.getDate();
//                }
//            }


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

    @Override
    public void onStart() {
        super.onStart();
    }
}
