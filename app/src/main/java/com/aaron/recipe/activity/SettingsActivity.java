package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.SettingsFragment;

import static com.aaron.recipe.bean.DataKey.EXTRA_SETTINGS;

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
        Settings settings = this.getIntent().getParcelableExtra(EXTRA_SETTINGS.toString());
        this.fragment = SettingsFragment.newInstance(this.fragment, settings);

        return this.fragment;
    }

}
