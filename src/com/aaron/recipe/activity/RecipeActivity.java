package com.aaron.recipe.activity;

import java.util.ArrayList;

import com.aaron.recipe.R;
import com.aaron.recipe.adapter.RecipePagerAdapter;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.RecipeFragment;
import com.aaron.recipe.model.LogsManager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import static com.aaron.recipe.adapter.RecipePagerAdapter.EXTRA_PAGE;
import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_LIST;
import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * Recipe activity, uses old SDK to support view pager.
 */
public class RecipeActivity extends FragmentActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        FragmentManager fm = getSupportFragmentManager();

        @SuppressWarnings("unchecked")
        ArrayList<Recipe> recipeList = (ArrayList<Recipe>) this.getIntent().getSerializableExtra(EXTRA_LIST);
        Settings settings = (Settings) this.getIntent().getSerializableExtra(EXTRA_SETTINGS);
        int page = this.getIntent().getIntExtra(EXTRA_PAGE, 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentPagerAdapter pagerAdapter = new RecipePagerAdapter(fm, recipeList, settings);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(page);
    }

    /**
     * This method is called when the user pressed back button
     * Resets previousPageLoaded because user exits the Recipe view pager.
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        RecipeFragment.previousPageLoaded = 0;

        Log.d(LogsManager.TAG, "RecipeActivity: onBackPressed.");
    }

    /**
     * This method is called when the user selects Home button.
     * Resets previousPageLoaded because user exits the Recipe view pager.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                RecipeFragment.previousPageLoaded = 0;
            }
        }

        Log.d(LogsManager.TAG, "RecipeActivity: onOptionsItemSelected.");

        return super.onOptionsItemSelected(item);
    }
}
