package com.aaron.recipe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.aaron.recipe.R;
import com.aaron.recipe.adapter.RecipePagerAdapter;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.listener.PageChangeListener;

import java.util.ArrayList;

import static com.aaron.recipe.adapter.RecipePagerAdapter.EXTRA_PAGE;
import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_RECIPE_LIST;
import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * Recipe activity, uses old SDK to support view pager.
 */
public class RecipeActivity extends FragmentActivity
{
    private ArrayList<Recipe> recipeList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        this.recipeList = this.getIntent().getParcelableArrayListExtra(EXTRA_RECIPE_LIST);
        int page = this.getIntent().getIntExtra(EXTRA_PAGE, 0);

        setTitle(getRecipeTitle(page));
        initializeViewPager(page);
    }

    private void initializeViewPager(int page)
    {
        FragmentManager fm = getSupportFragmentManager();
        Settings settings = this.getIntent().getParcelableExtra(EXTRA_SETTINGS);

        ViewPager viewPager = findViewById(R.id.view_pager);
        FragmentPagerAdapter pagerAdapter = new RecipePagerAdapter(fm, recipeList, settings);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(page);
        viewPager.clearOnPageChangeListeners();

        viewPager.addOnPageChangeListener(new PageChangeListener(this));
    }

    public String getRecipeTitle(int page)
    {
        return recipeList.get(page).getTitle();
    }
}
