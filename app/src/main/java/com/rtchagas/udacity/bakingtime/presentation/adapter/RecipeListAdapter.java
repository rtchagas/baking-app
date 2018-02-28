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
import com.rtchagas.udacity.bakingtime.presentation.RecipesDetailActivity;
import com.rtchagas.udacity.bakingtime.presentation.RecipesDetailFragment;
import com.rtchagas.udacity.bakingtime.presentation.RecipesListActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    private final RecipesListActivity mParentActivity;
    private final List<Recipe> mRecipeList;
    private final boolean mTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe item = (Recipe) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putSerializable(RecipesDetailFragment.ARG_RECIPE, item);
                RecipesDetailFragment fragment = new RecipesDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipes_detail_container, fragment)
                        .commit();
            }
            else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipesDetailActivity.class);
                intent.putExtra(RecipesDetailFragment.ARG_RECIPE, item);
                context.startActivity(intent);
            }
        }
    };

    public RecipeListAdapter(RecipesListActivity parent, List<Recipe> items, boolean twoPane) {
        mRecipeList = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipes_list, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {

        Recipe recipe = mRecipeList.get(position);

        // Recipe's name
        holder.textName.setText(recipe.getName());

        // Recipe's servings
        holder.textServings.setText(String.valueOf(recipe.getServings()));

        // Recipes image (if available)
        if (!TextUtils.isEmpty(recipe.getImage())) {
            Picasso.with(holder.itemView.getContext())
                    .load(recipe.getImage())
                    .into(holder.imageRecipe);
        }
        else {
            Picasso.with(holder.itemView.getContext())
                    .load(R.drawable.img_ingredients)
                    .into(holder.imageRecipe);
        }

        holder.itemView.setTag(mRecipeList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_recipe)
        ImageView imageRecipe;

        @BindView(R.id.text_recipe_name)
        TextView textName;

        @BindView(R.id.text_recipe_serving)
        TextView textServings;

        RecipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
