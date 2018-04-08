package com.aaron.recipe.bean;
/**
 * Holds all the keys used in Bundle and Intent-extra.
 */
public enum DataKey
{
    EXTRA_PAGE("com.aaron.recipe.adapter.page"),
    EXTRA_SETTINGS("com.aaron.recipe.fragment.settings"),
    EXTRA_RECIPE_LIST("com.aaron.recipe.fragment.recipe_list.list");

    private String value;

    DataKey(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}