package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceInstance;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.DataHandlers.User;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class AttendanceActivity extends AppCompatActivity {
    EventInstance mEventInstance;
    RecyclerView mAttendanceRV;
    AttendanceAdapter mAdapter;
    private static final int ATTENDANCE_LOADER_ID = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAttendanceRV = (RecyclerView) findViewById(R.id.attendanceRV);
        initializeRecyclerView();
        handleIntent();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportLoaderManager().initLoader(ATTENDANCE_LOADER_ID, null, loaderCallBacks);
    }

    private void initializeRecyclerView() {
        mAdapter = new AttendanceAdapter(AttendanceActivity.this, null);
        mAttendanceRV.setLayoutManager(new LinearLayoutManager(AttendanceActivity.this));
        mAttendanceRV.setAdapter(mAdapter);
    }

    private void handleIntent() {
        mEventInstance = getIntent().getParcelableExtra(EventInstance.EVENT_INSTANCE_PARCELABLE_KEY);
    }

    Menu activityMenu = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance_activity, menu);
        if (areParticipantsAdded)
            menu.getItem(1).setIcon(R.drawable.ic_account_edit_white_36dp);
        else
            menu.getItem(1).setIcon(R.drawable.ic_account_multiple_plus_white_36dp);

        return super.onCreateOptionsMenu(menu);
    }

    EditText usnPrefixEditText;
    EditText startRangeET;
    EditText endRangeET;

    boolean areParticipantsAdded = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addParticipantsMenu: {
                if (!areParticipantsAdded) {
                    createAndShowParticipantsAdditionDialog();
                } else {
                    createAndShowParticipantsEditDialog();
                }
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAndShowParticipantsAdditionDialog() {
        MaterialDialog participantFormDialog

                = new MaterialDialog.Builder(AttendanceActivity.this)
                .title("")
                .customView(R.layout.participants_addition_form, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String usnP = usnPrefixEditText
                                .getText().toString();
                        try {
                            int sR = Integer.parseInt(startRangeET.getText().toString());
                            int eR = Integer.parseInt(endRangeET.getText().toString());

                            if (!usnP.isEmpty()) {
                                AsyncParticipantAccumulator
                                        a = new AsyncParticipantAccumulator(
                                        new IAccumulatorResultCallBack() {
                                            @Override
                                            public void onParticipantsInserted() {
                                                Toast
                                                        .makeText(AttendanceActivity.this
                                                                , "Insertion Done"
                                                                , Toast.LENGTH_SHORT)
                                                        .show();
                                                getSupportLoaderManager()
                                                        .restartLoader(
                                                                ATTENDANCE_LOADER_ID
                                                                , null
                                                                , loaderCallBacks
                                                        );
                                            }
                                        }
                                        , usnP, sR, eR
                                );
                                a.execute();
                            } else {
                                Toast.makeText(AttendanceActivity.this
                                        , "Please do not leave any field empty"
                                        , Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(AttendanceActivity.this
                                    , "Please do not leave any field empty"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).build();
        View view = participantFormDialog.getCustomView();
        usnPrefixEditText
                = (EditText) view.findViewById(R.id.usnPrefixET);
        startRangeET
                = (EditText) view.findViewById(R.id.usnStartRangeET);
        endRangeET
                = (EditText) view.findViewById(R.id.usnEndRangeET);

        participantFormDialog.show();
    }

    private void createAndShowParticipantsEditDialog() {
        MaterialDialog participantFormDialog

                = new MaterialDialog.Builder(AttendanceActivity.this)
                .title("")
                .customView(R.layout.participants_addition_form, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).build();
        participantFormDialog.show();
    }

    private class AsyncParticipantAccumulator extends AsyncTask<String, String, String> {
        IAccumulatorResultCallBack resultCallback;
        String usnPrefix;
        int startRange;
        int endRange;

        AsyncParticipantAccumulator(IAccumulatorResultCallBack resultCallback,
                                    String usnPrefix, int startRange, int endRange) {
            this.resultCallback = resultCallback;
            this.usnPrefix = usnPrefix;
            this.startRange = startRange;
            this.endRange = endRange;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = Utility.getMaterialProgressDialog("Creating Users", "Please Wait", AttendanceActivity.this);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            ArrayList<User> l = new ArrayList<>();
            for (int i = startRange; i <= endRange; i++) {
                //Timestamp s = new Timestamp(Calendar.getInstance().getTime().getTime());
                User u = new User(
                        (usnPrefix + String.valueOf(i))
                        , null, null, 0, null

                );
                l.add(u);
                AttendanceActivity.this.getContentResolver()
                        .insert(AttendanceContract.Users.CONTENT_URI
                                , User.getContentValues(u));
            }
            for (User u : l) {
                AttendanceActivity.this.getContentResolver()
                        .insert(AttendanceContract.InstanceAttendance.CONTENT_URI
                                , AttendanceInstance
                                        .getContentValues(
                                                new AttendanceInstance(
                                                        u.getUserID()
                                                        , mEventInstance.getEventID()
                                                        , mEventInstance.getInstanceID()
                                                        , AttendanceContract.
                                                        InstanceAttendance.TYPE_UNAVAILABLE
                                                        , 0
                                                        , null
                                                )
                                        ));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            resultCallback.onParticipantsInserted();
            mProgressDialog.dismiss();
        }
    }

    MaterialDialog mProgressDialog;
    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            mProgressDialog = Utility.getMaterialProgressDialog("Loading Users", "Please Wait", AttendanceActivity.this);
            return new CursorLoader(
                    getBaseContext()
                    , AttendanceContract.InstanceAttendance.CONTENT_URI
                    , null
                    , AttendanceContract.InstanceAttendance.column_eventId + " = ? AND "
                    + AttendanceContract.InstanceAttendance.column_instanceId + " = ? "
                    , new String[]{mEventInstance.getEventID(), mEventInstance.getInstanceID()}
                    , null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (data.getCount() > 0) {
                Log.d("Thread", "Swapping cursor");
                areParticipantsAdded = true;
                invalidateOptionsMenu();
                Log.d("InstanceActivity", "Count" + data.getCount());
                mAdapter.swapCursor(data);
            } else {
                areParticipantsAdded = false;
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    };

    public interface IAccumulatorResultCallBack {
        void onParticipantsInserted();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopJobCruncher();

    }
}
