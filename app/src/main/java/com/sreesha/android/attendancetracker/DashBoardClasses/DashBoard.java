package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sreesha.android.attendancetracker.R;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initializeViewElements();
    }

    private void initializeViewElements() {
        mAttendanceFragment = AttendanceFragment.newInstance(null, null);
        mStatisticsFragment = StatisticsFragment.newInstance(null, null);
        mEventsFragment = EventsFragment.newInstance(null, null);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);


        setupViewPager(mViewPager);
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
