package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.Context;
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
import android.widget.TextView;

import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;

public class AttendanceFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mEventInstanceRV;
    private CombinedAtAdapter combinedAtAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        initializeViewElements(view);
        initializeRecyclerView();
        initializeListeners();
        return view;
    }

    void initializeViewElements(View view) {
        mEventInstanceRV = (RecyclerView) view.findViewById(R.id.combinedAttendanceRV);
    }

    private void initializeListeners() {
    }

    private void initializeRecyclerView() {
        combinedAtAdapter = new CombinedAtAdapter(getActivity(), null);
        mEventInstanceRV.setLayoutManager(
                new LinearLayoutManager(getActivity())
        );
        mEventInstanceRV.setAdapter(combinedAtAdapter);
    }

    private final int COMBINED_AT_LOADER_ID = 1001;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(COMBINED_AT_LOADER_ID, null, loaderCallBacks);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d("Loader", "Calling onCreateLoader");

            return new CursorLoader(
                    getActivity()
                    , AttendanceContract.EventInstance.CONTENT_URI
                    , null
                    , null
                    , null
                    , AttendanceContract.EventInstance.column_creationTimeStamp + " DESC"
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() > 0) {
                Log.d("InstanceActivity", "Count" + data.getCount());
                combinedAtAdapter.swapCursor(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            combinedAtAdapter.swapCursor(null);
        }
    };

    private class CombinedAtAdapter extends CursorRecyclerViewAdapter<CombinedAtAdapter.ViewHolder> {
        public CombinedAtAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        private CustomOnClickListener mClickListener;
        private CustomOnLongClickListener mLongClickListener;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_instance_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
            EventInstance instance = EventInstance.getEventFromCursor(cursor);
            Log.d("DateFormat", instance.getStartTimeStamp());
            holder.startDateTV.setText(Utility.getFormattedTimeStamp(instance.getStartTimeStamp()));
            holder.endDateTV.setText(Utility.getFormattedTimeStamp(instance.getEndTimeStamp()));

            holder.creationDateTV.setText(Utility.getFormattedTimeStamp(instance.getCreationTimeStamp()));
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView startDateTV;
            TextView endDateTV;
            TextView numParticipantTV;
            TextView creationDateTV;

            TextView presentNumTV;
            TextView absentNumTV;
            TextView unkownNumTV;
            TextView medicalNumTV;


            public ViewHolder(View itemView) {
                super(itemView);
                startDateTV = (TextView) itemView.findViewById(R.id.startTimeTV);
                endDateTV = (TextView) itemView.findViewById(R.id.endTimeTV);
                numParticipantTV = (TextView) itemView.findViewById(R.id.numParticipantsTV);
                creationDateTV = (TextView) itemView.findViewById(R.id.creationDateTV);

                presentNumTV = (TextView) itemView.findViewById(R.id.presentTV);
                absentNumTV = (TextView) itemView.findViewById(R.id.absentTV);
                unkownNumTV = (TextView) itemView.findViewById(R.id.unknownTV);
                medicalNumTV = (TextView) itemView.findViewById(R.id.medicalTV);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    Log.d("InstanceCreation", "Adapter Position + " + getAdapterPosition());
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    mClickListener.onClick(view, getAdapterPosition(), EventInstance.getEventFromCursor(c));
                    c.moveToFirst();
                }
            }

            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null) {
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    mLongClickListener.onLongClick(view, getAdapterPosition(), EventInstance.getEventFromCursor(c));
                    c.moveToFirst();
                }
                return true;
            }
        }

        void setCustomOnClickListener(CustomOnClickListener listener) {
            this.mClickListener = listener;
        }

        void setCustomOnLongClickListener(CustomOnLongClickListener listener) {
            this.mLongClickListener = listener;
        }

    }

    interface CustomOnClickListener {
        void onClick(View view, int position, EventInstance eventInstance);
    }

    interface CustomOnLongClickListener {
        void onLongClick(View view, int position, EventInstance eventInstance);
    }
}
