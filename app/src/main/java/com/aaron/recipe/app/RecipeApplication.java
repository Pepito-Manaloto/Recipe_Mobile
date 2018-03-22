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
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

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

        if(LeakCanary.isInAnalyzerProcess(this))
        {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        // Access in Google Chrome url via -> chrome://inspect
        Stetho.initializeWithDefaults(this);

        if(Categories.getCategories().size() <= 1)
        {
            loadCategories();
        }
    }

    private void loadCategories()
    {
        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate. Init categories.");
        LogsManager.addToLogs(CLASS_NAME + ": onCreate. Init categories.");

        CategoryManager categoryManager = new CategoryManager(this);
        SparseArray<String> categoriesArray = categoryManager.getCategoriesFromDisk();

        boolean haveCategories = categoriesArray != null && categoriesArray.size() > 1;
        if(haveCategories)
        {
            categoryManager.saveCategoriesInCache(categoriesArray);
        }
        else
        {
            if(!CategoriesRetrieverThread.isUpdating())
            {
                startCategoriesRetrieverThread();
            }
            else
            {
                Toast.makeText(this, this.getString(R.string.categories_currently_updating), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCategoriesRetrieverThread()
    {
        CategoriesRetrieverThread categoriesRetrieverThread = new CategoriesRetrieverThread(getApplicationContext(), null);
        categoriesRetrieverThread.execute();
        CategoriesRetrieverThread.setIsUpdating();
    }
}