package com.sreesha.android.attendancetracker.DataHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 17-01-2017.
 */

public class User implements Parcelable {

    public static final String USER_PARCELABLE_KEY = "userParcelableKey";

    private String userID;
    private String userName;
    private String userEmail;

    private int isAdmin;
    private String profileLink;

    public User(String userID, String userName, String userEmail, int isAdmin, String profileLink) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.isAdmin = isAdmin;
        this.profileLink = profileLink;
    }


    protected User(Parcel in) {
        userID = in.readString();
        userName = in.readString();
        userEmail = in.readString();
        isAdmin = in.readInt();
        profileLink = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getIsAdmin() {
        return isAdmin;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getProfileLink() {
        return profileLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userID);
        parcel.writeString(userName);
        parcel.writeString(userEmail);
        parcel.writeInt(isAdmin);
        parcel.writeString(profileLink);
    }

    public static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(AttendanceContract.Users.column_userId, user.getUserID());
        values.put(AttendanceContract.Users.column_userName, user.getUserName());
        values.put(AttendanceContract.Users.column_userEmail, user.getUserEmail());
        values.put(AttendanceContract.Users.column_isAdmin, user.getIsAdmin());
        values.put(AttendanceContract.Users.column_linkToProfile, user.getProfileLink());
        return values;
    }

    public static User getUserFromCursor(Cursor cursor) {
        return new User(
                cursor.getString(cursor.getColumnIndex(AttendanceContract.Users.column_userId))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.Users.column_userName))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.Users.column_userEmail))
                , cursor.getInt(cursor.getColumnIndex(AttendanceContract.Users.column_isAdmin))
                , cursor.getString(cursor.getColumnIndex(AttendanceContract.Users.column_linkToProfile))
        );
    }

    @Override
    public String toString() {
        return String.valueOf((userID + userName + userEmail + isAdmin).hashCode());
    }
}
