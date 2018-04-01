package com.aaron.recipe.fragment;

/**
 * Defines an Activity or Fragment that is capable of handling back button event.
 */
public interface Backable
{
    /**
     * Finishes the current activity and navigate to the next one.
     */
    void setActivityResultOnBackEvent();
}