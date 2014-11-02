package com.aaron.recipe.fragment;

import com.aaron.recipe.R;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.aaron.recipe.model.RecipeManager.*;
/**
 * The application about fragment.
 */
public class AboutFragment extends Fragment
{
    public static final String TAG = "AboutFragment";
    private RecipeManager recipeManager;

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.menu_about);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        this.recipeManager = new RecipeManager(getActivity());

        Log.d(LogsManager.TAG, "AboutFragment: onCreate.");
    }

    /**
     * Initializes about fragment user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_about, parent, false);

        view.setOnLongClickListener(new OnLongClickListener()
            {
                /**
                 * If yes is selected, vocabularies on disk will be deleted.
                 */
                @Override
                public boolean onLongClick(View arg0)
                {
                    promptUserOnDelete();
                    return true;
                }
            });

        final TextView buildNumberTextView = (TextView) view.findViewById(R.id.text_build_number);
        TextView lastUpdatedTextView = (TextView) view.findViewById(R.id.text_last_updated);

        String buildNumber = getActivity().getString(R.string.build_num);
        String lastUpdated = this.recipeManager.getLastUpdated(DATE_FORMAT_LONG);

        buildNumberTextView.setText(buildNumber);
        lastUpdatedTextView.setText(lastUpdated);

        Log.d(LogsManager.TAG, "AboutFragment: onCreateView.");

        return view;
    }

    /**
     * Pops-up a prompt dialog with 'yes' or 'no' button.
     * Selecting 'yes' will delete all recipes from disk. 
     */
    private void promptUserOnDelete()
    {
        Log.d(LogsManager.TAG, "AboutFragment: promptUserOnDelete.");
        LogsManager.addToLogs("AboutFragment: promptUserOnDelete.");

        AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
        prompt.setMessage("Delete vocabularies from disk?");

        prompt.setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    Log.d(LogsManager.TAG, "AboutFragment: promptUserOnDelete. Yes selected.");
                    LogsManager.addToLogs("AboutFragment: promptUserOnDelete. Yes selected.");

                    recipeManager.deleteRecipeFromDisk();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            });
        prompt.setNegativeButton("No", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    Log.d(LogsManager.TAG, "AboutFragment: promptUserOnDelete. No selected.");
                    LogsManager.addToLogs("AboutFragment: promptUserOnDelete. No selected.");

                    dialog.cancel();
                }
            });

        prompt.create()
              .show();
    }
}
