package com.aaron.recipe.bean;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Java bean for the application settings.
 */
public class Settings implements Parcelable
{
    /**
     * Enum for the list of default font name.
     */
    public enum FontName
    {
        Default, Serif, Sans_Serif, Monospace,
    }

    /**
     * Enum for the list of default font style.
     */
    public enum FontStyle
    {
        Normal, Bold, Italic, Bold_Italic,
    }

    private String category;
    private FontName fontName;
    private FontStyle fontStyle;
    private int fontSize;
    private String serverURL;

    /**
     * Default constructor, initializes with default values.
     */
    public Settings()
    {
        this.category = Categories.DEFAULT;
        this.fontName = FontName.Default;
        this.fontStyle = FontStyle.Normal;
        this.fontSize = 14;
        this.serverURL = "";
    }

    /**
     * Getter for Category.
     *
     * @return Category
     */
    public String getCategory()
    {
        return this.category;
    }

    /**
     * Getter for Category's index.
     *
     * @return Category index
     */
    public int getCategoryIndex()
    {
        return Categories.getIndex(this.category);
    }

    /**
     * Getter for fontSize.
     *
     * @return int
     */
    public int getFontSize()
    {
        return this.fontSize;
    }

    /**
     * Getter for FontName's index.
     *
     * @return FontName index
     */
    public int getFontNameIndex()
    {
        return this.fontName.ordinal();
    }

    /**
     * Getter for FontStyle's index.
     *
     * @return FontStyle index
     */
    public int getFontStyleIndex()
    {
        return this.fontStyle.ordinal();
    }

    /**
     * Getter for fontSize's index.
     *
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
     * Getter for Server URL.
     *
     * @return Server URL
     */
    public String getServerURL()
    {
        return this.serverURL;
    }

    /**
     * Returns the typeface of this recipe.
     *
     * @param isBold checker if typeface will return bold regardless of the selected settings
     * @return Typeface
     */
    public Typeface getTypeface(final boolean isBold)
    {
        Typeface family;

        switch(this.fontName)
        {
            case Serif:
                family = Typeface.SERIF;
                break;
            case Sans_Serif:
                family = Typeface.SANS_SERIF;
                break;
            case Monospace:
                family = Typeface.MONOSPACE;
                break;

            default:
                family = Typeface.DEFAULT;
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
     *
     * @param category the category to set
     * @return the settings object being updated
     */
    public Settings setCategory(final String category)
    {
        this.category = category;
        return this;
    }

    /**
     * Sets the fontName new value.
     *
     * @param fontName the font name to set
     * @return the settings object being updated
     */
    public Settings setFontName(final FontName fontName)
    {
        this.fontName = fontName;
        return this;
    }

    /**
     * Sets the fontStyle new value.
     *
     * @param fontStyle the font style to set
     * @return the settings object being updated
     */
    public Settings setFontStyle(final FontStyle fontStyle)
    {
        this.fontStyle = fontStyle;
        return this;
    }

    /**
     * Sets the fontSize new value.
     *
     * @param fontSize the font size to set
     * @return the settings object being updated
     */
    public Settings setFontSize(final int fontSize)
    {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * Sets the serverURL new value.
     *
     * @param serverURL the server url to set
     * @return the settings object being updated
     */
    public Settings setServerURL(final String serverURL)
    {
        this.serverURL = serverURL;
        return this;
    }

    /**
     * Returns the content of the Settings object in a formatted String.
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return "Category: " + this.category + " Font name: " + this.fontName + " Font style: " + this.fontStyle + " Font size: " + this.fontSize + " Server URL: " + this.serverURL;
    }

    /**
     * Checks all attribute for equality.
     *
     * @param o Settings to compare
     * @return true if equals, else false
     */
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof Settings))
        {
            return false;
        }
        else
        {
            Settings settings = (Settings) o;

            return fontSize == settings.fontSize && category.equals(settings.category) && fontName == settings.fontName && fontStyle == settings.fontStyle && (serverURL != null ? serverURL.equals(settings.serverURL) : settings.serverURL == null);
        }
    }

    /**
     * Returns a unique hash code of the Settings object.
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (fontName != null ? fontName.hashCode() : 0);
        result = 31 * result + (fontStyle != null ? fontStyle.hashCode() : 0);
        result = 31 * result + fontSize;
        result = 31 * result + (serverURL != null ? serverURL.hashCode() : 0);
        return result;
    }

    /**
     * Constructor that will be called in creating the parcel. Note: Reading the parcel should be the same order as writing the parcel!
     */
    private Settings(Parcel in)
    {
        this.category = in.readString();
        this.fontName = FontName.values()[in.readInt()];
        this.fontStyle = FontStyle.values()[in.readInt()];
        this.fontSize = in.readInt();
        this.serverURL = in.readString();
    }

    /**
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.category);
        dest.writeInt(this.fontName != null ? this.fontName.ordinal() : 0);
        dest.writeInt(this.fontStyle != null ? this.fontStyle.ordinal() : 0);
        dest.writeInt(this.fontSize);
        dest.writeString(this.serverURL);
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
    public static final Creator<Settings> CREATOR = new Creator<Settings>()
    {
        @Override
        public Settings createFromParcel(Parcel in)
        {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size)
        {
            return new Settings[size];
        }
    };
}
