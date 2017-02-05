package com.sreesha.android.attendancetracker.Timeline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;

import java.util.Calendar;

/**
 * Created by kssre on 02-02-2017.
 */

public class TimeLineRVAdapter extends RecyclerView.Adapter<TimeLineRVAdapter.ViewHolder> {
    private Calendar nowCal;

    private SQLiteDatabase db;
    private int day;
    private int month;
    private int year;

    TimeLineRVAdapter(SQLiteDatabase db, int month, int year, int day) {

        nowCal = Calendar.getInstance();

        this.db = db;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    private String[] timeLineList = {
            "6:00 AM"
            , "7:00 AM"
            , "8:00 AM"
            , "9:00 AM"
            , "10:00 AM"
            , "11:00 AM"
            , "12:00 PM"
            , "1:00 PM"
            , "2:00 PM"
            , "3:00 PM"
            , "4:00 PM"
            , "5:00 PM"
            , "6:00 PM"
            , "7:00 PM"
            , "8:00 PM"
            , "9:00 PM"
            , "10:00 PM"
            , "11:00 PM"
            , "12:00 AM"
            , "1:00 AM"
            , "2:00 AM"
            , "3:00 AM"
            , "4:00 AM"
            , "5:00 AM"
    };

    public void setMonthYear(int month, int year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_rv_time_bar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String[] s = timeLineList[position].split(" ");
        holder.hourTV.setText(s[0]);
        holder.amPmTV.setText(s[1]);

        int sH = Integer.parseInt(s[0].split(":")[0]);
        Context co = holder.itemView.getContext();

        switch (s[1].trim()) {
            case "PM":
                if (sH != 12)
                    sH += 12;
                break;
            case "AM":
                if (sH == 12)
                    sH = 0;
                break;
        }
        if (sH < nowCal.get(Calendar.HOUR)) {
            holder.addEventsCV.setVisibility(View.GONE);
        } else {
            holder.addEventsCV.setVisibility(View.VISIBLE);
        }
        Cursor c = db.query(
                AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                , null
                , " ( " + AttendanceContract.EventInstance.column_startHour + " >= ? AND "
                        + AttendanceContract.EventInstance.column_startHour + " < ? "
                        + " ) "
                        + " AND "
                        + AttendanceContract.EventInstance.column_startMonth + " = ? AND "
                        + AttendanceContract.EventInstance.column_startDay + " = ? AND "
                        + AttendanceContract.EventInstance.column_startYear + " = ? "
                , new String[]{
                        String.valueOf(String.valueOf(sH))
                        , String.valueOf(String.valueOf(sH + 1))
                        , String.valueOf(month)
                        , String.valueOf(day)
                        , String.valueOf(year)
                }
                , null
                , null
                , null
        );
        if (c != null) {
            if (c.moveToFirst()) {

                holder.mAdapter = new EInstanceRVAdapter(co, c);

                holder.eventInstanceRV.setLayoutManager(new LinearLayoutManager(co));
                holder.eventInstanceRV.setAdapter(holder.mAdapter);
            } else {

            }
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return timeLineList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView hourTV;
        TextView amPmTV;
        RecyclerView eventInstanceRV;
        EInstanceRVAdapter mAdapter;
        CardView addEventsCV;
        RelativeLayout mTimeBarLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            hourTV = (TextView) itemView.findViewById(R.id.hourTV);
            amPmTV = (TextView) itemView.findViewById(R.id.amPmTV);
            eventInstanceRV = (RecyclerView) itemView.findViewById(R.id.eventInstanceRecyclerView);
            addEventsCV = (CardView) itemView.findViewById(R.id.emptyEventCV);
            mTimeBarLayout = (RelativeLayout) itemView.findViewById(R.id.timeBarItemRootLayout);

            addEventsCV.setOnClickListener(mEventsClickListener);
        }

        View.OnClickListener mEventsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    public class EInstanceRVAdapter extends CursorRecyclerViewAdapter<EInstanceRVAdapter.ViewHolder> {

        public EInstanceRVAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        private CustomOnClickListener mClickListener;
        private CustomOnLongClickListener mLongClickListener;

        @Override
        public EInstanceRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_instance_item, parent, false);

            return new EInstanceRVAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EInstanceRVAdapter.ViewHolder holder, Cursor cursor) {

            EventInstance instance = EventInstance.getEventFromCursor(cursor);

            holder.startDateTV.setText(Utility.getFormattedTimeStamp(instance.getStartTimeStamp()));
            holder.endDateTV.setText(Utility.getFormattedTimeStamp(instance.getEndTimeStamp()));

            holder.creationDateTV.setText(Utility.getFormattedTimeStamp(instance.getCreationTimeStamp()));
            holder.presentNumTV.setText(String.valueOf(instance.getType0Count()));
            holder.absentNumTV.setText(String.valueOf(instance.getType1Count()));
            holder.unknownNumTV.setText(String.valueOf(instance.getType2Count()));
            holder.medicalNumTV.setText(String.valueOf(instance.getType3Count()));

            holder.numParticipantTV
                    .setText(
                            String.valueOf(
                                    instance.getType0Count()
                                            + instance.getType1Count()
                                            + instance.getType2Count()
                                            + instance.getType3Count()
                            )
                    );
            holder.eventNameTV.setText(instance.getEventName());
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView startDateTV;
            TextView endDateTV;
            TextView numParticipantTV;
            TextView creationDateTV;

            TextView presentNumTV;
            TextView absentNumTV;
            TextView unknownNumTV;
            TextView medicalNumTV;

            TextView eventNameTV;

            public ViewHolder(View itemView) {
                super(itemView);
                startDateTV = (TextView) itemView.findViewById(R.id.startTimeTV);
                endDateTV = (TextView) itemView.findViewById(R.id.endTimeTV);
                numParticipantTV = (TextView) itemView.findViewById(R.id.numParticipantsTV);
                creationDateTV = (TextView) itemView.findViewById(R.id.creationDateTV);
                eventNameTV = (TextView) itemView.findViewById(R.id.eventNameTV);

                presentNumTV = (TextView) itemView.findViewById(R.id.presentTV);
                absentNumTV = (TextView) itemView.findViewById(R.id.absentTV);
                unknownNumTV = (TextView) itemView.findViewById(R.id.unknownTV);
                medicalNumTV = (TextView) itemView.findViewById(R.id.medicalTV);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    Log.d("InstanceCreation", "Adapter Position + " + getAdapterPosition());
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    mClickListener.onClick(view, getAdapterPosition(), EventInstance.getEventFromCursor(c));
                    c.moveToFirst();
                }
            }

            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null) {
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    mLongClickListener.onLongClick(view, getAdapterPosition(), EventInstance.getEventFromCursor(c));
                    c.moveToFirst();
                }
                return true;
            }
        }

        void setCustomOnClickListener(CustomOnClickListener listener) {
            this.mClickListener = listener;
        }

        void setCustomOnLongClickListener(CustomOnLongClickListener listener) {
            this.mLongClickListener = listener;
        }
    }

    interface CustomOnClickListener {
        void onClick(View view, int position, EventInstance eventInstance);
    }

    interface CustomOnLongClickListener {
        void onLongClick(View view, int position, EventInstance eventInstance);
    }
}
