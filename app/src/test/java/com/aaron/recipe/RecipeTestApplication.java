package com.aaron.recipe;

import com.aaron.recipe.app.RecipeApplication;

public class RecipeTestApplication extends RecipeApplication
{
    @Override
    protected boolean isTest()
    {
        return true;
    }
}
