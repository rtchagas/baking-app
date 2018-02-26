package com.rtchagas.udacity.bakingtime.controller;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rtchagas.udacity.bakingtime.core.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RecipesController {

    private static RecipesAPI sRecipesAPI = null;
    private static RecipesController sInstance = null;

    private RecipesController() {
        // Singleton
    }

    public RecipesController getInstance() {

        if (sInstance == null) {
            sInstance = new RecipesController();
            initRecipesApi();
        }

        return sInstance;
    }

    private static void initRecipesApi() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RecipesAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        sRecipesAPI = retrofit.create(RecipesAPI.class);
    }

    public void loadRecipesAsync(@NonNull final OnRecipesResultListener resultListener) {

        Callback<List<Recipe>> resultCallback = new Callback<List<Recipe>>() {

            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.body() != null) {
                    resultListener.onResultReady(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                resultListener.onResultError(t.getMessage());
            }
        };

        sRecipesAPI.getRecipes().enqueue(resultCallback);
    }
}
