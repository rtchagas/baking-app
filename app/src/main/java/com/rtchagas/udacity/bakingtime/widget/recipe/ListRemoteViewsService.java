package com.rtchagas.udacity.bakingtime.widget.recipe;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViewsService;


public class ListRemoteViewsService extends RemoteViewsService {

    public static Intent getRemoteServiceIntent(@NonNull Context context, int appWidgetId) {
        Intent intent = new Intent(context, ListRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return intent;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return new ListRemoteViewsFactory(getApplicationContext(), appWidgetId);
    }
}
