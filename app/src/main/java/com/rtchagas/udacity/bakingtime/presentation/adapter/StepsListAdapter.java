package com.rtchagas.udacity.bakingtime.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Ingredient;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.core.Step;
import com.rtchagas.udacity.bakingtime.presentation.StepDetailActivity;
import com.rtchagas.udacity.bakingtime.presentation.StepDetailFragment;
import com.rtchagas.udacity.bakingtime.presentation.StepsListActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_INGREDIENTS = 1;
    private static final int VIEWTYPE_STEP = 2;

    private final StepsListActivity mParentActivity;
    private final Recipe mRecipe;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnStepClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Step item = (Step) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putSerializable(StepDetailFragment.ARG_STEP, item);
                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);
                // Show the fragment
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, fragment)
                        .commit();
            }
            else {
                Context context = view.getContext();
                Intent intent = new Intent(context, StepDetailActivity.class);
                intent.putExtra(StepDetailFragment.ARG_STEP, item);
                context.startActivity(intent);
            }
        }
    };

    public StepsListAdapter(StepsListActivity parent, Recipe recipe, boolean twoPane) {
        mRecipe = recipe;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 ? VIEWTYPE_INGREDIENTS : VIEWTYPE_STEP);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (VIEWTYPE_STEP == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_step_list, parent, false);
            return new StepViewHolder(view);
        }
        else if (VIEWTYPE_INGREDIENTS == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ingredient_list, parent, false);
            return new IngredientsViewHolder(view);
        }

        throw new IllegalArgumentException("Unsupported view type!");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();

        if (VIEWTYPE_STEP == getItemViewType(position)) {
            bindStepViewHolder(context, (StepViewHolder) holder, position);
        }
        else if (VIEWTYPE_INGREDIENTS == getItemViewType(position)) {
            bindIngredientsViewHolder(context, (IngredientsViewHolder) holder, position);
        }
        else {
            throw new IllegalArgumentException("Unsupported view type!");
        }
    }

    @Override
    public int getItemCount() {
        return (mRecipe.getSteps() != null ? mRecipe.getSteps().size() : 0);
    }

    private void bindIngredientsViewHolder(@NonNull Context context,
                                           @NonNull IngredientsViewHolder holder, int position) {

        // Nothing should be done here as the item
        // is unique and always the same.
    }

    private void bindStepViewHolder(@NonNull Context context,
                                    @NonNull StepViewHolder holder, int position) {

        Step step = mRecipe.getSteps().get(position);

        // Step's summary
        holder.textStepDescription.setText(step.getShortDescription());

        // Current step count
        holder.textStepCount.setText(context.getString(R.string.step_number, position));

        // Has video or not
        if (TextUtils.isEmpty(step.getVideoURL())) {
            holder.imageVideo.setVisibility(View.INVISIBLE);
            holder.textVideoAvailable.setVisibility(View.INVISIBLE);
            holder.imageStepThumb.setVisibility(View.GONE);
        }
        else {
            holder.imageVideo.setVisibility(View.VISIBLE);
            holder.textVideoAvailable.setVisibility(View.VISIBLE);
            holder.imageStepThumb.setVisibility(View.VISIBLE);

            // Step image thumbnail (if available)
            if (!TextUtils.isEmpty(step.getThumbnailURL())) {
                Picasso.with(context)
                        .load(step.getThumbnailURL())
                        .error(R.drawable.img_cooking_step_thumb)
                        .into(holder.imageStepThumb);
            }
            else {
                Picasso.with(holder.itemView.getContext())
                        .load(R.drawable.img_cooking_step_thumb)
                        .into(holder.imageStepThumb);
            }
        }

        holder.itemView.setTag(step);
        holder.itemView.setOnClickListener(mOnStepClickListener);
    }

    class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_step_thumb)
        ImageView imageStepThumb;

        @BindView(R.id.text_step_short_description)
        TextView textStepDescription;

        @BindView(R.id.text_step_count)
        TextView textStepCount;

        @BindView(R.id.image_step_video)
        ImageView imageVideo;

        @BindView(R.id.text_video_available)
        TextView textVideoAvailable;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recyclerview_ingredients)
        RecyclerView recyclerIngredients;

        IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Configure the recycler view and set the adapter.
            // This can be done only once here as the ingredients list
            // is always the same for this recipe.
            recyclerIngredients.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerIngredients.setHasFixedSize(true);
            recyclerIngredients.setNestedScrollingEnabled(false);
            recyclerIngredients.setAdapter(new IngredientsListAdapter());
        }
    }

    private class IngredientsListAdapter extends RecyclerView.Adapter<SingleIngredientViewHolder> {

        @Override
        public SingleIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ingredient_single_item, parent, false);
            return new SingleIngredientViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SingleIngredientViewHolder holder, int position) {
            Ingredient ingredient = mRecipe.getIngredients().get(position);
            // Just format the ingredient as a single string and bind it.
            holder.textIngredient.setText(
                    formatIngredient(holder.itemView.getContext(), ingredient));
        }

        @Override
        public int getItemCount() {
            return (mRecipe.getIngredients() != null ? mRecipe.getIngredients().size() : 0);
        }

        @SuppressWarnings("defaultlocale")
        private String formatIngredient(@NonNull Context context, @NonNull Ingredient ingredient) {

            // Get the measure
            String measure = ingredient.getMeasure().toLowerCase();

            // Get the quantity
            double rawQuantity = ingredient.getQuantity();
            String quantity = (rawQuantity % 1d > 0)
                    ? String.format("%.1f", rawQuantity)
                    : String.format("%.0f", rawQuantity);

            // Get the ingredient name
            String name = ingredient.getIngredient();

            return context.getString(R.string.ingredient_format, quantity, measure, name);
        }
    }

    class SingleIngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_ingredient_single)
        TextView textIngredient;

        public SingleIngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
