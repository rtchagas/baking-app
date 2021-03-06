package com.rtchagas.udacity.bakingtime.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.core.Step;
import com.rtchagas.udacity.bakingtime.presentation.adapter.OnItemClickListener;
import com.rtchagas.udacity.bakingtime.presentation.adapter.StepsListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepsListActivity extends AppCompatActivity implements OnItemClickListener {

    public static final String EXTRA_RECIPE = "extra_recipe";
    private static final String STATE_SELECTED_STEP = "state_selected_step";
    private static final String FRAGMENT_STEP_DETAIL = "fragment_step_detail";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private Recipe mRecipe = null;

    private int mSelectedStep = -1;

    // All views should be declared here
    @BindView(R.id.recyclerview_steps)
    RecyclerView mRecyclerViewSteps;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        ButterKnife.bind(this);

        // Get the current recipe from the incoming intent.
        mRecipe = (Recipe) getIntent().getSerializableExtra(EXTRA_RECIPE);
        assert mRecipe != null;

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Set the toolbar's title according to this recipe.
        toolbar.setTitle(mRecipe.getName());
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.step_detail_container_composite) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Restore the selected step, if any.
        if (savedInstanceState != null) {
            mSelectedStep = savedInstanceState.getInt(STATE_SELECTED_STEP);
        }

        // If master/detail, show the first or any previously selected step
        if (mTwoPane) {
            // If no previous step, set the first
            if (mSelectedStep < 0) {
                mSelectedStep = 0;
            }
            showSelectedStep();
        }
        else {
            // If step list view only make sure there's no previous detail fragment
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_STEP_DETAIL);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commitNow();
            }
        }

        // Setup the RecyclerView
        setupRecyclerView();
        // Bind the adapter
        bindStepsAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Back/home button just finishes this activity
        if (android.R.id.home == item.getItemId()) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SELECTED_STEP, mSelectedStep);
    }

    @Override
    public void onItemClick(int position) {

        if (position < 0) {
            return;
        }

        // Do not replace the same step fragment
        if (mTwoPane && (mSelectedStep == position)) {
            return;
        }

        mSelectedStep = position;
        showSelectedStep();
    }

    private void showSelectedStep() {

        Step step = mRecipe.getSteps().get(mSelectedStep);

        if (mTwoPane) {

            // Create the fragment
            StepFragment fragment = StepFragment.newInstance(step);

            // Show the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container_composite, fragment, FRAGMENT_STEP_DETAIL)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(StepActivity.EXTRA_STEP_LIST, new ArrayList<>(mRecipe.getSteps()));
            intent.putExtra(StepActivity.EXTRA_STEP_INDEX, mSelectedStep);
            startActivity(intent);
        }
    }

    private void setupRecyclerView() {

        mRecyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewSteps.setHasFixedSize(true);

        // Set some nice item divider
        DividerItemDecoration dividerVertical = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerVertical.setDrawable(ContextCompat.getDrawable(this,
                R.drawable.sp_divider_line_horizontal));

        mRecyclerViewSteps.addItemDecoration(dividerVertical);
    }

    private void bindStepsAdapter() {
        StepsListAdapter stepsListAdapter = new StepsListAdapter(mRecipe, this);
        mRecyclerViewSteps.setAdapter(stepsListAdapter);

        if (mSelectedStep >= 0) {
            stepsListAdapter.setSelectedStep(mSelectedStep);
        }
    }
}
