package com.aaron.recipe.activity;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.RecipeFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_RECIPE;
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
        Fragment recipeFragment = fm.findFragmentById(R.id.fragment_container);

        if(recipeFragment == null)
        {
            Recipe recipe = (Recipe) this.getIntent().getSerializableExtra(EXTRA_RECIPE);
            Settings settings = (Settings) this.getIntent().getSerializableExtra(EXTRA_SETTINGS);

            recipeFragment = RecipeFragment.newInstance(recipe, settings);

            fm.beginTransaction()
                .add(R.id.fragment_container, recipeFragment)
                .commit();
        }
    }
}
