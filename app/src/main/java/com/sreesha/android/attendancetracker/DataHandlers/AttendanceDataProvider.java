package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Sreesha on 08-01-2017.
 */

public class AttendanceDataProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AttendanceDBHelper mOpenHelper;
    private static final SQLiteQueryBuilder sAttendanceBuilder;

    public static final int USERS = 10;
    public static final int EVENTS = 11;
    public static final int INSTANCE_EVENT = 12;
    public static final int INSTANCE_ATTENDANCE = 13;

    static {
        sAttendanceBuilder = new SQLiteQueryBuilder();
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AttendanceContract.CONTENT_AUTHORITY;

        //TODO:Add Uris to be recognized
        matcher.addURI(authority, AttendanceContract.PATH_USERS, USERS);
        matcher.addURI(authority, AttendanceContract.PATH_EVENTS, EVENTS);
        matcher.addURI(authority, AttendanceContract.PATH_EVENT_INSTANCE, INSTANCE_EVENT);
        matcher.addURI(authority, AttendanceContract.PATH_INSTANCE_ATTENDANCE, INSTANCE_ATTENDANCE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AttendanceDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        Log.d("Loader", "Querying DataBase");
        switch (sUriMatcher.match(uri)) {
            case USERS:
                retCursor = mOpenHelper
                        .getReadableDatabase()
                        .query(AttendanceContract.Users.TABLE_USERS,
                                projection,
                                selection,
                                selectionArgs, null, null, sortOrder);
                break;
            case EVENTS:
                Log.d("Loader", "Querying DataBase");
                retCursor = mOpenHelper
                        .getReadableDatabase()
                        .query(AttendanceContract.Events.TABLE_EVENTS,
                                projection,
                                selection,
                                selectionArgs, null, null, sortOrder);
                break;
            case INSTANCE_EVENT:
                Log.d("Loader", "Querying Event Instance Table");
                retCursor = mOpenHelper
                        .getReadableDatabase()
                        .query(AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE,
                                projection,
                                selection,
                                selectionArgs, null, null, sortOrder);

                break;
            case INSTANCE_ATTENDANCE:
                Log.d("Loader", "Querying Event Instance Table");
                retCursor = mOpenHelper
                        .getReadableDatabase()
                        .query(AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE,
                                projection,
                                selection,
                                selectionArgs, null, null, sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        /**
         * Register to watch a content URI for changes. This can be the URI of a specific data row (for
         * example, "content://my_provider_type/23"), or a a generic URI for a content type.
         *
         * @param cr The content resolver from the caller's context. The listener attached to
         * this resolver will be notified.
         * @param uri The content URI to watch.
         */
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        Log.d("Loader", "getType");
        switch (match) {
            //TODO: Add Code to match Uris and remove the return null statement
            case USERS:
                return AttendanceContract.Users.CONTENT_TYPE;
            case EVENTS:
                Log.d("Loader", "Events Matched");
                return AttendanceContract.Events.CONTENT_TYPE;
            case INSTANCE_EVENT:
                return AttendanceContract.EventInstance.CONTENT_TYPE;
            case INSTANCE_ATTENDANCE:
                return AttendanceContract.InstanceAttendance.CONTENT_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {
            case USERS: {
                try {
                    long _id = -1;
                    _id = db.insert(AttendanceContract.Users.TABLE_USERS, null, contentValues);

                    if (_id > 0) {
                        returnUri = AttendanceContract.Users.buildUserUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                    Log.e("SQLExceptionLog", "Unique ConstraintFailed , User Already Exists");
                }
                break;
            }
            case EVENTS: {
                long _id = db.insert(AttendanceContract.Events.TABLE_EVENTS, null, contentValues);
                if (_id > 0) {
                    returnUri = AttendanceContract.Events.buildEventsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case INSTANCE_EVENT: {
                long _id = db.insert(AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE, null, contentValues);
                if (_id > 0) {
                    returnUri = AttendanceContract.Events.buildEventsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case INSTANCE_ATTENDANCE: {
                try {
                    long _id = db.insert(AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE, null, contentValues);
                    if (_id > 0) {
                        returnUri = AttendanceContract.Events.buildEventsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d("SQLException", "INSTANCE Attendance , Unique Constraint Failed , User Already Exists");
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case USERS:
                rowsDeleted = db.delete(
                        AttendanceContract.Users.TABLE_USERS, selection, selectionArgs);
                break;
            case EVENTS:
                rowsDeleted = db.delete(
                        AttendanceContract.Events.TABLE_EVENTS, selection, selectionArgs);
                break;
            case INSTANCE_EVENT:
                rowsDeleted = db.delete(
                        AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE, selection, selectionArgs);
                break;
            case INSTANCE_ATTENDANCE:
                rowsDeleted = db.delete(
                        AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case USERS:
                rowsUpdated = db.update(AttendanceContract.Users.TABLE_USERS, contentValues, selection,
                        selectionArgs);
                break;
            case EVENTS:
                rowsUpdated = db.update(AttendanceContract.Events.TABLE_EVENTS, contentValues, selection,
                        selectionArgs);
                break;
            case INSTANCE_EVENT:
                rowsUpdated = db.update(AttendanceContract.EventInstance.TABLE_EVENT_INSTANCE
                        , contentValues
                        , selection
                        , selectionArgs);
                break;
            case INSTANCE_ATTENDANCE:
                try {
                    rowsUpdated =
                            db.update(
                                    AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE
                                    , contentValues
                                    , selection
                                    , selectionArgs
                            );

                    Log.d("Thread", "RowsUpdated Num : " + rowsUpdated);
                } catch (SQLiteException e) {
                    Log.d("Thread", "Exception Raised" + e.getMessage());
                    //TODO : Investigate this exception triggered from @link{AttendanceAdapter}
                    e.printStackTrace();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
