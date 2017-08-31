package com.aaron.recipe.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Categories bean.
 */
public class Categories
{
    public static final String DEFAULT = "All";
    private static final int DEFAULT_INDEX = -1;
    // Map of categories and its database id
    private static final ConcurrentHashMap<Integer, String> CATEGORIES_MAP = new ConcurrentHashMap<>();
    private static final CopyOnWriteArrayList<String> CATEGORIES = new CopyOnWriteArrayList<>();

    static
    {
        CATEGORIES_MAP.put(DEFAULT_INDEX, DEFAULT);
        CATEGORIES.add(DEFAULT);
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
     * Returns if the categories is updated
     *
     * @return true if the number of categories is greater than 1
     */
    public static boolean isCategoriesUpdated()
    {
        return CATEGORIES.size() > 1;
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
     * @param category
     *            the category
     * @return int
     */
    public static int getIndex(String category)
    {
        return CATEGORIES.indexOf(category);
    }

    /**
     * Returns the id of the category
     *
     * @param category
     *            the category
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
