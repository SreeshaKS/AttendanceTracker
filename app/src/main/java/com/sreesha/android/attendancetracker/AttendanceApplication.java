package com.sreesha.android.attendancetracker;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Sreesha on 13-01-2017.
 */

public class AttendanceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
