package com.aaron.recipe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.aaron.recipe.R;
import com.aaron.recipe.adapter.RecipePagerAdapter;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;

import java.lang.ref.WeakReference;
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

        FragmentManager fm = getSupportFragmentManager();

        this.recipeList = this.getIntent().getParcelableArrayListExtra(EXTRA_RECIPE_LIST);
        Settings settings = this.getIntent().getParcelableExtra(EXTRA_SETTINGS);
        int page = this.getIntent().getIntExtra(EXTRA_PAGE, 0);

        setTitle(recipeList.get(page).getTitle());

        ViewPager viewPager = findViewById(R.id.view_pager);
        FragmentPagerAdapter pagerAdapter = new RecipePagerAdapter(fm, recipeList, settings);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(page);
        viewPager.clearOnPageChangeListeners();

        viewPager.addOnPageChangeListener(new PageChangeListener(this));

    }

    protected ArrayList<Recipe> getRecipeList()
    {
        return this.recipeList;
    }

    private static class PageChangeListener implements OnPageChangeListener
    {
        private WeakReference<RecipeActivity> activityRef;

        PageChangeListener(RecipeActivity activity)
        {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            // No action
        }

        /**
         * Sets the activity's title depending on the selected recipe. Title is updated here to become more responsive (reduce delay)
         */
        @Override
        public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels)
        {
            if(positionOffsetPixels == 0) // Change title after fully swiping to another recipe
            {
                final RecipeActivity activity = this.activityRef.get();

                if(activity != null)
                {
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            activity.setTitle(activity.getRecipeList().get(position).getTitle());
                        }
                    });
                }
            }
        }

        @Override
        public void onPageSelected(int position)
        {
            // No Action
        }
    }
}
