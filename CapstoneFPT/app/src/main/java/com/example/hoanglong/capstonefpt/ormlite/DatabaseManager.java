package com.example.hoanglong.capstonefpt.ormlite;

import android.content.Context;
import android.database.SQLException;

import com.example.hoanglong.capstonefpt.POJOs.Schedule;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.util.List;

/**
 * Created by hoanglong on 21-Mar-17.
 */

public class DatabaseManager {

    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseHelper helper;

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = null;
        try {
            schedules = getHelper().getScheduleDao().queryForAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    public void addSchedule(Schedule l) {
        try {
            getHelper().getScheduleDao().create(l);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAllSchedule(List<Schedule> l) {
        try {
            getHelper().getScheduleDao().create(l);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void createOrUpdateSchedule(Schedule s) {
        try {
            getHelper().getScheduleDao().createOrUpdate(s);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeIsNewToFalse(int id) {
        try {
            Schedule updateSchedule = getHelper().getScheduleDao().queryForId(id);
            updateSchedule.setIsNew("false");
            getHelper().getScheduleDao().update(updateSchedule);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteSchedule(Schedule subject) {
        try {
            getHelper().getScheduleDao().delete(subject);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteScheduleByDateAndSlot(Schedule subject) {
        try {

            QueryBuilder<Schedule, Integer> builder = getHelper().getScheduleDao().queryBuilder();
            Where where = builder.where();
            where.eq("date", subject.getDate());
            // and
            where.and();
            where.eq("slot", subject.getSlot());
            PreparedQuery<Schedule> preparedQuery = builder.prepare();
            List<Schedule> aList = getHelper().getScheduleDao().query(preparedQuery);

            if (aList.size() > 0) {
                getHelper().getScheduleDao().delete(aList.get(0));
                return true;
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteAllSchedules() {
        try {
            getHelper().getScheduleDao().delete(getAllSchedules());
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


}
