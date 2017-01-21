package com.sreesha.android.attendancetracker;

import android.content.Context;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.sreesha.android.attendancetracker.DataHandlers.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sreesha on 19-01-2017.
 */

public class Utility {
    public static String getFormattedTimeStamp(String timestamp) {
        SimpleDateFormat f = new SimpleDateFormat(Event.timeStamp_pattern);

        try {
            String time = timestamp;
            time = time.replace(" ", "T");
            Date d = f.parse(time);
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static MaterialDialog getMaterialProgressDialog(String title, String message, Context c) {
        return new MaterialDialog.Builder(c)
                .title(title)
                .content(message)
                .progress(true, 0)
                .build();
    }

    public static void loadImage(Context c, ImageView v, int resId) {
        Picasso.with(c)
                .load(resId)
                .into(v);
    }

    public static void loadImage(Context c, ImageView v, String url) {
        Picasso.with(c)
                .load(url)
                .into(v);
    }
}
