package com.aaron.recipe.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseIngredient
{
    private double quantity;
    private String measurement;
    private String ingredient;
    private String comment;

    public double getQuantity()
    {
        return quantity;
    }

    public void setQuantity(double quantity)
    {
        this.quantity = quantity;
    }

    public String getMeasurement()
    {
        return measurement;
    }

    public void setMeasurement(String measurement)
    {
        this.measurement = measurement;
    }

    public String getIngredient()
    {
        return ingredient;
    }

    public void setIngredient(String ingredient)
    {
        this.ingredient = ingredient;
    }

    public String getComment()
    {
        return comment;
    }

    @JsonProperty("comment_")
    public void setComment(String comment)
    {
        this.comment = comment;
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

        ResponseIngredient that = (ResponseIngredient) o;

        return new EqualsBuilder()
                .append(quantity, that.quantity)
                .append(measurement, that.measurement)
                .append(ingredient, that.ingredient)
                .append(comment, that.comment)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(quantity, measurement, ingredient, comment);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("quantity", quantity)
                .append("measurement", measurement)
                .append("ingredient", ingredient)
                .append("comment", comment)
                .toString();
    }
}
