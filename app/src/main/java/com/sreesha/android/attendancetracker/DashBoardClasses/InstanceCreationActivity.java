package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sreesha.android.attendancetracker.AttendanceApplication;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceDBHelper;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;

public class InstanceCreationActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final int INSTANCE_LOADER_ID = 999;
    private static final String SELECTED_INSTANCE_ID_KEY = "selectedInstanceIDKey";
    private static final String SELECTED_EVENT_ID_KEY = "selectedEventIDKey";
    private Event event;

    /**
     * Data Variable to be gathered from Dialogs
     **/
    private EventInstance mEventInstance;

    private String instanceID;
    private String eventID;

    private Calendar creationCal;
    private Calendar startTimeCal;
    private Calendar endTimeCal;

    private String timeStamp;
    private String startTimeStamp;
    private String endTimeStamp;
    private int hour;
    private int duration;
    private String note;

    private CoordinatorLayout mCoOrdLayout;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolBarLayout;

    private RecyclerView mEventInstanceRV;
    private EInstanceRVAdapter mEventInstanceAdapter;
    boolean isStateRestored = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instance_creation);
        if (savedInstanceState != null) {
            isStateRestored = true;
        }
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1922951486583620~9852395998");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mCoOrdLayout = (CoordinatorLayout) findViewById(R.id.coOrdLayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mEventInstanceRV = (RecyclerView) findViewById(R.id.eventInstanceRecyclerView);

        setSupportActionBar(toolbar);

        handleIntent();

        getSupportActionBar().setTitle(event.getEventName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventAdditionDialog();
            }
        });
        initializeRecyclerView();
        initializeListeners();

        getSupportLoaderManager().initLoader(INSTANCE_LOADER_ID, null, loaderCallBacks);
    }


    private void initializeListeners() {
        mEventInstanceAdapter.setCustomOnClickListener(new EInstanceRVAdapter.CustomOnClickListener() {
            @Override
            public void onClick(View view, int position, EventInstance eventInstance) {
                Log.d("InstanceCreation", eventInstance.getInstanceID());
                AttendanceApplication
                        .getSharedPreferences(InstanceCreationActivity.this)
                        .edit()
                        .putString(SELECTED_INSTANCE_ID_KEY, eventInstance.getInstanceID())
                        .apply();
                AttendanceApplication
                        .getSharedPreferences(InstanceCreationActivity.this)
                        .edit()
                        .putString(SELECTED_EVENT_ID_KEY, eventInstance.getEventID())
                        .apply();

                Intent i = new Intent(InstanceCreationActivity.this, AttendanceActivity.class);

                i.putExtra(EventInstance.EVENT_INSTANCE_PARCELABLE_KEY, eventInstance);
                startActivity(i);
            }
        });
        mEventInstanceAdapter.setCustomOnLongClickListener(new EInstanceRVAdapter.CustomOnLongClickListener() {
            @Override
            public void onLongClick(View view, int position, EventInstance eventInstance) {

            }
        });
    }

    private void initializeRecyclerView() {
        mEventInstanceAdapter = new EInstanceRVAdapter(getBaseContext(), null);
        mEventInstanceRV.setLayoutManager(
                new LinearLayoutManager(getBaseContext())
        );
        mEventInstanceRV.setAdapter(mEventInstanceAdapter);
    }


    TextView startTimeTV;
    TextView endTimeTV;
    TextView dateTextView;
    EditText notesET;

    void showEventAdditionDialog() {
        MaterialDialog createInstanceDialog = new MaterialDialog.Builder(InstanceCreationActivity.this)
                .title(R.string.create_an_instance)
                .customView(R.layout.instance_creation_form, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (timeStamp != null
                                && startTimeStamp != null
                                && endTimeStamp != null) {

                            String notes = notesET.getText().toString();
                            if (notes.isEmpty())
                                notes = null;
                            timeStamp = timeStamp.substring(0, timeStamp.indexOf("."));
                            startTimeStamp = startTimeStamp.substring(0, startTimeStamp.indexOf("."));
                            endTimeStamp = endTimeStamp.substring(0, endTimeStamp.indexOf("."));
                            Log.d("Debug", timeStamp);
                            mEventInstance
                                    = new EventInstance(
                                    String.valueOf((event.getEventId() + timeStamp).hashCode())
                                    , event.getEventId()
                                    , event.getEventName()
                                    , timeStamp/*.split(".")[0]*/
                                    , startTimeStamp/*.split(".")[0]*/
                                    , endTimeStamp/*.split(".")[0]*/
                                    , endTimeCal.getTimeInMillis() - startTimeCal.getTimeInMillis()
                                    , notes
                                    , creationCal.get(Calendar.YEAR)
                                    , creationCal.get(Calendar.MONTH)
                                    , creationCal.get(Calendar.DAY_OF_MONTH)
                                    , creationCal.get(Calendar.HOUR_OF_DAY)
                                    , creationCal.get(Calendar.MINUTE)
                                    , creationCal.get(Calendar.SECOND)
                                    , startTimeCal.get(Calendar.YEAR)
                                    , startTimeCal.get(Calendar.MONTH)
                                    , startTimeCal.get(Calendar.DAY_OF_MONTH)
                                    , startTimeCal.get(Calendar.HOUR_OF_DAY)
                                    , startTimeCal.get(Calendar.MINUTE)
                                    , startTimeCal.get(Calendar.SECOND)
                                    , startTimeCal.get(Calendar.YEAR)
                                    , endTimeCal.get(Calendar.MONTH)
                                    , endTimeCal.get(Calendar.DAY_OF_MONTH)
                                    , endTimeCal.get(Calendar.HOUR_OF_DAY)
                                    , endTimeCal.get(Calendar.MINUTE)
                                    , endTimeCal.get(Calendar.SECOND)
                            );
                            ContentValues values = EventInstance.getContentValues(mEventInstance);
                            getContentResolver()
                                    .insert(
                                            AttendanceContract.EventInstance.CONTENT_URI
                                            , values
                                    );
                            event.setNumberOfInstances(
                                    event.getNumberOfInstances() + 1
                            );
                            getContentResolver()
                                    .update(
                                            AttendanceContract.Events.CONTENT_URI
                                            , Event.getContentValues(event)
                                            , AttendanceContract.Events.column_eventId + " = ? "
                                            , new String[]{event.getEventId()}
                                    );

                        } else {
                            Toast.makeText(getBaseContext()
                                    , R.string.date_and_time_cannot_be_empty, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).build();
        View view = createInstanceDialog.getCustomView();

        startTimeTV = (TextView) view.findViewById(R.id.startTimeDisplayTV);
        endTimeTV = (TextView) view.findViewById(R.id.endTimeDisplayTV);
        dateTextView = (TextView) view.findViewById(R.id.dateDisplayTV);
        notesET = (EditText) view.findViewById(R.id.instanceNotesET);

        RelativeLayout datePickerLayout = (RelativeLayout) view.findViewById(R.id.datePickerLayout);

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.startTimeDisplayTV: {
                        isStartTimeBeingPicked = 0;
                        Calendar now = Calendar.getInstance();

                        TimePickerDialog tpd
                                = TimePickerDialog.newInstance(
                                InstanceCreationActivity.this
                                , now.get(Calendar.HOUR_OF_DAY)
                                , now.get(Calendar.MINUTE)
                                , false
                        );
                        tpd.show(getFragmentManager(), getString(R.string.time_picker_dialog_title));

                    }
                    break;
                    case R.id.endTimeDisplayTV: {
                        if (startTimeCal != null) {
                            isStartTimeBeingPicked = 1;
                            Calendar now = Calendar.getInstance();

                            TimePickerDialog tpd
                                    = TimePickerDialog.newInstance(
                                    InstanceCreationActivity.this
                                    , now.get(Calendar.HOUR_OF_DAY)
                                    , now.get(Calendar.MINUTE)
                                    , false
                            );
                            tpd.setMinTime(
                                    startTimeCal.get(Calendar.HOUR)
                                    , startTimeCal.get(Calendar.MINUTE)
                                    , startTimeCal.get(Calendar.SECOND)
                            );
                            tpd.show(getFragmentManager(), getString(R.string.time_picker_dialog_title));
                        } else {
                            Toast
                                    .makeText(getBaseContext()
                                            , R.string.please_set_the_start_time
                                            , Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    break;
                    case R.id.datePickerLayout: {
                        Calendar now = Calendar.getInstance();

                        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = DatePickerDialog.newInstance(
                                InstanceCreationActivity.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMinDate(now);
                        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                        dpd.show(getFragmentManager(), getString(R.string.date_picker_dialog_title));
                    }
                    break;
                }
            }
        };

        startTimeTV.setOnClickListener(l);
        endTimeTV.setOnClickListener(l);
        datePickerLayout.setOnClickListener(l);

        createInstanceDialog.show();
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getBaseContext()
                    , AttendanceContract.EventInstance.CONTENT_URI
                    , null
                    , AttendanceContract.EventInstance.column_eventId + " = ?"
                    , new String[]{event.getEventId()}
                    , AttendanceContract.EventInstance.column_creationTimeStamp + " DESC"
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() > 0) {
                Log.d("InstanceActivity", "Count" + data.getCount());
                mEventInstanceAdapter.swapCursor(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mEventInstanceAdapter.swapCursor(null);
        }
    };

    int isStartTimeBeingPicked = 0;

    void handleIntent() {
        event = getIntent().getParcelableExtra(EventsFragment.EVENT_OBJECT_PARCELABLE_KEY);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar d = Calendar.getInstance();
        d.set(year, monthOfYear, dayOfMonth);
        creationCal = d;
        String dateHR = DateFormat.getDateInstance().format(d.getTime());

        dateTextView.setText(dateHR);

        Timestamp t = new Timestamp(d.getTime().getTime());

        timeStamp = t.toString();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (creationCal != null) {
            switch (isStartTimeBeingPicked) {
                case 0:
                    Calendar cal1 = Calendar.getInstance();
                    cal1.set(creationCal.get(Calendar.YEAR)
                            , creationCal.get(Calendar.MONTH), creationCal.get(Calendar.DATE)
                            , hourOfDay, minute, second);
                    startTimeCal = cal1;
                    Timestamp t1 = new Timestamp(cal1.getTime().getTime());
                    startTimeStamp = t1.toString();

                    String dateHR1 = DateFormat
                            .getTimeInstance(
                                    DateFormat.MEDIUM
                            )
                            .format(cal1.getTime());

                    startTimeTV.setText(dateHR1);
                    Log.d("Time", t1.toString());
                    break;
                case 1:
                    Calendar cal2 = Calendar.getInstance();
                    cal2.set(creationCal.get(Calendar.YEAR)
                            , creationCal.get(Calendar.MONTH), creationCal.get(Calendar.DATE)
                            , hourOfDay, minute, second);
                    endTimeCal = cal2;
                    Timestamp t2 = new Timestamp(cal2.getTime().getTime());
                    endTimeStamp = t2.toString();

                    String dateHR2 = DateFormat.getTimeInstance().format(cal2.getTime());

                    endTimeTV.setText(dateHR2);
                    break;
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.please_set_date_field_first, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(INSTANCE_LOADER_ID, null, loaderCallBacks);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AttendanceDBHelper mOpenHelper = new AttendanceDBHelper(InstanceCreationActivity.this);

        Cursor c = participantCountQueryFunction(mOpenHelper);
        long p = 0;
        if (c.moveToFirst()) {
            p = c.getLong(c.getColumnIndex(c.getColumnName(0)));
        }

        ContentValues cV = new ContentValues();
        cV.put(AttendanceContract.Events.column_numOfParticipants, p);
        getContentResolver()
                .update(AttendanceContract.Events.CONTENT_URI
                        , cV
                        , AttendanceContract.Events.column_eventId + " = ? "
                        , new String[]{event.getEventId()}
                );
    }

    private Cursor participantCountQueryFunction(AttendanceDBHelper mOpenHelper) {
        return mOpenHelper
                .getReadableDatabase()
                .query(AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE,
                        new String[]{" COUNT( DISTINCT(" + AttendanceContract.InstanceAttendance.column_userId + " ) ) "}
                        , AttendanceContract.InstanceAttendance.column_eventId + " = ? "
                        , new String[]{
                                event.getEventId()
                        }
                        , null
                        , null
                        , null
                );
    }
}
