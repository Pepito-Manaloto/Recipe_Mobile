package com.aaron.recipe.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.async.CategoriesRetrieverThread;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.bean.Settings.FontName;
import com.aaron.recipe.bean.Settings.FontStyle;
import com.aaron.recipe.model.LogsManager;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;

/**
 * The application settings fragment.
 */
public class SettingsFragment extends Fragment
{
    public static final String CLASS_NAME = SettingsFragment.class.getSimpleName();
    public static final String EXTRA_SETTINGS = "com.aaron.recipe.fragment.settings";
    private Settings settings;

    private ArrayAdapter<String> categoryAdapter;

    private Spinner categorySpinner;
    private Spinner fontNameSpinner;
    private Spinner fontStyleSpinner;
    private Spinner fontSizeSpinner;

    private ImageView categoryImageView;
    private EditText serverURLEditText;

    /**
     * Returns a new SettingsFragment with the given settings as arguments.
     */
    public static SettingsFragment newInstance(SettingsFragment fragment, final Settings settings)
    {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SETTINGS, settings);

        SettingsFragment settingsFragment;
        if(fragment != null)
        {
            settingsFragment = fragment;
        }
        else
        {
            settingsFragment = new SettingsFragment();
        }

        settingsFragment.setArguments(args);

        Log.d(LogsManager.TAG, CLASS_NAME + ": newInstance. settings=" + settings);

        return settingsFragment;
    }

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.settings = getArguments().getParcelable(SettingsFragment.EXTRA_SETTINGS);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.menu_settings);

        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Categories.getCategoriesArray());
        this.categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate. settings=" + this.settings);
        LogsManager.addToLogs(CLASS_NAME + ": onCreate. settings=" + this.settings);
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, parent, false);

        this.categoryImageView = view.findViewById(R.id.imageview_refresh_category);
        this.categoryImageView.setClickable(true);

        this.categoryImageView.setOnClickListener(new UpdateCategoriesListener(this));

        this.categorySpinner = view.findViewById(R.id.spinner_category);
        this.categorySpinner.setAdapter(this.categoryAdapter);

        this.fontNameSpinner = view.findViewById(R.id.spinner_font_name);
        this.fontStyleSpinner = view.findViewById(R.id.spinner_font_style);
        this.fontSizeSpinner = view.findViewById(R.id.spinner_font_size);
        this.serverURLEditText = view.findViewById(R.id.edittext_server_url);
        this.serverURLEditText.addTextChangedListener(new ServerUrlTextListener(this.settings));

        this.categorySpinner.setSelection(this.settings.getCategoryIndex());
        this.fontNameSpinner.setSelection(this.settings.getFontNameIndex());
        this.fontStyleSpinner.setSelection(this.settings.getFontStyleIndex());
        this.fontSizeSpinner.setSelection(this.settings.getFontSizeIndex());

        String serverUrl = this.settings.getServerURL();

        if(StringUtils.isBlank(serverUrl))
        {
            serverUrl = getActivity().getString(R.string.url_address_default);
        }

        this.serverURLEditText.setText(serverUrl);

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        BackButtonListener backButtonListener = new BackButtonListener(this);
        view.setOnKeyListener(backButtonListener);
        this.serverURLEditText.setOnKeyListener(backButtonListener);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateView");

        return view;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        categoryImageView.clearAnimation();
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
                this.setFragmentActivityResult();
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void updateCategoriesSpinner()
    {
        Activity settingsActivity = getActivity();

        // The activity is null if the AsyncTask is not yet finished but this is no longer the current activity.
        if(settingsActivity != null)
        {
            this.categoryAdapter = new ArrayAdapter<>(settingsActivity, android.R.layout.simple_spinner_item, Categories.getCategoriesArray());
            this.categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.categorySpinner.setAdapter(this.categoryAdapter);
            this.categoryImageView.clearAnimation();
        }
    }

    /**
     * Sets the new settings and sends it to the main activity fragment.
     */
    private void setFragmentActivityResult()
    {
        Intent data = new Intent();

        String category = this.categorySpinner.getSelectedItem().toString();
        FontName fontName = FontName.valueOf(this.fontNameSpinner.getSelectedItem().toString());
        FontStyle fontStyle = FontStyle.valueOf(this.fontStyleSpinner.getSelectedItem().toString());
        int fontSize = Integer.parseInt(this.fontSizeSpinner.getSelectedItem().toString());
        String serverURL = this.serverURLEditText.getText().toString();

        this.settings.setCategory(category).setFontName(fontName).setFontStyle(fontStyle).setFontSize(fontSize).setServerURL(serverURL);

        data.putExtra(EXTRA_SETTINGS, this.settings);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();

        Log.d(LogsManager.TAG, CLASS_NAME + ": setFragmentActivityResult. New settings -> " + this.settings);
        LogsManager.addToLogs(CLASS_NAME + ": setFragmentActivityResult. New settings -> " + this.settings);
    }

    private Settings getSettings()
    {
        return this.settings;
    }

    private class BackButtonListener implements View.OnKeyListener
    {
        private WeakReference<SettingsFragment> fragmentRef;

        BackButtonListener(SettingsFragment fragment)
        {
            this.fragmentRef = new WeakReference<>(fragment);
        }

        /**
         * Handles back button.
         */
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            // For back button
            if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            {
                SettingsFragment fragment = this.fragmentRef.get();
                if(fragment != null)
                {
                    fragment.setFragmentActivityResult();
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    private static class UpdateCategoriesListener implements View.OnClickListener
    {
        private WeakReference<SettingsFragment> fragmentRef;

        UpdateCategoriesListener(SettingsFragment fragment)
        {
            this.fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void onClick(View imageView)
        {
            SettingsFragment fragment = this.fragmentRef.get();

            if(fragment != null)
            {
                Activity activity = fragment.getActivity();

                if(!CategoriesRetrieverThread.isUpdating())
                {
                    final Animation rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate_refresh);
                    rotation.setRepeatCount(Animation.INFINITE);

                    CategoriesRetrieverThread categoriesRetrieverThread = new CategoriesRetrieverThread(fragment, fragment.getSettings());
                    categoriesRetrieverThread.execute();

                    CategoriesRetrieverThread.setIsUpdating();
                    imageView.startAnimation(rotation);
                }
                else
                {
                    Toast.makeText(activity, activity.getString(R.string.categories_currently_updating), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static class ServerUrlTextListener implements TextWatcher
    {
        private Settings settings;

        ServerUrlTextListener(Settings settings)
        {
            this.settings = settings;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            // No Action
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable editable)
        {
            this.settings.setServerURL(editable.toString());
        }
    }
}