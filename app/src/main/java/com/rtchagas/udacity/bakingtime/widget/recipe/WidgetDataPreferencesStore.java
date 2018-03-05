package com.rtchagas.udacity.bakingtime.widget.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.rtchagas.udacity.bakingtime.core.Ingredient;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.util.SerializationUtil;

import java.util.ArrayList;
import java.util.List;

public final class WidgetDataPreferencesStore {

    private static final String WIDGET_PREFS_NAME = "com.rtchagas.udacity.bakingtime.widget.recipe.RecipeWidget";
    private static final String WIDGET_PREF_PREFIX_KEY = "appwidget_";
    private static final String WIDGET_PREF_SUFFIX_KEY_NAME = "_name";
    private static final String WIDGET_PREF_SUFFIX_KEY_INGREDIENTS = "_ingredients";

    // Write the Recipe info to the SharedPreferences object for the widget
    public static void saveRecipeInfo(@NonNull Context context, int appWidgetId,
                                      @NonNull Recipe recipe) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(WIDGET_PREFS_NAME, 0).edit();

        // Serialize the recipe's ingredients
        String serializedIngredients =
                SerializationUtil.toString(new ArrayList<>(recipe.getIngredients()));

        // Save the name
        prefs.putString(WIDGET_PREF_PREFIX_KEY + appWidgetId + WIDGET_PREF_SUFFIX_KEY_NAME,
                recipe.getName());

        // Save the ingredients
        prefs.putString(WIDGET_PREF_PREFIX_KEY + appWidgetId + WIDGET_PREF_SUFFIX_KEY_INGREDIENTS,
                serializedIngredients);

        prefs.apply();
    }

    @Nullable
    static String loadRecipeName(@NonNull Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(WIDGET_PREFS_NAME, 0);

        return prefs.getString(
                WIDGET_PREF_PREFIX_KEY + appWidgetId + WIDGET_PREF_SUFFIX_KEY_NAME,
                null);
    }

    @Nullable
    static List<Ingredient> loadRecipeIngredients(@NonNull Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(WIDGET_PREFS_NAME, 0);

        String serializedIngredients = prefs.getString(
                WIDGET_PREF_PREFIX_KEY + appWidgetId + WIDGET_PREF_SUFFIX_KEY_INGREDIENTS,
                null);

        if (!TextUtils.isEmpty(serializedIngredients)) {
            return SerializationUtil.fromString(serializedIngredients);
        }

        return null;
    }

    static void deleteRecipeInfo(@NonNull Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(WIDGET_PREFS_NAME, 0)
                .edit();
        prefs.remove(WIDGET_PREF_PREFIX_KEY + appWidgetId + WIDGET_PREF_SUFFIX_KEY_NAME);
        prefs.remove(WIDGET_PREF_PREFIX_KEY + appWidgetId + WIDGET_PREF_SUFFIX_KEY_INGREDIENTS);
        prefs.apply();
    }

}
