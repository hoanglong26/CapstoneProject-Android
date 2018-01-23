package com.example.hoanglong.capstonefpt.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hoanglong on 06-Dec-16.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    String userRole;

    public FragmentAdapter(FragmentManager fm, String userRole) {
        super(fm);
        this.userRole = userRole;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ScheduleFragment.newInstance("");
            case 1:
                return ScheduleFragment.newInstance("");

            default:
                return ScheduleFragment.newInstance("");
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }


    public static class OpenEvent {
        public final int position;
        public final String lat;
        public final String lng;

        public OpenEvent(int position, String lat, String lng) {
            this.position = position;
            this.lat = lat;
            this.lng = lng;
        }
    }
}
