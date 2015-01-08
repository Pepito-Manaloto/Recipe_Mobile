package com.aaron.recipe.fragment;

import java.util.ArrayList;

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
import static com.aaron.recipe.adapter.RecipePagerAdapter.EXTRA_PAGE;
import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_LIST;
import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * The recipe fragment, shows all recipe in one scrollable screen.
 */
public class RecipeFragment extends Fragment
{
    public static final String TAG = "RecipeFragment";

    private int page;
    private ArrayList<Recipe> recipeList;
    private Settings settings;
    private Recipe recipe;
    public static int previousPageLoaded;

    /**
     * Creates a new RecipeFragment instance and stores the passed Recipe data as arguments.
     * Note: Android will call no-argument constructor of a fragment when it decides to recreate the fragment;
     *       hence, overloading a fragment constructor for data passing will not be able to save the passed data.
     *       That is why this static initializer is used.
     * @param Recipe the recipe object to be used later 
     */
    public static RecipeFragment newInstance(final int page, final ArrayList<Recipe> recipeList, final Settings settings)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PAGE, page);
        bundle.putSerializable(EXTRA_LIST, recipeList);
        bundle.putSerializable(EXTRA_SETTINGS, settings);

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.setArguments(bundle);

        Log.d(LogsManager.TAG, "RecipeFragment: newInstance. page=" + page);

        return recipeFragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.page = getArguments().getInt(EXTRA_PAGE);
        this.recipeList = (ArrayList<Recipe>) getArguments().getSerializable(EXTRA_LIST);
        this.settings = (Settings) getArguments().getSerializable(EXTRA_SETTINGS);

        this.recipe = this.recipeList.get(this.page);

        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(LogsManager.TAG, "RecipeFragment: onCreate.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_recipe, parent, false);

        if(previousPageLoaded == 0 && this.page == (this.recipeList.size() - 1)) // Last item is selected
        {
            previousPageLoaded = this.page;
        }
        else if(previousPageLoaded < this.page) // Swipe to the right
        {
            previousPageLoaded = this.page - 1;
        }
        else if(previousPageLoaded > this.page) // swipe to the left
        {
            previousPageLoaded = this.page + 1;
        }
        else
        {
            previousPageLoaded = this.page;
        }

        String title = this.recipeList.get(previousPageLoaded).getTitle();
        getActivity().setTitle(title);

        Log.d(LogsManager.TAG, "RecipeFragment: onCreateView. title=" + title);
        LogsManager.addToLogs("RecipeFragment: onCreateView. title=" + title);

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
