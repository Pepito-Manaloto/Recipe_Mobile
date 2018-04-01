package com.aaron.recipe.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.aaron.recipe.adapter.RecipeListRowAdapter;
import com.aaron.recipe.fragment.RecipeListFragment;
import com.aaron.recipe.model.LogsManager;

public class RecipeSearchListener implements TextWatcher
{
    private RecipeListRowAdapter adapter;

    public RecipeSearchListener(RecipeListRowAdapter adapter)
    {
        this.adapter = adapter;
    }

    /**
     * Handles search on text update.
     */
    @Override
    public void afterTextChanged(Editable editable)
    {
        String searched = editable.toString();
        this.adapter.filter(searched);

        Log.d(LogsManager.TAG, RecipeListFragment.CLASS_NAME + ": onCreateOptionsMenu(afterTextChanged). searched=" + searched);
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