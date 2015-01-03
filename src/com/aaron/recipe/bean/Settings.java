package com.aaron.recipe.bean;

import java.io.Serializable;

import com.aaron.recipe.bean.Recipe.Category;

import android.graphics.Typeface;

/**
 * Java bean for the application settings.
 */
public class Settings implements Serializable
{
    private static final long serialVersionUID = -8674493096543909252L;

    /**
     * Enum for the list of default font name.
     */
    public enum FontName
    {
        Default,
        Serif,
        Sans_Serif,
        Monospace,
    }

    /**
     * Enum for the list of default font style.
     */
    public enum FontStyle
    {
        Normal,
        Bold,
        Italic,
        Bold_Italic,
    }

    private Category category;
    private FontName fontName;
    private FontStyle fontStyle;
    private int fontSize;

    /**
     * Default constructor, initializes with default values.
     */
    public Settings()
    {
        this.category = Category.All;
        this.fontName = FontName.Default;
        this.fontStyle = FontStyle.Normal;
        this.fontSize = 14;
    }

    /**
     * Getter for Category.
     * @return Category
     */
    public Category getCategory()
    {
        return this.category;
    }

    /**
     * Getter for Category's index.
     * @return Category index
     */
    public int getCategoryIndex()
    {
        return this.category.ordinal();
    }

    /**
     * Getter for fontSize.
     * @return int
     */
    public int getFontSize()
    {
        return this.fontSize;
    }

    /**
     * Getter for FontName's index.
     * @return FontName index
     */
    public int getFontNameIndex()
    {
        return this.fontName.ordinal();
    }

    /**
     * Getter for FontStyle's index.
     * @return FontStyle index
     */
    public int getFontStyleIndex()
    {
        return this.fontStyle.ordinal();
    }

    /**
     * Getter for fontSize's index.
     * @return FontSize index
     */
    public int getFontSizeIndex()
    {
        switch(this.fontSize)
        {
            case 14: 
                return 0;
            case 15: 
                return 1;
            case 16: 
                return 2;
            case 17:
                return 3;
            case 18:
                return 4;
            case 19:
                return 5;
            case 20:
                return 6;
            default: 
                throw new AssertionError();
        }
    }

    /**
     * Returns the content of the Settings object in a formatted String.
     * @return String
     */
    @Override
    public String toString()
    {
        return " Category: " + this.category +
               " Font name: " + this.fontName +
               " Font style: " + this.fontStyle +
               " Font size: " + this.fontSize;
    }

    /**
     * Returns the typeface of this recipe.
     * @param isBold checker if typeface will return bold regardless of the selected settings
     * @return Typeface
     */
    public Typeface getTypeface(final boolean isBold)
    {
        Typeface family;

        switch(this.fontName)
        {
            case Serif:      family = Typeface.SERIF; 
                             break;
            case Sans_Serif: family = Typeface.SANS_SERIF; 
                             break;
            case Monospace:  family = Typeface.MONOSPACE; 
                             break;

            default: family = Typeface.DEFAULT;
        }
        
        int style = this.getFontStyleIndex();

        if(isBold)
        {
            style = 1;
        }

        return Typeface.create(family, style);
    }

    /**
     * Sets the category new value.
     * @param Category
     * @return the settings object being updated
     */
    public Settings setCategory(final Category category)
    {
        this.category = category;
        return this;
    }

    /**
     * Sets the fontName new value.
     * @param FontName
     * @return the settings object being updated
     */
    public Settings setFontName(final FontName fontName)
    {
        this.fontName = fontName;
        return this;
    }

    /**
     * Sets the fontStyle new value.
     * @param FontStyle
     * @return the settings object being updated
     */
    public Settings setFontStyle(final FontStyle fontStyle)
    {
        this.fontStyle = fontStyle;
        return this;
    }

    /**
     * Sets the fontSize new value.
     * @param fontSize
     * @return the settings object being updated
     */
    public Settings setFontSize(final int fontSize)
    {
        this.fontSize = fontSize;
        return this;
    }
}
