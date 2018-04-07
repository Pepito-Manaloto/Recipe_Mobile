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
public class ResponseRecipe
{
    private String title;
    private String category;
    private int preparationTime;
    private int servings;
    private String description;
    private List<ResponseIngredient> ingredientList;
    private List<ResponseInstruction> instructionList;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public int getPreparationTime()
    {
        return preparationTime;
    }

    @JsonProperty("preparation_time")
    public void setPreparationTime(int preparationTime)
    {
        this.preparationTime = preparationTime;
    }

    public int getServings()
    {
        return servings;
    }

    public void setServings(int servings)
    {
        this.servings = servings;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<ResponseIngredient> getIngredientList()
    {
        return ingredientList;
    }

    @JsonProperty("ingredients")
    public void setIngredientList(List<ResponseIngredient> ingredientList)
    {
        this.ingredientList = ingredientList;
    }

    public List<ResponseInstruction> getInstructionList()
    {
        return instructionList;
    }

    @JsonProperty("instructions")
    public void setInstructionList(List<ResponseInstruction> instructionList)
    {
        this.instructionList = instructionList;
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

        ResponseRecipe that = (ResponseRecipe) o;

        return new EqualsBuilder()
                .append(preparationTime, that.preparationTime)
                .append(servings, that.servings)
                .append(title, that.title)
                .append(category, that.category)
                .append(description, that.description)
                .append(ingredientList, that.ingredientList)
                .append(instructionList, that.instructionList)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(category)
                .append(preparationTime)
                .append(servings)
                .append(description)
                .append(ingredientList)
                .append(instructionList)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("title", title)
                .append("category", category)
                .append("preparationTime", preparationTime)
                .append("servings", servings)
                .append("description", description)
                .append("ingredientList", ingredientList)
                .append("instructionList", instructionList)
                .toString();
    }
}
