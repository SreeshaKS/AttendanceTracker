package com.sreesha.android.attendancetracker.Statistics;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceDBHelper;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.DataHandlers.User;
import com.sreesha.android.attendancetracker.R;

import java.util.ArrayList;

/**
 * Created by Sreesha on 21-01-2017.
 */

public class StatsRVAdapter extends CursorRecyclerViewAdapter<StatsRVAdapter.ViewHolder> {


    String queryUSN = null;


    public StatsRVAdapter(Context context, Cursor cursor, FragmentManager fragmentManager) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    void setQueryUSN(String usn) {
        this.queryUSN = usn;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        User u = User.getUserFromCursor(cursor);
        setUpUserData(u, holder, cursor.getPosition());
    }

    private void setUpUserData(User u, ViewHolder holder, int position) {
        StatsRVAdapter mAdapter;
        queryAndSetStatsData(u, holder);
        holder.mUserIDTV.setText(u.getUserID());

    }

    private final String column_eventCount = "eventCountColumn";
    private final String column_instanceCount = "instanceCountColumn";
    private final String column_dateWiseInstanceCount = "dateWiseInstanceCountColumn";

    private void queryAndSetStatsData(User u, ViewHolder holder) {
        AttendanceDBHelper mOpenHelper = new AttendanceDBHelper(holder.itemView.getContext());
        SQLiteDatabase db;

        db = mOpenHelper.getReadableDatabase();
        UserStatData userStatData = doQuery(u, db);
        holder.instanceCTV.setText(String.valueOf(userStatData.getInstanceCount()
                + holder.itemView.getContext().getString(R.string.instances)));
        holder.eventCTV.setText(String.valueOf(userStatData.getEventCount()
                + holder.itemView.getContext().getString(R.string.events)));

        holder.presentNumTV.setText(String.valueOf(userStatData.getType0()));
        holder.absentNumTV.setText(String.valueOf(userStatData.getType1()));
        holder.unkownNumTV.setText(String.valueOf(userStatData.getType2()));
        holder.medicalNumTV.setText(String.valueOf(userStatData.getType3()));

        Log.d("RVDebug", userStatData.toString());
        setUpChart(holder.mBarChart, userStatData);
    }

