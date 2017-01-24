package com.sreesha.android.attendancetracker.Statistics;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sreesha.android.attendancetracker.DashBoardClasses.InstanceCreationActivity;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.R;


public class StatisticsFragment extends Fragment {
    private static final int USER_DATA_LOADER_ID = 99;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mStatisticsRV;
    private StatsRVAdapter mStatsRVAdapter;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(USER_DATA_LOADER_ID, null, loaderCallBacks);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(USER_DATA_LOADER_ID, null, loaderCallBacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        initializeViewElements(view);
        initializeRecyclerView();
        return view;
    }

    FloatingActionButton fab;

    boolean isInRefreshMode;
    ImageView mEmptyStatsIV;

    private void initializeViewElements(View view) {
        mStatisticsRV = (RecyclerView) view.findViewById(R.id.statisticsRV);
        mEmptyStatsIV = (ImageView) view.findViewById(R.id.emptyStatsIV);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStatsRVAdapter != null) {
                    MaterialDialog.Builder submit = new MaterialDialog.Builder(getActivity())
                            .title(R.string.search_by_usn)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .inputRange(2, 16)
                            .positiveText(R.string.submit)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    queryUSN = input.toString();
                                    getLoaderManager().restartLoader(USER_DATA_LOADER_ID, null, loaderCallBacks);
                                    Log.d("USNQuery", queryUSN);
                                }
                            });
                    submit.show();

                }
            }
        });
    }

    private void initializeRecyclerView() {
        mStatsRVAdapter = new StatsRVAdapter(getActivity(), null, getChildFragmentManager());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity());
        mStatisticsRV.setLayoutManager(layoutManager);
        mStatisticsRV.setAdapter(mStatsRVAdapter);
    }

    String queryUSN = null;

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getActivity()
                    , AttendanceContract.Users.CONTENT_URI
                    , null
                    ,
                    queryUSN == null ?
                            AttendanceContract.Users.column_isAdmin + " = 0 "

                            :
                            AttendanceContract.Users.column_userId + " = ? AND "
                                    + AttendanceContract.Users.column_isAdmin + " = 0 "
                    ,
                    queryUSN == null ?
                            null
                            :
                            new String[]{queryUSN}
                    , null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.d("StatData", "Count : " + data.getCount());
            if (data.getCount() > 0) {
                mEmptyStatsIV.setVisibility(View.GONE);
                mStatsRVAdapter.swapCursor(data);
            } else {
                mEmptyStatsIV.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mStatsRVAdapter.swapCursor(null);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
