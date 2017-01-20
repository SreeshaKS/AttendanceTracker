package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 17-01-2017.
 */

public class AttendanceInstance implements Parcelable {
    String userID;
    String eventID;
    String instanceID;
    int attendanceType;
    int isLate;
    String note;

    public AttendanceInstance(String userID, String eventID, String instanceID, int attendanceType, int isLate, String note) {
        this.userID = userID;
        this.eventID = eventID;
        this.instanceID = instanceID;
        this.attendanceType = attendanceType;
        this.isLate = isLate;
        this.note = note;
    }

    protected AttendanceInstance(Parcel in) {
        userID = in.readString();
        eventID = in.readString();
        instanceID = in.readString();
        attendanceType = in.readInt();
        isLate = in.readInt();
        note = in.readString();
    }

    public static final Creator<AttendanceInstance> CREATOR = new Creator<AttendanceInstance>() {
        @Override
        public AttendanceInstance createFromParcel(Parcel in) {
            return new AttendanceInstance(in);
        }

        @Override
        public AttendanceInstance[] newArray(int size) {
            return new AttendanceInstance[size];
        }
    };

    public String getEventID() {
        return eventID;
    }

    public String getUserID() {
        return userID;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public int getAttendanceType() {
        return attendanceType;
    }

    public int getIsLate() {
        return isLate;
    }

    public String getNote() {
        return note;
    }

    public void setAttendanceType(int type) {
        attendanceType = type;
    }

    public void setIsLate(int isLate) {
        this.isLate = isLate;
    }

    public void setNotes(String note) {
        this.note = note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userID);
        parcel.writeString(eventID);
        parcel.writeString(instanceID);
        parcel.writeInt(attendanceType);
        parcel.writeInt(isLate);
        parcel.writeString(note);
    }

    public static ContentValues getContentValues(AttendanceInstance i) {
        ContentValues values = new ContentValues();
        values.put(AttendanceContract.InstanceAttendance.column_eventId, i.getEventID());
        values.put(AttendanceContract.InstanceAttendance.column_userId, i.getUserID());
        values.put(AttendanceContract.InstanceAttendance.column_instanceId, i.getInstanceID());
        values.put(AttendanceContract.InstanceAttendance.column_attendanceType, i.getAttendanceType());
        values.put(AttendanceContract.InstanceAttendance.column_isLate, i.getIsLate());
        values.put(AttendanceContract.InstanceAttendance.column_note, i.getNote());
        return values;
    }

    public static AttendanceInstance getEventFromCursor(Cursor cursor) {
        return new AttendanceInstance(
                cursor.getString(cursor.getColumnIndex(AttendanceContract.InstanceAttendance.column_userId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.InstanceAttendance.column_eventId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.InstanceAttendance.column_instanceId))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.InstanceAttendance.column_attendanceType))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.InstanceAttendance.column_isLate))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.InstanceAttendance.column_note))
        );
    }
}
