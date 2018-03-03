package com.rtchagas.udacity.bakingtime.presentation;

import android.os.Bundle;
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
import com.rtchagas.udacity.bakingtime.presentation.adapter.StepsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepsListActivity extends AppCompatActivity {

    private static final String TAG = "BakingApp/" + StepsListActivity.class.getSimpleName();

    public static final String EXTRA_RECIPE = "extra_recipe";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private Recipe mRecipe = null;

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

        // Setup the RecyclerView
        setupRecyclerView();

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

    private void setupRecyclerView() {

        mRecyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewSteps.setHasFixedSize(true);

        // Set some nice item divider
        DividerItemDecoration dividerVertical = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        dividerVertical.setDrawable(ContextCompat.getDrawable(this, R.drawable.sp_divider_line_horizontal));

        mRecyclerViewSteps.addItemDecoration(dividerVertical);
    }

    private void bindStepsAdapter() {
        StepsListAdapter stepsListAdapter = new StepsListAdapter(this, mRecipe, mTwoPane);
        mRecyclerViewSteps.setAdapter(stepsListAdapter);
    }
}
