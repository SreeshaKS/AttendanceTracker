package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sreesha on 08-01-2017.
 */

public class AttendanceDataDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "attendanceData.db";
    private static final int DATABASE_VERSION = 0;

    public AttendanceDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
