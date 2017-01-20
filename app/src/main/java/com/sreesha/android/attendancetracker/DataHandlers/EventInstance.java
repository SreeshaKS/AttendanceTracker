package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 16-01-2017.
 */

public class EventInstance implements Parcelable {
    public static final String EVENT_INSTANCE_PARCELABLE_KEY = "eventInstanceParcelableKey";
    String instanceID;
    String eventID;
    String timeStamp;

    String startTimeStamp;
    String endTimeStamp;

    long duration;
    String note;

    public EventInstance(
            String instanceID, String eventID
            , String timeStamp
            , String startTimeStamp
            , String endTimeStamp
            , long duration, String note) {
        this.instanceID = instanceID;
        this.eventID = eventID;
        this.timeStamp = timeStamp;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.duration = duration;
        this.note = note;
    }

    protected EventInstance(Parcel in) {
        instanceID = in.readString();
        eventID = in.readString();
        timeStamp = in.readString();
        startTimeStamp = in.readString();
        endTimeStamp = in.readString();
        duration = in.readLong();
        note = in.readString();
    }

    public static final Creator<EventInstance> CREATOR = new Creator<EventInstance>() {
        @Override
        public EventInstance createFromParcel(Parcel in) {
            return new EventInstance(in);
        }

        @Override
        public EventInstance[] newArray(int size) {
            return new EventInstance[size];
        }
    };

    public String getInstanceID() {
        return instanceID;
    }

    public String getEventID() {
        return eventID;
    }

    public String getCreationTimeStamp() {
        return timeStamp;
    }

    public String getStartTimeStamp() {
        return startTimeStamp;
    }

    public String getEndTimeStamp() {
        return endTimeStamp;
    }

    public long getDuration() {
        return duration;
    }

    public String getNote() {
        return note;
    }

    public static ContentValues getContentValues(EventInstance eI) {
        ContentValues values = new ContentValues();
        values.put(AttendanceContract.EventInstance.column_eventId, eI.getEventID());
        values.put(AttendanceContract.EventInstance.column_instanceId, eI.getInstanceID());
        values.put(AttendanceContract.EventInstance.column_creationTimeStamp, eI.getCreationTimeStamp());
        values.put(AttendanceContract.EventInstance.column_startTimeStamp, eI.getStartTimeStamp());
        values.put(AttendanceContract.EventInstance.column_endTimeStamp, eI.getEndTimeStamp());
        values.put(AttendanceContract.EventInstance.column_duration, eI.getDuration());
        values.put(AttendanceContract.EventInstance.column_note, eI.getNote());
        return values;
    }

    public static EventInstance getEventFromCursor(Cursor cursor) {
        return new EventInstance(
                cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_instanceId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_eventId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationTimeStamp))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startTimeStamp))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endTimeStamp))
                , cursor.getLong(cursor.getColumnIndex(AttendanceContract.EventInstance.column_duration))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_note))
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(instanceID);
        parcel.writeString(eventID);
        parcel.writeString(timeStamp);
        parcel.writeString(startTimeStamp);
        parcel.writeString(endTimeStamp);
        parcel.writeLong(duration);
        parcel.writeString(note);
    }
}
