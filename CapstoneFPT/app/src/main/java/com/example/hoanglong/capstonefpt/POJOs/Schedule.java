package com.example.hoanglong.capstonefpt.POJOs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.hoanglong.capstonefpt.R;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by hoanglong on 21-Jun-17.
 */
@DatabaseTable
public class Schedule extends AbstractItem<Schedule,Schedule.ViewHolder> implements Parcelable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String time;

    @DatabaseField
    private String room;

    @DatabaseField
    private String lecture;

    public Schedule(int id, String time, String room, String lecture) {
        this.id = id;
        this.time = time;
        this.room = room;
        this.lecture = lecture;
    }

    public Schedule() {
    }

    protected Schedule(Parcel in) {
        id = in.readInt();
        time = in.readString();
        room = in.readString();
        lecture = in.readString();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            Schedule instance = new Schedule();
            instance.id = ((int) in.readValue((Integer.class.getClassLoader())));
            instance.time = ((String) in.readValue((String.class.getClassLoader())));
            instance.lecture = ((String) in.readValue((String.class.getClassLoader())));
            instance.room = ((String) in.readValue((String.class.getClassLoader())));
            return instance;

        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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


    //The unique ID for this type of item
    @Override
    public int getType() {
        return 0;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.item;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, final List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);

        //bind our data
        //set the text for the time
        viewHolder.time.setText(time);

        viewHolder.room.setText("Room: "+ room +"");
        viewHolder.lecture.setText("Lecture: "+ lecture +"");

    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.time.setText(null);
        holder.room.setText(null);
        holder.lecture.setText(null);
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
        dest.writeValue(time);
        dest.writeValue(lecture);
        dest.writeValue(room);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView time;
        protected TextView room;
        protected TextView lecture;

        public ViewHolder(View view) {
            super(view);
            this.time = (TextView) view.findViewById(R.id.tvTime);
            this.room =(TextView) view.findViewById(R.id.tvRoom);
            this.lecture =(TextView) view.findViewById(R.id.tvLecture);
        }
    }

    @Override
    public boolean equals(Object v) {
        boolean flag = false;

        if (v instanceof Schedule){
            Schedule ptr = (Schedule) v;
            flag = ptr.getTime().equals(this.time);
        }

        return flag;
    }

}
