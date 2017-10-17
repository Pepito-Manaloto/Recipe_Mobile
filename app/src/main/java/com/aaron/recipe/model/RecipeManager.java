package com.aaron.recipe.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.Ingredient;
import com.aaron.recipe.bean.Ingredients;
import com.aaron.recipe.bean.Instructions;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.bean.ResponseRecipe;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.aaron.recipe.model.MySQLiteHelper.COLUMN_COUNT;
import static com.aaron.recipe.model.MySQLiteHelper.ColumnIngredients;
import static com.aaron.recipe.model.MySQLiteHelper.ColumnInstructions;
import static com.aaron.recipe.model.MySQLiteHelper.ColumnRecipe;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_CATEGORIES;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_INGREDIENTS;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_INSTRUCTIONS;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_RECIPE;

/**
 * Handles the web call to retrieve recipes in JSON object representation. Handles the data storage of recipes.
 */
public class RecipeManager
{
    private static final String RECENTLY_ADDED_COUNT = "recently_added_count";

    public static final String CLASS_NAME = RecipeManager.class.getSimpleName();

    public static final String DATE_FORMAT_LONG = "MMMM d, yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_SHORT_24 = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_LONG, Locale.getDefault());
    private static final List<HttpClient.Header> HEADERS;

    private MySQLiteHelper dbHelper;
    private Date curDate;
    private HttpClient<ResponseRecipe> httpClient;

    static
    {
        HEADERS = new ArrayList<>(0);
        HEADERS.add(new HttpClient.Header("Authorization", new String(Hex.encodeHex(DigestUtils.md5("aaron")))));
    }

    /**
     * Constructor initializes the url.
     *
     * @param context
     *            the caller context
     */
    public RecipeManager(final Context context)
    {
        this.dbHelper = new MySQLiteHelper(context);
        this.curDate = new Date();
        this.httpClient = new HttpClient<>(ResponseRecipe.class);
    }

