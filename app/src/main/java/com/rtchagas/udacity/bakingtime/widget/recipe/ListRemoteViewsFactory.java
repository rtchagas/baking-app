package com.rtchagas.udacity.bakingtime.widget.recipe;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Ingredient;

import java.util.List;

public final class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private List<Ingredient> mIngredients;

    ListRemoteViewsFactory(Context context, int appWidgetId) {
        mContext = context;
        mAppWidgetId = appWidgetId;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Load the ingredients list from preferences
        mIngredients = WidgetDataPreferencesStore.loadRecipeIngredients(mContext, mAppWidgetId);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return (mIngredients == null ? 0 : mIngredients.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews row = new RemoteViews(mContext.getPackageName(),
                R.layout.item_ingredient_single_item);

        Ingredient ingredient = mIngredients.get(position);

        // Just format the ingredient as a single string and bind it.
        row.setTextViewText(R.id.text_ingredient_single,
                Ingredient.getFormattedString(mContext, ingredient));

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
