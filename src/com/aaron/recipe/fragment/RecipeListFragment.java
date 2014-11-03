package com.aaron.recipe.fragment;

import java.util.ArrayList;

import com.aaron.recipe.R;
import com.aaron.recipe.activity.LogsActivity;
import com.aaron.recipe.activity.AboutActivity;
import com.aaron.recipe.fragment.UpdateFragment;
import com.aaron.recipe.activity.SettingsActivity;
import com.aaron.recipe.fragment.SettingsFragment;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;

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
    private static final String DIALOG_UPDATE = "update";
    private static final int REQUEST_UPDATE = 0;
    private static final int REQUEST_SETTINGS = 1;
    private static final int REQUEST_ABOUT = 2;

    private Settings settings;

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
        }

        if(this.settings == null)
        {
            this.settings = new Settings();
        }

        setHasOptionsMenu(true);
        
        Log.d(LogsManager.TAG, "RecipeListFragment: onCreate. settings=" + this.settings);
        LogsManager.addToLogs("RecipeListFragment: onCreate. settings=" + this.settings);

    }

    /**
     * Initializes the fragment's user interface.
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
    }

    /**
     * Saves current state and settings in memory. For screen rotation.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SettingsFragment.EXTRA_SETTINGS, this.settings);
        //outState.putSerializable(EXTRA_LIST, this.list);

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
        
        Log.d(LogsManager.TAG, "VocabularyListFragment: onActivityResult. requestCode=" + requestCode + " resultCode=" + resultCode);
        LogsManager.addToLogs("VocabularyListFragment: onActivityResult. requestCode=" + requestCode + " resultCode=" + resultCode);

        // Update action bar menu processing result
        if(requestCode == REQUEST_UPDATE)// && data.hasExtra(UpdateFragment.EXTRA_VOCABULARY_LIST))
        {
            //this.updateListOnUiThread(this.list);
        }
        else if(requestCode == REQUEST_SETTINGS && data.hasExtra(SettingsFragment.EXTRA_SETTINGS))
        {
            this.settings = (Settings) data.getSerializableExtra(SettingsFragment.EXTRA_SETTINGS);

        }
        else if(requestCode == REQUEST_ABOUT)
        {
            //this.list = this.vocabularyManager.getVocabulariesFromDisk();
            //this.updateListOnUiThread(this.list);
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
                UpdateFragment updateDialog = new UpdateFragment();
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
        this.getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //VocabularyAdapter vocabularyAdapter = new VocabularyAdapter(getActivity(), list, settings);
                    //setListAdapter(vocabularyAdapter);
    
                    Log.d(LogsManager.TAG, "VocabularyListFragment: updateListOnUiThread(run). settings=" + settings + " list=" + list);
                    LogsManager.addToLogs("VocabularyListFragment: updateListOnUiThread(run). settings=" + settings + " list_size=" + list.size());
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
                // Hides the fast-scroll after two seconds of no scrolling.
                this.handler.postDelayed(this.runnable, DELAY); 
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
        }
    }
}
