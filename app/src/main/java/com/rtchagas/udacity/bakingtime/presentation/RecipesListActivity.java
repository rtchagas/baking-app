package com.rtchagas.udacity.bakingtime.presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.controller.OnRecipesResultListener;
import com.rtchagas.udacity.bakingtime.controller.RecipesController;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.presentation.adapter.RecipesListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesListActivity extends AppCompatActivity implements OnRecipesResultListener {

    private static final String TAG = "BakingApp/" + StepsListActivity.class.getSimpleName();

    private static final String STATE_RECIPE_LIST = "state_recipe_list";

    private List<Recipe> mRecipeList = null;

    // All views should be declared here
    @BindView(R.id.recyclerview_recipes)
    RecyclerView mRecyclerViewRecipes;

    @BindView(R.id.progress_loading)
    ProgressBar mProgressLoading;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Setup the RecyclerView
        setupRecyclerView();

        // Restore any saved state
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_RECIPE_LIST)) {
                mRecipeList = (List<Recipe>) savedInstanceState.getSerializable(STATE_RECIPE_LIST);
            }
        }

        if (mRecipeList == null) {
            // Load the recipes in background
            loadRecipesAsync();
        }
        else {
            bindRecipesAdapter(mRecipeList);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecipeList != null) {
            outState.putSerializable(STATE_RECIPE_LIST, new ArrayList<>(mRecipeList));
        }
    }

    @Override
    public void onResultReady(@Nullable List<Recipe> list) {
        mRecipeList = list;
        bindRecipesAdapter(mRecipeList);
        setLoadingProgress(false);
    }

    @Override
    public void onResultError(@Nullable String message) {
        Log.w(TAG, message);
        Snackbar.make(mRecyclerViewRecipes, R.string.recipes_loading_error,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadRecipesAsync();
                    }
                })
                .show();
        setLoadingProgress(false);
    }

    private void setupRecyclerView() {

        mRecyclerViewRecipes.setLayoutManager(
                new GridLayoutManager(this,
                        getResources().getInteger(R.integer.grid_recipes_columns)));
        mRecyclerViewRecipes.setHasFixedSize(true);

        // Set some nice item dividers
        DividerItemDecoration dividerHorizontal = new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL);
        dividerHorizontal.setDrawable(ContextCompat.getDrawable(this, R.drawable.sp_divider_blank));

        DividerItemDecoration dividerVertical = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerVertical.setDrawable(ContextCompat.getDrawable(this, R.drawable.sp_divider_blank));

        mRecyclerViewRecipes.addItemDecoration(dividerHorizontal);
        mRecyclerViewRecipes.addItemDecoration(dividerVertical);
    }

    private void bindRecipesAdapter(List<Recipe> recipeList) {
        RecipesListAdapter recipeListAdapter = new RecipesListAdapter(recipeList);
        mRecyclerViewRecipes.setAdapter(recipeListAdapter);
    }

    private void loadRecipesAsync() {
        setLoadingProgress(true);
        RecipesController.getInstance().loadRecipesAsync(this);
    }

    private void setLoadingProgress(boolean isLoading) {
        mProgressLoading.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }
}
