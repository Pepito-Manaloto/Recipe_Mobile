package com.aaron.recipe.fragment;

import java.util.ArrayList;

import com.aaron.recipe.R;
import com.aaron.recipe.activity.LogsActivity;
import com.aaron.recipe.activity.AboutActivity;
import com.aaron.recipe.fragment.UpdateFragment;
import com.aaron.recipe.activity.SettingsActivity;
import com.aaron.recipe.adapter.RecipeListRowAdapter;
import com.aaron.recipe.fragment.SettingsFragment;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.AbsListView.OnScrollListener;

public class RecipeListFragment extends ListFragment
{
    public static final String TAG = "RecipeListFragment";
    private static final String DIALOG_UPDATE = "update";
    private static final int REQUEST_UPDATE = 0;
    private static final int REQUEST_SETTINGS = 1;
    private static final int REQUEST_ABOUT = 2;

    private ArrayList<Recipe> list;
    private Settings settings;
    private RecipeManager recipeManager;

    public static final String EXTRA_LIST = "com.aaron.recipe.fragment.list";
    public static final String EXTRA_RECIPE = "com.aaron.recipe.fragment.recipe";

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            this.settings = (Settings) savedInstanceState.getSerializable(SettingsFragment.EXTRA_SETTINGS);

            // But we are sure of its type
            @SuppressWarnings("unchecked")
            ArrayList<Recipe> listTemp = (ArrayList<Recipe>) savedInstanceState.getSerializable(EXTRA_LIST);

            if(listTemp != null)
            {
                this.list = listTemp;
            }
        }

        if(this.settings == null)
        {
            this.settings = new Settings();
        }

        this.recipeManager = new RecipeManager(getActivity(), this.settings.getCategory());

        if(this.list == null)
        {
            this.list = this.recipeManager.getRecipesFromDisk();
        }

        this.updateListOnUiThread(this.list);

        setHasOptionsMenu(true);

        Log.d(LogsManager.TAG, "RecipeListFragment: onCreate. settings=" + this.settings);
        LogsManager.addToLogs("RecipeListFragment: onCreate. settings=" + this.settings);

    }

    /**
     * Initializes the fragment's view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_list, parent, false);

        Log.d(LogsManager.TAG, "RecipeListFragment: onCreateView.");

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

        Log.d(LogsManager.TAG, "RecipeListFragment: onActivityCreated.");
    }

    /**
     * Saves current state and settings in memory. For screen rotation.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SettingsFragment.EXTRA_SETTINGS, this.settings);
        outState.putSerializable(EXTRA_LIST, this.list);

        Log.d(LogsManager.TAG, "RecipeListFragment: onSaveInstanceState");
    }

    /**
     * Receives the result data from the previous fragment. Updates the
     * application's state depending on the data received.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }
        
        Log.d(LogsManager.TAG, "RecipeListFragment: onActivityResult. requestCode=" + requestCode + " resultCode=" + resultCode);
        LogsManager.addToLogs("RecipeListFragment: onActivityResult. requestCode=" + requestCode + " resultCode=" + resultCode);

        // Update action bar menu processing result
        if(requestCode == REQUEST_UPDATE && data.hasExtra(UpdateFragment.EXTRA_RECIPE_LIST))
        {
            // But we are sure of its type
            @SuppressWarnings("unchecked")
            ArrayList<Recipe> list = (ArrayList<Recipe>) data.getSerializableExtra(UpdateFragment.EXTRA_RECIPE_LIST);

            // Handles occasional NullPointerException.
            if(list != null)
            {
                this.list = list;
            }

            this.updateListOnUiThread(this.list);
        }
        else if(requestCode == REQUEST_SETTINGS && data.hasExtra(SettingsFragment.EXTRA_SETTINGS))
        {
            this.settings = (Settings) data.getSerializableExtra(SettingsFragment.EXTRA_SETTINGS);

            this.list = this.recipeManager.getRecipesFromDisk();
            this.updateListOnUiThread(this.list);
        }
        else if(requestCode == REQUEST_ABOUT)
        {
            this.list = this.recipeManager.getRecipesFromDisk();
            this.updateListOnUiThread(this.list);
        }
    }

    /**
     * Inflates the menu items in the action bar.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipe, menu);

        /**
         * Get the action view of the menu item whose id is
         * edittext_search_field
         */
        View view = (View) menu.findItem(R.id.menu_search).getActionView();

        /** Get the edit text from the action view */
        final EditText searchTextfield = (EditText) view.findViewById(R.id.edittext_search_field);
        searchTextfield.setHint(R.string.hint_recipe);

        searchTextfield.addTextChangedListener(new TextWatcher()
            {
                /**
                 * Handles search on text update.
                 */
                @Override
                public void afterTextChanged(Editable arg0)
                {
                    String searched = searchTextfield.getText().toString();
                    RecipeListRowAdapter recipeAdapter = (RecipeListRowAdapter) getListAdapter();
                    recipeAdapter.filter(searched);
                    recipeAdapter.notifyDataSetChanged();
                    
                    Log.d(LogsManager.TAG, "RecipeListFragment: onCreateOptionsMenu(afterTextChanged). searched=" + searched);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
                {
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
                {
                }
            });
    }

    /**
     * This method is called when a user selects an item in the menu bar. Opens
     * the fragment of selected item.
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
                updateDialog.setTargetFragment(this, REQUEST_UPDATE);
                updateDialog.show(fm, DIALOG_UPDATE);

                return true;
            }
            case R.id.menu_settings:
            {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(SettingsFragment.EXTRA_SETTINGS, this.settings);
                startActivityForResult(intent, REQUEST_SETTINGS);

                return true;
            }
            case R.id.menu_about:
            {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivityForResult(intent, REQUEST_ABOUT);

                return true;
            }
            case R.id.menu_logs:
            {
                Intent intent = new Intent(getActivity(), LogsActivity.class);
                startActivity(intent);
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Updates the list view on UI thread.
     * @param list the new list
     */
    private void updateListOnUiThread(final ArrayList<Recipe> list)
    {
        if(list == null)
        {
            return;
        }

        this.getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    RecipeListRowAdapter recipeAdapter = new RecipeListRowAdapter(getActivity(), list, settings);
                    setListAdapter(recipeAdapter);
    
                    Log.d(LogsManager.TAG, "RecipeListFragment: updateListOnUiThread(run). settings=" + settings + " list=" + list);
                    LogsManager.addToLogs("RecipeListFragment: updateListOnUiThread(run). settings=" + settings + " list_size=" + list.size());
                }
            });
    }

    /**
     * Helper class for ListView's scroll listener.
     */
    private static class ShowHideFastScrollListener implements OnScrollListener
    {
        private static final int DELAY = 1000;
        private AbsListView view;

        private Handler handler = new Handler();
        // Runnable for handler object.
        private Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    view.setFastScrollAlwaysVisible(false);
                    view = null;
                }
            };

        /**
         * Show fast-scroll thumb if scrolling, and hides fast-scroll thumb if not scrolling.
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            if(scrollState != SCROLL_STATE_IDLE)
            {
                view.setFastScrollAlwaysVisible(true);
                
                // Removes the runnable from the message queue.
                // Stops the handler from hiding the fast-scroll.
                this.handler.removeCallbacks(this.runnable);
            }
            else
            {
                this.view = view;

                // Adds the runnable to the message queue, will run after the DELAY.
                // Hides the fast-scroll after one seconds of no scrolling.
                this.handler.postDelayed(this.runnable, DELAY); 
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
        }
    }
}
