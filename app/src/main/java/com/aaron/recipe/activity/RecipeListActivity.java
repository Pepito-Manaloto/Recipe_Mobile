package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.fragment.RecipeListFragment;

/**
 * RecipeListFragment activity.
 */
public class RecipeListActivity extends SingleFragmentActivity
{
    /**
     * Returns a recipe list fragment.
     *
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        return new RecipeListFragment();
    }
}
