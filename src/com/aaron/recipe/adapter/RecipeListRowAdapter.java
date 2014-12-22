package com.aaron.recipe.adapter;

import java.util.ArrayList;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ListView adapter for recipe list.
 */
public class RecipeListRowAdapter extends ArrayAdapter<Recipe>
{
    public static final String TAG = "RecipeAdapter";
    private Activity activity;
    private ArrayList<Recipe> recipeList;
    private ArrayList<Recipe> recipeListTemporaryholder;
    private Settings settings;
    private RecipeManager recipeManager;

    /**
     * Default constructor. 0 is passed to the resource id, because we will be creating our own custom layout.
     * @param context the current context
     * @param vocabularyList the vocabulary list
     */
    public RecipeListRowAdapter(final Activity context, final ArrayList<Recipe> recipeList, final Settings settings)
    {
        super(context, 0, recipeList);

        this.recipeManager = new RecipeManager(context, settings.getCategory());

        this.activity = context;
        this.recipeList = recipeList;
        this.recipeListTemporaryholder = this.recipeManager.getRecipesFromDisk();
        this.settings = settings;
    }

    /**
     * Populates the ListView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if(convertView == null)
        {
            convertView = this.activity.getLayoutInflater().inflate(R.layout.fragment_recipe_list_row, parent, false);
            
            holder = new ViewHolder();
            holder.titleText = (TextView) convertView.findViewById(R.id.text_row_title);
            holder.categoryText = (TextView) convertView.findViewById(R.id.text_row_category);
            holder.preparationTimeText = (TextView) convertView.findViewById(R.id.text_row_preparation_time);
            holder.servingsText = (TextView) convertView.findViewById(R.id.text_row_servings);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Recipe recipe = getItem(position);
    
        holder.titleText.setText(recipe.getTitle());
        holder.titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.settings.getFontSize());
        holder.titleText.setTypeface(this.settings.getTypeface());

        holder.categoryText.setText(recipe.getCategory());
        holder.categoryText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.settings.getFontSize());
        holder.categoryText.setTypeface(this.settings.getTypeface());
        
        holder.preparationTimeText.setText(recipe.getPreparationTime());
        holder.preparationTimeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.settings.getFontSize());
        holder.preparationTimeText.setTypeface(this.settings.getTypeface());
        
        holder.servingsText.setText(recipe.getServings());
        holder.servingsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.settings.getFontSize());
        holder.servingsText.setTypeface(this.settings.getTypeface());

        return convertView;
    }

    /**
     * Filters the recipe list in the adapter with the given searched text. Only shows recipe title that starts with the searched text.
     * @param searched the searched word
     */
    public void filter(final String searched)
    {
        this.recipeList.clear();
        String searchedText = searched.trim();
        String titleWord;

        if(searchedText.length() == 0)
        {
            this.recipeList.addAll(this.recipeListTemporaryholder);
        }
        else
        {
            for(Recipe recipe: this.recipeListTemporaryholder)
            {
                titleWord = recipe.getTitle();

                if(titleWord.startsWith(searchedText))
                {
                    this.recipeList.add(recipe);
                }
            }
        }
        

        Log.d(LogsManager.TAG, "RecipeAdapter: filter. New list -> " + this.recipeList);
        LogsManager.addToLogs("RecipeAdapter: filter. New list size -> " + this.recipeList.size());
    }

    /**
     * Helper class for storing view values. Ensures findViewById() will only be called ones if convertView is not null.
     */
    private static class ViewHolder
    {
        public TextView titleText;
        public TextView categoryText;
        public TextView preparationTimeText;
        public TextView servingsText;
    }
}
