package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.R;
import java.util.ArrayList;
import java.util.Date;

public class DashBoard extends AppCompatActivity
        implements EventsFragment.OnFragmentInteractionListener
        , AttendanceFragment.OnFragmentInteractionListener
        , StatisticsFragment.OnFragmentInteractionListener {

    ViewPager mViewPager;
    Adapter mAdapter;
    TabLayout mTabLayout;

    AttendanceFragment mAttendanceFragment;
    EventsFragment mEventsFragment;
    StatisticsFragment mStatisticsFragment;
    FloatingActionButton fab;

    EditText mEventNameEditText;
    String eventName = null;

    RadioGroup mEventTypeRadioGroup;
    int eventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mTabLayout.getSelectedTabPosition();

                switch (position) {
                    case 0:
                        break;
                    case 1:
                        showEventAdditionDialog();
                        break;
                    case 2:
                        break;
                }
            }
        });
        initializeViewElements();
    }

    MaterialDialog createEventDialog;

    void showEventAdditionDialog() {
        createEventDialog = new MaterialDialog.Builder(this)
                .title("Create an Event")
                .customView(R.layout.event_creation_dialog_form, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        eventName = (
                                (EditText) dialog
                                        .getCustomView()
                                        .findViewById(R.id.eventNameEditText)
                        )
                                .getText()
                                .toString();

                        if (eventName.isEmpty()) {
                            Toast.makeText(getBaseContext()
                                    , R.string.event_name_cannot_be_empty
                                    , Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Log.d("Android ID", Settings.Secure.ANDROID_ID);
                            java.sql.Timestamp timestamp = new java.sql.Timestamp(new Date().getTime());
                            Event newEvent = new Event(
                                    String.valueOf(
                                            (eventName + timestamp.toString())
                                                    .hashCode()
                                    )
                                    , null
                                    , eventName
                                    , eventType, 0, 0, timestamp.toString()
                            );
                            ContentValues v = Event.getContentValues(newEvent);
                            Uri uri = getContentResolver().insert(
                                    AttendanceContract.Events.CONTENT_URI
                                    , v
                            );
                            if (uri != null)
                                Log.d("Insertion", "Uri : " + uri.toString());
                            else
                                Log.d("Insertion", "Bad insertion");

                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).build();
        mEventTypeRadioGroup = (RadioGroup) createEventDialog
                .getCustomView()
                .findViewById(R.id.eventTypeRadioGroup);
        mEventTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedButton
                        = (RadioButton) createEventDialog.getCustomView().findViewById(checkedId);

                if (checkedButton.isChecked()) {
                    switch (checkedButton.getText().toString()) {
                        case "Lecture":
                            eventType = AttendanceContract.Events.TYPE_LECTURE;
                            break;
                        case "Practical":
                            eventType = AttendanceContract.Events.TYPE_PRACTICAL;
                            break;
                        case "Seminar":
                            eventType = AttendanceContract.Events.TYPE_SEMINAR;
                            break;
                        case "Workshop":
                            eventType = AttendanceContract.Events.TYPE_WORKSHOP;
                            break;
                        case "Exam":
                            eventType = AttendanceContract.Events.TYPE_EXAM;
                            break;
                    }
                }
            }
        });
        createEventDialog.show();
    }

    private void initializeViewElements() {
        mAttendanceFragment = AttendanceFragment.newInstance(null, null);
        mStatisticsFragment = StatisticsFragment.newInstance(null, null);
        mEventsFragment = EventsFragment.newInstance(null, null);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        initializeListeners();
        setupViewPager(mViewPager);
    }

    private void initializeListeners() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
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

        mAdapter = new Adapter(getSupportFragmentManager());
        /*TODO:Add FragmentClasses Here*/
        mAdapter.addFragment(mAttendanceFragment, getString(R.string.attendance_tab_title));
        mAdapter.addFragment(mEventsFragment, getString(R.string.events_tab_title));
        mAdapter.addFragment(mStatisticsFragment, getString(R.string.statistics_tab_title));
        viewPager.setAdapter(mAdapter);

        //mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(1).select();
    }

    class Adapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentsArrayList = new ArrayList<>();
        ArrayList<String> pageTitleArrayList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String fragmentPageTitle) {
            fragmentsArrayList.add(fragment);
            pageTitleArrayList.add(fragmentPageTitle);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsArrayList.get(position);
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
}
