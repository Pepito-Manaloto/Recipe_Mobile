package com.aaron.recipe.bean;

import java.io.Serializable;

public class Recipe implements Serializable
{
    private static final long serialVersionUID = 3783918997340685575L;
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

    private byte[] image;
    private String title;
    private String servings;
    private Category category;
    private String preparationTime;

    /**
     * Default constructor.
     */
    public Recipe(final byte[] image, final String title, final String servings, final Category category, final String preparationTime)
    {
        this.image = image;
        this.title = title;
        this.servings = servings;
        this.category = category;
        this.preparationTime = preparationTime;
    }

    /**
     * Gets the image.
     * @return byte[]
     */
    public byte[] getImage()
    {
        return this.image;
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
     * @return String
     */
    public String getServings()
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
                   this.servings.equals(that.getServings()) &&
                   this.category.name().equals(that.getCategory()) &&
                   this.preparationTime.equals(that.getPreparationTime());
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
        hash = 47 * hash + this.servings.hashCode();
        hash = 47 * hash + this.category.hashCode();
        hash = 47 * hash + this.preparationTime.hashCode();

        return hash;
    }

    /**
     * Returns the content of the Recipe object in a formatted String.
     * @return String
     */
    @Override
    public String toString()
    {
        return "Title: " + this.title + " " +
               "Category: " + this.category + " " + 
               "Servings: " + this.servings + " " +
               "Preparation Time: " + this.preparationTime + " ";
    }
}
