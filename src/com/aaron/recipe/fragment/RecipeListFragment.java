package com.aaron.recipe.fragment;

import java.util.ArrayList;

import com.aaron.recipe.R;

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
    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_list, parent, false);

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
     * Receives the result data from the previous fragment. Updates the
     * application's state depending on the data received.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        
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
        switch(item.getItemId())
        {
            case R.id.menu_search:
            {
                return true;
            }
            case R.id.menu_update:
            {

                return true;
            }
            case R.id.menu_settings:
            {

                return true;
            }
            case R.id.menu_about:
            {

                return true;
            }
            case R.id.menu_logs:
            {

                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
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
