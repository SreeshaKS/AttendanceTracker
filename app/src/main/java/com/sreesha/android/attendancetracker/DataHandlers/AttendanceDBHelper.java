package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sreesha on 08-01-2017.
 */

public class AttendanceDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "attendanceData.db";
    private static final int DATABASE_VERSION = 15;


    public AttendanceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_USERS_TABLE
                = "Create Table " +
                AttendanceContract.Users.TABLE_USERS
                + "( "
                + AttendanceContract.Users._ID
                + " integer primary key autoincrement, "
                + AttendanceContract.Users.column_userId
                + " text , "
                + AttendanceContract.Users.column_userName
                + " text , "
                + AttendanceContract.Users.column_userEmail
                + " text , "
                + AttendanceContract.Users.column_linkToProfile
                + " text , "
                + AttendanceContract.Users.column_isAdmin
                + " boolean , " + " UNIQUE ( "+ AttendanceContract.Users.column_userId +" ) )";
        final String SQL_CREATE_EVENTS_TABLE
                = "Create Table " +
                AttendanceContract.Events.TABLE_EVENTS
                + "( "
                + AttendanceContract.Events._ID
                + " integer primary key autoincrement , "
                + AttendanceContract.Events.column_userId
                + " text , "
                + AttendanceContract.Events.column_eventId
                + " text , "
                + AttendanceContract.Events.column_creationDate
                + " timestamp , "
                + AttendanceContract.Events.column_eventType
                + " integer , "
                + AttendanceContract.Events.column_eventName
                + " text , "
                + AttendanceContract.Events.column_numOfParticipants
                + " integer , "
                + AttendanceContract.Events.column_numOfInstances
                + " integer , "
                + "foreign key( " + AttendanceContract.Events.column_userId + " ) "
                + " references "
                + AttendanceContract.Users.TABLE_USERS + " ( " + AttendanceContract.Users.column_userId + " ) "
                + " , unique ( "
                + AttendanceContract.Events.column_userId
                + " , "
                + AttendanceContract.Events.column_eventId + " ) ) ";

        final String SQL_CREATE_EVENT_INSTANCE_TABLE = "Create Table "
                + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                + " ( "
                + AttendanceContract.EventInstance._ID
                + " integer primary key autoincrement , "
                + AttendanceContract.EventInstance.column_instanceId
                + " text , "
                + AttendanceContract.EventInstance.column_eventId
                + " text , "
                + AttendanceContract.EventInstance.column_eventName
                + " text , "
                + AttendanceContract.EventInstance.column_creationTimeStamp
                + " text , "
                + AttendanceContract.EventInstance.column_startTimeStamp
                + " text , "
                + AttendanceContract.EventInstance.column_endTimeStamp
                + " text , "
                + AttendanceContract.EventInstance.column_duration
                + " integer , "
                + AttendanceContract.EventInstance.column_note
                + " text , "
                + AttendanceContract.EventInstance.column_type0Count
                + " integer , "
                + AttendanceContract.EventInstance.column_type1Count
                + " integer , "
                + AttendanceContract.EventInstance.column_type2Count
                + " integer , "
                + AttendanceContract.EventInstance.column_type3Count
                + " integer , "
                + " foreign key( " + AttendanceContract.EventInstance.column_eventId + " ) "
                + " references "
                + AttendanceContract.Events.TABLE_EVENTS + " ( " + AttendanceContract.Events.column_eventId + " ) "
                + " , unique ( "
                + AttendanceContract.EventInstance.column_eventId
                + " , "
                + AttendanceContract.EventInstance.column_instanceId + " ) ) ";
        final String SQL_CREATE_INSTANCE_ATTENDANCE = "Create Table "
                + AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                + " ( "
                + AttendanceContract.InstanceAttendance._ID
                + " integer primary key autoincrement , "
                + AttendanceContract.InstanceAttendance.column_instanceId
                + " text , "
                + AttendanceContract.InstanceAttendance.column_eventId
                + " text , "
                + AttendanceContract.InstanceAttendance.column_userId
                + " text , "
                + AttendanceContract.InstanceAttendance.column_attendanceType
                + " integer , "
                + AttendanceContract.InstanceAttendance.column_isLate
                + " integer , "
                + AttendanceContract.EventInstance.column_note
                + " text , "
                + " foreign key( " + AttendanceContract.InstanceAttendance.column_eventId + " ) "
                + " references "
                + AttendanceContract.Events.TABLE_EVENTS + " ( " + AttendanceContract.Events.column_eventId + " ) , "
                + " foreign key( " + AttendanceContract.InstanceAttendance.column_instanceId + " ) "
                + " references "
                + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE + " ( " + AttendanceContract.EventInstance.column_instanceId + " ) , "
                + " foreign key( " + AttendanceContract.InstanceAttendance.column_userId + " ) "
                + " references "
                + AttendanceContract.Users.TABLE_USERS + " ( " + AttendanceContract.Users.column_userId + " ) "
                + " , unique ( "
                + AttendanceContract.InstanceAttendance.column_eventId
                + " , "
                + AttendanceContract.InstanceAttendance.column_instanceId
                + " , "
                + AttendanceContract.InstanceAttendance.column_userId + " ) "
                +  " ) ";


        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_EVENT_INSTANCE_TABLE);
        db.execSQL(SQL_CREATE_INSTANCE_ATTENDANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" ALTER TABLE " + AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
        +" ADD COLUMN "+AttendanceContract.EventInstance.column_eventName);

    }
}
