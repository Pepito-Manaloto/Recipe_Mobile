package com.aaron.recipe.activity;

import com.aaron.recipe.fragment.LogsFragment;

import android.app.Fragment;

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
