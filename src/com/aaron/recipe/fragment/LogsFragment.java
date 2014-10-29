package com.aaron.recipe.fragment;

import com.aaron.recipe.R;
import com.aaron.recipe.model.LogsManager;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The application logs fragment.
 */
public class LogsFragment extends Fragment
{
    public static final String TAG = "LogsFragment";
    private TextView textarea;
    private LogsManager logsManager;

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.menu_logs);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        this.logsManager = new LogsManager(); 

        Log.d(LogsManager.TAG, "LogsFragment: onCreate.");
    }

    /**
     * Initializes logs fragment user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_logs, parent, false);

        this.textarea = (TextView) view.findViewById(R.id.textarea_logs);
        this.textarea.setText(this.logsManager.getLogs());
        this.textarea.setMovementMethod(new ScrollingMovementMethod());

        Log.d(LogsManager.TAG, "LogsFragment: onCreateView.");

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

        /** Get the action view of the menu item whose id is edittext_search_field */
        View view = (View) menu.findItem(R.id.menu_search).getActionView();
        
        /** Get the edit text from the action view */
        final EditText searchTextfield = (EditText) view.findViewById(R.id.edittext_search_field);
        searchTextfield.setHint(R.string.hint_logs);

        searchTextfield.addTextChangedListener(new TextWatcher()
            {
                /**
                 * Handles search on text update.
                 */
                @Override
                public void afterTextChanged(Editable arg0)
                {
                    String searched = searchTextfield.getText().toString();
                    
                    if(searched.length() <= 0)
                    {
                        textarea.setText(logsManager.getLogs());
                    }
                    else
                    {
                        textarea.setText(logsManager.getLogs(searched));
                    }

                    Log.d(LogsManager.TAG, "LogsFragment: onCreateOptionsMenu(afterTextChanged). searched=" + searched);
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
}
