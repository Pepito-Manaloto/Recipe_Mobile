package com.aaron.recipe.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.aaron.recipe.activity.RecipeActivity;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.aaron.recipe.adapter.RecipePagerAdapter.EXTRA_PAGE;
import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_RECIPE_LIST;
import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * Helper class for handling Recipe selection and Recipe list row scrolling.
 */
public class RecipeListRowTouchListener implements OnTouchListener
{
    private WeakReference<Activity> activityRef;
    private ArrayList<Recipe> recipeList;
    private Settings settings;
    private float historicX;
    private int page;

    /**
     * Default constructor.
     *
     * @param page
     *            the position of the selected recipe
     */
    public RecipeListRowTouchListener(Activity activity, ArrayList<Recipe> recipeList, Settings settings, final int page)
    {
        this.activityRef = new WeakReference<>(activity);
        this.recipeList = recipeList;
        this.settings = settings;
        this.page = page;
    }

    /**
     * If the touch moves MORE than 15 pixels horizontally then the gesture will be treated as a scrolling event, else it will be treated as selecting the
     * row which will start RecipeActivity.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                this.historicX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                Activity activity = this.activityRef.get();

                if(activity != null)
                {
                    boolean touchMovedLessThan10Pixels = Math.abs(this.historicX - event.getX()) < 15;
                    if(touchMovedLessThan10Pixels)
                    {
                        startRecipeActivity(activity);
                    }
                }

                // Removes compiler warning
                v.performClick();

                break;
            }
            default:
            {
                return false;
            }
        }

        return true;
    }

    private void startRecipeActivity(Activity activity)
    {
        Intent intent = new Intent(activity, RecipeActivity.class);
        intent.putExtra(EXTRA_PAGE, this.page);
        intent.putExtra(EXTRA_RECIPE_LIST, this.recipeList);
        intent.putExtra(EXTRA_SETTINGS, this.settings);
        activity.startActivity(intent);
    }
}