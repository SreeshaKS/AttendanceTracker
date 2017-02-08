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
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;

import java.util.ArrayList;

/**
 * Created by Sreesha on 14-01-2017.
 */

public class EventsRVAdapter extends CursorRecyclerViewAdapter<EventsRVAdapter.ViewHolder> {

    private ArrayList<Event> selectedList;
    private ArrayList<String> selectedObjectStringDescriptorList;

    public EventsRVAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        selectedList = new ArrayList<>();
        selectedObjectStringDescriptorList = new ArrayList<>();
    }

    private CustomOnClickListener mClickListener;
    private CustomOnLongClickListener mLongClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    private Event currentInstance;

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {

        Event event = Event.getEventFromCursor(cursor);
        currentInstance = event;

        holder.eventNameTV.setText(event.getEventName());
        switch (event.getEventType()) {
            case AttendanceContract.Events.TYPE_LECTURE:
                holder.eventTypeTV
                        .setText(holder.itemView.getContext().getString(R.string.lecture_event_type));
                break;
            case AttendanceContract.Events.TYPE_PRACTICAL:
                holder.eventTypeTV.setText(holder.itemView.getContext().getString(R.string.practical_event_type));
                break;
            case AttendanceContract.Events.TYPE_SEMINAR:
                holder.eventTypeTV.setText(holder.itemView.getContext().getString(R.string.seminar_event_type));
                break;
            case AttendanceContract.Events.TYPE_WORKSHOP:
                holder.eventTypeTV.setText(holder.itemView.getContext().getString(R.string.workshop_event_type));
                break;
            case AttendanceContract.Events.TYPE_EXAM:
                holder.eventTypeTV.setText(holder.itemView.getContext().getString(R.string.exam_event_type));
                break;
        }
        Log.d("EventTS", event.getTimeStamp());
        holder.creationDateTV.setText(Utility.getFormattedTimeStamp(event.getTimeStamp()));
        holder.numInstancesTV.setText(event.getNumberOfInstances()
                + " " + holder.itemView.getContext().getString(R.string.instances));
        holder.numParticipantsTV.setText(event.getNumOfParticipants()
                + " " + holder.itemView.getContext().getString(R.string.participants));

        if (selectedObjectStringDescriptorList.contains(currentInstance.toString())) {
            holder.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
        } else {
            holder.selectionIndicatorLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private boolean isInSelectionMode = false;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnLongClickListener {
        RelativeLayout selectionIndicatorLayout;

        TextView numParticipantsTV;
        TextView numInstancesTV;
        TextView eventNameTV;
        TextView eventTypeTV;
        TextView creationDateTV;

        public ViewHolder(View itemView) {
            super(itemView);
            selectionIndicatorLayout = (RelativeLayout) itemView.findViewById(R.id.selectionIndicatorLayout);

            numParticipantsTV = (TextView) itemView.findViewById(R.id.numParticipantsTV);
            numInstancesTV = (TextView) itemView.findViewById(R.id.numInstancesTV);
            eventNameTV = (TextView) itemView.findViewById(R.id.eventNameTV);
            eventTypeTV = (TextView) itemView.findViewById(R.id.eventTypeTV);
            creationDateTV = (TextView) itemView.findViewById(R.id.creationDateTV);

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
                    Event aI
                            = Event.getEventFromCursor(c);
                    if (selectedObjectStringDescriptorList.contains(aI.toString())) {
                        Log.d("Selection", "Removing Object");
                        removeFromSelectionList(aI);
                        EventsRVAdapter.ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.TRANSPARENT);
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
                        EventsRVAdapter.ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
                        addToSelectionList(aI);
                    }
                } else {
                    if (mClickListener != null) {
                        //Create a new cursor object , initialize it to the cursor object used by the Recycler View
                        Cursor c = getCursor();
                        //Move to the tuple which represents the current adapter position
                        c.moveToPosition(getAdapterPosition());
                        mClickListener.onClick(
                                view
                                , getAdapterPosition()
                                , Event.getEventFromCursor(c)
                        );
                        //Move Back to zeroth position
                        c.moveToPosition(0);
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
                            Event.getEventFromCursor(c)
                    );
                    EventsRVAdapter.ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
                } else {
                    Log.d("Selection", "Already in Selection mode");
                }
                return true;
            }
        };

        private void addToSelectionList(Event i) {
            selectedList.add(i);
            selectedObjectStringDescriptorList.add(i.toString());
            if (mGlobalSelectionEventNotifier != null)
                mGlobalSelectionEventNotifier.OnSelectionEventTriggered(selectedList.size());
        }

        private void removeFromSelectionList(Event instance) {
            Event temp = null;
            for (Event i : selectedList) {
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

        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) {
                //Create a new cursor object , initialize it to the cursor object used by the Recycler View
                Cursor c = getCursor();
                //Move to the tuple which represents the current adapter position
                c.moveToPosition(getAdapterPosition());
                mLongClickListener.onLongClick(
                        view
                        , getAdapterPosition()
                        , Event.getEventFromCursor(c)
                );
                //Move Back to zeroth position
                c.moveToPosition(0);
            }
            return false;
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
                ArrayList<Event>
                        list = EventsRVAdapter.this.getSelectionList();

                for (Event i : list) {
                    db.delete(
                            AttendanceContract.Events.TABLE_EVENTS

                            , AttendanceContract.InstanceAttendance.column_eventId + " = ? "

                            , new String[]{
                                    i.getEventId()
                            }
                    );
                    db.delete(
                            AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE

                            , AttendanceContract.InstanceAttendance.column_eventId + " = ? "

                            , new String[]{
                                    i.getEventId()
                            }
                    );
                    db.delete(
                            AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE

                            , AttendanceContract.InstanceAttendance.column_eventId + " = ? "

                            , new String[]{
                                    i.getEventId()
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

    public ArrayList<Event> getSelectionList() {
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

    void setCustomOnClickListener(CustomOnClickListener listener) {
        this.mClickListener = listener;
    }

    void setCustomOnLongClickListener(CustomOnLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    interface CustomOnClickListener {
        void onClick(View view, int position, Event attendanceEvent);
    }

    interface CustomOnLongClickListener {
        void onLongClick(View view, int position, Event attendanceEvent);
    }
}
