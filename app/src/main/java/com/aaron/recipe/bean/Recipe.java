package com.aaron.recipe.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

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
    public Recipe()
    {
        /* Used for builder */
    }

    public Recipe(final int id, final String title, final String category, final int servings, final int preparationTime, final String description,
            final Ingredients ingredients, final Instructions instructions)
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

    public Recipe(final String title, final String category, final int servings, final int preparationTime, final String description,
            final Ingredients ingredients, final Instructions instructions)
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

    public Recipe setId(int id)
    {
        this.id = id;
        return this;
    }

    public Recipe setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public Recipe setCategory(String category)
    {
        this.category = category;
        return this;
    }

    public Recipe setServings(int servings)
    {
        this.servings = servings;
        return this;
    }

    public Recipe setPreparationTime(int preparationTime)
    {
        this.preparationTime = preparationTime;
        return this;
    }

    public Recipe setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public Recipe setIngredients(Ingredients ingredients)
    {
        this.ingredients = ingredients;
        return this;
    }

    public Recipe setInstructions(Instructions instructions)
    {
        this.instructions = instructions;
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

        Recipe recipe = (Recipe) o;

        return new EqualsBuilder()
                .append(id, recipe.id)
                .append(servings, recipe.servings)
                .append(preparationTime, recipe.preparationTime)
                .append(title, recipe.title)
                .append(category, recipe.category)
                .append(description, recipe.description)
                .append(ingredients, recipe.ingredients)
                .append(instructions, recipe.instructions)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, title, category, servings, preparationTime, description, ingredients, instructions);
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
                " Ingredients: " + this.ingredients +
                " Instructions: " + this.instructions;
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
