package com.aaron.recipe.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.CategoryManager;
import com.aaron.recipe.model.HttpClient;

import java.util.regex.Pattern;

public class ServerUrlTextListener implements TextWatcher
{
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
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
        String newBaseUrl = editable.toString();
        this.settings.setServerURL(newBaseUrl);

        if(isValidURL(newBaseUrl))
        {
            HttpClient.reinitializeRetrofit(newBaseUrl);
        }
    }

    private boolean isValidURL(String newBaseUrl)
    {
        return IP_ADDRESS_PATTERN.matcher(newBaseUrl).matches();
    }
}