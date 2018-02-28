package com.rtchagas.udacity.bakingtime.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.core.Step;
import com.rtchagas.udacity.bakingtime.presentation.StepDetailActivity;
import com.rtchagas.udacity.bakingtime.presentation.StepDetailFragment;
import com.rtchagas.udacity.bakingtime.presentation.StepsListActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.RecipeViewHolder> {

    private final StepsListActivity mParentActivity;
    private final Recipe mRecipe;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Step item = (Step) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putSerializable(StepDetailFragment.ARG_STEP, item);
                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);
                /*
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipes_detail_container, fragment)
                        .commit();
                */
            }
            else {
                Context context = view.getContext();
                Intent intent = new Intent(context, StepDetailActivity.class);
                intent.putExtra(StepDetailFragment.ARG_STEP, item);
                //context.startActivity(intent);
            }
        }
    };

    public StepsListAdapter(StepsListActivity parent, Recipe recipe, boolean twoPane) {
        mRecipe = recipe;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step_list, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {

        Context context = holder.itemView.getContext();

        Step step = mRecipe.getSteps().get(position);

        // Step's summary
        holder.textStepDescription.setText(step.getShortDescription());

        // Current step count
        holder.textStepCount.setText(context.getString(R.string.step_number, position + 1));

        // Has video or not
        if (TextUtils.isEmpty(step.getVideoURL())) {
            holder.imageVideo.setVisibility(View.INVISIBLE);
        }
        else {
            holder.imageVideo.setVisibility(View.VISIBLE);
        }

        // Step image thumbnail (if available)
        if (!TextUtils.isEmpty(step.getThumbnailURL())) {
            Picasso.with(context)
                    .load(step.getThumbnailURL())
                    .error(R.drawable.img_step_thumb)
                    .into(holder.imageStepThumb);
        }
        else {
            Picasso.with(holder.itemView.getContext())
                    .load(R.drawable.img_step_thumb)
                    .into(holder.imageStepThumb);
        }

        holder.itemView.setTag(step);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return (mRecipe.getSteps() != null ? mRecipe.getSteps().size() : 0);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_step_thumb)
        ImageView imageStepThumb;

        @BindView(R.id.text_step_short_description)
        TextView textStepDescription;

        @BindView(R.id.text_step_count)
        TextView textStepCount;

        @BindView(R.id.image_step_video)
        ImageView imageVideo;

        RecipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