    /**
     * Does the following logic. (1) Retrieves the recipes from the server. (2) Parse the json response and converts it to ResponseRecipe
     *
     * @param url
     *            the url of the recipe web service
     * @return ResponseRecipe
     */
    public ResponseRecipe getRecipesFromWeb(String url)
    {
        ResponseRecipe response = new ResponseRecipe();
        Exception ex = null;

        try
        {
            String query = "?last_updated=" + URLEncoder.encode(this.getLastUpdated(DATE_FORMAT_SHORT_24), "UTF-8");

            Log.d(LogsManager.TAG, CLASS_NAME + ": getRecipesFromWeb. url=" + url + query);
            LogsManager.addToLogs(CLASS_NAME + ": getRecipesFromWeb. url=" + url + query);

            response = this.httpClient.get(url, query, HEADERS);

            if(response.getStatusCode() == HttpURLConnection.HTTP_OK)
            {
                if(StringUtils.isBlank(response.getBody())) // Response body empty
                {
                    return response;
                }

                JSONObject jsonObject = new JSONObject(response.getBody()); // Response body in JSON object

                int recentlyAddedCount = this.parseRecentlyAddedCountFromJsonObject(jsonObject);
                response.setRecentlyAddedCount(recentlyAddedCount);
                if(recentlyAddedCount <= 0) // No need to save to disk, because there are no new data entries.
                {
                    return response;
                }

                response.setRecipeMap(this.parseRecipesFromJsonObject(jsonObject));
                response.setTextSuccess();
                return response;
            }
        }
        catch(final IOException | JSONException e)
        {
            response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            response.setText(e.getMessage());
            ex = e;
        }
        catch(final NumberFormatException e)
        {
            response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            response.setText("Error parsing json response: recently_added_count is not a number.");
            ex = e;
        }
        catch(final IllegalArgumentException e)
        {
            response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            response.setText(url + " is not a valid host name.");
            ex = e;
        }
        finally
        {
            if(ex == null)
            {
                Log.d(LogsManager.TAG, CLASS_NAME + ": getRecipesFromWeb. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
                LogsManager.addToLogs(CLASS_NAME + ": getRecipesFromWeb. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
            }
            else
            {
                Log.e(LogsManager.TAG, CLASS_NAME + ": getRecipesFromWeb. " + ex.getClass().getSimpleName() + ": " + ex.getMessage(), ex);
                LogsManager.addToLogs(CLASS_NAME + ": getRecipesFromWeb. Exception=" + ex.getClass().getSimpleName() + " trace=" + ex.getStackTrace());
            }
        }

        return response;
    }

    /**
     * Parse the given jsonObject and returns the recently added count.
     *
     * @param jsonObject
     *            the jsonObject to be parsed
     * @return int
     * @throws NumberFormatException
     *             recently added count is not an integer
     */
    private int parseRecentlyAddedCountFromJsonObject(final JSONObject jsonObject) throws NumberFormatException
    {
        return Integer.parseInt(String.valueOf(jsonObject.remove(RECENTLY_ADDED_COUNT)));
    }

    /**
     * Parse the given jsonObject containing the list of recipes retrieved from the web call.
     *
     * @param jsonObject
     *            the jsonObject to be parsed
     * @return jsonObject converted into an HashMap, wherein the key is the category and values are list of recipes
     * @throws JSONException
     *             if the json parameter is invalid
     */
    private Map<String, ArrayList<Recipe>> parseRecipesFromJsonObject(final JSONObject jsonObject) throws JSONException
    {
        // Ensure the json string only contains recipes
        if(jsonObject.has(RECENTLY_ADDED_COUNT))
        {
            jsonObject.remove(RECENTLY_ADDED_COUNT);
        }

        Map<String, ArrayList<Recipe>> map = new HashMap<>();

        for(String cat : Categories.getCategories())
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

            String category = recipeJsonObj.getString(ColumnRecipe.category.name());
            int preparationTime = recipeJsonObj.getInt(ColumnRecipe.preparation_time.name());
            int servings = recipeJsonObj.getInt(ColumnRecipe.servings.name());
            String description = recipeJsonObj.getString(ColumnRecipe.description.name());

            JSONArray ingredientsJsonArray = jsonArray.getJSONArray(1);
            int ingredientsJsonArraySize = ingredientsJsonArray.length();
            Ingredients ingredients = new Ingredients(title, ingredientsJsonArraySize);

            for(int i = 0; i < ingredientsJsonArraySize; i++)
            {
                JSONObject ingredientsJsonObj = ingredientsJsonArray.getJSONObject(i);
                ingredients
                        .addIngredient(new Ingredient(ingredientsJsonObj.getDouble(ColumnIngredients.quantity.name()), ingredientsJsonObj.getString(ColumnIngredients.measurement.name()),
                                ingredientsJsonObj.getString(ColumnIngredients.ingredient.name()), ingredientsJsonObj.getString(ColumnIngredients.comment_.name())));
            }

            JSONArray instructionsJsonArray = jsonArray.getJSONArray(2);
            int instructionsJsonArraySize = instructionsJsonArray.length();
            Instructions instructions = new Instructions(title, instructionsJsonArraySize);
            for(int i = 0; i < instructionsJsonArraySize; i++)
            {
                JSONObject instructionsJsonObj = instructionsJsonArray.getJSONObject(i);
                instructions.addInstruction(instructionsJsonObj.getString(ColumnInstructions.instruction.name()));
            }

            Recipe recipe = new Recipe(title, category, servings, preparationTime, description, ingredients, instructions);
            ArrayList<Recipe> listTemp = map.get(category);

            if(listTemp == null)
            {
                throw new JSONException("Categories not updated.");
            }
            else
            {
                listTemp.add(recipe);
            }
        }

        Log.d(LogsManager.TAG, CLASS_NAME + ": parseRecipesFromJsonObject. map=" + map);
        LogsManager.addToLogs(CLASS_NAME + ": parseRecipesFromJsonObject. json_length=" + jsonObject.length());

        return map;
    }

    /**
     * Saves the given lists of recipes to the local database.
     *
     * @param recipeLists
     *            the recipe lists to be stored
     * @return true on success, else false
     */
    public boolean saveRecipesToDisk(final Collection<ArrayList<Recipe>> recipeLists)
    {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues recipeValues = new ContentValues();
        ContentValues ingredientsValues = new ContentValues();
        ContentValues instructionsValues = new ContentValues();
        dateFormatter.applyPattern(DATE_FORMAT_LONG);

        try
        {
            db.beginTransaction();
            // Delete recipes. To ensure no duplicates, if existing recipes are modified in the server.
            this.deleteQuery(db);

            // Iterate each recipe list
            for(ArrayList<Recipe> recipeList : recipeLists)
            {
                // Iterate each recipe of a particular category
                for(Recipe recipe : recipeList)
                {
                    recipeValues.put(ColumnRecipe.title.name(), recipe.getTitle());
                    recipeValues.put(ColumnRecipe.category.name(), Categories.getId(recipe.getCategory()));
                    recipeValues.put(ColumnRecipe.preparation_time.name(), recipe.getPreparationTime());
                    recipeValues.put(ColumnRecipe.servings.name(), recipe.getServings());
                    recipeValues.put(ColumnRecipe.description.name(), recipe.getDescription());
                    recipeValues.put(ColumnRecipe.date_in.name(), dateFormatter.format(this.curDate));

                    long recipeId = db.insert(TABLE_RECIPE, null, recipeValues);

                    int count = 1;
                    // Iterate over all ingredients of a recipe
                    for(Ingredient ingredient : recipe.getIngredients().getIngredientsList())
                    {
                        ingredientsValues.put(ColumnIngredients.recipe_id.name(), recipeId);
                        ingredientsValues.put(ColumnIngredients.quantity.name(), ingredient.getQuantity());
                        ingredientsValues.put(ColumnIngredients.measurement.name(), ingredient.getMeasurement());
                        ingredientsValues.put(ColumnIngredients.ingredient.name(), ingredient.getIngredient());
                        ingredientsValues.put(ColumnIngredients.comment_.name(), ingredient.getComment());
                        ingredientsValues.put(ColumnIngredients.count.name(), count++);

                        db.insert(TABLE_INGREDIENTS, null, ingredientsValues);
                    }

                    count = 1; // reset count

                    // Iterate over all instructions of a recipe
                    for(String instruction : recipe.getInstructions().getInstructionsList())
                    {
                        instructionsValues.put(ColumnInstructions.recipe_id.name(), recipeId);
                        instructionsValues.put(ColumnInstructions.instruction.name(), instruction);
                        instructionsValues.put(ColumnInstructions.count.name(), count++);

                        db.insert(TABLE_INSTRUCTIONS, null, instructionsValues);
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

        Log.d(LogsManager.TAG, CLASS_NAME + ": saveToDisk.");
        LogsManager.addToLogs(CLASS_NAME + ": saveToDisk.");

        return true;
    }

    /**
     * Gets the latest date_in of the recipes.
     *
     * @param format
     *            the date format used in formatting the last_updated date
     * @return String
     */
    public String getLastUpdated(final String format)
    {
        String lastUpdatedDate = "1950-01-01 00:00:00";
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String[] columns = new String[] { ColumnRecipe.date_in.name(), };
        String orderBy = ColumnRecipe.date_in.name() + " DESC";
        String limit = "1";

        try(Cursor cursor = db.query(TABLE_RECIPE, columns, null, null, null, null, orderBy, limit))
        {
            if(cursor.moveToFirst())
            {
                lastUpdatedDate = cursor.getString(0);
            }
            else
            {
                return lastUpdatedDate;
            }
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
            Log.e(LogsManager.TAG, CLASS_NAME + ": getLastUpdated. " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            LogsManager.addToLogs(CLASS_NAME + ": getLastUpdated. Exception=" + e.getClass().getSimpleName() + " trace=" + e.getStackTrace());
        }

        Log.d(LogsManager.TAG, CLASS_NAME + ": getLastUpdated. lastUpdatedDate=" + lastUpdatedDate);
        LogsManager.addToLogs(CLASS_NAME + ": getLastUpdated. lastUpdatedDate=" + lastUpdatedDate);

        return lastUpdatedDate;
    }

    /**
     * Gets the current recipe count per category, and returns them as an HashMap.
     *
     * @return {@code HashMap<String, Integer>}
     */
    public Map<String, Integer> getRecipesCount()
    {
        Map<String, Integer> map = new HashMap<>();
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String whereClause = ColumnRecipe.category.name() + " = ?";

        for(Map.Entry<Integer, String> entry : Categories.getCategoriesMap().entrySet())
        {
            try(Cursor cursor = db.query(TABLE_RECIPE, COLUMN_COUNT, whereClause, new String[] { entry.getKey().toString() }, null, null, null))
            {
                if(cursor.moveToFirst())
                {
                    map.put(entry.getValue(), cursor.getInt(0));
                }
            }
        }

        Log.d(LogsManager.TAG, CLASS_NAME + ": getRecipesCount. values=" + map.values());
        LogsManager.addToLogs(CLASS_NAME + ": getRecipesCount. values_size=" + map.values().size());

        return map;
    }

    /**
     * Does the following logic. (1) Retrieves the recipes from the local disk. (2) Returns the recipe list of the selected Category.
     *
     * @param selectedCategory
     *            the current selected category in the settings
     * @return ArrayList<Vocabulary>
     */
    public ArrayList<Recipe> getRecipesFromDisk(final String selectedCategory)
    {
        ArrayList<Recipe> list;
        try(SQLiteDatabase db = this.dbHelper.getReadableDatabase())
        {
            String[] columns = new String[] { ColumnRecipe.id.name(), ColumnRecipe.title.name(),
                                              ColumnRecipe.category.name(), ColumnRecipe.preparation_time.name(),
                                              ColumnRecipe.servings.name(), ColumnRecipe.description.name() };
            String whereClause;
            String[] whereArgs;
            String orderBy = ColumnRecipe.title.name() + " ASC";

            if(Categories.DEFAULT.equals(selectedCategory))
            {
                whereClause = null;
                whereArgs = null;
            }
            else
            {
                whereClause = ColumnRecipe.category.name() + " = ?";
                whereArgs = new String[] { String.valueOf(Categories.getId(selectedCategory)) };
            }

            Cursor cursor = db.query(TABLE_RECIPE, columns, whereClause, whereArgs, null, null, orderBy);
            list = new ArrayList<>(cursor.getCount());

            if(cursor.moveToFirst())
            {
                do
                {
                    list.add(this.cursorToRecipe(cursor));
                } while(cursor.moveToNext());
            }
        }

        Log.d(LogsManager.TAG, CLASS_NAME + ": getRecipesFromDisk. category=" + selectedCategory);
        LogsManager.addToLogs(CLASS_NAME + ": getRecipesFromDisk. category=" + selectedCategory);

        return list;
    }

    /**
     * Retrieves the recipe from the cursor.
     *
     * @param cursor
     *            the cursor resulting from a query
     * @return Recipe
     */
    private Recipe cursorToRecipe(final Cursor cursor)
    {
        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        String category = Categories.getCategoriesMap().get(cursor.getInt(2));
        int preparationTime = cursor.getInt(3);
        int servings = cursor.getInt(4);
        String description = cursor.getString(5);
        String orderBy = ColumnIngredients.count.name() + " ASC";

        SQLiteDatabase db = this.dbHelper.getReadableDatabase();

        String[] ingredientsColumns = new String[] { ColumnIngredients.quantity.name(), ColumnIngredients.measurement.name(), ColumnIngredients.ingredient.name(), ColumnIngredients.comment_.name() };
        String[] instructionsColumns = new String[] { ColumnInstructions.instruction.name() };
        String whereClause = ColumnIngredients.recipe_id.name() + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        Ingredients ingredients;
        try(Cursor ingredientCursor = db.query(TABLE_INGREDIENTS, ingredientsColumns, whereClause, whereArgs, null, null, orderBy))
        {
            ingredients = new Ingredients(title, ingredientCursor.getCount());

            if(ingredientCursor.moveToFirst())
            {
                do
                {
                    double quantity = ingredientCursor.getDouble(0);
                    String measurement = ingredientCursor.getString(1);
                    String ingredient = ingredientCursor.getString(2);
                    String comment = ingredientCursor.getString(3);

                    ingredients.addIngredient(new Ingredient(quantity, measurement, ingredient, comment));
                } while(ingredientCursor.moveToNext());
            }
        }

        whereClause = ColumnInstructions.recipe_id.name() + " = ?";
        Instructions instructions;
        try(Cursor instructionCursor = db.query(TABLE_INSTRUCTIONS, instructionsColumns, whereClause, whereArgs, null, null, orderBy))
        {
            instructions = new Instructions(title, instructionCursor.getCount());

            if(instructionCursor.moveToFirst())
            {
                do
                {
                    String instruction = instructionCursor.getString(0);

                    instructions.addInstruction(instruction);
                } while(instructionCursor.moveToNext());
            }
        }

        return new Recipe(id, title, category, servings, preparationTime, description, ingredients, instructions);
    }

    /**
     * Deletes the recipe from disk. Warning: this action cannot be reverted
     */
    public void deleteRecipeFromDisk()
    {
        try(SQLiteDatabase db = this.dbHelper.getWritableDatabase())
        {
            int result = this.deleteQuery(db);

            Log.d(LogsManager.TAG, CLASS_NAME + ": deleteRecipeFromDisk. affected=" + result);
        }
    }

    /**
     * Deletes the recipe, ingredients, and instructions from disk. Warning: this action cannot be reverted
     *
     * @param db
     *            the database connection to use
     * @return int
     */
    private int deleteQuery(SQLiteDatabase db)
    {
        int result = db.delete(TABLE_RECIPE, null, null);
        db.delete(TABLE_INGREDIENTS, null, null);
        db.delete(TABLE_INSTRUCTIONS, null, null);

        return result;
    }

    /**
     * Returns the recipes based on the current selected category.
     *
     * @param recipeMap
     *            the recipe map
     * @param size
     *            the number of recipes in the map
     * @param selectedCategory
     *            the current selected category
     * @return ArrayList<Recipe>
     */
    public ArrayList<Recipe> getRecipesFromMap(final Map<String, ArrayList<Recipe>> recipeMap, final int size, final String selectedCategory)
    {
        if(Categories.DEFAULT.equals(selectedCategory)) // Combines all ArrayList<Recipe> into a single ArrayList
        {
            ArrayList<Recipe> allRecipeList;

            if(size == 0)
            {
                allRecipeList = new ArrayList<>();
            }
            else
            {
                allRecipeList = new ArrayList<>(size);
            }

            for(ArrayList<Recipe> list : recipeMap.values())
            {
                allRecipeList.addAll(list);
            }

            return allRecipeList;
        }
        else
        {
            return recipeMap.get(selectedCategory);
        }
    }
}
