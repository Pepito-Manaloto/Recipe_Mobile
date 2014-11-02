package com.aaron.recipe.fragment;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.bean.Settings.FontName;
import com.aaron.recipe.bean.Settings.FontStyle;
import com.aaron.recipe.model.LogsManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

/**
 * The application settings fragment.
 */
public class SettingsFragment extends Fragment
{
    public static final String TAG = "SettingsFragment";
    public static final String EXTRA_SETTINGS = "com.aaron.recipe.fragment.settings";
    private Settings settings;

    private Spinner fontNameSpinner;
    private Spinner fontStyleSpinner;
    private Spinner fontSizeSpinner;

    /**
     * Returns a new SettingsFragment with the given settings as arguments.
     */
    public static SettingsFragment newInstance(final Settings settings)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SETTINGS, settings);
        
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);

        Log.d(LogsManager.TAG, "SettingsFragment: newInstance. settings=" + settings);

        return fragment;
    }

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.settings = (Settings) getArguments().getSerializable(SettingsFragment.EXTRA_SETTINGS);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.menu_settings);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(LogsManager.TAG, "SettingsFragment: onCreate. settings=" + this.settings);
        LogsManager.addToLogs("SettingsFragment: onCreate. settings=" + this.settings);
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, parent, false);

        this.fontNameSpinner = (Spinner) view.findViewById(R.id.spinner_font_name);
        this.fontStyleSpinner = (Spinner) view.findViewById(R.id.spinner_font_style);
        this.fontSizeSpinner = (Spinner) view.findViewById(R.id.spinner_font_size);

        this.fontNameSpinner.setSelection(this.settings.getFontNameIndex());
        this.fontStyleSpinner.setSelection(this.settings.getFontStyleIndex());
        this.fontSizeSpinner.setSelection(this.settings.getFontSizeIndex());

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener()
            {
                /**
                 * Handles back button.
                 */
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) 
                {
                    // For back button
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    {
                        setFragmentAcivityResult();
                        return true;
                    } 
                    else 
                    {
                        return false;
                    }
                }
            });

        Log.d(LogsManager.TAG, "SettingsFragment: onCreateView");

        return view;
    }

    /**
     * This method is called when a user selects an item in the menu bar. Home button.
     * the fragment of selected item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                this.setFragmentAcivityResult();
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Sets the new settings and sends it to the main activity fragment.
     */
    private void setFragmentAcivityResult()
    {
        Intent data = new Intent();

        FontName fontName = FontName.valueOf(this.fontNameSpinner.getSelectedItem().toString());
        FontStyle fontStyle = FontStyle.valueOf(this.fontStyleSpinner.getSelectedItem().toString());
        int fontSize = Integer.parseInt(this.fontSizeSpinner.getSelectedItem().toString());

        this.settings.setFontName(fontName)
                     .setFontStyle(fontStyle)
                     .setFontSize(fontSize);

        data.putExtra(EXTRA_SETTINGS, this.settings);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();

        Log.d(LogsManager.TAG, "SettingsFragment: setFragmentAcivityResult. New settings -> " + this.settings);
        LogsManager.addToLogs("SettingsFragment: setFragmentAcivityResult. New settings -> " + this.settings);
    }
}