package com.sreesha.android.attendancetracker;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Sreesha on 19-01-2017.
 */

public class Utility {
    public static String getFormattedTimeStamp(String timestamp) {
        String formattedTime = null;

        return timestamp;
    }

    public static MaterialDialog getMaterialProgressDialog(String title, String message, Context c) {
        return new MaterialDialog.Builder(c)
                .title(title)
                .content(message)
                .progress(true, 0)
                .build();
    }
}
