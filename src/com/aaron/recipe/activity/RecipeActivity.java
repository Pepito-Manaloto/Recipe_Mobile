package com.aaron.recipe.activity;

import java.util.ArrayList;

import com.aaron.recipe.R;
import com.aaron.recipe.adapter.RecipePagerAdapter;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

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

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentPagerAdapter pagerAdapter = new RecipePagerAdapter(fm, recipeList, settings);
        viewPager.setAdapter(pagerAdapter);
    }
}
