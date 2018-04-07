package com.aaron.recipe.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

/**
 * A single ingredient.
 */
public class Ingredient implements Parcelable
{
    private double quantity;
    private String measurement;
    private String ingredient;
    private String comment;

    /**
     * Default constructor.
     */
    public Ingredient()
    {
        /* Used for builder */
    }

    public Ingredient(final double quantity, final String measurement, final String ingredient, final String comment)
    {
        this.quantity = quantity;
        this.measurement = measurement;
        this.ingredient = ingredient;
        this.comment = comment;
    }

    /**
     * Gets the quantity.
     * 
     * @return double
     */
    public double getQuantity()
    {
        return this.quantity;
    }

    /**
     * Gets the measurement.
     * 
     * @return String
     */
    public String getMeasurement()
    {
        return this.measurement;
    }

    /**
     * Gets the ingredient.
     * 
     * @return String
     */
    public String getIngredient()
    {
        return this.ingredient;
    }

    /**
     * Gets the comment.
     * 
     * @return String
     */
    public String getComment()
    {
        return this.comment;
    }

    public Ingredient setQuantity(double quantity)
    {
        this.quantity = quantity;
        return this;
    }

    public Ingredient setMeasurement(String measurement)
    {
        this.measurement = measurement;
        return this;
    }

    public Ingredient setIngredient(String ingredient)
    {
        this.ingredient = ingredient;
        return this;
    }

    public Ingredient setComment(String comment)
    {
        this.comment = comment;
        return this;
    }

    /**
     * Checks for equality, only checks the title of the ingredient.
     * 
     * @param obj
     *            Ingredients object to compare to
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
     * 
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
     * 
     * @return String
     */
    @Override
    public String toString()
    {
        Fraction quantityInFraction = new Fraction(this.quantity);

        String toReturn = quantityInFraction.getFraction() + " " + this.measurement + " " + this.ingredient;

        if(StringUtils.isNotBlank(comment))
        {
            toReturn += " (" + this.comment + ")";
        }

        return toReturn;
    }

    /**
     * Constructor that will be called in creating the parcel. Note: Reading the parcel should be the same order as writing the parcel!
     */
    private Ingredient(Parcel in)
    {
        this.quantity = in.readDouble();
        this.measurement = in.readString();
        this.ingredient = in.readString();
        this.comment = in.readString();
    }

    /**
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeDouble(this.quantity);
        dest.writeString(this.measurement);
        dest.writeString(this.ingredient);
        dest.writeString(this.comment);
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
    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>()
    {
        @Override
        public Ingredient createFromParcel(Parcel in)
        {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size)
        {
            return new Ingredient[size];
        }
    };
}