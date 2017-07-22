package com.aaron.recipe.app;

import android.app.Application;

import com.aaron.recipe.async.CategoriesRetrieverThread;
import com.aaron.recipe.bean.Categories;

public class RecipeApplication extends Application
{
    public static final String CLASS_NAME = RecipeApplication.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        if(Categories.getCategories().size() <= 1)
        {
            CategoriesRetrieverThread categoriesRetrieverThread = new CategoriesRetrieverThread(this, null);
            categoriesRetrieverThread.execute();
        }
    }
}