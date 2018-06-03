package com.aaron.recipe;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Aaron on 02/01/2018.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = RecipeTestApplication.class)
public abstract class RobolectricTest
{
    protected Context getContext()
    {
        return RuntimeEnvironment.application.getApplicationContext();
    }

    protected Activity getActivity(Class<? extends Activity> clazz)
    {
        return Robolectric.buildActivity(clazz).get();
    }
}
