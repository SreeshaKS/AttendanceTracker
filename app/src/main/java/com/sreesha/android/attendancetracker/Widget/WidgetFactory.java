package com.sreesha.android.attendancetracker.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.Event;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;


/**
 * Created by Sreesha on 24-01-2017.
 */

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String EVENT_CLICK_ACTION = "com.sreesha.android.attendancetracker.INSTANCE_DETAIL_ACTION";

    private Cursor mCursor;
    private Context mContext;
    private int mAttendanceWidgetId;

    public WidgetFactory(Context context, Intent intent) {
        mContext = context;
        mAttendanceWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor =
                mContext
                        .getContentResolver()
                        .query(
                                AttendanceContract.Events.CONTENT_URI
                                , null
                                , null
                                , null
                                , null
                        );
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_event_list_item);
        Log.d("WidgetDebug", "Cursor Count:" + mCursor.getCount() + "Position : " + position);
        if (mCursor.moveToPosition(position)) {
            Log.d("WidgetDebug", "Cursor Count:" + mCursor.getCount() + "Position : " + position);
            Event e = Event.getEventFromCursor(mCursor);
            remoteViews.setTextViewText(R.id.eventNameTV, e.getEventName());

            switch (e.getEventType()) {
                case AttendanceContract.Events.TYPE_LECTURE:
                    remoteViews.setTextViewText(R.id.eventTypeTV, mContext.getString(R.string.lecture_event_type));
                    break;
                case AttendanceContract.Events.TYPE_PRACTICAL:
                    remoteViews.setTextViewText(R.id.eventTypeTV, mContext.getString(R.string.practical_event_type));
                    break;
                case AttendanceContract.Events.TYPE_SEMINAR:
                    remoteViews.setTextViewText(R.id.eventTypeTV, mContext.getString(R.string.seminar_event_type));
                    break;
                case AttendanceContract.Events.TYPE_WORKSHOP:
                    remoteViews.setTextViewText(R.id.eventTypeTV, mContext.getString(R.string.workshop_event_type));
                    break;
                case AttendanceContract.Events.TYPE_EXAM:
                    remoteViews.setTextViewText(R.id.eventTypeTV, mContext.getString(R.string.exam_event_type));
                    break;
            }
            Log.d("WidgetEventTS", e.getTimeStamp());
            remoteViews.setTextViewText(R.id.creationDateTV, Utility.getFormattedTimeStamp(e.getTimeStamp()));
            remoteViews.setTextViewText(R.id.numInstancesTV,
                    e.getNumberOfInstances()
                            + mContext.getString(R.string.instances));
            remoteViews.setTextViewText(R.id.numParticipantsTV,
                    e.getNumOfParticipants()
                            + mContext.getString(R.string.participants));
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
