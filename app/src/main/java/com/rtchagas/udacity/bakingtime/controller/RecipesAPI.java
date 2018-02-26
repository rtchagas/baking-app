package com.rtchagas.udacity.bakingtime.controller;


import com.rtchagas.udacity.bakingtime.core.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipesAPI {

    String BASE_URL = "http://go.udacity.com/";

    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}
