package com.aaron.recipe.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.bean.Settings.FontName;
import com.aaron.recipe.bean.Settings.FontStyle;
import com.aaron.recipe.listener.BackButtonListener;
import com.aaron.recipe.listener.ServerUrlTextListener;
import com.aaron.recipe.listener.UpdateCategoriesListener;
import com.aaron.recipe.model.CategoryManager;
import com.aaron.recipe.model.LogsManager;

import static com.aaron.recipe.bean.DataKey.EXTRA_SETTINGS;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * The application settings fragment.
 */
public class SettingsFragment extends Fragment implements Backable
{
    public static final String CLASS_NAME = SettingsFragment.class.getSimpleName();
    private Settings settings;

    private ArrayAdapter<String> categoryAdapter;

    private Spinner categorySpinner;
    private Spinner fontNameSpinner;
    private Spinner fontStyleSpinner;
    private Spinner fontSizeSpinner;

    private ImageView categoryImageView;
    private EditText serverURLEditText;

    private CategoryManager categoryManager;

    /**
     * Returns a new SettingsFragment with the given settings as arguments.
     */
    public static SettingsFragment newInstance(SettingsFragment fragment, final Settings settings)
    {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SETTINGS.toString(), settings);

        SettingsFragment settingsFragment = newSettingsFragment(fragment);
        settingsFragment.setArguments(args);

        Log.d(LogsManager.TAG, CLASS_NAME + ": newInstance. settings=" + settings);

        return settingsFragment;
    }

    private static SettingsFragment newSettingsFragment(SettingsFragment fragment)
    {
        SettingsFragment settingsFragment = fragment;
        if(settingsFragment == null)
        {
            settingsFragment = new SettingsFragment();
        }

        return settingsFragment;
    }

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.menu_settings);
        initializeActionBar();

        this.settings = getArguments().getParcelable(EXTRA_SETTINGS.toString());
        this.categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Categories.getCategoriesArray());
        this.categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.categoryManager = new CategoryManager(getContext());

        LogsManager.log(CLASS_NAME, "onCreate", "settings=" + this.settings);
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
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        BackButtonListener backButtonListener = new BackButtonListener(this);
        View view = inflater.inflate(R.layout.fragment_settings, parent, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(backButtonListener);

        this.categoryImageView = view.findViewById(R.id.imageview_refresh_category);
        this.categoryImageView.setClickable(true);
        this.categoryImageView.setOnClickListener(new UpdateCategoriesListener(this, categoryManager));

        this.categorySpinner = view.findViewById(R.id.spinner_category);
        this.categorySpinner.setAdapter(this.categoryAdapter);
        this.categorySpinner.setSelection(this.settings.getCategoryIndex());

        this.fontNameSpinner = view.findViewById(R.id.spinner_font_name);
        this.fontNameSpinner.setSelection(this.settings.getFontNameIndex());

        this.fontStyleSpinner = view.findViewById(R.id.spinner_font_style);
        this.fontStyleSpinner.setSelection(this.settings.getFontStyleIndex());

        this.fontSizeSpinner = view.findViewById(R.id.spinner_font_size);
        this.fontSizeSpinner.setSelection(this.settings.getFontSizeIndex());

        this.serverURLEditText = view.findViewById(R.id.edittext_server_url);
        this.serverURLEditText.addTextChangedListener(new ServerUrlTextListener(this.settings));
        String serverUrl = getServerUrl();
        this.serverURLEditText.setText(serverUrl);
        this.serverURLEditText.setOnKeyListener(backButtonListener);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateView");

        return view;
    }

    public String getServerUrl()
    {
        String url = this.settings.getServerURL();
        if(isBlank(url))
        {
            url = getActivity().getString(R.string.url_address_default);
        }

        return url;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        categoryImageView.clearAnimation();
        CategoryManager.clearCategoriesWebObserver();
    }

    /**
     * This method is called when a user selects an item in the menu bar. Home button. the fragment of selected item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                setFragmentActivityResult();
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void updateCategoriesSpinnerAndStopRefreshAnimation()
    {
        Activity settingsActivity = getActivity();

        // The activity is null if the update is not yet finished but this is no longer the current activity.
        if(settingsActivity != null)
        {
            this.categoryAdapter = new ArrayAdapter<>(settingsActivity, android.R.layout.simple_spinner_item, Categories.getCategoriesArray());
            this.categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.categorySpinner.setAdapter(this.categoryAdapter);
            this.categoryImageView.clearAnimation();
        }
    }

    @Override
    public void setActivityResultOnBackEvent()
    {
        setFragmentActivityResult();
    }

    /**
     * Sets the new settings and sends it to the main activity fragment.
     */
    private void setFragmentActivityResult()
    {
        updateSettings();

        Intent data = new Intent();
        data.putExtra(EXTRA_SETTINGS.toString(), this.settings);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();

        LogsManager.log(CLASS_NAME, "setFragmentActivityResult", "New settings -> " + this.settings);
    }

    private void updateSettings()
    {
        String category = categorySpinner.getSelectedItem().toString();
        FontName fontName = FontName.valueOf(fontNameSpinner.getSelectedItem().toString());
        FontStyle fontStyle = FontStyle.valueOf(fontStyleSpinner.getSelectedItem().toString());
        int fontSize = Integer.parseInt(fontSizeSpinner.getSelectedItem().toString());
        String serverURL = serverURLEditText.getText().toString();

        settings.setCategory(category).setFontName(fontName).setFontStyle(fontStyle).setFontSize(fontSize).setServerURL(serverURL);
    }

    public Settings getSettings()
    {
        return this.settings;
    }
}