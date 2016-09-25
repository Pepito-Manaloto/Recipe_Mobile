package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.fragment.AboutFragment;

/**
 * About activity.
 */
public class AboutActivity extends SingleFragmentActivity
{
    /**
     * Returns a about fragment.
     *
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        return new AboutFragment();
    }

}
