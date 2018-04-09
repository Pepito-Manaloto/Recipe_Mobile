package com.aaron.recipe.listener;

import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.aaron.recipe.activity.RecipeActivity;

import java.lang.ref.WeakReference;

public class PageChangeListener implements OnPageChangeListener
{
    private WeakReference<RecipeActivity> activityRef;

    public PageChangeListener(RecipeActivity activity)
    {
        this.activityRef = new WeakReference<>(activity);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
        // No action
    }

    /**
     * Sets the activity's title depending on the selected recipe.
     * Title is updated here to become more responsive (reduce delay)
     */
    @Override
    public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels)
    {
        if(positionOffsetPixels == 0) // Change title after fully swiping to another recipe
        {
            final RecipeActivity activity = this.activityRef.get();
            if(activity != null)
            {
                Runnable updateRecipeTitle = () -> activity.setTitle(activity.getRecipeTitleFromPage(position));
                activity.runOnUiThread(updateRecipeTitle);
            }
        }
    }

    @Override
    public void onPageSelected(int position)
    {
        // No Action
    }
}