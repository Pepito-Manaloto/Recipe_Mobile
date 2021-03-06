package com.aaron.recipe.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.aaron.recipe.bean.Ingredient;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;

import java.util.ArrayList;

import static android.widget.LinearLayout.LayoutParams;
import static com.aaron.recipe.bean.DataKey.EXTRA_PAGE;
import static com.aaron.recipe.bean.DataKey.EXTRA_RECIPE_LIST;
import static com.aaron.recipe.bean.DataKey.EXTRA_SETTINGS;

/**
 * The recipe fragment, shows all recipe in one scrollable screen.
 */
public class RecipeFragment extends Fragment
{
    public static final String CLASS_NAME = RecipeFragment.class.getSimpleName();

    private Settings settings;
    private Recipe recipe;

    /**
     * Creates a new RecipeFragment instance and stores the passed Recipe data as arguments. Note: Android will call no-argument constructor of a fragment
     * when it decides to recreate the fragment; hence, overloading a fragment constructor for data passing will not be able to save the passed data. That
     * is why this static initializer is used. There is also no way to pass data to RecipePageAdapter through savedInstanceState intent, that is why we
     * pass data through instance creation.
     *
     * @param page the page
     * @param recipeList the recipe list
     * @param settings the settings
     */
    public static RecipeFragment newInstance(int page, final ArrayList<Recipe> recipeList, final Settings settings)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PAGE.toString(), page);
        bundle.putParcelableArrayList(EXTRA_RECIPE_LIST.toString(), recipeList);
        bundle.putParcelable(EXTRA_SETTINGS.toString(), settings);

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.setArguments(bundle);

        LogsManager.log(CLASS_NAME, "newInstance", "page=" + page);

        return recipeFragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null)
        {
            parseBundleArguments(args);
        }

        setHasOptionsMenu(true);
        initializeActionBar();

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate.");
    }

    private void parseBundleArguments(Bundle args)
    {
        this.settings = args.getParcelable(EXTRA_SETTINGS.toString());

        int page = args.getInt(EXTRA_PAGE.toString());
        ArrayList<Recipe> recipeList = args.getParcelableArrayList(EXTRA_RECIPE_LIST.toString());
        this.recipe = recipeList == null ? null : recipeList.get(page);
    }

    private void initializeActionBar()
    {
        Activity activity = getActivity();
        if(activity != null)
        {
            ActionBar actionBar = activity.getActionBar();
            if(actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_recipe, parent, false);

        LayoutParams layoutParamsLabel = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsLabel.setMargins(0, 10, 5, 0);

        LayoutParams layoutParamsListLabel = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsListLabel.setMargins(0, 30, 0, 5);

        initializeRecipeViewDetails(linearLayout, layoutParamsLabel);
        initializeIngredientsAndInstructionsView(linearLayout, layoutParamsLabel, layoutParamsListLabel);

        // Add newline at the end of the page
        linearLayout.addView(createTextView(""), layoutParamsLabel);

        ScrollView scroll = new ScrollView(getActivity());
        scroll.addView(linearLayout);

        Log.d(LogsManager.TAG, CLASS_NAME + ": onCreateView.");

        return scroll;
    }

    private void initializeRecipeViewDetails(LinearLayout linearLayout, LayoutParams layoutParamsLabel)
    {
        linearLayout.addView(createTextView("Title: " + this.recipe.getTitle()), layoutParamsLabel);
        linearLayout.addView(createTextView("Category: " + this.recipe.getCategory()), layoutParamsLabel);
        linearLayout.addView(createTextView("Preparation Time: " + this.recipe.getPreparationTimeString()), layoutParamsLabel);
        linearLayout.addView(createTextView("Servings: " + this.recipe.getServings()), layoutParamsLabel);
        linearLayout.addView(createTextView("Description: " + this.recipe.getDescription()), layoutParamsLabel);
    }

    private void initializeIngredientsAndInstructionsView(LinearLayout linearLayout, LayoutParams layoutParamsLabel, LayoutParams layoutParamsListLabel)
    {
        linearLayout.addView(createTextView("Ingredients:"), layoutParamsListLabel);

        ArrayList<Ingredient> ingredientsList = recipe.getIngredients().getIngredientsList();
        for(Ingredient ingredient : ingredientsList)
        {
            linearLayout.addView(createTextView(ingredient.toString()), layoutParamsLabel);
        }

        linearLayout.addView(createTextView("Instructions:"), layoutParamsListLabel);

        int count = 1;
        ArrayList<String> instructionsList = recipe.getInstructions().getInstructionsList();
        for(String instruction : instructionsList)
        {
            linearLayout.addView(createTextView(count + ". " + instruction), layoutParamsLabel);
            count++;
        }
    }

    /**
     * Creates a TextView with the given text and current selected settings.
     *
     * @param text
     *            the text of the TextView to create
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
