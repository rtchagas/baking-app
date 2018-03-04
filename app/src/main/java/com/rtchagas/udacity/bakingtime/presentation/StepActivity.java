package com.rtchagas.udacity.bakingtime.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Step;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;

/**
 * An activity representing a single Step detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StepsListActivity}.
 */
public class StepActivity extends AppCompatActivity {

    public static final String EXTRA_STEP_LIST = "extra_step_list";
    public static final String EXTRA_STEP_INDEX = "extra_step_index";

    @BindView(R.id.toolbar_step_detail)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_layout_step)
    CollapsingToolbarLayout mAppBarLayout;

    @BindView(R.id.image_step_backdrop)
    ImageView mImageStepBackdrop;

    @BindView(R.id.fab_menu_steps)
    FABsMenu mFabMenuSteps;

    private List<Step> mStepList = null;
    private int mCurrentStep = 0;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Get the mandatory extras from the incoming intent
        mStepList = (List<Step>) getIntent().getSerializableExtra(EXTRA_STEP_LIST);
        mCurrentStep = getIntent().getIntExtra(EXTRA_STEP_INDEX, 0);

        if (mStepList == null) {
            throw new IllegalArgumentException("Must pass the step list as EXTRA_STEP_LIST to this activity.");
        }

        // Get the current step
        Step step = mStepList.get(mCurrentStep);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set the toolbar title
        mAppBarLayout.setTitle(step.getShortDescription());

        // Set the backdrop
        Picasso.with(this).load(R.drawable.img_backing_step_backdrop).into(mImageStepBackdrop);

        // Make sure the FAB menu starts collapsed
        mFabMenuSteps.collapse();

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            StepFragment fragment = StepFragment.newInstance(step);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_detail_container_single, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Do not keep the FAB menu open.
        mFabMenuSteps.collapseImmediately();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, StepsListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fab_item_step_next, R.id.fab_item_step_previous})
    public void onFabItemClick(View fabItem) {

        if (R.id.fab_item_step_next == fabItem.getId()) {

            if ((mCurrentStep + 1) < mStepList.size()) {
                // Get the next step
                Step step = mStepList.get(++mCurrentStep);
                replaceFragment(step);
            }
            else {
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        R.string.step_jump_last, Snackbar.LENGTH_SHORT).show();
            }
        }
        else {
            if ((mCurrentStep - 1) >= 0) {
                // Get the next step
                Step step = mStepList.get(--mCurrentStep);
                replaceFragment(step);
            }
            else {
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        R.string.step_jump_first, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void replaceFragment(Step step) {
        StepFragment fragment = StepFragment.newInstance(step);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container_single, fragment)
                .commit();
    }
}
