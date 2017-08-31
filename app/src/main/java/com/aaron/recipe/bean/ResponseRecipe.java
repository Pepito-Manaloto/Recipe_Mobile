package com.aaron.recipe.bean;

import java.util.ArrayList;
import java.util.Map;

/**
 * Bean representing the http response from a recipe request.
 */
public class ResponseRecipe extends Response
{
    private int recentlyAddedCount;
    private Map<String, ArrayList<Recipe>> recipeMap;

    public ResponseRecipe()
    {
        super();
    }

    public ResponseRecipe(int statusCode)
    {
        super(statusCode);
    }

    public int getRecentlyAddedCount()
    {
        return this.recentlyAddedCount;
    }

    public void setRecentlyAddedCount(int recentlyAddedCount)
    {
        this.recentlyAddedCount = recentlyAddedCount;
    }

    public Map<String, ArrayList<Recipe>> getRecipeMap()
    {
        return this.recipeMap;
    }

    public void setRecipeMap(Map<String, ArrayList<Recipe>> recipeMap)
    {
        this.recipeMap = recipeMap;
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof ResponseRecipe))
        {
            return false;
        }
        else
        {
            ResponseRecipe that = (ResponseRecipe) o;

            return statusCode != that.statusCode || recentlyAddedCount != that.recentlyAddedCount || text != null ? !text.equals(that.text) : that.text != null || body != null ? !body.equals(that.body) : that.body != null || recipeMap != null ? recipeMap.equals(that.recipeMap) : that.recipeMap == null;
        }
    }

    @Override
    public int hashCode()
    {
        int result = statusCode;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + recentlyAddedCount;
        result = 31 * result + (recipeMap != null ? recipeMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", recentlyAddedCount: " + recentlyAddedCount + ", recipeMap: " + recipeMap;
    }
}
