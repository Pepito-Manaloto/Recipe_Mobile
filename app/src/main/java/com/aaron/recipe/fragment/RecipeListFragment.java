package com.aaron.recipe.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.activity.AboutActivity;
import com.aaron.recipe.activity.LogsActivity;
import com.aaron.recipe.activity.SettingsActivity;
import com.aaron.recipe.adapter.RecipeListRowAdapter;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.listener.RecipeSearchListener;
import com.aaron.recipe.listener.ShowHideFastScrollListener;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import java.util.ArrayList;

import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

public class RecipeListFragment extends ListFragment
{
    private enum MenuRequest
    {
        UPDATE(0), SETTINGS(1), ABOUT(2), LOGS(3);

        private int code;

        MenuRequest(int code)
        {
            this.code = code;
        }

        int getCode()
        {
            return code;
        }
    }

    public static final String CLASS_NAME = RecipeListFragment.class.getSimpleName();
    private static final String DIALOG_UPDATE = "update";
    private ArrayList<Recipe> list;
    private Settings settings;
    private RecipeManager recipeManager;
    private RecipeListRowAdapter recipeAdapter;

    public static final String EXTRA_RECIPE_LIST = "com.aaron.recipe.fragment.recipe_list.list";

    /**
     * Initializes non-fragment user interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            this.settings = savedInstanceState.getParcelable(EXTRA_SETTINGS);

            // But we are sure of its type
            this.list = savedInstanceState.getParcelableArrayList(EXTRA_RECIPE_LIST);
        }

        if(this.settings == null)
        {
            this.settings = new Settings();
        }

        this.recipeManager = new RecipeManager(getActivity());

        if(this.list == null)
        {
            this.list = this.recipeManager.getRecipesFromDisk(this.settings.getCategory());
        }

        this.recipeAdapter = new RecipeListRowAdapter(getActivity(), this.list, this.settings);
        this.setListAdapter(this.recipeAdapter);

        setHasOptionsMenu(true);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate. settings=" + this.settings);
        LogsManager.addToLogs(CLASS_NAME + ": onCreate. settings=" + this.settings);
    }

    /**
     * Initializes the fragment's view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_list, parent, false);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateView.");

        return view;
    }

    /**
     * Called after onCreateView(), sets the action listeners of the UI.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnScrollListener(new ShowHideFastScrollListener());

        Log.d(LogsManager.TAG, CLASS_NAME + ": onActivityCreated.");
    }

    /**
     * Saves current state and settings in memory. For screen rotation.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_SETTINGS, this.settings);
        outState.putParcelableArrayList(EXTRA_RECIPE_LIST, this.list);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onSaveInstanceState");
    }

    /**
     * Receives the result data from the previous fragment. Updates the application's state depending on the data received.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }

        Log.d(LogsManager.TAG, CLASS_NAME + ": onActivityResult. requestCode=" + requestCode + " resultCode=" + resultCode);
        LogsManager.addToLogs(CLASS_NAME + ": onActivityResult. requestCode=" + requestCode + " resultCode=" + resultCode);

        boolean activityFromUpdateFragmentWithExtraData = requestCode == MenuRequest.UPDATE.getCode() && data != null
                && data.hasExtra(UpdateFragment.EXTRA_RECIPE_LIST);
        if(activityFromUpdateFragmentWithExtraData)
        {
            // But we are sure of its type
            @SuppressWarnings("unchecked")
            ArrayList<Recipe> list = data.getParcelableArrayListExtra(UpdateFragment.EXTRA_RECIPE_LIST);

            // Handles occasional NullPointerException.
            if(list != null && !list.isEmpty())
            {
                this.list = list;
            }
            else
            {
                this.list = this.recipeManager.getRecipesFromDisk(this.settings.getCategory());
            }

            updateListOnUiThread(this.list);

            return;
        }

        boolean activityFromSettingsOrAboutOrLogsFragment = requestCode == MenuRequest.SETTINGS.getCode() || requestCode == MenuRequest.ABOUT.getCode()
                || requestCode == MenuRequest.LOGS.getCode();
        boolean settingsHasExtraData = data != null && data.hasExtra(EXTRA_SETTINGS);
        if(activityFromSettingsOrAboutOrLogsFragment && settingsHasExtraData)
        {
            this.settings = data.getParcelableExtra(EXTRA_SETTINGS);

            this.list = this.recipeManager.getRecipesFromDisk(this.settings.getCategory());
            this.updateListOnUiThread(this.list);
        }
    }

    /**
     * Updates the list view on UI thread.
     *
     * @param list
     *            the new list
     */
    private void updateListOnUiThread(final ArrayList<Recipe> list)
    {
        if(list == null)
        {
            return;
        }

        Runnable updateListInAdapter = () -> recipeAdapter.update(list);
        getActivity().runOnUiThread(updateListInAdapter);
    }

    /**
     * Inflates the menu items in the action bar.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipe, menu);

        // Get the action view of the menu item whose id is edittext_search_field
        View view = menu.findItem(R.id.menu_search).getActionView();

        // Get the edit text from the action view
        EditText searchTextfield = view.findViewById(R.id.edittext_search_field);
        searchTextfield.setHint(R.string.hint_recipe);
        searchTextfield.addTextChangedListener(new RecipeSearchListener(this.recipeAdapter));
    }

    /**
     * This method is called when a user selects an item in the menu bar. Opens the fragment of selected item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        FragmentManager fm = getActivity().getFragmentManager();

        switch(item.getItemId())
        {
            case R.id.menu_search:
            {
                return true;
            }
            case R.id.menu_update:
            {
                UpdateFragment updateDialog = UpdateFragment.newInstance(this.settings);

                if(updateDialog.isUpdating())
                {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.dialog_already_updating_message), Toast.LENGTH_LONG).show();
                }
                else
                {
                    updateDialog.setTargetFragment(this, MenuRequest.UPDATE.getCode());
                    updateDialog.show(fm, DIALOG_UPDATE);
                }

                return true;
            }
            case R.id.menu_settings:
            {
                startNextActivityWithExtraSettingsData(SettingsActivity.class, MenuRequest.SETTINGS);
                return true;
            }
            case R.id.menu_about:
            {
                startNextActivityWithExtraSettingsData(AboutActivity.class, MenuRequest.ABOUT);
                return true;
            }
            case R.id.menu_logs:
            {
                startNextActivityWithExtraSettingsData(LogsActivity.class, MenuRequest.LOGS);
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void startNextActivityWithExtraSettingsData(Class<? extends Activity> nextActivityClass, MenuRequest requestCode)
    {
        Intent intent = new Intent(getActivity(), nextActivityClass);
        intent.putExtra(EXTRA_SETTINGS, this.settings);
        startActivityForResult(intent, requestCode.getCode());
    }
}
