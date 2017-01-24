package com.sreesha.android.attendancetracker.Statistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sreesha.android.attendancetracker.DashBoardClasses.InstanceCreationActivity;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceDBHelper;
import com.sreesha.android.attendancetracker.DataHandlers.User;
import com.sreesha.android.attendancetracker.R;

import java.util.ArrayList;


public class StatsDataFragment extends Fragment implements OnChartValueSelectedListener {

    public static final String STATS_DATA_FRAGMENT_TAG = "statsDataFragmentString";

    private User mUser;
    private UserStatData mUserStatData;
    private BarChart mBarChart;
    private TextView mUserIDTV;

    private boolean isStateRestored = false;
    private StatsRVAdapter mAdapter;

    public StatsDataFragment() {
        // Required empty public constructor
    }

    public static StatsDataFragment newInstance(User user) {
        StatsDataFragment fragment = new StatsDataFragment();
        Bundle args = new Bundle();
        args.putParcelable(User.USER_PARCELABLE_KEY, user);
        fragment.setArguments(args);
        return fragment;
    }

    public static StatsDataFragment newInstance() {
        StatsDataFragment fragment = new StatsDataFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mUser = getArguments().getParcelable(User.USER_PARCELABLE_KEY);
            }
        } else {
            isStateRestored = true;
            mUser = savedInstanceState.getParcelable(User.USER_PARCELABLE_KEY);
            mUserStatData = savedInstanceState.getParcelable(UserStatData.STAT_USER_PARCELABLE_KEY);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(User.USER_PARCELABLE_KEY, mUser);
        outState.putParcelable(UserStatData.STAT_USER_PARCELABLE_KEY, mUserStatData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_data, container, false);
        initializeViewElements(view);
        initializeListeners();

        return view;
    }


    private void initializeViewElements(View view) {
        mBarChart = (BarChart) view.findViewById(R.id.attendanceStatBarChart);
        mUserIDTV = (TextView) view.findViewById(R.id.userIDTV);
    }

    private void initializeListeners() {
        mBarChart.setOnChartValueSelectedListener(this);
    }

    private void loadUIWithUserStats() {
        mUserIDTV.setText(mUser.getUserID());
        setUpChart(mBarChart);
    }

    private void setUpChart(BarChart mChart) {

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        setChartData(mChart);
    }

    private String[] month = {
            "Jan"
            , "Feb"
            , "March"
            , "April"
            , "May"
            , "June"
            , "Jul"
            , "Aug"
            , "Sep"
            , "Oct"
            , "Nov"
            , "Dec"
    };

    private void setChartData(BarChart mChart) {
        int start = 20, range = 5, count = 11;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);
            yVals1.add(new BarEntry(i, val,month[i]));
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    AsyncStatsQueryTask task;

    @Override
    public void onResume() {
        super.onResume();
        if (mUser != null) {
            if (isStateRestored) {
                Log.d("RVDebug", "User : " + mUser.getUserID() + " Fragment Restored");
                loadUIWithUserStats();
            } else {

                Log.d("RVDebug", "User : " + mUser.getUserID() + " Fragment Created");
                Log.d("RVDebug", "Running Stats Async Task");

                task = new AsyncStatsQueryTask();
                task.execute();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (task != null) {
            Log.d("RVDebug", " User : " + mUser.getUserID() + " Stopping Stats Async Task");
            task.cancel(true);
            task = null;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private class AsyncStatsQueryTask extends AsyncTask<String, String, String> {
        AttendanceDBHelper mOpenHelper = new AttendanceDBHelper(getActivity());
        SQLiteDatabase db;
        public final String column_eventCount = "eventCountColumn";
        public final String column_instanceCount = "instanceCountColumn";
        public final String column_dateWiseInstanceCount = "dateWiseInstanceCountColumn";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            db = mOpenHelper.getReadableDatabase();
        }

        /*MySQL does have an ISNULL() function.
        However, it works a little bit different from Microsoft's ISNULL() function.

        In MySQL we can use the IFNULL() function, like this:

        SELECT ProductName,UnitPrice*(UnitsInStock+IFNULL(UnitsOnOrder,0))
        FROM Products
        or we can use the COALESCE() function, like this:

        SELECT ProductName,UnitPrice*(UnitsInStock+COALESCE(UnitsOnOrder,0))
        FROM Products*/

        @Override
        protected String doInBackground(String... strings) {
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
                    , new String[]{mUser.getUserID()}
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
            t0 = getCountDataWithAttType(0);
            t1 = getCountDataWithAttType(1);
            t2 = getCountDataWithAttType(2);
            t3 = getCountDataWithAttType(3);
            String d = mUser.getUserID() + "\t" + c1.getCount() + "\tt2:\t" + t2;

            long[] mC = new long[12];
            long yearInt = 2017;
            /*"yyyy-MM-dd HH:mm:ss.SSS"*/
            for (int i = 0; i < mC.length; i++) {
                Cursor cursor =
                        queryDateWiseData(
                                formatTimeStamp(
                                        i + 1
                                        , String.valueOf(yearInt)
                                )
                                , db
                        );
                if (cursor.moveToFirst()) {
                    mC[i] = cursor.getLong(cursor.getColumnIndex(column_dateWiseInstanceCount));
                } else {
                    mC[i] = 0;
                }
                Log.d("StatData", "Month " + (i + 1) + " : " + mC[i] + " / " + (t0 + t1 + t2 + t3));
            }
            mUserStatData = new UserStatData(eC, iC, t0, t1, t2, t3, mC);

            c1.close();
            db.close();
            return null;
        }

        private String formatTimeStamp(int monthInt, String year) {
            String month = "%%";
            if (monthInt < 10)
                month = "0" + monthInt;
            else
                month = String.valueOf(monthInt);

            String pat = String.valueOf(year) + "-" + month + "%%" + " " + "%%:%%:%%.%%%";
            Log.d("StatData", "SQLTimePattern : " + pat);
            return pat;
        }

        private long getCountDataWithAttType(int type) {
            Cursor c = queryFunction(type, db);
            long t0 = 0;

            if (c.moveToFirst())
                t0 = c.getLong(c.getColumnIndex(c.getColumnName(0)));
            c.close();
            return t0;
        }

        private Cursor queryDateWiseData(String timePat, SQLiteDatabase dBase) {
            return dBase
                    .query(
                            AttendanceContract.Events.TABLE_EVENTS + " , "
                                    + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE + " , "
                                    + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                            , new String[]{
                                    " coalesce ( COUNT ( " +
                                            AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                            + " . " +
                                            AttendanceContract.InstanceAttendance.column_instanceId
                                            + " ) , 0 ) AS " + column_dateWiseInstanceCount
                            }
                            , AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                    + "."
                                    + AttendanceContract.InstanceAttendance.column_userId + " = ? AND "
                                    /*Events.eventId = InstanceAttendance.eventId*/
                                    + AttendanceContract.Events.TABLE_EVENTS
                                    + "."
                                    + AttendanceContract.Events.column_eventId
                                    + " =  "
                                    + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                    + "." + AttendanceContract.InstanceAttendance.column_eventId + " AND "
                                    /*EventInstance.eventId = InstanceAttendance.eventId*/
                                    + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                                    + "."
                                    + AttendanceContract.EventInstance.column_eventId
                                    + " = "
                                    + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                    + "." + AttendanceContract.InstanceAttendance.column_eventId + " AND "
                                    /*EventInstance.startTimeStamp LIKE "yyyy-MM-dd HH:mm:ss.SSS" */
                                    + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                                    + "." + AttendanceContract.EventInstance.column_startTimeStamp
                                    + " LIKE " + "\'" + timePat + "\'"

                            , new String[]{mUser.getUserID()}
                            , null
                            , null
                            , null
                    );
        }

        private Cursor queryFunction(int type, SQLiteDatabase dBase) {
            return dBase
                    .query(AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE,
                            new String[]{" COUNT( " + AttendanceContract.InstanceAttendance.column_attendanceType + " ) "}
                            , AttendanceContract.InstanceAttendance.column_userId + " = ? AND "
                                    + AttendanceContract.InstanceAttendance.column_attendanceType + " = ? "

                            , new String[]{
                                    mUser.getUserID()
                                    , String.valueOf(type)
                            }
                            , null
                            , null
                            , null
                    );
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loadUIWithUserStats();
        }
    }
}
