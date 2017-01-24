package com.sreesha.android.attendancetracker.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sreesha.android.attendancetracker.R;

/**
 * Created by Sreesha on 24-01-2017.
 */

public class WidgetProvider extends AppWidgetProvider {

    void initializeList(RemoteViews remoteView, Context context, int stockWidgetId) {
        Intent adapterIntent = new Intent(context, WidgetService.class);
        adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, stockWidgetId);
        remoteView.setRemoteAdapter(R.id.eventListView, adapterIntent);
    }

    void updateStockWidget(Context context, AppWidgetManager appWidgetManager,
                           int eventWidgetId) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.event_widget_layout);
        initializeList(remoteView, context, eventWidgetId);
        appWidgetManager.updateAppWidget(eventWidgetId, remoteView);
        appWidgetManager.notifyAppWidgetViewDataChanged(eventWidgetId,
                R.id.eventListView);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] eventWidgetIdArray) {
        super.onUpdate(context, appWidgetManager, eventWidgetIdArray);
        for (int eventWidgetId : eventWidgetIdArray) {
            updateStockWidget(context, appWidgetManager, eventWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WidgetFactory.EVENT_CLICK_ACTION)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        super.onReceive(context, intent);
    }
}
