package com.aaron.recipe.activity;

import android.app.Fragment;

import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.SettingsFragment;

/**
 * SettingsFragment activity.
 */
public class SettingsActivity extends SingleFragmentActivity
{
    /**
     * Returns a settings fragment.
     *
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        Settings settings = (Settings) this.getIntent().getSerializableExtra(SettingsFragment.EXTRA_SETTINGS);

        return SettingsFragment.newInstance(settings);
    }

}
