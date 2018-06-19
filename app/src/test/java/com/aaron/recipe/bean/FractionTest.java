package com.aaron.recipe.bean;

import static org.junit.Assert.*;
import static java.util.Map.Entry;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class FractionTest
{
    @Test
    public void givenCommonDecimals_whenGetFraction_thenShouldReturnSingleFractions()
    {
        Map<Double, String> commonDecimals = givenCommonDecimals();

        iterateOverTestMapAndAssert(commonDecimals);
    }

    @Test
    public void givenComplexDecimals_whenGetFraction_thenShouldReturnTwoFractions()
    {
        Map<Double, String> complexDecimals = givenComplexDecimals();

        iterateOverTestMapAndAssert(complexDecimals);
    }

    @Test
    public void givenSuperComplexDecimals_whenGetFraction_thenShouldReturnSameDecimal()
    {
        double[] superComplexDecimals = givenSuperComplexDecimals();

        for(double decimal : superComplexDecimals)
        {
            Fraction fraction = new Fraction(decimal);

            assertEquals(String.valueOf(decimal), fraction.getFraction());
        }
    }

    private Map<Double, String> givenCommonDecimals()
    {
        Map<Double, String> commonDecimals = new HashMap<>();
        commonDecimals.put(0.125, "⅛");
        commonDecimals.put(0.2, "⅕");
        commonDecimals.put(0.25, "¼");
        commonDecimals.put(0.33, "⅓");
        commonDecimals.put(0.5, "½");
        commonDecimals.put(0.66, "⅔");
        commonDecimals.put(0.75, "¾");

        return commonDecimals;
    }

    private Map<Double, String> givenComplexDecimals()
    {
        Map<Double, String> complexDecimals = new HashMap<>();
        complexDecimals.put(0.325, "⅕ + ⅛");
        complexDecimals.put(0.375, "¼ + ⅛");
        complexDecimals.put(0.45, "¼ + ⅕");
        complexDecimals.put(0.625, "½ + ⅛");
        complexDecimals.put(0.7, "½ + ⅕");

        return complexDecimals;
    }

    private double[] givenSuperComplexDecimals()
    {
        return new double[] { 0.05, 0.175, 0.38, 0.49, 0.77, 0.89, 0.92 };
    }

    private void iterateOverTestMapAndAssert(Map<Double, String> testMap)
    {
        for(Entry<Double, String> entry : testMap.entrySet())
        {
            double decimal = entry.getKey();
            String expectedFraction = entry.getValue();

            Fraction fraction = new Fraction(decimal);

            assertEquals(expectedFraction, fraction.getFraction());
        }
    }
}
