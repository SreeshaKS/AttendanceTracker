package com.sreesha.android.attendancetracker.Timeline;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sreesha.android.attendancetracker.AttendanceApplication;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceDBHelper;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimelineActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ViewPager mViewPager;
    TimelineActivity.Adapter mAdapter;
    TabLayout mTabLayout;

    TextView mTodayNumTV;

    ArrayList<DayFragment> mDayFragmentList;
    ArrayList<String> mMonthNameList;
    int year;
    int day;
    int month;

    RelativeLayout mCurrentDateLayout;
    TextView mCurrentDateTV;
    TextView mNextClassTimeTV;

    Calendar now = Calendar.getInstance();
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AttendanceDBHelper dH = new AttendanceDBHelper(TimelineActivity.this);
        db = dH.getReadableDatabase();

        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH);
        day = now.get(Calendar.DAY_OF_MONTH);

        Log.d("Debug", "Day OF Month : " + now.get(Calendar.DAY_OF_MONTH) + "Day Of Week" + now.get(Calendar.DAY_OF_WEEK)
                + "Day Of Week Of Month" + now.get(Calendar.DAY_OF_WEEK_IN_MONTH));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDayFragmentList = new ArrayList<>();
        mMonthNameList = new ArrayList<>();

        initializeViewElements();

        setNumClasses(now);
        setNextClassTime();
    }

    private void setNumClasses(Calendar cal) {
        Cursor c = db.query(
                AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                , new String[]{" COUNT ( * ) AS count "}
                , AttendanceContract.EventInstance.column_startMonth + " = ? AND "
                        + AttendanceContract.EventInstance.column_startDay + " = ? AND "
                        + AttendanceContract.EventInstance.column_startYear + " = ? "
                , new String[]{
                        String.valueOf(cal.get(Calendar.MONTH))
                        , String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
                        , String.valueOf(cal.get(Calendar.YEAR))
                }
                , null
                , null
                , null
        );
        String s = "";
        if (c.moveToFirst()) {
            if (cal.equals(now)) {
                s = getString(R.string.you_have) + " " + String.valueOf(c.getInt(0)) + " " + getString(R.string.classes_today);
            } else {
                s = getString(R.string.you_have) + " " + String.valueOf(c.getInt(0)) + " " + "classes on " + cal.getTime().toString();
            }
        }
        mTodayNumTV.setText(s);
        c.close();
    }

    private void setNextClassTime() {

        Cursor c = db.query(
                AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                , null
                , AttendanceContract.EventInstance.column_startMonth + " >= ? AND "
                        + AttendanceContract.EventInstance.column_startDay + " >= ? AND "
                        + AttendanceContract.EventInstance.column_startYear + " >= ? AND "
                        + AttendanceContract.EventInstance.column_startHour + " >= ? "
                , new String[]{
                        String.valueOf(now.get(Calendar.MONTH))
                        , String.valueOf(now.get(Calendar.DAY_OF_MONTH))
                        , String.valueOf(now.get(Calendar.YEAR))
                        , String.valueOf(now.get(Calendar.HOUR_OF_DAY))
                }
                , null
                , null
                , AttendanceContract.EventInstance.column_startHour + " ASC "
        );
        if (c.moveToFirst()) {
            EventInstance eI = EventInstance.getEventFromCursor(c);

            String s = "Next Class is at " + Utility.getFormattedTimeStamp(eI.getStartTimeStamp());
            mNextClassTimeTV.setText(s);
        } else {
            mNextClassTimeTV.setText("");
        }

        c.close();
    }

    Calendar[] currentCalList = new Calendar[8];

    private void addFragmentListToAdapter(Calendar cal) {

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);


        Calendar temp = Calendar.getInstance();
        temp.setTime(cal.getTime());

        currentCalList[dayOfWeek] = Calendar.getInstance();
        currentCalList[dayOfWeek].setTime(temp.getTime());

        for (int j = dayOfWeek + 1; j <= 7; j++) {
            currentCalList[j] = Calendar.getInstance();
            temp.add(Calendar.DAY_OF_YEAR, 1);
            currentCalList[j].setTime(temp.getTime());
        }

        temp = Calendar.getInstance();
        temp.setTime(cal.getTime());

        for (int j = dayOfWeek - 1; j >= 0; j--) {
            currentCalList[j] = Calendar.getInstance();
            temp.add(Calendar.DAY_OF_YEAR, -1);
            currentCalList[j].setTime(temp.getTime());
        }

        mDayFragmentList.clear();
        mMonthNameList.clear();

        for (int i = 0; i < 7; i++) {
            Log.d("Debug", currentCalList[i + 1].getTime().toString());
            mDayFragmentList.add(
                    DayFragment.newInstance(
                            currentCalList[i + 1].get(Calendar.DAY_OF_MONTH)
                            , currentCalList[i + 1].get(Calendar.MONTH)
                            , currentCalList[i + 1].get(Calendar.YEAR)
                    )
            );
            /*mMonthNameList.add(dayList[i]);*/
            String[] s = currentCalList[i + 1].getTime().toString().split(" ");
            mMonthNameList.add(s[0] + " " + s[2]);
        }
    }

    private void initializeViewElements() {

        mNextClassTimeTV = (TextView) findViewById(R.id.nextClassTimeTV);
        mTodayNumTV = (TextView) findViewById(R.id.todayNumInstanceTV);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mCurrentDateLayout = (RelativeLayout) findViewById(R.id.currentDateLayout);
        mCurrentDateTV = (TextView) findViewById(R.id.currentDateTV);

        String dateHR = DateFormat.getDateInstance().format(now.getTime());
        mCurrentDateTV.setText(dateHR);

        initializeListeners();

        setupViewPager(mViewPager);
    }


    private void initializeListeners() {

        mCurrentDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = DatePickerDialog.newInstance(
                        TimelineActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                /*dpd.setMinDate(now);*/
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.show(getFragmentManager(), getString(R.string.date_picker_dialog_title));
            }
        });
    }

    private String[] dayList = {
            "Sun"
            , "Mon"
            , "Tue"
            , "Wed"
            , "Thu"
            , "Fri"
            , "Sat"
    };

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        break;
                    case 1:


                        break;
                    case 2:

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mAdapter = new TimelineActivity.Adapter(getSupportFragmentManager());
        /*TODO:Add FragmentClasses Here*/

        addFragmentListToAdapter(now);
        mAdapter.addFragment(mDayFragmentList, mMonthNameList);

        viewPager.setAdapter(mAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(now.get(Calendar.DAY_OF_WEEK) - 1).select();
    }

    Calendar currentSelectionCal;

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar d = Calendar.getInstance();
        d.set(year, monthOfYear, dayOfMonth);
        currentSelectionCal = d;
        String dateHR = DateFormat.getDateInstance().format(d.getTime());

        mCurrentDateTV.setText(dateHR);
        day = currentSelectionCal.get(Calendar.DAY_OF_MONTH);
        month = currentSelectionCal.get(Calendar.MONTH);
        TimelineActivity.this.year = currentSelectionCal.get(Calendar.YEAR);


        if (mAdapter != null) {
            mAdapter.clearList();

            mAdapter.notifyDataSetChanged();
            mViewPager.invalidate();

            addFragmentListToAdapter(currentSelectionCal);
            mAdapter.addFragment(mDayFragmentList, mMonthNameList);

            mAdapter.notifyDataSetChanged();
            mViewPager.invalidate();

            mTabLayout.getTabAt(currentSelectionCal.get(Calendar.DAY_OF_WEEK) - 1).select();
        }

        setNumClasses(currentSelectionCal);
    }

    class Adapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> fragmentsArrayList = new ArrayList<>();
        ArrayList<String> pageTitleArrayList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String fragmentPageTitle) {
            fragmentsArrayList.add(fragment);
            pageTitleArrayList.add(fragmentPageTitle);
        }

        public void clearList() {
            if (!fragmentsArrayList.isEmpty())
                fragmentsArrayList.clear();
            if (!pageTitleArrayList.isEmpty())
                pageTitleArrayList.clear();
        }

        public void addFragment(ArrayList<DayFragment> fragmentList, ArrayList<String> titleList) {
            fragmentsArrayList.addAll(fragmentList);
            pageTitleArrayList.addAll(titleList);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsArrayList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitleArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsArrayList.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem i;
        if (mTabLayout != null) {
            Log.d("Menu", "Invalidating");
        }
        return true;
    }
}
