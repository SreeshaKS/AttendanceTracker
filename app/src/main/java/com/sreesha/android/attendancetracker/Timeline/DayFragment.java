package com.sreesha.android.attendancetracker.Timeline;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceDBHelper;
import com.sreesha.android.attendancetracker.R;

public class DayFragment extends Fragment {
    private static final String ARG_YEAR = "yearArg";
    private static final String ARG_MONTH = "monthArg";
    private static final String ARG_DAY = "dayArg";

    private String mParam1;
    private String mParam2;

    RecyclerView mTimelineRV;
    TimeLineRVAdapter mTimeLineRVAdapter;

    private OnFragmentInteractionListener mListener;

    int day, month, year;

    public DayFragment() {
        // Required empty public constructor
    }


    public static DayFragment newInstance(int day, int month, int year) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY, day);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_YEAR, year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getInt(ARG_DAY);
            month = getArguments().getInt(ARG_MONTH);
            year = getArguments().getInt(ARG_YEAR);
        }
    }

    Cursor c;
    SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        AttendanceDBHelper dH = new AttendanceDBHelper(getActivity());
        db = dH.getReadableDatabase();

        initializeViewElements(view);
        setUpRecyclerView();


        c = db.query(AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE, null, null, null, null, null, null);
        Log.d("Cursor", "" + c.getCount() + " Day : " + day);

        return view;
    }

    private void initializeViewElements(View view) {
        mTimelineRV = (RecyclerView) view.findViewById(R.id.timelineRV);
    }

    private void setUpRecyclerView() {
        mTimeLineRVAdapter = new TimeLineRVAdapter(db, month, year, day);
        mTimelineRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTimelineRV.setAdapter(mTimeLineRVAdapter);
    }

    public void setMonthYear(int month, int year) {
        if (mTimeLineRVAdapter != null) {
            mTimeLineRVAdapter.setMonthYear(month, year);
            mTimeLineRVAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        c.close();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
