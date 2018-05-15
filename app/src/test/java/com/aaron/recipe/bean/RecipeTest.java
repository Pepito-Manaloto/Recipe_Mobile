package com.aaron.recipe.bean;

import static org.junit.Assert.*;

import org.junit.Test;

public class RecipeTest
{
    @Test
    public void givenLessThanAnHourPreparationTime_whenGetPreparationTimeString_thenShouldReturnFormattedPreparationTime()
    {
        Recipe recipe = new Recipe();
        int preparationTime = 35;

        recipe.setPreparationTime(preparationTime);

        assertEquals("35 mins", recipe.getPreparationTimeString());
    }

    @Test
    public void givenAnHourPreparationTime_whenGetPreparationTimeString_thenShouldReturnFormattedPreparationTime()
    {
        Recipe recipe = new Recipe();
        int preparationTime = 60;

        recipe.setPreparationTime(preparationTime);

        assertEquals("1 hr", recipe.getPreparationTimeString());
    }

    @Test
    public void givenMoreThanAnHourPreparationTime_whenGetPreparationTimeString_thenShouldReturnFormattedPreparationTime()
    {
        Recipe recipe = new Recipe();
        int preparationTime = 180;

        recipe.setPreparationTime(preparationTime);

        assertEquals("3 hrs", recipe.getPreparationTimeString());
    }

    @Test
    public void givenAnHourAndAMinutePreparationTime_whenGetPreparationTimeString_thenShouldReturnFormattedPreparationTime()
    {
        Recipe recipe = new Recipe();
        int preparationTime = 61;

        recipe.setPreparationTime(preparationTime);

        assertEquals("1 hr 1 min", recipe.getPreparationTimeString());
    }

    @Test
    public void givenAnHourAndMoreThanAMinutePreparationTime_whenGetPreparationTimeString_thenShouldReturnFormattedPreparationTime()
    {
        Recipe recipe = new Recipe();
        int preparationTime = 65;

        recipe.setPreparationTime(preparationTime);

        assertEquals("1 hr 5 mins", recipe.getPreparationTimeString());
    }

    @Test
    public void givenMoreThanAnHourAndAMinutePreparationTime_whenGetPreparationTimeString_thenShouldReturnFormattedPreparationTime()
    {
        Recipe recipe = new Recipe();
        int preparationTime = 181;

        recipe.setPreparationTime(preparationTime);

        assertEquals("3 hrs 1 min", recipe.getPreparationTimeString());
    }

    @Test
    public void givenMoreThanAnHourAndMoreThanAMinutePreparationTime_whenGetPreparationTimeString_thenShouldReturnFormattedPreparationTime()
    {
        Recipe recipe = new Recipe();
        int preparationTime = 216;

        recipe.setPreparationTime(preparationTime);

        assertEquals("3 hrs 36 mins", recipe.getPreparationTimeString());
    }
}
