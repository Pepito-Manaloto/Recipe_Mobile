package com.aaron.recipe.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.aaron.recipe.adapter.RecipeListRowAdapter;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.model.LogsManager;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class RecipeSearchListener implements TextWatcher
{
    public static final String CLASS_NAME = RecipeSearchListener.class.getSimpleName();
    private static final Set<String> IGNORABLE_WORDS = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    private RecipeListRowAdapter adapter;

    static
    {
        IGNORABLE_WORDS.addAll(asList("a", "an", "and", "at", "by", "in", "on", "the", "with"));
    }

    public RecipeSearchListener(RecipeListRowAdapter adapter)
    {
        this.adapter = adapter;
    }

    /**
     * Filters the recipe list in the adapter with the given searched text. Only shows recipe title that starts with the searched text.
     *
     * @param editable the searched word
     */
    @Override
    public void afterTextChanged(Editable editable)
    {
        String searched = editable.toString();

        adapter.clear();
        String searchedText = searched.trim();

        if(isBlank(searchedText))
        {
            adapter.addAll(adapter.getRecipeListAllUnfiltered());
        }
        else
        {
            filterRecipeByTitle(searchedText);
        }

        LogsManager.log(CLASS_NAME, "afterTextChanged", "New list size -> " + adapter.getCount());

        LogsManager.log(CLASS_NAME, "afterTextChanged", "searched=" + searched);
    }

    private void filterRecipeByTitle(String searchedText)
    {
        adapter.getRecipeListAllUnfiltered().stream().filter(r -> recipeTitleStartsWithSearchedText(searchedText, r)).forEach(adapter::add);
    }

    private boolean recipeTitleStartsWithSearchedText(String searchedText, Recipe recipe)
    {
        String search = searchedText.toLowerCase(Locale.getDefault());
        String title = recipe.getTitle().toLowerCase(Locale.getDefault());
        List<String> titleWordParts = stream(title.split(" ")).filter(s -> !IGNORABLE_WORDS.contains(s)).collect(toList());

        for(String word : titleWordParts)
        {
            if(word.startsWith(search))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
        // No Action
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
    {
        // No Action
    }
}