package com.aaron.recipe.activity;

import com.aaron.recipe.fragment.RecipeListFragment;

import android.app.Fragment;

/**
 * RecipeListFragment activity.
 */
public class RecipeListActivity extends SingleFragmentActivity
{
    /**
     * Returns a recipe list fragment.
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        return new RecipeListFragment();
    }
}
