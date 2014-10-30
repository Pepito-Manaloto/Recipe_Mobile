package com.aaron.recipe.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;

/**
 * Handles the web call to retrieve recipes in JSON object representation.
 * Handles the data storage of recipes.
 */
public class RecipeManager
{
    public static final String DATE_FORMAT_LONG = "MMMM d, yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_SHORT_24 = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_LONG, Locale.getDefault());

    public RecipeManager(final Activity activity)
    {}
    
    public String getLastUpdated()
    {
        return null;
    }
    
    public void deleteRecipeFromDisk()
    {}
}
