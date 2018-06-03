package com.aaron.recipe.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseInstruction
{
    private String instruction;

    public ResponseInstruction()
    {
    }

    public ResponseInstruction(String instruction)
    {
        this.instruction = instruction;
    }

    public String getInstruction()
    {
        return instruction;
    }

    public void setInstruction(String instruction)
    {
        this.instruction = instruction;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }

        if(o == null || getClass() != o.getClass())
        {
            return false;
        }

        ResponseInstruction that = (ResponseInstruction) o;

        return new EqualsBuilder()
                .append(instruction, that.instruction)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(instruction);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("instruction", instruction)
                .toString();
    }
}
