package com.aaron.recipe.listener;

import android.view.KeyEvent;
import android.view.View;

import com.aaron.recipe.fragment.Backable;

import java.lang.ref.WeakReference;

public class BackButtonListener implements View.OnKeyListener
{
    private WeakReference<Backable> backableRef;

    public BackButtonListener(Backable backable)
    {
        this.backableRef = new WeakReference<>(backable);
    }

    /**
     * If back button is the key event then finish current activity and move to the next with extra data result.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        // For back button
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
        {
            Backable backable = this.backableRef.get();
            if(backable != null)
            {
                backable.setActivityResultOnBackEvent();
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}