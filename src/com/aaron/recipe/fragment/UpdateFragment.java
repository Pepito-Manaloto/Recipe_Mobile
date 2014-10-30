package com.aaron.recipe.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

/**
 * The update dialog fragment that retrieves recipe list from the server. 
 */
public class UpdateFragment extends DialogFragment
{
    public static final String TAG = "UpdateFragment";
    public static final String EXTRA_RECIPE_LIST = "com.aaron.recipe.fragment.recipe_list";
    private RecipeManager recipeManager;
    private RecipeRetrieverThread recipeRetrieverThread;

    /**
     * Creates the update dialog box.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.dialog_update_title));
        progressDialog.setMessage(getString(R.string.dialog_update_message));
        progressDialog.setIndeterminate(true);

        this.recipeManager = new RecipeManager(getActivity());
        this.recipeRetrieverThread = new RecipeRetrieverThread();

        Log.d(LogsManager.TAG, "UpdateFragment: onCreateDialog.");
        LogsManager.addToLogs("UpdateFragment: onCreateDialog.");

        return progressDialog;
    }

    /**
     * Start the retriever thread.
     */
    @Override
    public void onStart()
    {
        super.onStart();
        this.recipeRetrieverThread.execute();
        Log.d(LogsManager.TAG, "UpdateFragment: onStart");
    }

    /**
     * Called when dialog is cancelled before finishing its task. Stops the retriever thread.
     */
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        this.recipeRetrieverThread.cancel(true);
    }

    /**
     * Helper thread class that does the retrieval of the recipe list from the server.
     */
    private class RecipeRetrieverThread extends AsyncTask<Void, Void, String>
    {
        /**
         * Encapsulates the recipe list and response to an intent and sends the intent + resultCode to VocaublaryListFragment.
         * @param vocabList the retrieved recipe list
         * @param response the response of the web call
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

            Log.d(LogsManager.TAG, "UpdateFragment(recipeRetrieverThread): sendResult. list=" + vocabList);
            LogsManager.addToLogs("UpdateFragment(recipeRetrieverThread): sendResult. list_size=" + vocabList.size());
        }

        /**
         * Retrieves data from server (also save to local disk) then returns the data, encapsulated in the intent, to VocaublaryListFragment.
         */
        @Override
        protected String doInBackground(Void... arg0)
        {
            ArrayList<Recipe> list = new ArrayList<>();

            list = recipeManager.getRecipesFromWeb();
            String responseCode = recipeManager.getStatusText();
            String responseText = recipeManager.getResponseText();

            this.sendResult(list, Activity.RESULT_OK);

            int newCount = recipeManager.getRecentlyAddedCount();
            String message = "";

            if("Ok".equals(responseCode))
            {
                if(!"Success".equals(responseText))
                {
                    message = responseText;
                }
                else if(newCount > 1)
                {
                    message = newCount + " new recipes added.";
                }
                else if(newCount == 1)
                {
                    message = newCount + " new recipe added.";
                }
                else
                {
                    message = "No new recipes available.";
                }
            }
            else
            {
                message = responseCode + ". " + responseText;
            }

            return message;
        }

        /**
         * Removes the dialog from screen, and shows the result of the operation on toast.
         */
        @Override
        public void onPostExecute(String message)
        {
            UpdateFragment.this.dismiss();
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
