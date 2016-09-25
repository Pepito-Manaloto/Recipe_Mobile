package com.aaron.recipe.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Instructions class.
 */
public class Instructions implements Serializable
{
    private static final long serialVersionUID = 5879474962110261190L;
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
     * @param instruction an instruction
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
     * @param obj Instruction object to compare to
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
}
