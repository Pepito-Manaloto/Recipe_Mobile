package com.aaron.recipe.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.listener.BackButtonListener;
import com.aaron.recipe.listener.DeleteLongClickListener;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import java.util.Map;
import java.util.Set;

import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;
import static com.aaron.recipe.model.RecipeManager.DATE_FORMAT_LONG;

/**
 * The application about fragment.
 */
public class AboutFragment extends Fragment implements Backable
{
    public static final String CLASS_NAME = AboutFragment.class.getSimpleName();
    private static final int RECIPE_COUNT_COLUMNS = 2;

    private RecipeManager recipeManager;
    private Settings settings;

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.menu_about);
        initializeActionBar();

        this.settings = getActivity().getIntent().getParcelableExtra(EXTRA_SETTINGS);
        this.recipeManager = new RecipeManager(getContext());

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
     * Initializes about fragment user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateView.");

        View view = inflater.inflate(R.layout.fragment_about, parent, false);
        initializeView(view);

        String buildNumber = getActivity().getString(R.string.build_num);
        final TextView buildNumberTextView = view.findViewById(R.id.text_build_number);
        buildNumberTextView.setText(buildNumber);

        String lastUpdated = this.recipeManager.getLastUpdated(DATE_FORMAT_LONG);
        TextView lastUpdatedTextView = view.findViewById(R.id.text_last_updated);
        lastUpdatedTextView.setText(lastUpdated);

        initializeRecipeCount(view);

        return view;
    }

    private void initializeView(View view)
    {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new BackButtonListener(this));
        view.setOnLongClickListener(new DeleteLongClickListener(this, recipeManager, this::setFragmentActivityResult));
    }

    private void initializeRecipeCount(View view)
    {
        GridLayout grid = view.findViewById(R.id.gridlayout_count);
        grid.setColumnCount(RECIPE_COUNT_COLUMNS);

        Map<String, Integer> recipesCount = this.recipeManager.getRecipesCount();
        Set<Map.Entry<String, Integer>> entrySet = recipesCount.entrySet();

        int gridRowCount = entrySet.size();
        grid.setRowCount(gridRowCount);

        int rowNumber = 0;
        for(Map.Entry<String, Integer> entry : entrySet)
        {
            addRecipeCountLabelToGrid(rowNumber, entry.getKey(), grid);
            addRecipeCountToGrid(rowNumber, String.valueOf(entry.getValue()), grid);
            rowNumber++;
        }
    }

    private void addRecipeCountLabelToGrid(int rowNumber, String labelText, GridLayout grid)
    {
        GridLayout.LayoutParams layoutParamLabel = new GridLayout.LayoutParams(GridLayout.spec(rowNumber, GridLayout.LEFT),
                GridLayout.spec(0, GridLayout.LEFT));
        TextView label = new TextView(getActivity());
        label.setText(labelText);
        TextViewCompat.setTextAppearance(label, R.style.TextView_sub_about);
        grid.addView(label, layoutParamLabel);
    }

    private void addRecipeCountToGrid(int rowNumber, String countText, GridLayout grid)
    {
        GridLayout.LayoutParams layoutParamCount = new GridLayout.LayoutParams(GridLayout.spec(rowNumber, GridLayout.LEFT),
                GridLayout.spec(1, GridLayout.LEFT));
        layoutParamCount.setMargins(75, 0, 0, 0);
        TextView count = new TextView(getActivity());
        count.setText(countText);
        TextViewCompat.setTextAppearance(count, R.style.TextView_sub_about);
        grid.addView(count, layoutParamCount);
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

        LogsManager.log(CLASS_NAME, "setFragmentActivityResult", "Current settings -> " + this.settings);
    }
}
