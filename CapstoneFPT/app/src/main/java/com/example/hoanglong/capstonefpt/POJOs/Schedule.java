package com.example.hoanglong.capstonefpt.POJOs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hoanglong.capstonefpt.R;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hoanglong on 21-Jun-17.
 */
@DatabaseTable
public class Schedule extends AbstractItem<Schedule, Schedule.ViewHolder> implements Parcelable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String startTime;
    @DatabaseField
    private String endTime;
    @DatabaseField
    private String course;
    @DatabaseField
    private String lecture;
    @DatabaseField
    private String room;
    @DatabaseField
    private String slot;
    @DatabaseField
    private String date;
    @DatabaseField
    private String isFirstSlot;

    public Schedule(int id, String startTime, String endTime, String course, String lecture, String room, String slot, String date, String isFirstSlot) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.course = course;
        this.lecture = lecture;
        this.room = room;
        this.slot = slot;
        this.date = date;
        this.isFirstSlot=isFirstSlot;
    }

    public Schedule() {
    }

    protected Schedule(Parcel in) {
        id = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        course = in.readString();
        lecture = in.readString();
        room = in.readString();
        slot = in.readString();
        date = in.readString();
        isFirstSlot = in.readString();

    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            Schedule instance = new Schedule();
            instance.id = ((int) in.readValue((Integer.class.getClassLoader())));
            instance.startTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.endTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.course = ((String) in.readValue((String.class.getClassLoader())));
            instance.lecture = ((String) in.readValue((String.class.getClassLoader())));
            instance.room = ((String) in.readValue((String.class.getClassLoader())));
            instance.slot = ((String) in.readValue((String.class.getClassLoader())));
            instance.date = ((String) in.readValue((String.class.getClassLoader())));
            instance.isFirstSlot = ((String) in.readValue((String.class.getClassLoader())));

            return instance;

        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public String getIsFirstSlot() {
        return isFirstSlot;
    }

    public void setIsFirstSlot(String isFirstSlot) {
        this.isFirstSlot = isFirstSlot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return 0;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item_subject;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, final List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        try {
            //bind our data
            //set the text for the time
            viewHolder.startTime.setText(startTime);
            viewHolder.endTime.setText(endTime);
            viewHolder.course.setText(course);
            viewHolder.lecture.setText(lecture);
            viewHolder.room.setText("Room: " + room + "");
            viewHolder.slot.setText(slot);

            if (isFirstSlot.equals("false")) {
                viewHolder.date.setVisibility(View.GONE);
            } else {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                Date aDate = df.parse(date);
                df = new SimpleDateFormat("EEEE, dd/MM/yyyy");
                viewHolder.date.setText(df.format(aDate));
                viewHolder.date.setVisibility(View.VISIBLE);

            }


            Date today = new Date();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            if (date.equals(df.format(today))) {
                Calendar now = Calendar.getInstance();

                int hour = now.get(Calendar.HOUR);
                int minute = now.get(Calendar.MINUTE);
                String am_pm = now.get(Calendar.AM_PM) == 0? "AM":"PM";
                String nowString = hour + ":" + minute + ":00 "+ am_pm;

                df = new SimpleDateFormat("HH:mm:ss");

                DateFormat df2 = new SimpleDateFormat("hh:mm:ss aa");
                Date aDate = df2.parse(nowString);
                String tmp = df.format(aDate);

                if (df.parse(tmp).after(df.parse(startTime)) && df.parse(tmp).before(df.parse(endTime))) {
                    viewHolder.itemContainer.setBackgroundColor(viewHolder.itemContainer.getResources().getColor(R.color.md_blue_100));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder viewHolder) {
        super.unbindView(viewHolder);
        viewHolder.startTime.setText(null);
        viewHolder.endTime.setText(null);
        viewHolder.course.setText(null);
        viewHolder.lecture.setText(null);
        viewHolder.room.setText(null);
        viewHolder.slot.setText(null);
        viewHolder.date.setText(null);
        viewHolder.itemContainer.setBackgroundColor(viewHolder.itemContainer.getResources().getColor(R.color.md_white_1000));

    }

    //Init the viewHolder for this Item
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeValue(id);
        dest.writeValue(startTime);
        dest.writeValue(endTime);
        dest.writeValue(course);
        dest.writeValue(lecture);
        dest.writeValue(room);
        dest.writeValue(slot);
        dest.writeValue(date);

    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView startTime;
        protected TextView endTime;
        protected TextView course;
        protected TextView lecture;
        protected TextView room;
        protected TextView slot;
        protected TextView date;
        protected LinearLayout itemContainer;

        public ViewHolder(View view) {
            super(view);
            this.startTime = (TextView) view.findViewById(R.id.tvStartTime);
            this.endTime = (TextView) view.findViewById(R.id.tvEndTime);
            this.course = (TextView) view.findViewById(R.id.tvCousre);
            this.lecture = (TextView) view.findViewById(R.id.tvLecture);
            this.room = (TextView) view.findViewById(R.id.tvRoom);
            this.slot = (TextView) view.findViewById(R.id.tvSlot);
            this.date = (TextView) view.findViewById(R.id.tvDate);
            this.itemContainer = (LinearLayout) view.findViewById(R.id.itemContainer);

        }
    }

    @Override
    public boolean equals(Object v) {
        boolean flag = false;

        if (v instanceof Schedule) {
            Schedule ptr = (Schedule) v;
            flag = ptr.getCourse().equals(this.course);
        }

        return flag;
    }

}
