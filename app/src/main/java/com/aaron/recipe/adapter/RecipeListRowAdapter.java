package com.aaron.recipe.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.aaron.recipe.R;
import com.aaron.recipe.activity.RecipeActivity;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.model.LogsManager;

import java.util.ArrayList;
import java.util.Locale;

import static com.aaron.recipe.adapter.RecipePagerAdapter.EXTRA_PAGE;
import static com.aaron.recipe.fragment.RecipeListFragment.EXTRA_RECIPE_LIST;
import static com.aaron.recipe.fragment.SettingsFragment.EXTRA_SETTINGS;

/**
 * ListView adapter for recipe list.
 */
public class RecipeListRowAdapter extends ArrayAdapter<Recipe>
{
    public static final String TAG = "RecipeAdapter";
    private Activity activity;
    private ArrayList<Recipe> recipeList;
    private ArrayList<Recipe> recipeListTemp;
    private Settings settings;

    /**
     * Default constructor. 0 is passed to the resource id, because we will be creating our own custom layout.
     *
     * @param context    the current context
     * @param recipeList the vocabulary list
     * @param settings   the current user settings
     */
    public RecipeListRowAdapter(final Activity context, final ArrayList<Recipe> recipeList, final Settings settings)
    {
        super(context, 0, recipeList);

        this.activity = context;
        this.recipeList = recipeList;
        this.recipeListTemp = new ArrayList<>(recipeList);
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
            holder.servingsText = (TextView) convertView.findViewById(R.id.text_row_servings);
            holder.preparationTimeText = (TextView) convertView.findViewById(R.id.text_row_preparation_time);
            holder.description = (TextView) convertView.findViewById(R.id.text_row_description);
            holder.scroll = (HorizontalScrollView) convertView.findViewById(R.id.horizontalscroll_list_row);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Recipe recipe = getItem(position);
        holder.setRecipeView(recipe, this.settings, new RecipeListRowTouchListener(position));

        return convertView;
    }

    /**
     * Filters the recipe list in the adapter with the given searched text. Only shows recipe title that starts with the searched text.
     *
     * @param searched the searched word
     */
    public void filter(final String searched)
    {
        this.recipeList.clear();
        String searchedText = searched.trim();
        String titleWord;

        if(searchedText.length() == 0)
        {
            this.recipeList.addAll(this.recipeListTemp);
        }
        else
        {
            for(Recipe recipe : this.recipeListTemp)
            {
                titleWord = recipe.getTitle().toLowerCase(Locale.getDefault());

                if(titleWord.startsWith(searchedText.toLowerCase(Locale.getDefault())))
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
        public TextView servingsText;
        public TextView preparationTimeText;
        public TextView description;
        public HorizontalScrollView scroll;

        public void setRecipeView(Recipe recipe, Settings settings, OnTouchListener listener)
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

    /**
     * Helper class for handling Recipe selection and Recipe list row scrolling.
     */
    private class RecipeListRowTouchListener implements OnTouchListener
    {
        private float historicX;
        private int page;

        /**
         * Default constructor.
         *
         * @param page the position of the selected recipe
         */
        public RecipeListRowTouchListener(final int page)
        {
            this.page = page;
        }

        /**
         * If the touch moves MORE than 15 pixels horizontally then the gesture will be treated as a scrolling event,
         * else it will be treated as selecting the row which will start RecipeActivity.
         */
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    this.historicX = event.getX();
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    boolean touchMovedLessThan10Pixels = Math.abs(this.historicX - event.getX()) < 15;

                    if(touchMovedLessThan10Pixels)
                    {
                        Intent intent = new Intent(activity, RecipeActivity.class);
                        intent.putExtra(EXTRA_PAGE, this.page);
                        intent.putExtra(EXTRA_RECIPE_LIST, RecipeListRowAdapter.this.recipeList);
                        intent.putExtra(EXTRA_SETTINGS, RecipeListRowAdapter.this.settings);
                        RecipeListRowAdapter.this.activity.startActivity(intent);
                    }

                    // Removes compiler warning
                    v.performClick();

                    break;
                }
                default:
                {
                    return false;
                }
            }

            return true;
        }
    }

}