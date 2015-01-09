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
import android.support.v4.view.ViewPager.OnPageChangeListener;

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
        final ArrayList<Recipe> recipeList = (ArrayList<Recipe>) this.getIntent().getSerializableExtra(EXTRA_LIST);
        Settings settings = (Settings) this.getIntent().getSerializableExtra(EXTRA_SETTINGS);
        int page = this.getIntent().getIntExtra(EXTRA_PAGE, 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentPagerAdapter pagerAdapter = new RecipePagerAdapter(fm, recipeList, settings);
    
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(page);

        viewPager.setOnPageChangeListener(new OnPageChangeListener()
            {
                @Override
                public void onPageScrollStateChanged(int state)
                {}
    
                /**
                 * Sets the activity's title depending on the selected recipe.
                 */
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {
                    if(positionOffsetPixels == 0) // Change title after fully swiping to another recipe 
                    {
                        setTitle(recipeList.get(position).getTitle());
                    }
                }
    
                @Override
                public void onPageSelected(int position)
                {}
            });
        
    }
}
