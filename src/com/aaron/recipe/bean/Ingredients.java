package com.aaron.recipe.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Ingredients Class.
 */
public class Ingredients implements Serializable
{
    private static final long serialVersionUID = -7291846786735906304L;

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
     * Helper class, a single ingredient.
     */
    public static class Ingredient implements Serializable
    {
        private static final long serialVersionUID = -618537983872529668L;
        private double quantity;
        private String measurement;
        private String ingredient;
        private String comment;

        /**
         * Default constructor.
         */
        public Ingredient(final double quantity, final String measurement, final String ingredient, final String comment)
        {
            this.quantity = quantity;
            this.measurement = measurement;
            this.ingredient = ingredient;
            this.comment = comment;
        }

        /**
         * Gets the quantity.
         * @return double
         */
        public double getQuantity()
        {
            return this.quantity;
        }

        /**
         * Gets the measurement.
         * @return String
         */
        public String getMeasurement()
        {
            return this.measurement;
        }

        /**
         * Gets the ingredient.
         * @return String
         */
        public String getIngredient()
        {
            return this.ingredient;
        }

        /**
         * Gets the comment.
         * @return String
         */
        public String getComment()
        {
            return this.comment;
        }

        /**
         * Checks for equality, only checks the title of the ingredient.
         * @param obj Ingredients object to compare to
         * @return boolean true if equal, else false
         */
        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof Ingredient))
            {
                return false;
            }
            else
            {
                Ingredient that = (Ingredient) obj;
                return this.quantity == that.getQuantity() &&
                       this.measurement.equals(that.getMeasurement()) &&
                       this.ingredient.equals(that.getIngredient()) &&
                       this.comment.equals(that.getComment());
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
            hash = 47 * hash + (int) this.quantity;
            hash = 47 * hash + this.measurement.hashCode();
            hash = 47 * hash + this.ingredient.hashCode();
            hash = 47 * hash + this.comment.hashCode();

            return hash;
        }

        /**
         * Returns a string representation of the object.
         * @return String
         */
        @Override
        public String toString()
        {
            Fraction quantityInFraction = new Fraction(this.quantity);

            String toReturn = quantityInFraction.getFraction()  + " " + this.measurement + " " + this.ingredient;

            if(this.comment.length() > 0)
            {
                toReturn += " (" + this.comment + ")";
            }

            return toReturn;
        }
    }
}
