package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceDBHelper;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceInstance;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Sreesha on 16-01-2017.
 */

public class EInstanceRVAdapter extends CursorRecyclerViewAdapter<EInstanceRVAdapter.ViewHolder> {
    private ArrayList<EventInstance> selectedList;
    private ArrayList<String> selectedObjectStringDescriptorList;

    public EInstanceRVAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        selectedList = new ArrayList<>();
        selectedObjectStringDescriptorList = new ArrayList<>();
    }

    private EInstanceRVAdapter.CustomOnClickListener mClickListener;
    private EInstanceRVAdapter.CustomOnLongClickListener mLongClickListener;

    @Override
    public EInstanceRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_instance_item, parent, false);

        return new ViewHolder(view);
    }

    private EventInstance currentInstance;

    @Override
    public void onBindViewHolder(EInstanceRVAdapter.ViewHolder holder, Cursor cursor) {
        EventInstance instance = EventInstance.getEventFromCursor(cursor);
        currentInstance = instance;

        Log.d("DateFormat", instance.getStartTimeStamp());
        holder.startDateTV.setText(Utility.getFormattedTimeStamp(instance.getStartTimeStamp()));
        holder.endDateTV.setText(Utility.getFormattedTimeStamp(instance.getEndTimeStamp()));

        holder.creationDateTV.setText(Utility.getFormattedTimeStamp(instance.getCreationTimeStamp()));
        holder.presentNumTV.setText(String.valueOf(instance.getType0Count()));
        holder.absentNumTV.setText(String.valueOf(instance.getType1Count()));
        holder.unkownNumTV.setText(String.valueOf(instance.getType2Count()));
        holder.medicalNumTV.setText(String.valueOf(instance.getType3Count()));

        holder.numParticipantTV
                .setText(
                        String.valueOf(
                                instance.getType0Count()
                                        + instance.getType1Count()
                                        + instance.getType2Count()
                                        + instance.getType3Count()
                        )
                );
        holder.eventNameTV.setText(instance.getEventName());

        if (selectedObjectStringDescriptorList.contains(currentInstance.toString())) {
            holder.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
        } else {
            holder.selectionIndicatorLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private boolean isInSelectionMode = false;


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        RelativeLayout selectionIndicatorLayout;

        TextView startDateTV;
        TextView endDateTV;
        TextView numParticipantTV;
        TextView creationDateTV;

        TextView presentNumTV;
        TextView absentNumTV;
        TextView unkownNumTV;
        TextView medicalNumTV;

        TextView eventNameTV;

        public ViewHolder(View itemView) {
            super(itemView);
            selectionIndicatorLayout = (RelativeLayout) itemView.findViewById(R.id.selectionIndicatorLayout);
            startDateTV = (TextView) itemView.findViewById(R.id.startTimeTV);
            endDateTV = (TextView) itemView.findViewById(R.id.endTimeTV);
            numParticipantTV = (TextView) itemView.findViewById(R.id.numParticipantsTV);
            creationDateTV = (TextView) itemView.findViewById(R.id.creationDateTV);
            eventNameTV = (TextView) itemView.findViewById(R.id.eventNameTV);

            presentNumTV = (TextView) itemView.findViewById(R.id.presentTV);
            absentNumTV = (TextView) itemView.findViewById(R.id.absentTV);
            unkownNumTV = (TextView) itemView.findViewById(R.id.unknownTV);
            medicalNumTV = (TextView) itemView.findViewById(R.id.medicalTV);

            itemView.setOnClickListener(selectionClickListener);
            itemView.setOnLongClickListener(selectionLongClickListener);
        }
        private View.OnClickListener selectionClickListener
                = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Selection", "Triggering OnClick Selection Mode : " + isInSelectionMode);
                if (isInSelectionMode) {
                    Log.d("Selection", "In Selection Mode");
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    EventInstance aI
                            = EventInstance.getEventFromCursor(c);
                    if (selectedObjectStringDescriptorList.contains(aI.toString())) {
                        Log.d("Selection", "Removing Object");
                        removeFromSelectionList(aI);
                        EInstanceRVAdapter.ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.TRANSPARENT);
                        if (selectedList.isEmpty()) {
                            Log.d("Selection", "NO More Selections / Returning back to Non-Selection Mode");
                            isInSelectionMode = false;
                            if (mGlobalSelectionEventNotifier != null)
                                mGlobalSelectionEventNotifier.OnSelectionStateChanged(false);
                        }
                    } else if (selectedList.isEmpty()) {
                        Log.d("Selection", "NO More Selections / Returning back to Non-Selection Mode");
                        isInSelectionMode = false;
                        if (mGlobalSelectionEventNotifier != null)
                            mGlobalSelectionEventNotifier.OnSelectionStateChanged(false);
                    } else {
                        Log.d("Selection", "NOt Selected / Adding to selection");
                        EInstanceRVAdapter.ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
                        addToSelectionList(aI);
                    }
                } else {
                    if (mClickListener != null) {
                        Log.d("InstanceCreation", "Adapter Position + " + getAdapterPosition());
                        Cursor c = getCursor();
                        c.moveToPosition(getAdapterPosition());
                        mClickListener.onClick(view, getAdapterPosition(), EventInstance.getEventFromCursor(c));
                        c.moveToFirst();
                    }
                }
            }
        };
        private View.OnLongClickListener selectionLongClickListener
                = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("Selection", "Triggering OnLongClick");
                if (!isInSelectionMode) {
                    if (mGlobalSelectionEventNotifier != null) {
                        mGlobalSelectionEventNotifier.OnSelectionStateChanged(true);
                    }
                    isInSelectionMode = true;
                    Log.d("Selection", "Changing to Selection mode Selection Mode : " + isInSelectionMode);
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    addToSelectionList(
                            EventInstance.getEventFromCursor(c)
                    );
                    EInstanceRVAdapter.ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
                } else {
                    Log.d("Selection", "Already in Selection mode");
                }
                return true;
            }
        };

        private void addToSelectionList(EventInstance i) {
            selectedList.add(i);
            selectedObjectStringDescriptorList.add(i.toString());
            if (mGlobalSelectionEventNotifier != null)
                mGlobalSelectionEventNotifier.OnSelectionEventTriggered(selectedList.size());
        }

        private void removeFromSelectionList(EventInstance instance) {
           EventInstance temp = null;
            for (EventInstance i : selectedList) {
                if (i.toString().equals(instance.toString())) {
                    temp = i;
                }
            }
            if (temp != null) {
                selectedList.remove(temp);
            }
            selectedObjectStringDescriptorList.remove(instance.toString());
            if (mGlobalSelectionEventNotifier != null)
                mGlobalSelectionEventNotifier.OnSelectionEventTriggered(selectedList.size());
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
    private AttendanceAdapter.SelectionEventNotifier mGlobalSelectionEventNotifier;

    public void clearSelectionList() {
        selectedList.clear();
        selectedObjectStringDescriptorList.clear();

        isInSelectionMode = false;
        if (mGlobalSelectionEventNotifier != null) {
            mGlobalSelectionEventNotifier.OnSelectionStateChanged(false);
            mGlobalSelectionEventNotifier.OnSelectedDeleteRequestComplete();
        }

        notifyDataSetChanged();
    }

    public void notifySelectionDeleteRequest(final Context c) {
        AsyncTask asyncTask = new AsyncTask<Object, Object, Object>() {
            SQLiteDatabase db;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AttendanceDBHelper a
                        = new AttendanceDBHelper(c);
                db = a.getReadableDatabase();
            }

            @Override
            protected String[] doInBackground(Object... params) {
                ArrayList<EventInstance>
                        list = EInstanceRVAdapter.this.getSelectionList();

                for (EventInstance i : list) {
                    db.delete(
                            AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE

                            , AttendanceContract.InstanceAttendance.column_eventId + " = ? AND "
                                    + AttendanceContract.InstanceAttendance.column_instanceId + " = ? "

                            , new String[]{
                                    i.getEventID()
                                    , i.getInstanceID()
                            }
                    );
                    db.delete(
                            AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE

                            , AttendanceContract.InstanceAttendance.column_eventId + " = ? AND "
                                    + AttendanceContract.InstanceAttendance.column_instanceId + " = ? "

                            , new String[]{
                                    i.getEventID()
                                    , i.getInstanceID()
                            }
                    );
                }
                list.clear();
                return null;
            }

            @Override
            protected void onPostExecute(Object s) {
                super.onPostExecute(s);
                db.close();
                clearSelectionList();
            }
        };
        asyncTask.execute(null, null, null);
    }

    public ArrayList<EventInstance> getSelectionList() {
        return selectedList;
    }

    public void registerSelectionEventNotifier(AttendanceAdapter.SelectionEventNotifier mGlobalSelectionEventNotifier) {
        this.mGlobalSelectionEventNotifier = mGlobalSelectionEventNotifier;
    }

    public void unRegisterSelectionEventNotifier() {
        this.mGlobalSelectionEventNotifier = null;
    }

    interface SelectionEventNotifier {
        void OnSelectionEventTriggered(int count);

        void OnSelectionStateChanged(boolean isInSelectionMode);
        void OnSelectedDeleteRequestComplete();
    }
    void setCustomOnClickListener(EInstanceRVAdapter.CustomOnClickListener listener) {
        this.mClickListener = listener;
    }

    void setCustomOnLongClickListener(EInstanceRVAdapter.CustomOnLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    interface CustomOnClickListener {
        void onClick(View view, int position, EventInstance eventInstance);
    }

    interface CustomOnLongClickListener {
        void onLongClick(View view, int position, EventInstance eventInstance);
    }
}