    private void setUpChart(BarChart mChart, UserStatData userStatData) {

        long tC = userStatData.getInstanceCount();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(12);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount((int) tC, false);
        leftAxis.setGranularity(1f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setGranularity(1f);
        rightAxis.setLabelCount((int) tC, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        setChartData(mChart, userStatData);
    }

    private void setChartData(BarChart mChart, UserStatData userStatData) {
        int start = 0, count = 11;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = start; i < count; i++) {
            float value = (float) Math.random();
            Log.d("Vals", " Val : " + userStatData.getMonthWiseCount()[i]);
            yVals1.add(new BarEntry(i + 1, (int) userStatData.getMonthWiseCount()[i]));
        }
        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "The year 2017");
        set1.setColors(ColorTemplate.MATERIAL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        mChart.setData(data);
        mChart.notifyDataSetChanged();
    }

    private UserStatData doQuery(User u, SQLiteDatabase db) {
        Log.d("StatData", "--------------------------------" + u.getUserID() + "---------------------------------------");
        Cursor c1 = db.query(
                AttendanceContract.Events.TABLE_EVENTS + " , "
                        + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                , new String[]{
                        " coalesce ( COUNT ( " +
                                "DISTINCT ( " +
                                AttendanceContract.Events.TABLE_EVENTS
                                + " . "
                                + AttendanceContract.Events.column_eventId
                                + " ) ) , 0 )AS " + column_eventCount
                        ,
                        " coalesce ( COUNT ( " +
                                AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                + " . " +
                                AttendanceContract.InstanceAttendance.column_instanceId
                                + " ) , 0 ) AS " + column_instanceCount
                }
                , AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                        + "."
                        + AttendanceContract.InstanceAttendance.column_userId + " = ? AND "
                        + AttendanceContract.Events.TABLE_EVENTS
                        + "."
                        + AttendanceContract.Events.column_eventId
                        + " =  "
                        + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                        + "." + AttendanceContract.InstanceAttendance.column_eventId + " "
                , new String[]{u.getUserID()}
                , null
                , null
                , null
        );
        long eC = 0, iC = 0;
        if (c1.moveToFirst()) {
            eC = c1.getLong(c1.getColumnIndex(column_eventCount));
            iC = c1.getLong(c1.getColumnIndex(column_instanceCount));
            Log.d("StatData", "Cursor Count : " + c1.getCount() + "Event Count : " + eC + "Instance Count : " + iC);
        }

        long t0 = 0, t1 = 0, t2 = 0, t3 = 0;
        t0 = getCountDataWithAttType(u, 0, db);
        t1 = getCountDataWithAttType(u, 1, db);
        t2 = getCountDataWithAttType(u, 2, db);
        t3 = getCountDataWithAttType(u, 3, db);
        String d = u.getUserID() + "\t" + c1.getCount() + "\tt2:\t" + t2;

        long[] mC = new long[12];
        long yearInt = 2017;
            /*"yyyy-MM-dd HH:mm:ss.SSS"*/
        for (int i = 0; i < mC.length; i++) {
            Cursor cursor =
                    queryDateWiseData(
                            u
                            , String.valueOf(yearInt)
                            , (i + 1) < 10 ? String.valueOf("0" + (i + 1)) : String.valueOf(i + 1)
                            , db
                    );
            if (cursor.moveToFirst()) {
                mC[i] = cursor.getLong(cursor.getColumnIndex(column_dateWiseInstanceCount));
            } else {
                mC[i] = 0;
            }
            Log.d("StatData", "Month " + (i + 1) + " : " + mC[i]);
        }


        c1.close();
        db.close();
        Log.d("StatData", "---------------------------------------------------------------------------------");
        return new UserStatData(eC, iC, t0, t1, t2, t3, mC);
    }

    private String formatTimeStamp(int monthInt, String year) {
        String month = "__";
        if (monthInt < 10)
            month = "0" + monthInt;
        else
            month = String.valueOf(monthInt);

        String pat = String.valueOf(year) + "-" + month + "-__ __:__:__";
        Log.d("StatData", "SQLTimePattern : " + "\'" + pat + "\'");
        return pat;
    }

    private long getCountDataWithAttType(User u, int type, SQLiteDatabase db) {
        Cursor c = queryFunction(u, type, db);
        long t0 = 0;

        if (c.moveToFirst())
            t0 = c.getLong(c.getColumnIndex(c.getColumnName(0)));
        c.close();
        return t0;
    }

    private Cursor queryDateWiseData(User u, String year, String month, SQLiteDatabase dBase) {
        return dBase
                .query(
                        AttendanceContract.Events.TABLE_EVENTS + " , "
                                + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE + " , "
                                + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                        , new String[]{
                                " coalesce ( COUNT ( DISTINCT ( " +
                                        AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                        + " . " +
                                        AttendanceContract.InstanceAttendance.column_instanceId
                                        + " ) ) , 0 ) AS " + column_dateWiseInstanceCount
                        }
                        , AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                + "."
                                + AttendanceContract.InstanceAttendance.column_userId + " = ? AND "
                                 /*   *//*Events.eventId = InstanceAttendance.eventId*//*
                                + AttendanceContract.Events.TABLE_EVENTS
                                + "."
                                + AttendanceContract.Events.column_eventId
                                + " =  "
                                + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                + "." + AttendanceContract.InstanceAttendance.column_eventId + " AND "*/
                                    /*EventInstance.eventId = InstanceAttendance.eventId*/
                                + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                                + "."
                                + AttendanceContract.EventInstance.column_eventId
                                + " = "
                                + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                + "." + AttendanceContract.InstanceAttendance.column_eventId
                                //EventInstance.startTimeStamp LIKE "yyyy-MM-dd HH:mm:ss.SSS"

                                /*+ " AND "
                                + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                                + "." + AttendanceContract.EventInstance.column_startTimeStamp
                                + " LIKE " + "\'" + timePat + "\' " */
                                + " AND "
                                + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                + "." + AttendanceContract.InstanceAttendance.column_attendanceType + " = ? "
                                + " AND "
                                + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                                + "." + AttendanceContract.EventInstance.column_startYear
                                + " = ? "
                                + " AND "
                                + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                                + "." + AttendanceContract.EventInstance.column_startMonth
                                + " = ? "


                        , new String[]{
                                u.getUserID()
                                , String.valueOf(AttendanceContract.InstanceAttendance.TYPE_PRESENT)
                                , year
                                , month
                        }
                        , null
                        , null
                        , null
                );
    }

    private Cursor queryFunction(User u, int type, SQLiteDatabase dBase) {
        return dBase
                .query(AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE,
                        new String[]{" COUNT( " + AttendanceContract.InstanceAttendance.column_attendanceType + " ) "}
                        , AttendanceContract.InstanceAttendance.column_userId + " = ? AND "
                                + AttendanceContract.InstanceAttendance.column_attendanceType + " = ? "

                        , new String[]{
                                u.getUserID()
                                , String.valueOf(type)
                        }
                        , null
                        , null
                        , null
                );
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        BarChart mBarChart;
        TextView mUserIDTV;
        TextView eventCTV;
        TextView instanceCTV;


        TextView presentNumTV;
        TextView absentNumTV;
        TextView unkownNumTV;
        TextView medicalNumTV;

        public ViewHolder(View itemView) {
            super(itemView);
            mBarChart = (BarChart) itemView.findViewById(R.id.attendanceStatBarChart);
            mUserIDTV = (TextView) itemView.findViewById(R.id.userIDTV);
            eventCTV = (TextView) itemView.findViewById(R.id.eventCTV);
            instanceCTV = (TextView) itemView.findViewById(R.id.instanceCTV);

            presentNumTV = (TextView) itemView.findViewById(R.id.presentTV);
            absentNumTV = (TextView) itemView.findViewById(R.id.absentTV);
            unkownNumTV = (TextView) itemView.findViewById(R.id.unknownTV);
            medicalNumTV = (TextView) itemView.findViewById(R.id.medicalTV);
        }
    }
}
