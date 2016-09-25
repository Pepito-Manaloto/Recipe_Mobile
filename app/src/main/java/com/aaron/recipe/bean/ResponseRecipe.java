package com.aaron.recipe.bean;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Created by Aaron on 9/24/2016.
 */
public class ResponseRecipe
{
    private int statusCode;
    private String text;
    private String body;
    private int recentlyAddedCount;
    private EnumMap<Recipe.Category, ArrayList<Recipe>> recipeMap;

    public ResponseRecipe()
    {
    }

    public ResponseRecipe(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public int getStatusCode()
    {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getBody()
    {
        return this.body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public int getRecentlyAddedCount()
    {
        return recentlyAddedCount;
    }

    public void setRecentlyAddedCount(int recentlyAddedCount)
    {
        this.recentlyAddedCount = recentlyAddedCount;
    }

    public EnumMap<Recipe.Category, ArrayList<Recipe>> getRecipeMap()
    {
        return recipeMap;
    }

    public void setRecipeMap(EnumMap<Recipe.Category, ArrayList<Recipe>> recipeMap)
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

            return statusCode != that.statusCode || recentlyAddedCount != that.recentlyAddedCount ||
                    text != null ? !text.equals(that.text) : that.text != null || body != null ? !body.equals(that.body) : that.body != null || recipeMap != null ? recipeMap.equals(that.recipeMap) : that.recipeMap == null;
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
        return "statusCode: " + statusCode +
                ", text: " + text + ", body: " + body +
                ", recentlyAddedCount: " + recentlyAddedCount +
                ", recipeMap: " + recipeMap;
    }
}
