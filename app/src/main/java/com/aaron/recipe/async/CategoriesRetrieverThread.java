package com.aaron.recipe.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.ResponseCategory;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.SettingsFragment;
import com.aaron.recipe.model.CategoryManager;
import com.aaron.recipe.model.LogsManager;

import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

public class CategoriesRetrieverThread extends AsyncTask<Void, Void, String>
{
    public static final String CLASS_NAME = CategoriesRetrieverThread.class.getSimpleName();
    private static final AtomicBoolean isUpdating = new AtomicBoolean(false);
    private Context context;
    private SettingsFragment settingsFragment;
    private CategoryManager categoryManager;
    private String url;

    public CategoriesRetrieverThread(SettingsFragment settingsFragment, Settings settings)
    {
        this(settingsFragment.getActivity(), settings);
        this.settingsFragment = settingsFragment;
        this.categoryManager = new CategoryManager(settingsFragment.getActivity());
    }

    public CategoriesRetrieverThread(Context context, Settings settings)
    {
        this.context = context;
        this.categoryManager = new CategoryManager(this.context);

        if(settings != null && settings.getServerURL() != null && !settings.getServerURL().isEmpty())
        {
            this.url = "http://" + settings.getServerURL() + this.context.getString(R.string.url_resource_categories);
        }
        else
        {
            this.url = "http://" + this.context.getString(R.string.url_address_default) + this.context.getString(R.string.url_resource_categories);
        }
    }

    /**
     * Retrieves data from server (also save to local disk) then returns the data, encapsulated in the intent, to RecipeListFragment.
     */
    @Override
    protected String doInBackground(Void... arg0)
    {
        ResponseCategory response = this.categoryManager.getCategoriesFromWeb(this.url);

        if(response.getStatusCode() == HttpURLConnection.HTTP_OK)
        {
            SparseArray<String> categoriesArray = response.getCategories();

            if(categoriesArray != null && categoriesArray.size() > 1)
            {
                this.categoryManager.saveCategoriesInCache(categoriesArray);
                this.categoryManager.saveCategoriesInDatabase(categoriesArray);

                Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate Categories = " + Categories.getCategories());
                LogsManager.addToLogs(CLASS_NAME + ": onCreate Categories = " + Categories.getCategories());

                return String.valueOf(HttpURLConnection.HTTP_OK);
            }
        }

        return null;
    }

    /**
     * Removes the dialog from screen, shows the result of the operation on toast, and sets the isUpdating flag to false.
     */
    @Override
    public void onPostExecute(String message)
    {
        if(this.settingsFragment != null)
        {
            this.settingsFragment.updateCategoriesSpinner();
        }

        if(StringUtils.isBlank(message))
        {
            Toast.makeText(this.context, this.context.getString(R.string.error_retrieving_categories), Toast.LENGTH_LONG).show();
        }

        isUpdating.set(false);
    }

    /**
     * Sets the isUpdating flag to false.
     */
    @Override
    protected void onCancelled(String message)
    {
        super.onCancelled();
        isUpdating.set(false);
    }

    public static void setIsUpdating()
    {
        isUpdating.set(true);
    }

    public static boolean isUpdating()
    {
        return isUpdating.get();
    }
}