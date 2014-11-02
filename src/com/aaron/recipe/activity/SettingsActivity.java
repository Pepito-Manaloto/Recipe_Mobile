package com.aaron.recipe.activity;

import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.SettingsFragment;
import android.app.Fragment;

/**
 * SettingsFragment activity.
 */
public class SettingsActivity extends SingleFragmentActivity
{
    /**
     * Returns a settings fragment.
     * @return a fragment to be added.
     */
    @Override
    protected Fragment createFragment()
    {
        Settings settings = (Settings) this.getIntent().getSerializableExtra(SettingsFragment.EXTRA_SETTINGS);

        return SettingsFragment.newInstance(settings);
    }

}
