package com.aaron.recipe.fragment;

import android.app.Activity;
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
import android.widget.ProgressBar;
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
import com.aaron.recipe.model.CategoryManager;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

public class RecipeListFragment extends ListFragment
{
    private enum MenuRequest
    {
        SETTINGS(1), ABOUT(2), LOGS(3);

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
    public static final String EXTRA_RECIPE_LIST = "com.aaron.recipe.fragment.recipe_list.list";
    private static final AtomicBoolean IS_UPDATING = new AtomicBoolean(false);

    private ArrayList<Recipe> list;
    private Settings settings;
    private CategoryManager categoryManager;
    private RecipeManager recipeManager;
    private RecipeListRowAdapter recipeAdapter;
    private ProgressBar updateProgressBar;

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

        this.categoryManager = new CategoryManager(getContext());
        this.recipeManager = new RecipeManager(getContext());

        if(this.list == null)
        {
            this.list = this.recipeManager.getRecipesFromDisk(this.settings.getCategory());
        }

        this.recipeAdapter = new RecipeListRowAdapter(getActivity(), this.list, this.settings);
        this.setListAdapter(this.recipeAdapter);

        setHasOptionsMenu(true);

        LogsManager.log(CLASS_NAME, "onCreate", "settings=" + this.settings);
    }

    /**
     * Initializes the fragment's view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_list, parent, false);

        updateProgressBar = view.findViewById(R.id.progress_bar_update);
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

        LogsManager.log(CLASS_NAME, "onActivityResult", "requestCode=" + requestCode + " resultCode=" + resultCode);

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
        switch(item.getItemId())
        {
            case R.id.menu_search:
            {
                return true;
            }
            case R.id.menu_update:
            {
                if(!IS_UPDATING.get())
                {
                    Log.d(LogsManager.TAG, CLASS_NAME + ": onOptionsItemSelected. Updating vocabularies.");
                    preUpdating();
                    categoryManager.updateCategories(this::updateRecipes);
                }
                else
                {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.dialog_already_updating_message), Toast.LENGTH_LONG).show();
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

    @Override
    public void onStop()
    {
        super.onStop();
        CategoryManager.clearCategoriesWebObserver();
        RecipeManager.clearRecipesWebObserver();
    }

    private void preUpdating()
    {
        IS_UPDATING.set(true);
        updateProgressBar.setVisibility(View.VISIBLE);
    }

    public void updateRecipes()
    {
        CategoryManager.doneUpdating();
        recipeManager.updateRecipesFromWeb(this::doneUpdating, this::updateRecipeList);
    }

    private void doneUpdating()
    {
        IS_UPDATING.set(false);
        updateProgressBar.setVisibility(View.INVISIBLE);
    }

    private void updateRecipeList(ArrayList<Recipe> recipeList)
    {
        if(recipeList != null && !recipeList.isEmpty())
        {
            this.list = recipeList;
            updateListOnUiThread(this.list);
        }
    }

    private void startNextActivityWithExtraSettingsData(Class<? extends Activity> nextActivityClass, MenuRequest requestCode)
    {
        Intent intent = new Intent(getActivity(), nextActivityClass);
        intent.putExtra(EXTRA_SETTINGS, this.settings);
        startActivityForResult(intent, requestCode.getCode());
    }
}
