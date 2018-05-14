package com.aaron.recipe.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseCategory
{
    private int id;
    private String name;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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

        ResponseCategory that = (ResponseCategory) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name);
    }

    @Override
    public String toString()
    {
        return "ResponseCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
