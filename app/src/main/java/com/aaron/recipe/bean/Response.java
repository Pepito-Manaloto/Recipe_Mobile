package com.aaron.recipe.bean;

import java.io.IOException;

/**
 * Abstract class representing http response.
 */
public abstract class Response
{
    private static final String SUCCESS = "Success";

    protected int statusCode;
    protected String text;
    protected String body;

    public Response()
    {
    }

    public Response(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public int getStatusCode()
    {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getBody()
    {
        return this.body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public void setTextSuccess()
    {
        this.text = SUCCESS;
    }

    @Override
    public String toString()
    {
        return "statusCode: " + statusCode + ", text: " + text + ", body: " + body;
    }

    /**
     * Static factory method for creating generic Response implementation
     * 
     * @param clazz
     *            the class type of the Response implementation
     *
     * @throws IOException
     *             if the class instance cannot be created, Should never happen!
     * @return the new Response implementation instance
     */
    public static <T extends Response> T newInstance(Class<T> clazz) throws IOException
    {
        try
        {
            return clazz.newInstance();
        }
        catch(InstantiationException | IllegalAccessException e)
        {
            // This should never happen!
            throw new IOException(e);
        }
    }
}
