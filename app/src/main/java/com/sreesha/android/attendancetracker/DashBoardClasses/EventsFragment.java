package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.R;

public class EventsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String EVENT_OBJECT_PARCELABLE_KEY = "eventParcelableStringKey";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final int EVENTS_LOADER_ID = 1;

    RecyclerView mRecyclerView;
    EventsRVAdapter mEventsAdapter;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(EVENTS_LOADER_ID, null, loaderCallBacks);
    }

    boolean isStateRestored = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            isStateRestored = true;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStateRestored)
            getLoaderManager().initLoader(EVENTS_LOADER_ID, null, loaderCallBacks);
        else {
            getLoaderManager().restartLoader(EVENTS_LOADER_ID, null, loaderCallBacks);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        initializeViewElements(view);
        return view;
    }

    ImageView mEmptyEventsIV;

    private void initializeViewElements(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.eventsRecyclerView);
        mEmptyEventsIV = (ImageView) view.findViewById(R.id.emptyEventsIV);
        initializeRecyclerView();
        initializeListeners();
    }


    private void initializeRecyclerView() {
        mEventsAdapter = new EventsRVAdapter(getActivity(), null);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mEventsAdapter);
    }

    private void initializeListeners() {
        mEventsAdapter.setCustomOnClickListener(new EventsRVAdapter.CustomOnClickListener() {
            @Override
            public void onClick(View view, int position, Event attendanceEvent) {
                Intent intent = new Intent(getActivity(), InstanceCreationActivity.class);
                intent.putExtra(EVENT_OBJECT_PARCELABLE_KEY, attendanceEvent);
                startActivity(intent);
            }
        });
        mEventsAdapter.setCustomOnLongClickListener(new EventsRVAdapter.CustomOnLongClickListener() {
            @Override
            public void onLongClick(View view, int position, Event attendanceEvent) {
                //TODO : Inflate Appropriate toolbar menu items !
            }
        });
    }

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

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d("Loader", "onCreateLoader");
            return new CursorLoader(
                    getActivity()
                    , AttendanceContract.Events.CONTENT_URI
                    , null
                    , null
                    , null
                    , AttendanceContract.Events.column_creationDate + " DESC"
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data .getCount()>0) {
                mEmptyEventsIV.setVisibility(View.GONE);
                Log.d("Loader", "onLoadFinished" + data.getCount());
            }else {
                mEmptyEventsIV.setVisibility(View.VISIBLE);
                Log.d("Loader", "onLoadFinished : Null Data");
            }
            mEventsAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mEventsAdapter.swapCursor(null);
        }
    };

    public void OnEventCreated(Event event) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
