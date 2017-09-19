package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.fragment.AboutFragment;

/**
 * About activity.
 */
public class AboutActivity extends SingleFragmentActivity
{
    private AboutFragment fragment;

    /**
     * Returns a about fragment.
     *
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        if(this.fragment == null)
        {
            this.fragment = new AboutFragment();
        }

        return this.fragment;
    }

}
