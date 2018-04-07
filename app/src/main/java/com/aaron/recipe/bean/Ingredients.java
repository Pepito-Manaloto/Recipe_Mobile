package com.aaron.recipe.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Checks for equality, only checks the title of the ingredient.
     * @param obj Ingredients object to compare to
     * @return boolean true if equal, else false
     */
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Ingredients))
        {
            return false;
        }
        else
        {
            Ingredients that = (Ingredients) obj;
            return this.title.equals(that.getTitle());
        }
    }

    /**
     * Returns the hashcode of this object. Derived from title.
     * @return int
     */
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 47 * hash + (this.title + "_INGREDIENTS").hashCode();

        return hash;
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
