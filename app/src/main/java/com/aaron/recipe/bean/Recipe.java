package com.aaron.recipe.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Recipe class.
 */
public class Recipe implements Parcelable
{
    private int id;
    private String title;
    private String category;
    private int servings;
    private int preparationTime;
    private String description;
    private Ingredients ingredients;
    private Instructions instructions;

    /**
     * Default constructor.
     */
    public Recipe(final int id, final String title, final String category, final int servings, final int preparationTime, final String description, final Ingredients ingredients, final Instructions instructions)
    {
        this.id = id;
        this.title = title;
        this.category = category;
        this.servings = servings;
        this.preparationTime = preparationTime;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Recipe(final String title, final String category, final int servings, final int preparationTime, final String description, final Ingredients ingredients, final Instructions instructions)
    {
        this(-1, title, category, servings, preparationTime, description, ingredients, instructions);
    }

    /**
     * Formats the given preparation time from minutes into hours + minutes. The given time is always assume to be in minutes.
     *
     * @param minutes
     *            the minutes to convert
     * @return the minutes converted to hours+minutes
     */
    private String formatPreparationTime(final int minutes)
    {
        int hrs = minutes / 60;
        int mins = minutes % 60;
        String formattedPreparationTime = "";

        // set minute/s
        if(mins > 0)
        {
            formattedPreparationTime = mins + "";
        }

        // set hour/s and hr/hrs string
        if(hrs == 1)
        {
            formattedPreparationTime = hrs + " hr " + formattedPreparationTime;
        }
        else if(hrs > 1)
        {
            formattedPreparationTime = hrs + " hrs " + formattedPreparationTime;
        }

        // set min/mins string
        if(mins == 1)
        {
            formattedPreparationTime += " min";
        }
        else if(mins > 1)
        {
            formattedPreparationTime += " mins";
        }

        return formattedPreparationTime;
    }

    /**
     * Gets the id.
     *
     * @return int
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Gets the title.
     *
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Gets the servings
     *
     * @return int
     */
    public int getServings()
    {
        return this.servings;
    }

    /**
     * Gets the category.
     *
     * @return String
     */
    public String getCategory()
    {
        return this.category;
    }

    /**
     * Gets the preparationTime.
     *
     * @return String
     */
    public String getPreparationTimeString()
    {
        return this.formatPreparationTime(this.preparationTime);
    }

    /**
     * Gets the preparationTime.
     *
     * @return int
     */
    public int getPreparationTime()
    {
        return this.preparationTime;
    }

    /**
     * Gets the description.
     *
     * @return String
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Gets the ingredients.
     *
     * @return String
     */
    public Ingredients getIngredients()
    {
        return this.ingredients;
    }

    /**
     * Gets the instructions.
     *
     * @return String
     */
    public Instructions getInstructions()
    {
        return this.instructions;
    }

    /**
     * Checks all attribute for equality.
     *
     * @param o
     *            Recipe to compare
     * @return true if equals, else false
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof Recipe)) // object being compared is not Recipe
        {
            return false;
        }
        else
        {
            Recipe that = (Recipe) o;

            return this.id == that.getId() &&
                    this.title.equals(that.getTitle()) &&
                    this.servings == that.getServings() &&
                    this.category.equals(that.getCategory()) &&
                    this.getPreparationTimeString().equals(that.getPreparationTimeString()) &&
                    this.description.equals(that.getDescription()) &&
                    this.ingredients.equals(that.getIngredients()) &&
                    this.instructions.equals(that.getInstructions());
        }
    }

    /**
     * Returns a unique hash code of the Recipe object.
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 47 * hash + this.id;
        hash = 47 * hash + this.title.hashCode();
        hash = 47 * hash + this.category.hashCode();
        hash = 47 * hash + this.servings;
        hash = 47 * hash + this.getPreparationTimeString().hashCode();
        hash = 47 * hash + this.description.hashCode();
        hash = 47 * hash + this.ingredients.hashCode();
        hash = 47 * hash + this.instructions.hashCode();

        return hash;
    }

    /**
     * Returns the content of the Recipe object in a formatted String.
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return "Id: " + this.id +
                " Title: " + this.title +
                " Category: " + this.category +
                " Servings: " + this.servings +
                " Preparation Time: " + this.getPreparationTimeString() +
                " Description: " + this.description +
                " Ingredients: " + this.ingredients.toString() +
                " Instructions: " + this.instructions.toString();
    }

    /**
     * Constructor that will be called in creating the parcel. Note: Reading the parcel should be the same order as writing the parcel!
     */
    private Recipe(Parcel in)
    {
        this.id = in.readInt();
        this.title = in.readString();
        this.category = in.readString();
        this.servings = in.readInt();
        this.preparationTime = in.readInt();
        this.description = in.readString();
        this.ingredients = in.readParcelable(Ingredients.class.getClassLoader());
        this.instructions = in.readParcelable(Instructions.class.getClassLoader());
    }

    /**
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.category);
        dest.writeInt(this.servings);
        dest.writeInt(this.preparationTime);
        dest.writeString(this.description);
        dest.writeParcelable(this.ingredients, flags);
        dest.writeParcelable(this.instructions, flags);
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
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>()
    {
        @Override
        public Recipe createFromParcel(Parcel in)
        {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size)
        {
            return new Recipe[size];
        }
    };
}
