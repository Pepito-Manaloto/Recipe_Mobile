package com.aaron.recipe.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.ResponseCategory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.aaron.recipe.model.MySQLiteHelper.ColumnCategories;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_CATEGORIES;

/**
 * Handles the web call to retrieve recipes in JSON object representation. Handles the data storage of recipes.
 */
public class CategoryManager
{
    public static final String CLASS_NAME = CategoryManager.class.getSimpleName();
    private static final List<HttpClient.Header> HEADERS;

    private MySQLiteHelper dbHelper;
    private HttpClient<ResponseCategory> httpClient;

    static
    {
        HEADERS = new ArrayList<>(0);
        HEADERS.add(new HttpClient.Header("Authorization", new String(Hex.encodeHex(DigestUtils.md5("aaron")))));
    }

    /**
     * Constructor initializes the url.
     *
     * @param context
     *            the caller activity
     */
    public CategoryManager(final Context context)
    {
        this.dbHelper = new MySQLiteHelper(context);
        this.httpClient = new HttpClient<>(ResponseCategory.class);
    }

    /**
     * Retrieves the categories from the server.
     *
     * @param url
     *            the url of the recipe web service
     * @return ResponseCategory
     */
    public ResponseCategory getCategoriesFromWeb(String url)
    {
        ResponseCategory response = new ResponseCategory();
        try
        {
            response = this.httpClient.get(url, HEADERS);

            if(response.getStatusCode() == HttpURLConnection.HTTP_OK)
            {
                String responseBody = response.getBody();
                if(StringUtils.isNotBlank(responseBody)) // Response body empty
                {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    SparseArray<String> categoriesArray = new SparseArray<>();

                    int length = jsonArray.length();
                    for(int i = 0; i < length; i++)
                    {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String name = json.getString(ColumnCategories.name.name());
                        int id = json.getInt(ColumnCategories.id.name());

                        categoriesArray.append(id, name);
                    }

                    response.setCategories(categoriesArray);
                    response.setTextSuccess();

                    return response;
                }
                else
                {
                    response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    response.setText("Error no categories retrieved.");
                    Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate Error initializing categories, response empty. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
                    LogsManager.addToLogs(CLASS_NAME + ": onCreate Error initializing categories, response empty. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
                }
            }
            else
            {
                response.setText("Error response is not 200.");
                Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate Error initializing categories, response not 200. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
                LogsManager.addToLogs(CLASS_NAME + ": onCreate Error initializing categories, response not 200. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
            }
        }
        catch(JSONException e)
        {
            response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            response.setText("Error parsing categories." + e.getMessage());
            Log.e(LogsManager.TAG, CLASS_NAME + ": onCreate. Error parsing categories response. Error: " + e.getMessage());
            LogsManager.addToLogs(CLASS_NAME + ": onCreate. Error parsing categories response. Error: " + e.getMessage());
        }
        catch(IOException e)
        {
            response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            response.setText("Error retrieving categories. " + e.getMessage());
            Log.e(LogsManager.TAG, CLASS_NAME + ": onCreate. Error retrieving categories. Error: " + e.getMessage());
            LogsManager.addToLogs(CLASS_NAME + ": onCreate. Error retrieving categories. Error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Store categories in cache and persist to the database.
     *
     * @param categoriesArray
     *            the category map where the id is the key and category is the value
     *
     */
    public boolean saveCategories(SparseArray<String> categoriesArray)
    {
        if(categoriesArray != null && categoriesArray.size() > 1)
        {
            this.saveCategoriesInCache(categoriesArray);
            this.saveCategoriesInDatabase(categoriesArray);

            return true;
        }

        return false;
    }

    /**
     * Store categories in cache.
     *
     * @param categoryArray
     *            the category map where the id is the key and category is the value
     *
     */
    public void saveCategoriesInCache(SparseArray<String> categoryArray)
    {
        if(categoryArray != null)
        {
            int length = categoryArray.size();
            for(int i = 0; i < length; i++)
            {
                int id = categoryArray.keyAt(i);
                String category = categoryArray.get(id);
                Categories.getCategoriesMap().put(id, category);
                Categories.getCategories().add(category);
            }
        }
    }

    /**
     * Persists the category list to the database.
     *
     * @param categoryArray
     *            the category map where the id is the key and category is the value
     */
    public void saveCategoriesInDatabase(SparseArray<String> categoryArray)
    {
        if(categoryArray != null)
        {
            int length = categoryArray.size();
            if(length > 0)
            {
                SQLiteDatabase db = this.dbHelper.getWritableDatabase();
                ContentValues categoryValues = new ContentValues();

                try
                {
                    db.beginTransaction();

                    // Delete categories to insert latest data
                    db.delete(TABLE_CATEGORIES, null, null);

                    for(int i = 0; i < length; i++)
                    {
                        int id = categoryArray.keyAt(i);
                        String name = categoryArray.get(id);
                        categoryValues.put(ColumnCategories.id.name(), id);
                        categoryValues.put(ColumnCategories.name.name(), name);

                        db.insert(TABLE_CATEGORIES, null, categoryValues);
                    }

                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                    db.close();
                    this.dbHelper.close();
                }
            }
        }
    }

    /**
     * Retrieves the categories from the database.
     *
     * @return List<String>
     */
    public SparseArray<String> getCategoriesFromDisk()
    {
        SparseArray<String> array;
        try(SQLiteDatabase db = this.dbHelper.getReadableDatabase())
        {
            String[] columns = new String[] { ColumnCategories.id.name(), ColumnCategories.name.name() };
            String orderBy = ColumnCategories.name.name() + " ASC";

            try(Cursor cursor = db.query(TABLE_CATEGORIES, columns, null, null, null, null, orderBy))
            {
                array = new SparseArray<>(cursor.getCount());

                if(cursor.moveToFirst())
                {
                    do
                    {
                        array.append(cursor.getInt(0), cursor.getString(1));
                    } while(cursor.moveToNext());
                }
            }
        }

        int size = array.size();
        Log.d(LogsManager.TAG, CLASS_NAME + ": getCategoriesFromDisk. length=" + size);
        LogsManager.addToLogs(CLASS_NAME + ": getCategoriesFromDisk. length=" + size);

        return array;
    }
}
