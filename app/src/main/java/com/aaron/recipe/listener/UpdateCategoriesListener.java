package com.aaron.recipe.listener;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.async.CategoriesRetrieverThread;
import com.aaron.recipe.fragment.SettingsFragment;

import java.lang.ref.WeakReference;

public class UpdateCategoriesListener implements View.OnClickListener
{
    private WeakReference<SettingsFragment> fragmentRef;

    public UpdateCategoriesListener(SettingsFragment fragment)
    {
        this.fragmentRef = new WeakReference<>(fragment);
    }

    @Override
    public void onClick(View imageView)
    {
        SettingsFragment fragment = this.fragmentRef.get();

        if(fragment != null)
        {
            Activity activity = fragment.getActivity();

            if(!CategoriesRetrieverThread.isUpdating())
            {
                final Animation rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate_refresh);
                rotation.setRepeatCount(Animation.INFINITE);

                CategoriesRetrieverThread categoriesRetrieverThread = new CategoriesRetrieverThread(fragment, fragment.getSettings());
                categoriesRetrieverThread.execute();

                CategoriesRetrieverThread.setIsUpdating();
                imageView.startAnimation(rotation);
            }
            else
            {
                Toast.makeText(activity, activity.getString(R.string.categories_currently_updating), Toast.LENGTH_SHORT).show();
            }
        }
    }
}