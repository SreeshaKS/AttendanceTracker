package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sreesha on 08-01-2017.
 */

public class AttendanceDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "attendanceData.db";
    private static final int DATABASE_VERSION = 6;


    public AttendanceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_USERS_TABLE
                = "Create Table " +
                AttendanceContract.Users.TABLE_USERS
                + "( "
                +AttendanceContract.Users._ID
                + " integer  , "
                + AttendanceContract.Users.column_userId
                + " integer primary key autoincrement , "
                + AttendanceContract.Users.column_userName
                + " text , "
                + AttendanceContract.Users.column_userEmail
                + " text , "
                + AttendanceContract.Users.column_linkToProfile
                + " text , "
                + AttendanceContract.Users.column_isAdmin
                + " boolean )";
        final String SQL_CREATE_EVENTS_TABLE
                = "Create Table " +
                AttendanceContract.Events.TABLE_EVENTS
                + "( "
                +AttendanceContract.Events._ID
                + " integer  , "
                + AttendanceContract.Events.column_userId
                + " integer , "
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
                + " , primary key ( "
                + AttendanceContract.Events.column_userId
                + " , "
                + AttendanceContract.Events.column_eventId + " ) ) ";
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + AttendanceContract.Events.TABLE_EVENTS);
        db.execSQL(" DROP TABLE IF EXISTS " + AttendanceContract.Users.TABLE_USERS);

        onCreate(db);
    }
}
