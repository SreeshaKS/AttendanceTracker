package com.sreesha.android.attendancetracker.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Sreesha on 24-01-2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetFactory(getApplicationContext(), intent);
    }
}