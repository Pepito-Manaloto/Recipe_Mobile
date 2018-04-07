package com.aaron.recipe.model;

import com.aaron.recipe.response.ResponseCategory;
import com.aaron.recipe.response.ResponseRecipes;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeService
{
    /**
     * Retrieves all recipes given the last updated date.
     *
     * @param lastUpdated filter get request with last updated date
     * @return {@code Call<ResponseRecipes>} response Recipe
     */
    @GET("recipes")
    Single<ResponseRecipes> getRecipes(@Query("last_updated") String lastUpdated);

    /**
     * Retrieves all categories.
     *
     * @return {@code Call<List<ResponseCategory>>} response Category
     */
    @GET("categories")
    Single<List<ResponseCategory>> getCategories();
}
