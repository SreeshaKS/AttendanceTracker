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

    private int creationYear = 0;

    public EventInstance(String instanceID, String eventID, String timeStamp, String eventName
            , String startTimeStamp, String endTimeStamp, long duration, String note, int creationYear
            , int creationMonth, int creationDay, int creationHour, int creationMinute, int creationSecond
            , int startYear, int startMonth, int startDay, int startHour, int startMinute, int startSecond
            , int endYear, int endMonth, int endDay, int endHour, int endMinute, int endSecond) {
        this.instanceID = instanceID;
        this.eventID = eventID;
        this.timeStamp = timeStamp;
        this.eventName = eventName;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.duration = duration;
        this.note = note;
        this.type0Count = type0Count;
        this.type1Count = type1Count;
        this.type2Count = type2Count;
        this.type3Count = type3Count;
        this.creationYear = creationYear;
        this.creationMonth = creationMonth;
        this.creationDay = creationDay;
        this.creationHour = creationHour;
        this.creationMinute = creationMinute;
        this.creationSecond = creationSecond;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.startSecond = startSecond;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.endSecond = endSecond;
    }

    private int creationMonth = 0;
    private int creationDay = 0;
    private int creationHour = 0;
    private int creationMinute = 0;
    private int creationSecond = 0;
    private int startYear = 0, startMonth = 0, startDay = 0, startHour = 0, startMinute = 0, startSecond = 0;
    private int endYear = 0, endMonth = 0, endDay = 0, endHour = 0, endMinute = 0, endSecond = 0;


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
        timeStamp = in.readString();
        eventName = in.readString();
        startTimeStamp = in.readString();
        endTimeStamp = in.readString();
        duration = in.readLong();
        note = in.readString();
        creationYear = in.readInt();
        creationMonth = in.readInt();
        creationDay = in.readInt();
        creationHour = in.readInt();
        creationMinute = in.readInt();
        creationSecond = in.readInt();
        startYear = in.readInt();
        startMonth = in.readInt();
        startDay = in.readInt();
        startHour = in.readInt();
        startMinute = in.readInt();
        startSecond = in.readInt();
        endYear = in.readInt();
        endMonth = in.readInt();
        endDay = in.readInt();
        endHour = in.readInt();
        endMinute = in.readInt();
        endSecond = in.readInt();
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

    public int getCreationYear() {
        return creationYear;
    }

    public int getCreationMonth() {
        return creationMonth;
    }

    public int getCreationDay() {
        return creationDay;
    }

    public int getCreationHour() {
        return creationHour;
    }

    public int getCreationMinute() {
        return creationMinute;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getCreationSecond() {
        return creationSecond;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartSecond() {
        return startSecond;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public int getEndSecond() {
        return endSecond;
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


        values.put(AttendanceContract.EventInstance.column_creationYear, eI.getCreationYear());
        values.put(AttendanceContract.EventInstance.column_creationMonth, eI.getCreationMonth());
        values.put(AttendanceContract.EventInstance.column_creationDay, eI.getCreationDay());
        values.put(AttendanceContract.EventInstance.column_creationHour, eI.getCreationHour());
        values.put(AttendanceContract.EventInstance.column_creationMinute, eI.getCreationMinute());
        values.put(AttendanceContract.EventInstance.column_creationSecond, eI.getCreationSecond());
        values.put(AttendanceContract.EventInstance.column_startYear, eI.getStartYear());
        values.put(AttendanceContract.EventInstance.column_startMonth, eI.getStartMonth());
        values.put(AttendanceContract.EventInstance.column_startDay, eI.getStartDay());
        values.put(AttendanceContract.EventInstance.column_startHour, eI.getStartHour());
        values.put(AttendanceContract.EventInstance.column_startMinute, eI.getStartMinute());
        values.put(AttendanceContract.EventInstance.column_startSecond, eI.getStartSecond());
        values.put(AttendanceContract.EventInstance.column_endYear, eI.getEndYear());
        values.put(AttendanceContract.EventInstance.column_endMonth, eI.getEndMonth());
        values.put(AttendanceContract.EventInstance.column_endDay, eI.getEndDay());
        values.put(AttendanceContract.EventInstance.column_endHour, eI.getEndHour());
        values.put(AttendanceContract.EventInstance.column_endMinute, eI.getEndMinute());
        values.put(AttendanceContract.EventInstance.column_endSecond, eI.getEndSecond());

        return values;
    }

    public static EventInstance getEventFromCursor(Cursor cursor) {
        EventInstance e = new EventInstance(
                cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_instanceId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_eventId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_eventName))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationTimeStamp))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startTimeStamp))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endTimeStamp))
                , cursor.getLong(cursor.getColumnIndex(AttendanceContract.EventInstance.column_duration))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.EventInstance.column_note))

                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationYear))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationMonth))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationDay))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationHour))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationMinute))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_creationSecond))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startYear))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startMonth))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startDay))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startHour))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startMinute))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_startSecond))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endYear))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endMonth))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endDay))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endHour))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endMinute))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.EventInstance.column_endSecond))
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
        parcel.writeString(timeStamp);
        parcel.writeString(eventName);
        parcel.writeString(startTimeStamp);
        parcel.writeString(endTimeStamp);
        parcel.writeLong(duration);
        parcel.writeString(note);

        parcel.writeInt(creationYear);
        parcel.writeInt(creationMonth);
        parcel.writeInt(creationDay);
        parcel.writeInt(creationHour);
        parcel.writeInt(creationMinute);
        parcel.writeInt(creationSecond);
        parcel.writeInt(startYear);
        parcel.writeInt(startMonth);
        parcel.writeInt(startDay);
        parcel.writeInt(startHour);
        parcel.writeInt(startMinute);
        parcel.writeInt(startSecond);
        parcel.writeInt(endYear);
        parcel.writeInt(endMonth);
        parcel.writeInt(endDay);
        parcel.writeInt(endHour);
        parcel.writeInt(endMinute);
        parcel.writeInt(endSecond);
    }
}
