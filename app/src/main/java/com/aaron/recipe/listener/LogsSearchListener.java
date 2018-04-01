package com.aaron.recipe.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.aaron.recipe.fragment.LogsFragment;
import com.aaron.recipe.model.LogsManager;

import java.lang.ref.WeakReference;

public class LogsSearchListener implements TextWatcher
{
    private WeakReference<LogsFragment> fragmentRef;
    private LogsManager logsManager;

    public LogsSearchListener(LogsFragment fragment, LogsManager logsManager)
    {
        this.fragmentRef = new WeakReference<>(fragment);
        this.logsManager = logsManager;
    }

    /**
     * Handles search on text update.
     */
    @Override
    public void afterTextChanged(Editable editable)
    {
        String searched = editable.toString();

        LogsFragment fragment = this.fragmentRef.get();
        if(fragment != null)
        {
            if(searched.length() <= 0)
            {
                fragment.setTextareaText(logsManager.getLogs());
            }
            else
            {
                fragment.setTextareaText(logsManager.getLogs(searched));
            }

            Log.d(LogsManager.TAG, LogsFragment.CLASS_NAME + ": onCreateOptionsMenu(afterTextChanged). searched=" + searched);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
        // No Action
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
        // No Action
    }
}