package com.aaron.recipe.activity;

import com.aaron.recipe.fragment.AboutFragment;

import android.app.Fragment;

/**
 * About activity.
 */
public class AboutActivity extends SingleFragmentActivity
{
    /**
     * Returns a about fragment.
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        return new AboutFragment();
    }

}
