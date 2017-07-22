package com.aaron.recipe.bean;

import android.database.Cursor;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Aaron on 7/21/2017.
 */
public class Categories
{
    public static final String DEFAULT = "All";
    public static final int DEFAULT_INDEX = -1;
    // Map of categories and its database id
    private static final ConcurrentHashMap<Integer, String> CATEGORIES_MAP = new ConcurrentHashMap<>();
    private static final CopyOnWriteArrayList<String> CATEGORIES = new CopyOnWriteArrayList<>();

    static
    {
        CATEGORIES_MAP.put(DEFAULT_INDEX, DEFAULT);
        CATEGORIES.add(DEFAULT);
    }

    /**
     * Store categories in cache.
     *
     * @param jsonArray the http query result
     */
    public static void initCategories(final JSONArray jsonArray) throws JSONException
    {
        int length = jsonArray.length();
        for(int i = 0; i < length; i++)
        {
            JSONObject json = jsonArray.getJSONObject(i);
            String name = json.getString("name");
            CATEGORIES_MAP.put(json.getInt("id"), name);
            CATEGORIES.add(name);
        }
    }

    /**
     * Returns the list of categories as List.
     *
     * @return List<String>
     */
    public static CopyOnWriteArrayList<String> getCategories()
    {
        return CATEGORIES;
    }

    /**
     * Returns the list of categories as Array.
     *
     * @return String[]
     */
    public static String[] getCategoriesArray()
    {
        String[] result = new String[CATEGORIES.size()];
        return CATEGORIES.toArray(result);
    }

    /**
     * Returns the list of categories with its database id.
     *
     * @return {@code ConcurrentHashMap<Integer, String>}
     */
    public static ConcurrentHashMap<Integer, String> getCategoriesMap()
    {
        return CATEGORIES_MAP;
    }

    /**
     * Returns the index of the category
     *
     * @param category the category
     * @return int
     */
    public static int getIndex(String category)
    {
        return CATEGORIES.indexOf(category);
    }

    /**
     * Returns the id of the category
     *
     * @param category the category
     * @return int
     */
    public static int getId(String category)
    {
        for(Map.Entry<Integer, String> entry : CATEGORIES_MAP.entrySet())
        {
            if(entry.getValue().equals(category))
            {
                return entry.getKey();
            }
        }

        return DEFAULT_INDEX;
    }
}
