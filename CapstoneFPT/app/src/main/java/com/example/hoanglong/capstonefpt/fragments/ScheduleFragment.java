package com.example.hoanglong.capstonefpt.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoanglong.capstonefpt.POJOs.Schedule;
import com.example.hoanglong.capstonefpt.R;
import com.example.hoanglong.capstonefpt.ormlite.DatabaseManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleFragment extends Fragment {
    @BindView(R.id.rvSchedule)
    RecyclerView rvSchedule;

    @BindView(R.id.empty_view)
    TextView empty;

    private Handler handler;
    @BindView(R.id.srlSchedule)
    SwipeRefreshLayout srlHistory;

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

        rvSchedule.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set our adapters to the RecyclerView
        //we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases
        rvSchedule.setAdapter(fastAdapter);

        //set the items to your ItemAdapter
        List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();

        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
        scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));


        if (scheduleList.size() == 0) {
            rvSchedule.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
            rvSchedule.setVisibility(View.VISIBLE);
        }

        Collections.reverse(scheduleList);
        fastAdapter.add(scheduleList);

        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<Schedule>() {
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
                            //set the items to your ItemAdapter
                            List<Schedule> scheduleList = DatabaseManager.getInstance().getAllSchedules();

                            scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));
                            scheduleList.add(new Schedule(0,"Slot 6: 12:00 - 13:00", "Room 206", "KhanhKT"));

                            if (scheduleList.size() == 0) {
                                rvSchedule.setVisibility(View.GONE);
                                empty.setVisibility(View.VISIBLE);
                            } else {
                                empty.setVisibility(View.GONE);
                                rvSchedule.setVisibility(View.VISIBLE);
                            }

                            Collections.reverse(scheduleList);
                            fastAdapter.clear();
                            fastAdapter.add(scheduleList);
                            fastAdapter.notifyAdapterDataSetChanged();
                            srlHistory.setRefreshing(false);
                        }


                    }
                }, 2000);
            }
        });
        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
