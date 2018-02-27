package com.rtchagas.udacity.bakingtime.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Recipe;
import com.rtchagas.udacity.bakingtime.presentation.RecipesDetailActivity;
import com.rtchagas.udacity.bakingtime.presentation.RecipesDetailFragment;
import com.rtchagas.udacity.bakingtime.presentation.RecipesListActivity;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private final RecipesListActivity mParentActivity;
    private final List<Recipe> mValues;
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
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipes_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(String.valueOf(mValues.get(position).getId()));
        holder.mContentView.setText(mValues.get(position).getName());

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
        }
    }
}
