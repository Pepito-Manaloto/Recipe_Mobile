package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.fragment.LogsFragment;

/**
 * Logs activity.
 */
public class LogsActivity extends SingleFragmentActivity
{
    private LogsFragment fragment;

    /**
     * Returns a logs fragment.
     *
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        if(this.fragment == null)
        {
            this.fragment = new LogsFragment();
        }

        return this.fragment;
    }

}
