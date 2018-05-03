package com.aaron.recipe.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.listener.RecipeListRowTouchListener;
import com.aaron.recipe.model.LogsManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * ListView adapter for recipe list.
 */
public class RecipeListRowAdapter extends ArrayAdapter<Recipe>
{
    public static final String CLASS_NAME = RecipeListRowAdapter.class.getSimpleName();
    private Activity activity;
    private ArrayList<Recipe> recipeListTemp;
    private Settings settings;

    /**
     * Default constructor. 0 is passed to the resource id, because we will be creating our own custom layout.
     *
     * @param activity   the current activity
     * @param recipeList the vocabulary list
     * @param settings   the current user settings
     */
    public RecipeListRowAdapter(final Activity activity, final ArrayList<Recipe> recipeList, final Settings settings)
    {
        super(activity, 0, recipeList);

        this.activity = activity;
        this.recipeListTemp = new ArrayList<>(recipeList);
        this.settings = settings;
    }

    /**
     * Populates the ListView.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder holder;
        View listRowView;

        if(convertView == null)
        {
            listRowView = this.activity.getLayoutInflater().inflate(R.layout.fragment_recipe_list_row, parent, false);

            holder = new ViewHolder();
            holder.titleText = listRowView.findViewById(R.id.text_row_title);
            holder.categoryText = listRowView.findViewById(R.id.text_row_category);
            holder.servingsText = listRowView.findViewById(R.id.text_row_servings);
            holder.preparationTimeText = listRowView.findViewById(R.id.text_row_preparation_time);
            holder.description = listRowView.findViewById(R.id.text_row_description);
            holder.scroll = listRowView.findViewById(R.id.horizontalscroll_list_row);

            listRowView.setTag(holder);
        }
        else
        {
            listRowView = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        Recipe recipe = getItem(position);
        holder.setRecipeView(recipe, settings, new RecipeListRowTouchListener(activity, recipeListTemp, settings, recipe, position));

        return listRowView;
    }

    /**
     * Filters the recipe list in the adapter with the given searched text. Only shows recipe title that starts with the searched text.
     *
     * @param searched the searched word
     */
    public void filter(final String searched)
    {
        clear();
        String searchedText = searched.trim();

        if(searchedText.length() == 0)
        {
            addAll(this.recipeListTemp);
        }
        else
        {
            filterRecipeByTitle(searchedText);
        }

        LogsManager.log(CLASS_NAME, "filter", "New list size -> " + getCount());
    }

    private void filterRecipeByTitle(String searchedText)
    {
        Predicate<Recipe> recipeTitleStartsWithSearchedText = recipe -> recipe.getTitle().toLowerCase(Locale.getDefault())
                .startsWith(searchedText.toLowerCase(Locale.getDefault()));

        recipeListTemp.stream().filter(recipeTitleStartsWithSearchedText).forEach(this::add);
    }

    /**
     * Updates the recipe list.
     *
     * @param list the list to replace the current
     */
    public void update(ArrayList<Recipe> list)
    {
        if(list != null)
        {
            // Store this new list into temp, because the list parameter shares the same reference as the Adapter's list.
            // Thus, calling clear() will clear out both the adapter's list and the new list.
            ArrayList<Recipe> tmpList = new ArrayList<>(list);
            clear();

            // If user deletes recipe list in AboutFragment
            if(!tmpList.isEmpty())
            {
                addAll(tmpList);
                recipeListTemp.clear();
                recipeListTemp.addAll(tmpList);
            }
        }
    }

    /**
     * Helper class for storing view values. Ensures findViewById() will only be called ones if convertView is not null.
     */
    private static class ViewHolder
    {
        private TextView titleText;
        private TextView categoryText;
        private TextView servingsText;
        private TextView preparationTimeText;
        private TextView description;
        private HorizontalScrollView scroll;

        private void setRecipeView(Recipe recipe, Settings settings, OnTouchListener listener)
        {
            this.scroll.setOnTouchListener(listener);

            this.titleText.setText(recipe.getTitle());
            this.titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getFontSize());
            this.titleText.setTypeface(settings.getTypeface(true));

            this.categoryText.setText(recipe.getCategory());
            this.categoryText.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getFontSize());
            this.categoryText.setTypeface(settings.getTypeface(false));

            this.servingsText.setText(String.valueOf(recipe.getServings()));
            this.servingsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getFontSize());
            this.servingsText.setTypeface(settings.getTypeface(false));

            this.preparationTimeText.setText(recipe.getPreparationTimeString());
            this.preparationTimeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getFontSize());
            this.preparationTimeText.setTypeface(settings.getTypeface(false));

            this.description.setText(recipe.getDescription());
            this.description.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getFontSize());
            this.description.setTypeface(settings.getTypeface(false));
        }
    }
}
