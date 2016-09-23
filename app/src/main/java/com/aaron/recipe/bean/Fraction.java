package com.aaron.recipe.bean;

import com.aaron.recipe.model.MathUtils;

import java.io.Serializable;

/**
 * Represents a decimal number as fraction.
 */
public class Fraction implements Serializable
{
    private static final long serialVersionUID = 3145067774846177809L;

    /**
     * Constants for fractional characters.
     */
    enum CommonFraction
    {
        THREE_FOURTH("¾", 0.75),
        ONE_HALF_PLUS_ONE_EIGHT("½ + ⅛", 0.625),
        ONE_HALF("½", 0.5),
        ONE_THIRD("⅓", 0.33),
        ONE_FOURTH("¼", 0.25),
        ONE_FIFTH("⅕", 0.2),
        ONE_EIGHT("⅛", 0.125);

        private final String code;
        private final double value;

        CommonFraction(String code, double value)
        {
            this.code = code;
            this.value = value;
        }

        public String getCode()
        {
            return this.code;
        }
        
        public double getValue()
        {
            return this.value;
        }
    }

    private String fraction;

    /**
     * Default constructor.
     * @param number the decimal number to be converted to fractional form 
     */
    public Fraction(double number)
    {
        this.fraction = this.convertToFraction(number);
    }

    /**
     * Getter for fraction.
     * @return String
     */
    public String getFraction()
    {
        return this.fraction;
    }

    /**
     * Converts the given decimal number to its fractional form with regards to CommonFraction enum. If the number is not found in CommonFraction then it will not be converted. 
     * @param number the decimal number
     * @return String
     */
    private String convertToFraction(final double number)
    {
        int wholeNumber = (int) number;
        double decimal = MathUtils.round(number - wholeNumber, 3);
        String fraction = "";

        for(CommonFraction cf : CommonFraction.values())
        {
            if(cf.getValue() == decimal)
            {
                fraction = cf.getCode();
            }
        }

        if(fraction.isEmpty())
        {
            return String.valueOf((int)number);
        }
        else
        {
            return wholeNumber + fraction;
        }
    }

    /**
     * Returns the fractional form.
     * @return String
     */
    @Override
    public String toString()
    {
        return this.fraction;
    }
}
