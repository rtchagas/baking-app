package com.rtchagas.udacity.bakingtime.presentation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is a composite adapter that displays the ingredient list as the first item
 * in the list and all the steps right below. <br/>
 * To make things easier it was decided to just manipulate the Adapter getCount()
 * and the position while binding the steps. <br/>
 * But we could'e used a mixes list of Objects as input and decided how to bind
 * each ViewHolder based on a simple instanceOf comparison.
 */
public class StepsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_INGREDIENTS = 1;
    private static final int VIEWTYPE_STEP = 2;

    private final OnItemClickListener mOnItemClickListener;
    private final Recipe mRecipe;

    // To control the recycler view selected item
    private int mSelectedPosition = RecyclerView.NO_POSITION;

    public StepsListAdapter(Recipe recipe, @Nullable OnItemClickListener listener) {
        mRecipe = recipe;
        mOnItemClickListener = listener;
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

        int stepsCount = (mRecipe.getSteps() != null ? mRecipe.getSteps().size() : 0);

        // Added +1 due to ingredients item in the first position
        return (stepsCount + 1);
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }

    private void bindIngredientsViewHolder(@NonNull Context context,
                                           @NonNull IngredientsViewHolder holder, int position) {

        // Nothing should be done here as the item
        // is unique and always the same.
    }

    private void bindStepViewHolder(@NonNull Context context,
                                    @NonNull StepViewHolder holder, int position) {

        // The step listing always begin at the position 1
        // So must adjust the index here
        int stepIndex = (position - 1);

        Step step = mRecipe.getSteps().get(stepIndex);

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

        holder.itemView.setTag(stepIndex);

        // Update selected position
        holder.itemView.setSelected(mSelectedPosition == position);
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            // Below line is just like a safety check, because sometimes holder could be null,
            // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            int newPosition = getAdapterPosition();
            // Do not fire the click twice on same item.
            if (mSelectedPosition == newPosition) return;

            // Updating old as well as new selected positions
            notifyItemChanged(mSelectedPosition);
            mSelectedPosition = newPosition;
            notifyItemChanged(mSelectedPosition);

            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick((int)view.getTag());
            }
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
