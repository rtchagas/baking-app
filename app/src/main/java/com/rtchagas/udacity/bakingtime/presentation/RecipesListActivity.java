package com.rtchagas.udacity.bakingtime.presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.controller.OnRecipesResultListener;
import com.rtchagas.udacity.bakingtime.controller.RecipesController;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.presentation.adapter.RecipeListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipesDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipesListActivity extends AppCompatActivity implements OnRecipesResultListener{

    private static final String TAG = "BakingApp/" + RecipesListActivity.class.getSimpleName();

    private static final String STATE_RECIPE_LIST = "state_recipe_list";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private List<Recipe> mRecipeList = null;

    // All views should be declared here
    @BindView(R.id.recyclerview_recipes)
    RecyclerView mRecyclerViewRecipes;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.recipes_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Setup the RecyclerView
        mRecyclerViewRecipes.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewRecipes.setHasFixedSize(true);

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
    }

    @Override
    public void onResultError(@Nullable String message) {
        Log.w(TAG, message);
        Snackbar.make(mRecyclerViewRecipes, R.string.recipes_loading_error, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadRecipesAsync();
                    }
                })
                .show();
    }

    private void bindRecipesAdapter(List<Recipe> recipeList) {
        RecipeListAdapter recipeListAdapter = new RecipeListAdapter(this, recipeList, mTwoPane);
        mRecyclerViewRecipes.setAdapter(recipeListAdapter);
    }

    private void loadRecipesAsync() {
        RecipesController.getInstance().loadRecipesAsync(this);
    }

}
