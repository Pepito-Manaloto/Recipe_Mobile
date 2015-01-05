package com.aaron.recipe.fragment;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Ingredients.Ingredient;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;

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

import static android.widget.LinearLayout.LayoutParams;
import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_RECIPE;
import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * The recipe fragment, shows all recipe in one scrollable screen.
 */
public class RecipeFragment extends Fragment
{
    public static final String TAG = "RecipeFragment";

    private Recipe recipe;
    private Settings settings;

    /**
     * Creates a new RecipeFragment instance and stores the passed Recipe data as arguments.
     * Note: Android will call no-argument constructor of a fragment when it decides to recreate the fragment;
     *       hence, overloading a fragment constructor for data passing will not be able to save the passed data.
     *       That is why this static initializer is used.
     * @param Recipe the recipe object to be used later 
     */
    public static RecipeFragment newInstance(final Recipe recipe, final Settings settings)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_RECIPE, recipe);
        bundle.putSerializable(EXTRA_SETTINGS, settings);

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.setArguments(bundle);

        Log.d(LogsManager.TAG, "RecipeFragment: newInstance. recipe=" + recipe);

        return recipeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.recipe = (Recipe) getArguments().getSerializable(EXTRA_RECIPE);
        this.settings = (Settings) getArguments().getSerializable(EXTRA_SETTINGS);

        setHasOptionsMenu(true);
        getActivity().setTitle(this.recipe.getTitle());
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(LogsManager.TAG, "RecipeFragment: onCreate. title=" + this.recipe.getTitle());
        LogsManager.addToLogs("RecipeFragment: onCreate. title=" + this.recipe.getTitle());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_recipe, parent, false);

        Log.d(LogsManager.TAG, "RecipeFragment: onCreateView.");

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 5, 0);

        LayoutParams layoutParamsLabel = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsLabel.setMargins(0, 30, 0, 5);

        view.addView(createTextView("Title: " + this.recipe.getTitle()), layoutParams);
        view.addView(createTextView("Category: " + this.recipe.getCategory()), layoutParams);
        view.addView(createTextView("Preparation Time: " + this.recipe.getPreparationTime()), layoutParams);
        view.addView(createTextView("Servings: " + this.recipe.getServings()), layoutParams);
        view.addView(createTextView("Description: " + this.recipe.getDescription()), layoutParams);

        view.addView(createTextView("Ingredients:"), layoutParamsLabel);

        for(Ingredient ingredient: this.recipe.getIngredients().getIngredientsList())
        {
            view.addView(createTextView(ingredient.toString()), layoutParams);
        }

        view.addView(createTextView("Instructions:"), layoutParamsLabel);
        
        int count = 1;
        for(String instruction: this.recipe.getInstructions().getInstructionsList())
        {
            view.addView(createTextView(count + ". " + instruction), layoutParams);
            count++;
        }

        view.addView(createTextView(""), layoutParams);

        ScrollView scroll = new ScrollView(getActivity());
        scroll.addView(view);

        return scroll;
    }
    
    private TextView createTextView(final String text)
    {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.settings.getFontSize());
        textView.setTypeface(this.settings.getTypeface(false));

        return textView;
    }
}
