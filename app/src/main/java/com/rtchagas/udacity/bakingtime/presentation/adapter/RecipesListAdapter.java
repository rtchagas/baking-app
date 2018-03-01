package com.rtchagas.udacity.bakingtime.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.presentation.StepsListActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.RecipeViewHolder> {

    private final List<Recipe> mRecipeList;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Recipe item = (Recipe) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, StepsListActivity.class);
            intent.putExtra(StepsListActivity.EXTRA_RECIPE, item);
            context.startActivity(intent);
        }
    };

    public RecipesListAdapter(List<Recipe> items) {
        mRecipeList = items;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_list, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {

        Context context = holder.itemView.getContext();

        Recipe recipe = mRecipeList.get(position);

        // Recipe's name
        holder.textName.setText(recipe.getName());

        // Recipe's servings
        int servings = recipe.getServings();
        holder.textServings.setText(context.getResources()
                .getQuantityString(R.plurals.recipe_servings_description, servings, servings));

        // Recipes image (if available)
        if (!TextUtils.isEmpty(recipe.getImage())) {
            Picasso.with(context)
                    .load(recipe.getImage())
                    .error(R.drawable.img_recipe_ingredients)
                    .into(holder.imageRecipe);
        } else {
            Picasso.with(context)
                    .load(R.drawable.img_recipe_ingredients)
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
