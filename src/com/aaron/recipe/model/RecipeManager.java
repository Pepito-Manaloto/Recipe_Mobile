package com.aaron.recipe.model;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aaron.recipe.bean.Ingredients;
import com.aaron.recipe.bean.Ingredients.Ingredient;
import com.aaron.recipe.bean.Instructions;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.Recipe.Category;
import com.aaron.recipe.model.MySQLiteHelper;
import com.aaron.recipe.R;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.aaron.recipe.model.MySQLiteHelper.*;
import static com.aaron.recipe.bean.Recipe.*;

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

    /**
     * Constructor initializes the url.
     * @param Activity the caller activity
     */
    public RecipeManager(final Activity activity)
    {
        this.url = "http://" + activity.getString(R.string.url_address) + activity.getString(R.string.url_resource);

        this.dbHelper = new MySQLiteHelper(activity);
        this.curDate = new Date();
        this.selectedCategory = Category.All;
    }

    /**
     * Constructor initializes the url and the current application settings.
     * @param activity the caller activity
     * @param settings the current settings
     */
    public RecipeManager(final Activity activity, final Category category)
    {
        this(activity);
        this.selectedCategory = category;
    }

    /**
     * Does the following logic.
     * (1) Retrieves the recipes from the server.
     * (2) Saves the recipes in local disk.
     * (3) Returns the recipe list of the current selected category.
     * @return ArrayList<Recipe>
     */
    public ArrayList<Recipe> getRecipesFromWeb()
    {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);

        try
        {
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            String params = "?last_updated=" + URLEncoder.encode(this.getLastUpdated(DATE_FORMAT_SHORT_24), "UTF-8");
    
            Log.d(LogsManager.TAG, "RecipeManager: getRecipesFromWeb. params=" + params);
            LogsManager.addToLogs("RecipeManager: getRecipesFromWeb. params=" + params);

            HttpGet httpGet = new HttpGet(this.url + params);
            httpGet.addHeader("Authorization", AUTH_KEY);

            HttpResponse response = httpclient.execute(httpGet);
            this.responseCode = response.getStatusLine().getStatusCode();

            if(this.responseCode == HttpStatus.SC_OK)
            {
                HttpEntity httpEntity = response.getEntity();

                String responseString = EntityUtils.toString(httpEntity); // Response body

                JSONObject jsonObject = new JSONObject(responseString); // Response body in JSON object

                HashMap<Category, ArrayList<Recipe>> map = this.parseJsonObject(jsonObject);

                boolean saveToDiskSuccess = this.saveToDisk(map);
                
                if(!saveToDiskSuccess)
                {
                    this.responseCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
                    this.responseText = "Failed saving to disk.";
                    
                    return new ArrayList<>(0);
                }

                this.responseText = "Success";

                // Entity is already consumed by EntityUtils; thus is already closed.

                return map.get(this.selectedCategory);
            }

            // Closes the connection/ Consume the entity.
            response.getEntity().getContent().close();
        }
        catch(final IOException | JSONException e)
        {
            Log.e(LogsManager.TAG, "RecipeManager: getRecipesFromWeb. " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            LogsManager.addToLogs("RecipeManager: getRecipesFromWeb. Exception=" + e.getClass().getSimpleName() + " trace=" + e.getStackTrace());

            this.responseText = e.getMessage();
        }
        finally
        {
            Log.d(LogsManager.TAG, "RecipeManager: getRecipesFromWeb. responseText=" + this.responseText +
                                   " responseCode=" + this.responseCode + " languageSelected=" + this.selectedCategory);
            LogsManager.addToLogs("RecipeManager: getRecipesFromWeb. responseText=" + this.responseText +
                                  " responseCode=" + this.responseCode + " languageSelected=" + this.selectedCategory);
        }

        return new ArrayList<>(0);
    }

    /**
     * Parse the given jsonObject containing the list of recipes retrieved from the web call.
     * @param jsonObject the jsonObject to be parsed
     * @throws JSONException
     * @return jsonObject converted into a hashmap
     */
    private HashMap<Category, ArrayList<Recipe>> parseJsonObject(final JSONObject jsonObject) throws JSONException
    {
        HashMap<Category, ArrayList<Recipe>> map = new HashMap<>();
        
        for(Category cat: Category.values())
        {
            map.put(cat, new ArrayList<Recipe>());
        }

        Iterator<String> jsonIterator = jsonObject.keys();

        while(jsonIterator.hasNext())
        {
            String title = jsonIterator.next();
            JSONArray jsonArray = jsonObject.getJSONArray(title);

            JSONArray recipeJsonArray = jsonArray.getJSONArray(0);
            JSONObject recipeJsonObj = recipeJsonArray.getJSONObject(0);

            Category category = Category.valueOf(recipeJsonObj.getString(ColumnRecipe.category.name()));
            int preparationTime = recipeJsonObj.getInt(ColumnRecipe.preparation_time.name());
            int servings = recipeJsonObj.getInt(ColumnRecipe.servings.name());
            String description = recipeJsonObj.getString(ColumnRecipe.description.name());

            JSONArray ingredientsJsonArray = jsonArray.getJSONArray(1);
            int ingredientsJsonArraySize = ingredientsJsonArray.length();
            Ingredients ingredients = new Ingredients(title, ingredientsJsonArraySize);

            for(int i=0; i < ingredientsJsonArraySize; i++)
            {
                JSONObject ingredientsJsonObj = ingredientsJsonArray.getJSONObject(i);

                ingredients.addIngredient(new Ingredients.Ingredient(ingredientsJsonObj.getDouble(ColumnIngredients.quantity.name()), 
                                                                     ingredientsJsonObj.getString(ColumnIngredients.measurement.name()),
                                                                     ingredientsJsonObj.getString(ColumnIngredients.ingredient.name()),
                                                                     ingredientsJsonObj.getString(ColumnIngredients.comment_.name())));
            }
            
            JSONArray instructionsJsonArray = jsonArray.getJSONArray(2);
            int instructionsJsonArraySize = instructionsJsonArray.length();
            Instructions instructions = new Instructions(title, instructionsJsonArraySize);
            for(int i=0; i < instructionsJsonArraySize; i++)
            {
                JSONObject instructionsJsonObj = instructionsJsonArray.getJSONObject(i);

                instructions.addInstruction(instructionsJsonObj.getString(ColumnInstructions.instruction.name()));
            }
            
            Recipe recipe = new Recipe(title, category, preparationTime, servings, description, ingredients, instructions);
            ArrayList<Recipe> listTemp = map.get(category);
            listTemp.add(recipe);
        }

        return map;
    }

    /**
     * Saves the given recipe map to the local database.
     * @param recipeMap the recipe map to be stored
     * @return true on success, else false
     */
    private boolean saveToDisk(final HashMap<Category, ArrayList<Recipe>> recipeMap)
    {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ArrayList<Recipe> listTemp;
        ContentValues recipeValues = new ContentValues();
        ContentValues ingredientsValues = new ContentValues();
        ContentValues instructionsValues = new ContentValues();

        dateFormatter.applyPattern(DATE_FORMAT_LONG);
        db.beginTransaction();

        try
        {
            // Delete recipes. To ensure no duplicates, if existing recipes are modified in the server.
            db.delete(TABLE_RECIPE, "1", null);

            // Iterate each category
            for(Category category: recipeMap.keySet())
            {
                listTemp = recipeMap.get(category);

                // Iterate each recipe of a particular category
                for(Recipe recipe: listTemp)
                {
                    String title = recipe.getTitle();

                    recipeValues.put(ColumnRecipe.title.name(), title);
                    recipeValues.put(ColumnRecipe.category.name(), recipe.getCategory());
                    recipeValues.put(ColumnRecipe.preparation_time.name(), recipe.getPreparationTime());
                    recipeValues.put(ColumnRecipe.servings.name(), recipe.getServings());
                    recipeValues.put(ColumnRecipe.description.name(), recipe.getDescription());
                    recipeValues.put(ColumnRecipe.date_in.name(), dateFormatter.format(this.curDate));

                    db.insert(TABLE_RECIPE, null, recipeValues);
                    
                    // Iterate over all ingredients of a recipe
                    for(Ingredient ingredient: recipe.getIngredients().getIngredientsList())
                    {
                        ingredientsValues.put(ColumnIngredients.title.name(), title);
                        ingredientsValues.put(ColumnIngredients.quantity.name(), ingredient.getQuantity());
                        ingredientsValues.put(ColumnIngredients.measurement.name(), ingredient.getMeasurement());
                        ingredientsValues.put(ColumnIngredients.ingredient.name(), ingredient.getIngredient());
                        ingredientsValues.put(ColumnIngredients.comment_.name(), ingredient.getComment());

                        db.insert(TABLE_INGREDIENTS, null, ingredientsValues);
                    }

                    // Iterate over all instructions of a recipe
                    for(String instruction: recipe.getInstructions().getInstructionsList())
                    {
                        instructionsValues.put(ColumnInstructions.title.name(), title);
                        instructionsValues.put(ColumnInstructions.instruction.name(), instruction);

                        db.insert(TABLE_INSTRUCTIONS, null, ingredientsValues);
                    }
                }
            }

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
            db.close();
            this.dbHelper.close();
        }

        Log.d(LogsManager.TAG, "RecipeManager: saveToDisk.");

        return true;
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

    /**
     * Gets the latest date_in of the recipes.
     * @param format the date format used in formatting the last_updated date
     * @return String
     */
    public String getLastUpdated(final String format)
    {
        String lastUpdatedDate = "";
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String[] columns = new String[]{ColumnRecipe.date_in.name(),};
        String orderBy = "date_in DESC";
        String limit = "1";

        Cursor cursor = db.query(TABLE_RECIPE, columns, null, null, null, null, orderBy, limit);

        if(cursor.moveToFirst())
        {
            lastUpdatedDate = cursor.getString(0);
        }

        if(lastUpdatedDate.length() <= 0)
        {
            return lastUpdatedDate;
        }

        try
        {
            dateFormatter.applyPattern(DATE_FORMAT_LONG);
            Date date = dateFormatter.parse(lastUpdatedDate); // Parse String to Date, to be able to format properly.

            dateFormatter.applyPattern(format);
            lastUpdatedDate = dateFormatter.format(date);
        }
        catch(ParseException e)
        {
            Log.e(LogsManager.TAG, "RecipeManager: getLastUpdated. " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            LogsManager.addToLogs("RecipeManager: getLastUpdated. Exception=" + e.getClass().getSimpleName() + " trace=" + e.getStackTrace());
        }

        Log.d(LogsManager.TAG, "RecipeManager: getLastUpdated. lastUpdatedDate=" + lastUpdatedDate);
        LogsManager.addToLogs("RecipeManager: getLastUpdated. lastUpdatedDate=" + lastUpdatedDate);

        return lastUpdatedDate;
    }

    /**
     * Sets the selected category.
     * @param category the current selected category
     */
    public void setSelectedCategory(final Category category)
    {
        this.selectedCategory = category;
    }

    /**
     * Gets the current vocabulary count per foreign languages, and returns them as a hashmap.
     * @return HashMap<ForeignLanguage, Integer>
     */
    public HashMap<Category, Integer> getRecipesCount()
    {
        HashMap<Category, Integer> map = new HashMap<>();
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String whereClause = "category = ?";
        Cursor cursor;
        
        for(Category category: CATEGORY_ARRAY)
        {
            cursor = db.query(TABLE_RECIPE, COLUMN_COUNT, whereClause, new String[]{category.name()}, null, null, null);
            
            if(cursor.moveToFirst())
            {
                map.put(category, cursor.getInt(0));
            }
        }

        Log.d(LogsManager.TAG, "RecipeManager: getRecipesCount. keys=" + map.keySet() + " values=" + map.values());
        LogsManager.addToLogs("RecipeManager: getRecipesCount. keys=" + map.keySet() + " values_size=" + map.values().size());

        return map;
    }

    /**
     * Does the following logic.
     * (1) Retrieves the recipes from the local disk.
     * (2) Returns the recipe list of the selected language.
     * @return ArrayList<Vocabulary>
     */
    public ArrayList<Recipe> getRecipesFromDisk()
    {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        
        String[] columns = new String[]{ColumnRecipe.title.name(),
                                        ColumnRecipe.category.name(),
                                        ColumnRecipe.preparation_time.name(),
                                        ColumnRecipe.servings.name(),
                                        ColumnRecipe.description.name()};
        String whereClause = "category = ?";
        String[] whereArgs = new String[]{this.selectedCategory.name()};
        
        Cursor cursor = db.query(TABLE_RECIPE, columns, whereClause, whereArgs, null, null, null);
        ArrayList<Recipe> list = new ArrayList<>(cursor.getCount());
        
        if(cursor.moveToFirst())
        {
            do
            {
                list.add(this.cursorToRecipe(cursor));
            }
            while(cursor.moveToNext());
        }

        db.close();
        this.dbHelper.close();

        Log.d(LogsManager.TAG, "RecipeManager: getRecipesFromDisk. list=" + list);
        LogsManager.addToLogs("RecipeManager: getRecipesFromDisk. list_size=" + list.size());

        return list;
    }

    /**
     * Retrieves the recipe from the cursor.
     * @param cursor the cursor resulting from a query
     * @return Recipe
     */
    private Recipe cursorToRecipe(final Cursor cursor)
    {
        String title = cursor.getString(0);
        Category category = Category.valueOf(cursor.getString(1));
        int preparationTime = cursor.getInt(2);
        int servings = cursor.getInt(3);
        String description = cursor.getString(4);

        return new Recipe(title, category, preparationTime, servings, description);
    }

    /**
     * Deletes the recipe from disk.
     * Warning: this action cannot be reverted
     */
    public void deleteRecipeFromDisk()
    {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String whereClause = "1";
        String[] whereArgs = null;

        int result = db.delete(TABLE_RECIPE, whereClause, whereArgs);

        db.close();
        this.dbHelper.close();

        Log.d(LogsManager.TAG, "RecipeManager: deleteRecipeFromDisk. affected=" + result);
    }
}
