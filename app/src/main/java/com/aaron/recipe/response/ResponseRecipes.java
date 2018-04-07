package com.aaron.recipe.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Bean representing the http response from a /categories request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseRecipes
{
    private int recentlyAddedCount;
    private List<ResponseRecipe> recipeList;

    public int getRecentlyAddedCount()
    {
        return recentlyAddedCount;
    }

    @JsonProperty("recently_added_count")
    public void setRecentlyAddedCount(int recentlyAddedCount)
    {
        this.recentlyAddedCount = recentlyAddedCount;
    }

    @JsonProperty("recipes")
    public void setRecipeList(List<ResponseRecipe> recipeList)
    {
        this.recipeList = recipeList;
    }

    public List<ResponseRecipe> getRecipeList()
    {
        return recipeList;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }

        if(o == null || getClass() != o.getClass())
        {
            return false;
        }

        ResponseRecipes that = (ResponseRecipes) o;

        return new EqualsBuilder()
                .append(recentlyAddedCount, that.recentlyAddedCount)
                .append(recipeList, that.recipeList)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(recentlyAddedCount)
                .append(recipeList)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("recentlyAddedCount", recentlyAddedCount)
                .append("recipeList", recipeList)
                .toString();
    }
}
