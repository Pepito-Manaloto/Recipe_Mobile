package com.aaron.recipe.adapter;

import com.aaron.recipe.RobolectricTest;
import com.aaron.recipe.activity.RecipeListActivity;
import com.aaron.recipe.bean.Recipe;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipeListRowAdapterTest extends RobolectricTest
{
    private RecipeListRowAdapter adapter;

    @Test
    public void givenEmptySearchTextAndRecipeList_whenFilter_thenShouldReturnRecipeListUnchanged()
    {
        ArrayList<Recipe> recipeList = givenRecipeList();
        adapter = initializeRecipeListRowAdapter(recipeList);
        ArrayList<Recipe> originalRecipeList = new ArrayList<>(recipeList);

        adapter.filter("");

        assertEquals(originalRecipeList, recipeList);
    }

    @Test
    public void givenSearchTextAndRecipeList_whenFilter_thenShouldOnlyReturnRecipeListWithTitleThatStartsWithTheSearchText()
    {
        String searchText = "lObSter";
        ArrayList<Recipe> recipeList = givenRecipeList();
        adapter = initializeRecipeListRowAdapter(recipeList);

        adapter.filter(searchText);

        boolean allRecipesStartsWithSearchText = recipeList.stream().map(r -> r.getTitle().toLowerCase()).allMatch(s -> s.startsWith(searchText.toLowerCase()));

        assertFalse(recipeList.isEmpty());
        assertTrue(allRecipesStartsWithSearchText);
    }

    @Test
    public void givenRecipeList_whenUpdate_thenShouldReplaceAdaptersList()
    {
        ArrayList<Recipe> adaptersList = givenRecipeList();
        adapter = initializeRecipeListRowAdapter(adaptersList);
        ArrayList<Recipe> recipeList = givenRandomRecipeList();

        adapter.update(recipeList);

        assertEquals(recipeList, adaptersList);
    }

    @Test
    public void givenNullRecipeList_whenUpdate_thenShouldNotReplaceAdaptersList()
    {
        ArrayList<Recipe> adaptersList = givenRecipeList();
        adapter = initializeRecipeListRowAdapter(adaptersList);
        ArrayList<Recipe> originalRecipeList = new ArrayList<>(adaptersList);

        adapter.update(null);

        assertEquals(originalRecipeList, adaptersList);
    }

    @Test
    public void givenEmptyRecipeList_whenUpdate_thenShouldClearAdaptersList()
    {
        ArrayList<Recipe> adaptersList = givenRecipeList();
        adapter = initializeRecipeListRowAdapter(adaptersList);

        adapter.update(new ArrayList<>(0));

        assertTrue(adaptersList.isEmpty());
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
        recipeList.add(newRecipe("Lobster tail"));
        recipeList.add(newRecipe("Crab stick"));
        recipeList.add(newRecipe("lobster soup"));
        recipeList.add(newRecipe("Beef celery"));
        recipeList.add(newRecipe("Lobster bisque"));

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
