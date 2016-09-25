package com.aaron.recipe.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Math utils.
 */
public class MathUtils
{
    /**
     * Rounds off a decimal number to nth places.
     *
     * @param value  the decimal number to round off
     * @param places the nth places to round off
     * @return the rounded off number
     */
    public static double round(double value, int places)
    {
        if(places < 0)
        {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}