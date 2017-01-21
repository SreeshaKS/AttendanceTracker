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
    private String instanceID;
    private String eventID;
    private String timeStamp;
    private String eventName;
    private String startTimeStamp;
    private String endTimeStamp;

    private long duration;
    private String note;


    private long type0Count;
    private long type1Count;
    private long type2Count;
    private long type3Count;

    public EventInstance(
            String instanceID, String eventID
            , String eventName
            , String timeStamp
            , String startTimeStamp
            , String endTimeStamp
            , long duration, String note) {
        this.instanceID = instanceID;
        this.eventID = eventID;
        this.eventName = eventName;
        this.timeStamp = timeStamp;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.duration = duration;
        this.note = note;

        type0Count = type1Count = type2Count = type3Count = 0;

    }

    protected EventInstance(Parcel in) {
        instanceID = in.readString();
        eventID = in.readString();
        eventName = in.readString();
        timeStamp = in.readString();
        startTimeStamp = in.readString();
        endTimeStamp = in.readString();
        duration = in.readLong();
        note = in.readString();
        type0Count = in.readLong();
        type1Count = in.readLong();
        type2Count = in.readLong();
        type3Count = in.readLong();
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
    public String getEventName() {
        return eventName;
    }
    public long getType0Count() {
        return type0Count;
    }

    public long getType1Count() {
        return type1Count;
    }

    public long getType2Count() {
        return type2Count;
    }

    public long getType3Count() {
        return type3Count;
    }

    public void setType0Count(long t) {
        type0Count = t;
    }

    public void setType1Count(long t) {
        type1Count = t;
    }

    public void setType2Count(long t) {
        type2Count = t;
    }

    public void setType3Count(long t) {
        type3Count = t;
    }

    public static ContentValues getContentValues(EventInstance eI) {
        ContentValues values = new ContentValues();
        values.put(AttendanceContract.EventInstance.column_eventId, eI.getEventID());
        values.put(AttendanceContract.EventInstance.column_instanceId, eI.getInstanceID());
        values.put(AttendanceContract.EventInstance.column_eventName, eI.getEventName());

        values.put(AttendanceContract.EventInstance.column_creationTimeStamp, eI.getCreationTimeStamp());
        values.put(AttendanceContract.EventInstance.column_startTimeStamp, eI.getStartTimeStamp());
        values.put(AttendanceContract.EventInstance.column_endTimeStamp, eI.getEndTimeStamp());
        values.put(AttendanceContract.EventInstance.column_duration, eI.getDuration());
        values.put(AttendanceContract.EventInstance.column_note, eI.getNote());
        return values;
    }

    public static EventInstance getEventFromCursor(Cursor cursor) {
        EventInstance e = new EventInstance(
                cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_instanceId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_eventId))
                ,cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_eventName))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationTimeStamp))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startTimeStamp))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endTimeStamp))
                , cursor.getLong(cursor.getColumnIndex(AttendanceContract.EventInstance.column_duration))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_note))
        );
        e.setType0Count(
                cursor
                        .getLong(cursor.getColumnIndex(AttendanceContract.EventInstance.column_type0Count)));
        e.setType1Count(
                cursor
                        .getLong(cursor.getColumnIndex(AttendanceContract.EventInstance.column_type1Count)));
        e.setType2Count(
                cursor
                        .getLong(cursor.getColumnIndex(AttendanceContract.EventInstance.column_type2Count)));
        e.setType3Count(
                cursor
                        .getLong(cursor.getColumnIndex(AttendanceContract.EventInstance.column_type3Count)));
        return e;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(instanceID);
        parcel.writeString(eventID);
        parcel.writeString(eventName);
        parcel.writeString(timeStamp);
        parcel.writeString(startTimeStamp);
        parcel.writeString(endTimeStamp);
        parcel.writeLong(duration);
        parcel.writeString(note);
        parcel.writeLong(type0Count);
        parcel.writeLong(type1Count);
        parcel.writeLong(type2Count);
        parcel.writeLong(type3Count);
    }
}
