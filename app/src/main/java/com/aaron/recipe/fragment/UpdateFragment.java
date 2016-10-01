package com.aaron.recipe.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.ResponseRecipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The update dialog fragment that retrieves recipe list from the server.
 */
public class UpdateFragment extends DialogFragment
{
    public static final String CLASS_NAME = UpdateFragment.class.getSimpleName();
    public static final String EXTRA_RECIPE_LIST = "com.aaron.recipe.fragment.update.list";
    private RecipeManager recipeManager;
    private Settings settings;
    private String url;
    private static final AtomicBoolean isUpdating = new AtomicBoolean(false);

    /**
     * Creates a new UpdateFragment and sets its arguments.
     *
     * @return UpdateFragment
     */
    public static UpdateFragment newInstance(final Settings settings)
    {
        Bundle args = new Bundle();
        args.putSerializable(SettingsFragment.EXTRA_SETTINGS, settings);
        UpdateFragment fragment = new UpdateFragment();
        fragment.setArguments(args);

        Log.d(LogsManager.TAG, CLASS_NAME + ": newInstance. settings=" + settings);

        return fragment;
    }

    /**
     * Creates the update dialog box.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Activity activity = getActivity();

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(getString(R.string.dialog_update_title));
        progressDialog.setMessage(getString(R.string.dialog_update_message));
        progressDialog.setIndeterminate(true);

        this.settings = (Settings) getArguments().getSerializable(SettingsFragment.EXTRA_SETTINGS);
        if(settings != null && settings.getServerURL() != null && !settings.getServerURL().isEmpty())
        {
            this.url = "http://" + settings.getServerURL() + activity.getString(R.string.url_resource);
        }
        else
        {
            this.url = "http://" + activity.getString(R.string.url_address_default) + activity.getString(R.string.url_resource);
        }

        this.recipeManager = new RecipeManager(activity);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateDialog.");
        LogsManager.addToLogs(CLASS_NAME + ": onCreateDialog.");

        return progressDialog;
    }

    /**
     * Start the retriever thread.
     */
    @Override
    public void onStart()
    {
        super.onStart();

        if(!isUpdating())
        {
            RecipeRetrieverThread recipeRetrieverThread = new RecipeRetrieverThread(getActivity());
            recipeRetrieverThread.execute();
            isUpdating.set(true);
        }

        Log.d(LogsManager.TAG, CLASS_NAME + ": onStart");
    }

    /**
     * Returns true if RecipeRetrieverThread is already executing update.
     *
     * @return boolean
     */
    public boolean isUpdating()
    {
        return isUpdating.get();
    }

    /**
     * Helper thread class that does the retrieval of the recipe list from the server.
     */
    private class RecipeRetrieverThread extends AsyncTask<Void, Void, String>
    {
        private Context context;

        RecipeRetrieverThread(Activity activity)
        {
            this.context = activity;
        }

        /**
         * Encapsulates the recipe list and response to an intent and sends the intent + resultCode to VocaublaryListFragment.
         *
         * @param vocabList  the retrieved recipe list
         * @param resultCode the result of the operation
         */
        private void sendResult(final ArrayList<Recipe> vocabList, final int resultCode)
        {
            if(getTargetFragment() == null)
            {
                return;
            }

            Intent data = new Intent();
            data.putExtra(EXTRA_RECIPE_LIST, vocabList);
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);

            Log.d(LogsManager.TAG, CLASS_NAME + "(recipeRetrieverThread): sendResult. list=" + vocabList);
            LogsManager.addToLogs(CLASS_NAME + "(recipeRetrieverThread): sendResult. list_size=" + vocabList.size());
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
                EnumMap<Recipe.Category, ArrayList<Recipe>> map = response.getRecipeMap();
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
            UpdateFragment.this.dismiss();
            Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
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
    }
}
