package com.aaron.recipe.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.listener.BackButtonListener;
import com.aaron.recipe.listener.LogsSearchListener;
import com.aaron.recipe.model.LogsManager;

import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * The application logs fragment.
 */
public class LogsFragment extends Fragment implements Backable
{
    public static final String CLASS_NAME = LogsFragment.class.getSimpleName();
    private TextView textarea;
    private LogsManager logsManager;
    private Settings settings;

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.menu_logs);
        initializeActionBar();

        this.settings = getActivity().getIntent().getParcelableExtra(EXTRA_SETTINGS);
        this.logsManager = new LogsManager();

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate.");
    }

    private void initializeActionBar()
    {
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes logs fragment user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_logs, parent, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new BackButtonListener(this));

        this.textarea = view.findViewById(R.id.textarea_logs);
        this.textarea.setText(this.logsManager.getLogs());
        this.textarea.setMovementMethod(new ScrollingMovementMethod());

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateView.");

        return view;
    }

    /**
     * Inflates the menu items in the action bar.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipe_search_only, menu);

        // Get the action view of the menu item whose id is edittext_search_field
        View view = menu.findItem(R.id.menu_search).getActionView();

        // Get the edit text from the action view
        EditText searchTextfield = view.findViewById(R.id.edittext_search_field);
        searchTextfield.setHint(R.string.hint_logs);

        searchTextfield.addTextChangedListener(new LogsSearchListener(this, logsManager));
    }

    /**
     * This method is called when a user selects an item in the menu bar. Home button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                this.setFragmentActivityResult();
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void setActivityResultOnBackEvent()
    {
        setFragmentActivityResult();
    }

    /**
     * Sets the current settings and sends it to the main activity fragment.
     */
    private void setFragmentActivityResult()
    {
        Intent data = new Intent();

        data.putExtra(EXTRA_SETTINGS, this.settings);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();

        LogsManager.log(CLASS_NAME, "setFragmentAcivityResult", "Current settings -> " + this.settings);
    }

    public void setTextareaText(final String text)
    {
        this.textarea.setText(text);
    }
}
