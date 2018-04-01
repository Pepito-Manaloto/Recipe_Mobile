package com.aaron.recipe.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.aaron.recipe.R;
import com.aaron.recipe.async.RecipeRetrieverThread;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The update dialog fragment that retrieves recipe list from the server.
 */
@Deprecated
public class UpdateFragment extends DialogFragment
{
    public static final String CLASS_NAME = UpdateFragment.class.getSimpleName();
    public static final String EXTRA_RECIPE_LIST = "com.aaron.recipe.fragment.update.list";
    private Settings settings;
    private static final AtomicBoolean isUpdating = new AtomicBoolean(false);

    /**
     * Creates a new UpdateFragment and sets its arguments.
     *
     * @return UpdateFragment
     */
    public static UpdateFragment newInstance(final Settings settings)
    {
        Bundle args = new Bundle();
        args.putParcelable(SettingsFragment.EXTRA_SETTINGS, settings);
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

        // TODO: Replace with ProgressBar
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(getString(R.string.dialog_update_title));
        progressDialog.setMessage(getString(R.string.dialog_update_message));
        progressDialog.setIndeterminate(true);

        this.settings = getArguments().getParcelable(SettingsFragment.EXTRA_SETTINGS);

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
            RecipeRetrieverThread recipeRetrieverThread = new RecipeRetrieverThread(this, isUpdating, this.settings);
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
}
