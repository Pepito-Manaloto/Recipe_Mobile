package com.aaron.recipe.app;

import android.app.Application;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.async.CategoriesRetrieverThread;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.model.CategoryManager;
import com.aaron.recipe.model.LogsManager;

/**
 * Initializes the categories.
 */
public class RecipeApplication extends Application
{
    public static final String CLASS_NAME = RecipeApplication.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        if(Categories.getCategories().size() <= 1)
        {
            Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate. Init categories.");
            LogsManager.addToLogs(CLASS_NAME + ": onCreate. Init categories.");

            CategoryManager categoryManager = new CategoryManager(this);
            SparseArray<String> categoriesArray = categoryManager.getCategoriesFromDisk();

            if(categoriesArray != null && categoriesArray.size() > 1)
            {
                categoryManager.saveCategoriesInCache(categoriesArray);
            }
            else
            {
                if(!CategoriesRetrieverThread.isUpdating())
                {
                    CategoriesRetrieverThread categoriesRetrieverThread = new CategoriesRetrieverThread(this, null);
                    categoriesRetrieverThread.execute();
                    CategoriesRetrieverThread.setIsUpdating();
                }
                else
                {
                    Toast.makeText(this, this.getString(R.string.categories_currently_updating), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}