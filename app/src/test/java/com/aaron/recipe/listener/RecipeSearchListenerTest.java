package com.aaron.recipe.listener;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import com.aaron.recipe.RobolectricTest;
import com.aaron.recipe.activity.RecipeListActivity;
import com.aaron.recipe.adapter.RecipeListRowAdapter;
import com.aaron.recipe.bean.Recipe;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class RecipeSearchListenerTest extends RobolectricTest
{
    private RecipeSearchListener listener;

    @Test
    public void givenEmptySearchTextAndRecipeList_whenAfterTextChanged_thenShouldReturnRecipeListUnchanged()
    {
        ArrayList<Recipe> recipeList = givenRecipeList();
        listener = new RecipeSearchListener(initializeRecipeListRowAdapter(recipeList));
        ArrayList<Recipe> originalRecipeList = new ArrayList<>(recipeList);

        Editable editable = new SpannableStringBuilder("");
        listener.afterTextChanged(editable);

        assertEquals(originalRecipeList, recipeList);
    }

    @Test
    public void givenSearchTextAndOneElementRecipeList_whenAfterTextChanged_thenShouldOnlyReturnRecipeListWithTitleThatStartsWithTheSearchText()
    {
        String searchText = "shr";
        ArrayList<Recipe> recipeList = new ArrayList<>();
        recipeList.add(newRecipe("Shrimp paste"));
        listener = new RecipeSearchListener(initializeRecipeListRowAdapter(recipeList));

        Editable editable = new SpannableStringBuilder(searchText);
        listener.afterTextChanged(editable);

        assertFalse(recipeList.isEmpty());
        assertThat(recipeList, contains(hasProperty("title", is("Shrimp paste"))));
    }

    @Test
    public void givenSearchTextAndRecipeList_whenFilter_thenShouldOnlyReturnRecipeListWithTitleThatStartsWithTheSearchText()
    {
        String searchText = "lObSter";
        ArrayList<Recipe> recipeList = givenRecipeList();
        listener = new RecipeSearchListener(initializeRecipeListRowAdapter(recipeList));

        Editable editable = new SpannableStringBuilder(searchText);
        listener.afterTextChanged(editable);

        assertFalse(recipeList.isEmpty());
        assertThat(recipeList, contains(hasProperty("title", is("Lobster and tail")),
                hasProperty("title", is("lobster and soup")),
                hasProperty("title", is("Lobster bisque")),
                hasProperty("title", is("Dessert lobster")),
                hasProperty("title", is("A whole bunch of LOBSTERballz"))));
    }

    @Test
    public void givenSearchTextAndRecipeList_whenFilter_thenShouldOnlyReturnRecipeListWithTitleThatStartsWithTheSearchTextExceptIgnoreWords()
    {
        String searchText = "a";
        ArrayList<Recipe> recipeList = givenRecipeList();
        listener = new RecipeSearchListener(initializeRecipeListRowAdapter(recipeList));

        Editable editable = new SpannableStringBuilder(searchText);
        listener.afterTextChanged(editable);

        assertFalse(recipeList.isEmpty());
        assertThat(recipeList, contains(hasProperty("title", is("Apricot yogurt")),
                hasProperty("title", is("Apple tart"))));
    }

    private RecipeListRowAdapter initializeRecipeListRowAdapter(ArrayList<Recipe> recipeList)
    {
        return new RecipeListRowAdapter(getActivity(RecipeListActivity.class), recipeList, null);
    }

    private ArrayList<Recipe> givenRecipeList()
    {
        ArrayList<Recipe> recipeList = new ArrayList<>();

        recipeList.add(newRecipe("Shrimp paste"));
        recipeList.add(newRecipe("Lasagna"));
        recipeList.add(newRecipe("Apricot yogurt"));
        recipeList.add(newRecipe("Lobster and tail"));
        recipeList.add(newRecipe("Crab stick"));
        recipeList.add(newRecipe("lobster and soup"));
        recipeList.add(newRecipe("Beef celery"));
        recipeList.add(newRecipe("Lobster bisque"));
        recipeList.add(newRecipe("Cake o cake"));
        recipeList.add(newRecipe("Dessert lobster"));
        recipeList.add(newRecipe("Pie"));
        recipeList.add(newRecipe("Apple tart"));
        recipeList.add(newRecipe("A whole bunch of LOBSTERballz"));

        return recipeList;
    }

    private ArrayList<Recipe> givenRandomRecipeList()
    {
        ArrayList<Recipe> recipeList = new ArrayList<>();

        for(int i = 0; i < 8; i++)
        {
            recipeList.add(newRecipe(RandomStringUtils.randomAlphabetic(8)));
        }

        return recipeList;
    }

    private Recipe newRecipe(String title)
    {
        Recipe recipe = new Recipe();
        recipe.setId(RandomUtils.nextInt());
        recipe.setTitle(title);

        return recipe;
    }
}
