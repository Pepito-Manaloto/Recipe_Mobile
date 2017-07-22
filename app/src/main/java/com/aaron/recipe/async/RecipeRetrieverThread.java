package com.aaron.recipe.async;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.ResponseRecipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.UpdateFragment;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper thread class that does the retrieval of the recipe list from the server.
 */
public class RecipeRetrieverThread extends AsyncTask<Void, Void, String>
{
    public static final String CLASS_NAME = RecipeRetrieverThread.class.getSimpleName();
    private Activity activity;
    private DialogFragment fragment;
    private AtomicBoolean isUpdating;
    private RecipeManager recipeManager;
    private Settings settings;
    private String url;

    public RecipeRetrieverThread(Activity activity, DialogFragment fragment, AtomicBoolean isUpdating, Settings settings)
    {
        this.activity = activity;
        this.fragment = fragment;
        this.isUpdating = isUpdating;
        this.recipeManager = new RecipeManager(activity);
        this.settings = settings;

        if(settings != null && settings.getServerURL() != null && !settings.getServerURL().isEmpty())
        {
            this.url = "http://" + settings.getServerURL() + activity.getString(R.string.url_resource_recipes);
        }
        else
        {
            this.url = "http://" + activity.getString(R.string.url_address_default) + activity.getString(R.string.url_resource_recipes);
        }
    }

    /**
     * Encapsulates the recipe list and response to an intent and sends the intent + resultCode to VocaublaryListFragment.
     *
     * @param vocabList  the retrieved recipe list
     * @param resultCode the result of the operation
     */
    private void sendResult(final ArrayList<Recipe> vocabList, final int resultCode)
    {
        Fragment targetFragment = this.fragment.getTargetFragment();
        if(targetFragment == null)
        {
            return;
        }

        Intent data = new Intent();
        data.putExtra(UpdateFragment.EXTRA_RECIPE_LIST, vocabList);
        targetFragment.onActivityResult(this.fragment.getTargetRequestCode(), resultCode, data);

        Log.d(LogsManager.TAG, CLASS_NAME + ": sendResult. list=" + vocabList);
        LogsManager.addToLogs(CLASS_NAME + ": sendResult. list_size=" + vocabList.size());
    }

    /**
     * Retrieves data from server (also save to local disk) then returns the data, encapsulated in the intent, to RecipeListFragment.
     */
    @Override
    protected String doInBackground(Void... arg0)
    {
        ResponseRecipe response = recipeManager.getRecipesFromWeb(url);
        String message;

        if(response.getStatusCode() == HttpURLConnection.HTTP_OK)
        {
            Map<String, ArrayList<Recipe>> map = response.getRecipeMap();
            if(map == null || map.isEmpty())
            {
                message = "No new recipes available.";
            }
            else
            {
                boolean saveToDiskSuccess = recipeManager.saveRecipesToDisk(map.values());
                if(saveToDiskSuccess)
                {
                    int newCount = response.getRecentlyAddedCount();
                    if(newCount > 1)
                    {
                        message = newCount + " new recipes added.";
                    }
                    else
                    {
                        message = newCount + " new recipe added.";
                    }

                    // Get recipes to be returned to RecipeListFragment
                    ArrayList<Recipe> list = recipeManager.getRecipesFromMap(map, newCount, settings.getCategory());
                    this.sendResult(list, Activity.RESULT_OK);
                }
                else
                {
                    message = "Failed saving to disk.";
                }
            }
        }
        else
        {
            message = response.getStatusCode() + ". " + response.getText();
        }

        this.sendResult(new ArrayList<Recipe>(0), Activity.RESULT_OK);
        return message;
    }

    /**
     * Removes the dialog from screen, shows the result of the operation on toast, and sets the isUpdating flag to false.
     */
    @Override
    public void onPostExecute(String message)
    {
        this.fragment.dismiss();
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
        this.isUpdating.set(false);
    }

    /**
     * Sets the isUpdating flag to false.
     */
    @Override
    protected void onCancelled(String message)
    {
        super.onCancelled();
        this.isUpdating.set(false);
    }
}