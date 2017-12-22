package com.aaron.recipe.model;

import com.aaron.recipe.bean.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Aaron on 9/24/2016.
 */
public class HttpClient<T extends Response>
{
    public static final int CONNECTION_TIMEOUT = 10_000;
    public static final int READ_TIMEOUT = 10_000;
    private Class<T> clazz;

    public HttpClient(final Class<T> clazz)
    {
        this.clazz = clazz;
    }

    /**
     * Performs a GET request.
     *
     * @param url the url to make http request
     * @return Response
     */
    public T get(String url) throws IOException
    {
        return this.get(url, null, null);
    }

    /**
     * Performs a GET request with query.
     *
     * @param url the url to make http request
     * @param query the query parameter
     * @return Response
     */
    public T get(String url, String query) throws IOException
    {
        return this.get(url, query, null);
    }

    /**
     * Performs a GET request with specific headers.
     *
     * @param url the url to make http request
     * @param headers the request headers
     * @return Response
     */
    public T get(String url, List<Header> headers) throws IOException
    {
        return this.get(url, null, headers);
    }

    /**
     * Performs a GET request with query and specific headers.
     *
     * @param url the url to make http request
     * @param query the query parameter
     * @param headers the request headers
     * @return Response
     */
    public T get(String url, String query, List<Header> headers) throws IOException
    {
        String urlStr = url;
        if(StringUtils.isNotBlank(query))
        {
            urlStr += query;
        }
        HttpURLConnection con = null;

        URL getURL = new URL(urlStr);
        T response = Response.newInstance(this.clazz);
        response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);

        try
        {
            con = (HttpURLConnection) getURL.openConnection();
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setReadTimeout(READ_TIMEOUT);

            if(headers != null)
            {
                for(Header header : headers)
                {
                    con.addRequestProperty(header.getField(), header.getValue());
                }
            }

            response.setStatusCode(con.getResponseCode());
            response.setBody(getResponseBodyFromStream(con.getInputStream()));
            response.setText(getStatusText(response.getStatusCode()));
        }
        finally
        {
            if(con != null)
            {
                con.disconnect();
            }
        }

        return response;
    }

    /**
     * Converts the inputStream to String.
     *
     * @param inputStream HttpURLConnection response
     * @return String
     */
    private String getResponseBodyFromStream(final InputStream inputStream) throws IOException
    {
        String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        IOUtils.closeQuietly(inputStream);

        return response;
    }

    /**
     * Returns the string representation of the status code returned by the last web call. Internal Server Error is returned if the class does not have a
     * previous web call.
     *
     * @return String
     */
    private String getStatusText(int statusCode)
    {
        switch(statusCode)
        {
            case 200:
                return "Ok";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized Access";
            case 500:
                return "Internal Server Error";
            default:
                return "Status Code Unknown: " + statusCode;
        }
    }

    /**
     * Helper class that represents an http header.
     */
    public static class Header
    {
        private String field;
        private String value;

        public Header()
        {
        }

        public Header(String field, String value)
        {
            this.field = field;
            this.value = value;
        }

        public void setField(String field)
        {
            this.field = field;
        }

        public String getField()
        {
            return this.field;
        }

        public String getValue()
        {
            return this.value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }
    }
}
