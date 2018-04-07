package com.aaron.recipe.listener;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.fragment.SettingsFragment;
import com.aaron.recipe.model.CategoryManager;

import java.lang.ref.WeakReference;

public class UpdateCategoriesListener implements View.OnClickListener
{
    private WeakReference<SettingsFragment> fragmentRef;
    private CategoryManager categoryManager;

    public UpdateCategoriesListener(SettingsFragment fragment, CategoryManager categoryManager)
    {
        this.fragmentRef = new WeakReference<>(fragment);
        this.categoryManager = categoryManager;
    }

    @Override
    public void onClick(View imageView)
    {
        SettingsFragment fragment = this.fragmentRef.get();
        if(fragment != null)
        {
            Activity activity = fragment.getActivity();

            if(CategoryManager.isNotUpdating())
            {
                final Animation rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate_refresh);
                rotation.setRepeatCount(Animation.INFINITE);

                categoryManager.updateCategories(() -> doAfterUpdateCategories(fragment));

                imageView.startAnimation(rotation);
            }
            else
            {
                Toast.makeText(activity, activity.getString(R.string.categories_currently_updating), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doAfterUpdateCategories(SettingsFragment fragment)
    {
        if(fragment != null)
        {
            fragment.updateCategoriesSpinnerAndStopRefreshAnimation();
        }

        CategoryManager.doneUpdating();
    }
}