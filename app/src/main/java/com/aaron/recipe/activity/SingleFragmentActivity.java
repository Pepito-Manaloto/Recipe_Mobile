package com.aaron.recipe.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.aaron.recipe.R;

/**
 * Abstract super class that creates a single fragment in the fragment container.
 */
public abstract class SingleFragmentActivity extends Activity
{
    /**
     * Adds the single fragment, returned from the abstract method createFragment(), into the fragment container.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null)
        {
            fragment = this.createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    /**
     * To be implemented by an activity that has a single fragment.
     * Returns a fragment that will be added to the fragment container.
     *
     * @return the fragment that will be added to the fragment container of the implementing Activity class
     */
    protected abstract Fragment createFragment();
}
