package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.fragment.LogsFragment;

/**
 * Logs activity.
 */
public class LogsActivity extends SingleFragmentActivity
{
    /**
     * Returns a logs fragment.
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        return new LogsFragment();
    }

}
