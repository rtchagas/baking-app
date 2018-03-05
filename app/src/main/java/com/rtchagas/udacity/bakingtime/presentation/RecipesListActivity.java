package com.rtchagas.udacity.bakingtime.presentation;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
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
import com.rtchagas.udacity.bakingtime.presentation.adapter.OnItemClickListener;
import com.rtchagas.udacity.bakingtime.presentation.adapter.RecipesListAdapter;
import com.rtchagas.udacity.bakingtime.widget.recipe.RecipeWidget;
import com.rtchagas.udacity.bakingtime.widget.recipe.WidgetDataPreferencesStore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesListActivity extends AppCompatActivity
        implements OnRecipesResultListener, OnItemClickListener {

    private static final String TAG = "BakingApp/" + StepsListActivity.class.getSimpleName();

    private static final String STATE_RECIPE_LIST = "state_recipe_list";

    private List<Recipe> mRecipeList = null;

    /**
     * Tells if this activity is being used to configure
     * the {@link RecipeWidget RecipeWidget} AppWidget.
     */
    private boolean mIsWidgetConfig = false;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

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
        toolbar.setTitle(getTitle());

        // Initialize the widget configuration
        if ("android.appwidget.action.APPWIDGET_CONFIGURE".equals(getIntent().getAction())) {

            mIsWidgetConfig = true;

            // Set the result to CANCELED.  This will cause the widget host to cancel
            // out of the widget placement if the user presses the back button.
            setResult(RESULT_CANCELED);

            // Find the widget id from the intent.
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }

            // If this activity was started with an intent without an app widget ID, finish with an error.
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
                return;
            }

            // Set the custom title
            toolbar.setTitle(R.string.recipe_widget_configure_choose);
        }

        // Set the toolbar
        setSupportActionBar(toolbar);

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

    @Override
    public void onItemClick(int position) {

        Recipe item = mRecipeList.get(position);

        if (!mIsWidgetConfig) {
            Intent intent = new Intent(this, StepsListActivity.class);
            intent.putExtra(StepsListActivity.EXTRA_RECIPE, item);
            startActivity(intent);
        }
        else {
            configureWidgetAndFinish(item);
        }
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
        RecipesListAdapter recipeListAdapter = new RecipesListAdapter(recipeList, this);
        mRecyclerViewRecipes.setAdapter(recipeListAdapter);
    }

    private void loadRecipesAsync() {
        setLoadingProgress(true);
        RecipesController.getInstance().loadRecipesAsync(this);
    }

    private void setLoadingProgress(boolean isLoading) {
        mProgressLoading.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    // Widget related methods

    private void configureWidgetAndFinish(Recipe recipe) {

        // Store the recipe locally
        WidgetDataPreferencesStore.saveRecipeInfo(this, mAppWidgetId, recipe);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RecipeWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
