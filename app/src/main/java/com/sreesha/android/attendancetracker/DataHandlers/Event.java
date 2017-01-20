package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 13-01-2017.
 */

public class Event implements Parcelable {
    public static final String timeStamp_pattern = "yyyy-mm-dd hh:mm:ss.[fff...]";
    private String eventId;
    private String userId;
    private String eventName;
    private int eventType;
    private long numOfParticipants;
    private long numberOfInstances;
    private String timeStamp;

    public Event(String eventId, String userId, String eventName, int eventType
            , long numOfParticipants, long numberOfInstances, String timeStamp) {
        this.eventId = eventId;
        this.userId = userId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.numOfParticipants = numOfParticipants;
        this.numberOfInstances = numberOfInstances;
        this.timeStamp = timeStamp;
    }

    protected Event(Parcel in) {
        eventId = in.readString();
        userId = in.readString();
        eventName = in.readString();
        eventType = in.readInt();
        numOfParticipants = in.readLong();
        numberOfInstances = in.readLong();
        timeStamp = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getEventName() {
        return eventName;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public int getEventType() {
        return eventType;
    }

    public long getNumOfParticipants() {
        return numOfParticipants;
    }

    public long getNumberOfInstances() {
        return numberOfInstances;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setNumberOfInstances(long numOfInstances) {
        this.numberOfInstances = numOfInstances;
    }

    public static ContentValues getContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(AttendanceContract.Events.column_eventId, event.getEventId());
        values.put(AttendanceContract.Events.column_userId, event.getUserId());
        values.put(AttendanceContract.Events.column_eventName, event.getEventName());
        values.put(AttendanceContract.Events.column_eventType, event.getEventType());
        values.put(AttendanceContract.Events.column_numOfInstances, event.getNumberOfInstances());
        values.put(AttendanceContract.Events.column_numOfParticipants, event.getNumOfParticipants());
        values.put(AttendanceContract.Events.column_creationDate, event.getTimeStamp());
        return values;
    }

    public static Event getEventFromCursor(Cursor cursor) {
        return new Event(
                cursor.getString(cursor.getColumnIndex(AttendanceContract.Events.column_eventId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.Events.column_userId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.Events.column_eventName))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.Events.column_eventType))
                , cursor.getLong(cursor.getColumnIndex(AttendanceContract.Events.column_numOfParticipants))
                , cursor.getLong(cursor.getColumnIndex(AttendanceContract.Events.column_numOfInstances))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.Events.column_creationDate))
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventId);
        parcel.writeString(userId);
        parcel.writeString(eventName);
        parcel.writeInt(eventType);
        parcel.writeLong(numOfParticipants);
        parcel.writeLong(numberOfInstances);
        parcel.writeString(timeStamp);
    }
}
