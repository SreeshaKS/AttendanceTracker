package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.DataHandlers.EventInstance;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;

import java.util.zip.Inflater;

/**
 * Created by Sreesha on 16-01-2017.
 */

public class EInstanceRVAdapter extends CursorRecyclerViewAdapter<EInstanceRVAdapter.ViewHolder> {

    public EInstanceRVAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    private EInstanceRVAdapter.CustomOnClickListener mClickListener;
    private EInstanceRVAdapter.CustomOnLongClickListener mLongClickListener;

    @Override
    public EInstanceRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_instance_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EInstanceRVAdapter.ViewHolder holder, Cursor cursor) {
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
