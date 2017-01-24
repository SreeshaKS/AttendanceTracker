package com.sreesha.android.attendancetracker.Statistics;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 21-01-2017.
 */

public class UserStatData implements Parcelable {
    public static final String STAT_USER_PARCELABLE_KEY = "userStatParcelableKey";
    private long eventCount = 0, instanceCount = 0;


    private long type0 = 0, type1 = 0, type2 = 0, type3 = 0;
    private long[] monthWiseCount = new long[12];

    public UserStatData(long eventCount
            , long instanceCount, long type0
            , long type1, long type2, long type3
            , long[] monthWiseCount) {
        this.eventCount = eventCount;
        this.instanceCount = instanceCount;
        this.type0 = type0;
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.monthWiseCount = monthWiseCount;
    }

    protected UserStatData(Parcel in) {
        eventCount = in.readLong();
        instanceCount = in.readLong();
        type0 = in.readLong();
        type1 = in.readLong();
        type2 = in.readLong();
        type3 = in.readLong();
        monthWiseCount = in.createLongArray();
    }

    public static final Creator<UserStatData> CREATOR = new Creator<UserStatData>() {
        @Override
        public UserStatData createFromParcel(Parcel in) {
            return new UserStatData(in);
        }

        @Override
        public UserStatData[] newArray(int size) {
            return new UserStatData[size];
        }
    };

    public long getEventCount() {
        return eventCount;
    }

    public long getInstanceCount() {
        return instanceCount;
    }

    public long getType0() {
        return type0;
    }

    public long getType1() {
        return type1;
    }

    public long getType2() {
        return type2;
    }

    public long getType3() {
        return type3;
    }

    public long[] getMonthWiseCount() {
        return monthWiseCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(eventCount);
        parcel.writeLong(instanceCount);
        parcel.writeLong(type0);
        parcel.writeLong(type1);
        parcel.writeLong(type2);
        parcel.writeLong(type3);
        parcel.writeLongArray(monthWiseCount);
    }

    @Override
    public String toString() {
        String d = "Event Count : " + eventCount + " \n "
                + "Instance Count : " + instanceCount
                + " 0 : " + type0 + "\n"
                + " 1 : " + type1 + "\n"
                + " 2 : " + type2 + "\n"
                + " 3 : " + type3 + "\n";
        StringBuilder b = new StringBuilder(d);
        for (int i = 0; i < monthWiseCount.length; i++) {
            if (i == 6) {
                b.append("\n");
            }
            b.append(i + " : " + monthWiseCount[i] + ",");
        }
        return b.toString();
    }
}
