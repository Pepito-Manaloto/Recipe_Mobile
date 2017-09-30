package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.SettingsFragment;

/**
 * SettingsFragment activity.
 */
public class SettingsActivity extends SingleFragmentActivity
{
    private SettingsFragment fragment;

    /**
     * Returns a settings fragment.
     *
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        Settings settings = this.getIntent().getParcelableExtra(SettingsFragment.EXTRA_SETTINGS);
        this.fragment = SettingsFragment.newInstance(this.fragment, settings);

        return this.fragment;
    }

}
