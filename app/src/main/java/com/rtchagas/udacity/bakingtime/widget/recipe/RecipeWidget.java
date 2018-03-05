package com.rtchagas.udacity.bakingtime.widget.recipe;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.presentation.RecipesListActivity;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link RecipesListActivity RecipesListActivity}
 */
public class RecipeWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        String name = WidgetDataPreferencesStore.loadRecipeName(context, appWidgetId);

        if (!TextUtils.isEmpty(name)) {

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            views.setTextViewText(R.id.text_recipe_widget_name, name);

            // Initialize the list view
            Intent intent = ListRemoteViewsService.getRemoteServiceIntent(context, appWidgetId);
            // Set a unique Uri to the intent to avoid
            // caching the same adapter for all widgets
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Bind the remote adapter
            views.setRemoteAdapter(R.id.listview_recipe_widget, intent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetDataPreferencesStore.deleteRecipeInfo(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

