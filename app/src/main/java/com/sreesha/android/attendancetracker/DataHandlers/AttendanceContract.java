package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sreesha on 08-01-2017.
 */

public class AttendanceContract {
    public static final String CONTENT_AUTHORITY = "com.sreesha.android.attendancetracker";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USERS = "users";
    public static final String PATH_EVENTS = "events";
    public static final String PATH_EVENT_ASSOCIATION = "eventAssociation";
    public static final String PATH_EVENT_INSTANCE = "eventInstance";
    public static final String PATH_INSTANCE_ATTENDANCE = "attendanceInstance";

    public static final class Users implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String TABLE_USERS = "UsersTable";

        public static final String column_userId = "userId";
        public static final String column_userName = "userName";
        public static final String column_userEmail = "userEmail";
        public static final String column_isAdmin = "isAdmin";
        public static final String column_linkToProfile = "profileLink";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Events implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;

        public static final String TABLE_EVENTS = "EventsTable";

        /*Values for column_eventType*/
        public static final int TYPE_LECTURE = 0;
        public static final int TYPE_PRACTICAL = 1;
        public static final int TYPE_SEMINAR = 2;
        public static final int TYPE_WORKSHOP = 3;
        public static final int TYPE_EXAM = 4;

        public static final String column_userId = "userId";
        public static final String column_eventId = "eventId";
        public static final String column_eventType = "eventType";
        public static final String column_eventName = "eventName";
        public static final String column_numOfParticipants = "numberOfParticipants";
        public static final String column_numOfInstances = "numberOfInstances";
        public static final String column_creationDate = "dateOfCreation";

        public static Uri buildEventsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class EventAssociation implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT_ASSOCIATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT_ASSOCIATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT_ASSOCIATION;

        public static final String TABLE_EVENT_ASSOCIATION = "EventAssociationTable";

        public static final String column_userId = "userId";
        public static final String column_eventId = "eventId";
        public static final String column_isAdmin = "isAdmin";
    }

    public static final class EventInstance implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT_INSTANCE).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT_INSTANCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT_INSTANCE;

        public static final String TABLE_EVENT_INSTANCE = "EventInstanceTable";

        public static final String column_instanceId = "instanceId";
        public static final String column_eventId = "eventId";
        public static final String column_date = "date";
        public static final String column_duration = "duration";
        public static final String column_hour = "hour";
        public static final String column_note = "note";
    }

    public static final class InstanceAttendance implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INSTANCE_ATTENDANCE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTANCE_ATTENDANCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTANCE_ATTENDANCE;

        public static final String TABLE_INSTANCE_ATTENDANCE = "InstanceAttendanceTable";

        /*Values for column_attendanceType*/
        public static final int TYPE_PRESENT = 0;
        public static final int TYPE_ABSENT = 1;
        public static final int TYPE_UNAVAILABLE = 2;
        public static final int TYPE_MEDICAL_LEAVE = 3;

        public static final String column_instanceId = "instanceId";
        public static final String column_eventId = "eventId";
        public static final String column_userId = "userId";
        public static final String column_attendanceType = "attendanceType";
        public static final String column_isLate = "isLate";
        public static final String column_note = "note";
    }
}
