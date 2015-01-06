package com.aaron.recipe.adapter;

import java.util.ArrayList;

import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.RecipeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Adapter class for RecipeFragment ViewPager.
 */
public class RecipePagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<Recipe> recipeList;
    private Settings settings;

    /**
     * Default constructor.
     */
    public RecipePagerAdapter(FragmentManager fm, final ArrayList<Recipe> recipeList, final Settings settings)
    {
        super(fm);

        this.recipeList = recipeList;
        this.settings = settings;
    }

    /**
     * Returns a RecipeFragment.
     * @return RecipeFragment
     */
    @Override
    public Fragment getItem(int position)
    {
        return RecipeFragment.newInstance(position, this.recipeList, this.settings);
    }

    /**
     * Returns the number of recipe in the list.
     * @return int
     */
    @Override
    public int getCount()
    {
        return this.recipeList.size();
    }
    
}
