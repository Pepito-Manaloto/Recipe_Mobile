package com.aaron.recipe.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.aaron.recipe.bean.Settings;

public class ServerUrlTextListener implements TextWatcher
{
    private Settings settings;

    public ServerUrlTextListener(Settings settings)
    {
        this.settings = settings;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        // No Action
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        // No Action
    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        this.settings.setServerURL(editable.toString());
    }
}