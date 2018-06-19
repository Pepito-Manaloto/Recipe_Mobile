package com.aaron.recipe.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.aaron.recipe.model.MathUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a decimal number as fraction.
 */
public class Fraction implements Parcelable
{
    private static final String PLUS = " + ";

    /**
     * Constants for fractional characters.
     */
    public enum CommonFraction
    {
        THREE_FOURTH("¾", 0.75),
        TWO_THIRD("⅔", 0.66),
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
     *
     * @param number the decimal number to be converted to fractional form
     */
    public Fraction(double number)
    {
        this.fraction = this.convertToFraction(number);
    }

    /**
     * Getter for fraction.
     *
     * @return String
     */
    public String getFraction()
    {
        return this.fraction;
    }

    /**
     * Converts the given decimal number to its fractional form with regards to CommonFraction enum. If the number is not found in CommonFraction then it will not be converted.
     *
     * @param decimalNumber the decimal number
     * @return String
     */
    private String convertToFraction(final double decimalNumber)
    {
        int wholeNumber = (int) decimalNumber;
        double decimal = MathUtils.round(decimalNumber - wholeNumber, 3);

        String fraction = deriveFractionFromDecimal(decimal);
        if(fraction.isEmpty())
        {
            if(decimal == 0)
            {
                return String.valueOf(wholeNumber);
            }
            else
            {
                return String.valueOf(decimalNumber);
            }
        }
        else
        {
            return (wholeNumber == 0 ? "" : wholeNumber) + fraction;
        }
    }

    private String deriveFractionFromDecimal(double decimal)
    {
        Predicate<CommonFraction> isFractionDecimalEqualToDecimal = cf -> cf.getValue() == decimal;
        Optional<CommonFraction> cf = Arrays.stream(CommonFraction.values()).filter(isFractionDecimalEqualToDecimal).findFirst();
        String fraction;
        if(cf.isPresent())
        {
            fraction = cf.get().getCode();
        }
        else
        {
            fraction = getComplexFraction(decimal);
        }

        return fraction;
    }

    /**
     *
     */
    private String getComplexFraction(double decimal)
    {
        CommonFraction[] commonFractions = CommonFraction.values();
        int lastIndex = commonFractions.length - 1;

        for(int firstFractionIndex = lastIndex; firstFractionIndex > 0; firstFractionIndex--)
        {
            for(int secondFractionIndex = firstFractionIndex - 1; secondFractionIndex >= 0; secondFractionIndex--)
            {
                CommonFraction firstFraction = commonFractions[firstFractionIndex];
                CommonFraction secondFraction = commonFractions[secondFractionIndex];
                double twoFractionsSum = firstFraction.getValue() + secondFraction.getValue();
                if(decimal == twoFractionsSum)
                {
                    return secondFraction.getCode() + PLUS + firstFraction.getCode();
                }
            }
        }

        return "";
    }

    /**
     * Returns the fractional form.
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return this.fraction;
    }

    /**
     * Constructor that will be called in creating the parcel. Note: Reading the parcel should be the same order as writing the parcel!
     */
    private Fraction(Parcel in)
    {
        this.fraction = in.readString();
    }

    /**
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.fraction);
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
    public static final Creator<Fraction> CREATOR = new Creator<Fraction>()
    {
        @Override
        public Fraction createFromParcel(Parcel in)
        {
            return new Fraction(in);
        }

        @Override
        public Fraction[] newArray(int size)
        {
            return new Fraction[size];
        }
    };
}
