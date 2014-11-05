package com.aaron.recipe.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpStatus;

import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Recipe.Category;
import com.aaron.recipe.model.MySQLiteHelper;
import com.aaron.recipe.R;

import android.app.Activity;

/**
 * Handles the web call to retrieve recipes in JSON object representation.
 * Handles the data storage of recipes.
 */
public class RecipeManager
{
    private int responseCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    private String responseText;
    private int recentlyAddedCount;

    private final String url;
    private static final String AUTH_KEY = "449a36b6689d841d7d27f31b4b7cc73a";

    public static final String TAG = "RecipeManager";

    public static final String DATE_FORMAT_LONG = "MMMM d, yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_SHORT_24 = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_LONG, Locale.getDefault());

    private MySQLiteHelper dbHelper;
    private Date curDate;
    private Category selectedCategory;

    public RecipeManager(final Activity activity)
    {
        this.url = "http://" + activity.getString(R.string.url_address) + activity.getString(R.string.url_resource);

        this.dbHelper = new MySQLiteHelper(activity);
        this.curDate = new Date();
    }

    public RecipeManager(final Activity activity, final Category category)
    {
        this(activity);
        this.selectedCategory = category;
    }
    
    public ArrayList<Recipe> getRecipesFromWeb()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the string representation of the status code returned by the last web call.
     * Internal Server Error is returned if the class does not have a previous web call.
     * @return String 
     */
    public String getStatusText()
    {
        switch(this.responseCode)
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
                return "Status Code Unknown";
        }
    }

    /**
     * Returns the response text by the last web call.
     * Empty text is returned if the class does not have a previous web call.
     * @return String
     */
    public String getResponseText()
    {
        return this.responseText;
    }

    /**
     * Returns the number of vocabularies that are new.
     * @return int
     */
    public int getRecentlyAddedCount()
    {
        return this.recentlyAddedCount;
    }

    public String getLastUpdated(final String dateFormat)
    {
        return null;
    }

    public ArrayList<Recipe> getRecipesFromDisk()
    {
        return null;
    }

    public void deleteRecipeFromDisk()
    {}
}
