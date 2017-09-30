package com.aaron.recipe.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Instructions class.
 */
public class Instructions implements Parcelable
{
    private String title;
    private ArrayList<String> instructionsList;

    /**
     * Default constructor.
     */
    public Instructions(final String title, final int numberOfInstructions)
    {
        this.title = title;
        this.instructionsList = new ArrayList<>(numberOfInstructions);
    }

    /**
     * Adds an instruction to the instructionsList.
     *
     * @param instruction
     *            an instruction
     */
    public void addInstruction(final String instruction)
    {
        this.instructionsList.add(instruction);
    }

    /**
     * Gets the instructionList.
     *
     * @return ArrayList<String>
     */
    public ArrayList<String> getInstructionsList()
    {
        return this.instructionsList;
    }

    /**
     * Gets the title.
     *
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Checks for equality, only checks the title of the instructions.
     *
     * @param obj
     *            Instruction object to compare to
     * @return boolean true if equal, else false
     */
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Instructions))
        {
            return false;
        }
        else
        {
            Instructions that = (Instructions) obj;
            return this.title.equals(that.getTitle());
        }
    }

    /**
     * Returns the hashcode of this object. Derived from title.
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 47 * hash + (this.title + "_INSTRUCTIONS").hashCode();

        return hash;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return "title: " + this.title +
                " instructions: " + this.instructionsList;
    }

    /**
     * Constructor that will be called in creating the parcel. Note: Reading the parcel should be the same order as writing the parcel!
     */
    private Instructions(Parcel in)
    {
        this.instructionsList = new ArrayList<>();

        this.title = in.readString();
        in.readStringList(this.instructionsList);
    }

    /**
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.title);
        dest.writeStringList(this.instructionsList);
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
    public static final Creator<Instructions> CREATOR = new Creator<Instructions>()
    {
        @Override
        public Instructions createFromParcel(Parcel in)
        {
            return new Instructions(in);
        }

        @Override
        public Instructions[] newArray(int size)
        {
            return new Instructions[size];
        }
    };
}
