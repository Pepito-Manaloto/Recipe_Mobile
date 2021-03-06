package com.aaron.recipe.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ingredients Class.
 */
public class Ingredients implements Parcelable
{
    private String title;
    private ArrayList<Ingredient> ingredientsList;

    /**
     * Default Constructor.
     */
    public Ingredients(final String title, final int numOfIngredient)
    {
        this.title = title;
        this.ingredientsList = new ArrayList<>(numOfIngredient);
    }

    public Ingredients(String title, List<Ingredient> ingredientsList)
    {
        this.title = title;
        this.ingredientsList = new ArrayList<>(ingredientsList);
    }

    /**
     * Adds an ingredient to the ingredientsList.
     * @param ingredient an ingredient
     */
    public void addIngredient(final Ingredient ingredient)
    {
        this.ingredientsList.add(ingredient);
    }

    /**
     * Gets the instructionsList.
     * @return ArrayList<String>
     */
    public ArrayList<Ingredient> getIngredientsList()
    {
        return this.ingredientsList;
    }

    /**
     * Gets the title.
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    public Ingredients setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public Ingredients setIngredientsList(ArrayList<Ingredient> ingredientsList)
    {
        this.ingredientsList = ingredientsList;
        return this;
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

        Ingredients that = (Ingredients) o;

        return new EqualsBuilder()
                .append(title, that.title)
                .append(ingredientsList, that.ingredientsList)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(title, ingredientsList);
    }

    /**
     * Returns a string representation of the object.
     * @return String
     */
    @Override
    public String toString()
    {
        return "title: " + this.title +
                " ingredients: " + this.ingredientsList;
    }

    /**
     * Constructor that will be called in creating the parcel. Note: Reading the parcel should be the same order as writing the parcel!
     */
    private Ingredients(Parcel in)
    {
        this.ingredientsList = new ArrayList<>();
        this.title = in.readString();
        in.readTypedList(this.ingredientsList, Ingredient.CREATOR);
    }

    /**
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.title);
        dest.writeTypedList(this.ingredientsList);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Generates instances of your Parcelable class from a Parcel.
     */
    public static final Creator<Ingredients> CREATOR = new Creator<Ingredients>()
    {
        @Override
        public Ingredients createFromParcel(Parcel in)
        {
            return new Ingredients(in);
        }

        @Override
        public Ingredients[] newArray(int size)
        {
            return new Ingredients[size];
        }
    };
}
