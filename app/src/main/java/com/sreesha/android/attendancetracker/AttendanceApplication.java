package com.sreesha.android.attendancetracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.facebook.stetho.Stetho;


/**
 * Created by Sreesha on 13-01-2017.
 */

public class AttendanceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Firebase.setAndroidContext(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    public static SharedPreferences getSharedPreferences(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c);
    }
}
