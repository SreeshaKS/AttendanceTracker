package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.R;

/**
 * Created by Sreesha on 14-01-2017.
 */

public class EventsRVAdapter extends CursorRecyclerViewAdapter<EventsRVAdapter.ViewHolder> {

    public EventsRVAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        Event event = Event.getEventFromContentValues(cursor);
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
        holder.creationDateTV.setText(event.getTimeStamp());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView numParticipantsTV;
        TextView numInstancesTV;
        TextView eventNameTV;
        TextView eventTypeTV;
        TextView creationDateTV;

        public ViewHolder(View itemView) {
            super(itemView);
            numParticipantsTV = (TextView) itemView.findViewById(R.id.numParticipantsTV);
            numInstancesTV = (TextView) itemView.findViewById(R.id.numInstancesTV);
            eventNameTV = (TextView) itemView.findViewById(R.id.eventNameTV);
            eventTypeTV = (TextView) itemView.findViewById(R.id.eventTypeTV);
            creationDateTV = (TextView) itemView.findViewById(R.id.creationDateTV);
        }
    }
}
