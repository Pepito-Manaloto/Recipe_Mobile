package com.aaron.recipe.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Ingredients.Ingredient;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;

import java.util.ArrayList;

import static android.widget.LinearLayout.LayoutParams;
import static com.aaron.recipe.adapter.RecipePagerAdapter.EXTRA_PAGE;
import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_RECIPE_LIST;
import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * The recipe fragment, shows all recipe in one scrollable screen.
 */
public class RecipeFragment extends Fragment
{
    public static final String CLASS_NAME = RecipeFragment.class.getSimpleName();

    private Settings settings;
    private Recipe recipe;

    /**
     * Creates a new RecipeFragment instance and stores the passed Recipe data as arguments.
     * Note: Android will call no-argument constructor of a fragment when it decides to recreate the fragment;
     * hence, overloading a fragment constructor for data passing will not be able to save the passed data.
     * That is why this static initializer is used.
     * There is also no way to pass data to RecipePageAdapter through savedInstanceState intent, that is why we pass data through instance creation.
     *
     * @param page       the page
     * @param recipeList the recipe list
     * @param settings   the settings
     */
    public static RecipeFragment newInstance(final int page, final ArrayList<Recipe> recipeList, final Settings settings)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PAGE, page);
        bundle.putSerializable(EXTRA_RECIPE_LIST, recipeList);
        bundle.putSerializable(EXTRA_SETTINGS, settings);

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.setArguments(bundle);

        Log.d(LogsManager.TAG, CLASS_NAME + ": newInstance. page=" + page);

        return recipeFragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        int page = getArguments().getInt(EXTRA_PAGE);
        ArrayList<Recipe> recipeList = (ArrayList<Recipe>) getArguments().getSerializable(EXTRA_RECIPE_LIST);
        this.settings = (Settings) getArguments().getSerializable(EXTRA_SETTINGS);

        this.recipe = recipeList == null ? null : recipeList.get(page);

        setHasOptionsMenu(true);

        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_recipe, parent, false);

        LayoutParams layoutParamsLabel = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsLabel.setMargins(0, 10, 5, 0);

        LayoutParams layoutParamsListLabel = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsListLabel.setMargins(0, 30, 0, 5);

        view.addView(createTextView("Title: " + this.recipe.getTitle()), layoutParamsLabel);
        view.addView(createTextView("Category: " + this.recipe.getCategory()), layoutParamsLabel);
        view.addView(createTextView("Preparation Time: " + this.recipe.getPreparationTimeString()), layoutParamsLabel);
        view.addView(createTextView("Servings: " + this.recipe.getServings()), layoutParamsLabel);
        view.addView(createTextView("Description: " + this.recipe.getDescription()), layoutParamsLabel);

        view.addView(createTextView("Ingredients:"), layoutParamsListLabel);

        for(Ingredient ingredient : this.recipe.getIngredients().getIngredientsList())
        {
            view.addView(createTextView(ingredient.toString()), layoutParamsLabel);
        }

        view.addView(createTextView("Instructions:"), layoutParamsListLabel);

        int count = 1;
        for(String instruction : this.recipe.getInstructions().getInstructionsList())
        {
            view.addView(createTextView(count + ". " + instruction), layoutParamsLabel);
            count++;
        }

        // Add newline at the end of the page
        view.addView(createTextView(""), layoutParamsLabel);

        ScrollView scroll = new ScrollView(getActivity());
        scroll.addView(view);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateView.");

        return scroll;
    }

    /**
     * Creates a TextView with the given text and current selected settings.
     *
     * @param text
     * @return TextView
     */
    private TextView createTextView(final String text)
    {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.settings.getFontSize());
        textView.setTypeface(this.settings.getTypeface(false));

        return textView;
    }
}
