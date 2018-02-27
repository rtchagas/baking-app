package com.rtchagas.udacity.bakingtime.presentation;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Recipe;

/**
 * A fragment representing a single Recipes detail screen.
 * This fragment is either contained in a {@link RecipesListActivity}
 * in two-pane mode (on tablets) or a {@link RecipesDetailActivity}
 * on handsets.
 */
public class RecipesDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_RECIPE = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Recipe mRecipe;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipesDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECIPE)) {
            // Load the recipe specified by the fragment arguments.
            mRecipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mRecipe.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipes_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mRecipe != null) {
            ((TextView) rootView.findViewById(R.id.recipes_detail)).setText(String.valueOf(mRecipe.getServings()));
        }

        return rootView;
    }
}
