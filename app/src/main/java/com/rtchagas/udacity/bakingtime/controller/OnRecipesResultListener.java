package com.rtchagas.udacity.bakingtime.controller;

import android.support.annotation.Nullable;

import com.rtchagas.udacity.bakingtime.core.Recipe;

import java.util.List;

/**
 * Listener for handling Recipes API calls that return a list as result.
 */
public interface OnRecipesResultListener {

    void onResultReady(@Nullable List<Recipe> list);

    void onResultError(@Nullable String message);

}
