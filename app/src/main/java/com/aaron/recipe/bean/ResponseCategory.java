package com.aaron.recipe.bean;

import android.util.SparseArray;

/**
 * Bean representing the http response from a category request.
 */
public class ResponseCategory extends Response
{
    private SparseArray<String> categories;

    public ResponseCategory()
    {
        super();
    }

    public ResponseCategory(int statusCode)
    {
        super(statusCode);
    }

    public SparseArray<String> getCategories()
    {
        return categories;
    }

    public void setCategories(SparseArray<String> categories)
    {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof ResponseCategory))
        {
            return false;
        }
        else
        {
            ResponseCategory that = (ResponseCategory) o;

            return statusCode != that.statusCode || text != null ? !text.equals(that.text)
                    : that.text != null || body != null ? !body.equals(that.body) : that.body != null ||
                    categories != null ? categories.equals(that.categories) : that.categories == null;
        }
    }

    @Override
    public int hashCode()
    {
        int result = statusCode;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (categories != null ? categories.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", categories: " + this.categories.size();
    }
}
