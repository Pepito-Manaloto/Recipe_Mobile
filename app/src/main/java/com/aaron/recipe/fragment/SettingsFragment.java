package com.aaron.recipe.fragment;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.bean.Settings.FontName;
import com.aaron.recipe.bean.Settings.FontStyle;
import com.aaron.recipe.model.LogsManager;

import static com.aaron.recipe.bean.Recipe.CATEGORY_ARRAY;
import static com.aaron.recipe.bean.Recipe.Category;

/**
 * The application settings fragment.
 */
public class SettingsFragment extends Fragment
{
    public static final String TAG = "SettingsFragment";
    public static final String EXTRA_SETTINGS = "com.aaron.recipe.fragment.settings";
    private Settings settings;

    private ArrayAdapter<Category> categoryAdapter;

    private Spinner categorySpinner;
    private Spinner fontNameSpinner;
    private Spinner fontStyleSpinner;
    private Spinner fontSizeSpinner;

    private EditText serverURLEditText;

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

        this.categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CATEGORY_ARRAY);
        this.categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        this.categorySpinner = (Spinner) view.findViewById(R.id.spinner_category);
        this.categorySpinner.setAdapter(this.categoryAdapter);

        this.fontNameSpinner = (Spinner) view.findViewById(R.id.spinner_font_name);
        this.fontStyleSpinner = (Spinner) view.findViewById(R.id.spinner_font_style);
        this.fontSizeSpinner = (Spinner) view.findViewById(R.id.spinner_font_size);
        this.serverURLEditText = (EditText) view.findViewById(R.id.edittext_server_url);

        this.categorySpinner.setSelection(this.settings.getCategoryIndex());
        this.fontNameSpinner.setSelection(this.settings.getFontNameIndex());
        this.fontStyleSpinner.setSelection(this.settings.getFontStyleIndex());
        this.fontSizeSpinner.setSelection(this.settings.getFontSizeIndex());

        String serverUrl = this.settings.getServerURL();
        
        if(serverUrl.isEmpty())
        {
            serverUrl = getActivity().getString(R.string.url_address_default);
        }

        this.serverURLEditText.setText(serverUrl);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new BackButtonListener());
        this.serverURLEditText.setOnKeyListener(new BackButtonListener());

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

        Category category = Category.valueOf(this.categorySpinner.getSelectedItem().toString());
        FontName fontName = FontName.valueOf(this.fontNameSpinner.getSelectedItem().toString());
        FontStyle fontStyle = FontStyle.valueOf(this.fontStyleSpinner.getSelectedItem().toString());
        int fontSize = Integer.parseInt(this.fontSizeSpinner.getSelectedItem().toString());
        String serverURL = this.serverURLEditText.getText().toString();

        this.settings.setCategory(category)
                     .setFontName(fontName)
                     .setFontStyle(fontStyle)
                     .setFontSize(fontSize)
                     .setServerURL(serverURL);

        data.putExtra(EXTRA_SETTINGS, this.settings);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();

        Log.d(LogsManager.TAG, "SettingsFragment: setFragmentAcivityResult. New settings -> " + this.settings);
        LogsManager.addToLogs("SettingsFragment: setFragmentAcivityResult. New settings -> " + this.settings);
    }

    private class BackButtonListener implements View.OnKeyListener
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
    }
}