package com.aaron.recipe.bean;

import java.io.Serializable;

/**
 * Recipe class.
 */
public class Recipe implements Serializable
{
    private static final long serialVersionUID = 5455732827456969313L;
    public static final Category[] CATEGORY_ARRAY = Category.values();

    /**
     * Enum for recipe category list.
     */
    public enum Category
    {
        All,
        Beef,
        Chicken,
        Pork,
        Lamb,
        Seafood,
        Pasta,
        Vegetable,
        Soup,
        Dessert,
    }

    private String title;
    private Category category;
    private int servings;
    private String preparationTime;
    private String description;
    private Ingredients ingredients;
    private Instructions instructions;

    /**
     * Default constructor.
     */
    public Recipe(final String title, final Category category, final int servings, final int preparationTime,
                  final String description, final Ingredients ingredients, final Instructions instructions)
    {
        this.title = title;
        this.category = category;
        this.servings = servings;
        this.preparationTime = formatPreperationTime(preparationTime);
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    /**
     * Formats the given preparation time from minutes into hours + minutes. The given time is always assume to be in minutes.
     * @param minutes the minutes to convert
     * @return the converted minutes
     */
    private String formatPreperationTime(final int minutes)
    {
        int minutesTmp = minutes;
        int hrs = minutesTmp / 60;
        int mins = minutesTmp % 60;
        String formattedPreparationTime = mins + "";

        if(hrs == 1)
        {
            formattedPreparationTime = hrs + " hr " + formattedPreparationTime;
        }
        else if(hrs > 1)
        {
            formattedPreparationTime = hrs + " hrs " + formattedPreparationTime;
        }
        
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
     * Gets the title.
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Gets the servings
     * @return int
     */
    public int getServings()
    {
        return this.servings;
    }

    /**
     * Gets the category.
     * @return String
     */
    public String getCategory()
    {
        return this.category.name();
    }

    /**
     * Gets the preparationTime.
     * @return String
     */
    public String getPreparationTime()
    {
        return this.preparationTime;
    }

    /**
     * Gets the description.
     * @return String
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Gets the ingredients.
     * @return String
     */
    public Ingredients getIngredients()
    {
        return this.ingredients;
    }

    /**
     * Gets the instructions.
     * @return String
     */
    public Instructions getInstructions()
    {
        return this.instructions;
    }

    /**
     * Checks all attribute for equality.
     * @param o Recipe to compare
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

            return this.title.equals(that.getTitle()) && 
                   this.servings == that.getServings() &&
                   this.category.name().equals(that.getCategory()) &&
                   this.preparationTime.equals(that.getPreparationTime()) &&
                   this.description.equals(that.getDescription()) &&
                   this.ingredients.equals(that.getIngredients()) &&
                   this.instructions.equals(that.getInstructions());
        }
    }

    /**
     * Returns a unique hash code of the Recipe object.
     * @return int
     */
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 47 * hash + this.title.hashCode();
        hash = 47 * hash + this.category.hashCode();
        hash = 47 * hash + this.servings;
        hash = 47 * hash + this.preparationTime.hashCode();
        hash = 47 * hash + this.description.hashCode();
        hash = 47 * hash + this.ingredients.hashCode();
        hash = 47 * hash + this.instructions.hashCode();

        return hash;
    }

    /**
     * Returns the content of the Recipe object in a formatted String.
     * @return String
     */
    @Override
    public String toString()
    {
        return "Title: " + this.title +
               " Category: " + this.category + 
               " Servings: " + this.servings +
               " Preparation Time: " + this.preparationTime +
               " Description: " + this.description +
               " Ingredients: " + this.ingredients.toString() +
               " Instructions: " + this.instructions.toString();
    }
}
