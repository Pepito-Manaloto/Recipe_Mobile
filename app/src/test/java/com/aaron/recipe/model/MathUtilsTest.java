package com.aaron.recipe.model;

import static org.junit.Assert.*;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class MathUtilsTest
{
    @Test
    public void givenNumberAndTwoPlaces_whenRound_thenShouldRoundUpToTwoDecimalPlaces()
    {
        double number = 10.1256789;
        int places = 2;

        double result = MathUtils.round(number, places);

        assertEquals(10.13, result, places);
    }

    @Test
    public void givenNumberAndFourPlaces_whenRound_thenShouldRoundUpToFourDecimalPlaces()
    {
        double number = 10.1256789;
        int places = 4;

        double result = MathUtils.round(number, places);

        assertEquals(10.1257, result, places);
    }

    @Test
    public void givenNumberAndThreePlaces_whenRound_thenShouldReturnUnRoundedThreeDecimalPlaces()
    {
        double number = 10.1254789;
        int places = 3;

        double result = MathUtils.round(number, places);

        assertEquals(10.125, result, places);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNumberAndNegativePlaces_whenRound_thenShouldThrowIllegalArgumentException()
    {
        MathUtils.round(RandomUtils.nextDouble(), -1);
    }
}
