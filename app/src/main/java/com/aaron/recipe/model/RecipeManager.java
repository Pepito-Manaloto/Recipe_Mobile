package com.aaron.recipe.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.Ingredient;
import com.aaron.recipe.bean.Ingredients;
import com.aaron.recipe.bean.Instructions;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.response.ResponseIngredient;
import com.aaron.recipe.response.ResponseInstruction;
import com.aaron.recipe.response.ResponseRecipe;
import com.aaron.recipe.response.ResponseRecipes;

import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.aaron.recipe.model.MySQLiteHelper.COLUMN_COUNT;
import static com.aaron.recipe.model.MySQLiteHelper.ColumnIngredients;
import static com.aaron.recipe.model.MySQLiteHelper.ColumnInstructions;
import static com.aaron.recipe.model.MySQLiteHelper.ColumnRecipe;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_INGREDIENTS;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_INSTRUCTIONS;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_RECIPE;

/**
 * Handles the web call to retrieve recipes in JSON object representation. Handles the data storage of recipes.
 */
public class RecipeManager
{
    public static final String CLASS_NAME = RecipeManager.class.getSimpleName();

    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    public static final String DATE_FORMAT_LONG = "MMMM d, yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_SHORT_24 = "yyyy-MM-dd HH:mm:ss";

    private MySQLiteHelper dbHelper;
    private Date curDate;
    private HttpClient httpClient;
    private WeakReference<Context> contextRef;

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
        this.httpClient = new HttpClient(context.getString(R.string.url_address_default));
        this.contextRef = new WeakReference<>(context);
    }

    /**
     * Does the following logic.
     * (1) Retrieves the recipes from the server
     * (2) Save to disk
     * (3) Execute Notification and updates in the UI
     *
     * @param doFinally the action to execute always at the end of this call
     * @param updateRecipeListFragment the action to execute after the web call
     */
    public void updateRecipesFromWeb(Action doFinally, Consumer<ArrayList<Recipe>> updateRecipeListFragment)
    {
        Disposable disposable = httpClient.getRecipes(getLastUpdated(DATE_FORMAT_SHORT_24))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(doFinally)
                .map(this::convertResponseRecipesToRecipes)
                .map(this::saveRecipeListInDatabase)
                .subscribeWith(updateRecipesFromWebObserver(updateRecipeListFragment));

        compositeDisposable.add(disposable);
    }

    private List<Recipe> convertResponseRecipesToRecipes(ResponseRecipes responseRecipes)
    {
        if(responseRecipes.getRecentlyAddedCount() <= 0)
        {
            return Collections.emptyList();
        }

        return responseRecipes.getRecipeList().stream().map(this::convertResponseRecipeIntoRecipe).collect(Collectors.toList());
    }

    private Recipe convertResponseRecipeIntoRecipe(ResponseRecipe responseRecipe)
    {
        return new Recipe()
                .setTitle(responseRecipe.getTitle())
                .setCategory(responseRecipe.getCategory())
                .setServings(responseRecipe.getServings())
                .setPreparationTime(responseRecipe.getPreparationTime())
                .setDescription(responseRecipe.getDescription())
                .setIngredients(convertResponseIngredientListIntoIngredients(responseRecipe.getTitle(), responseRecipe.getIngredientList()))
                .setInstructions(convertResponseInstructionListIntoInstructions(responseRecipe.getTitle(), responseRecipe.getInstructionList()));
    }

    private Ingredients convertResponseIngredientListIntoIngredients(String title, List<ResponseIngredient> responseIngredientList)
    {
        List<Ingredient> ingredientList = responseIngredientList.stream().map(this::convertResponseIngredientIntoIngredient).collect(Collectors.toList());
        return new Ingredients(title, ingredientList);
    }

    private Ingredient convertResponseIngredientIntoIngredient(ResponseIngredient responseIngredient)
    {
        return new Ingredient()
                .setQuantity(responseIngredient.getQuantity())
                .setMeasurement(responseIngredient.getMeasurement())
                .setIngredient(responseIngredient.getIngredient())
                .setComment(responseIngredient.getComment());
    }

    private Instructions convertResponseInstructionListIntoInstructions(String title, List<ResponseInstruction> responseInstructionList)
    {
        List<String> instructionsList = responseInstructionList.stream().map(ResponseInstruction::getInstruction).collect(Collectors.toList());
        return new Instructions(title, instructionsList);
    }

    private ArrayList<Recipe> saveRecipeListInDatabase(List<Recipe> recipes)
    {
        if(!recipes.isEmpty())
        {
            boolean saveToDiskSuccess = saveRecipesToDisk(recipes);

            if(saveToDiskSuccess)
            {
                return new ArrayList<>(recipes);
            }
            else
            {
                return null;
            }
        }

        return new ArrayList<>(0);
    }

    private DisposableSingleObserver<ArrayList<Recipe>> updateRecipesFromWebObserver(Consumer<ArrayList<Recipe>> updateFragment)
    {
        return new DisposableSingleObserver<ArrayList<Recipe>>()
        {
            @Override
            public void onSuccess(ArrayList<Recipe> recipes)
            {
                String message = determineToastMessageFromResult(recipes);

                Context context = contextRef.get();
                if(context != null)
                {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

                updateFragment.accept(recipes);
            }

            private String determineToastMessageFromResult(ArrayList<Recipe> recipes)
            {
                String message;
                if(recipes == null)
                {
                    message = "Failed saving to disk.";
                }
                else if(recipes.isEmpty())
                {
                    message = "No new recipes available.";
                }
                else
                {
                    int newCount = recipes.size();
                    if(newCount > 1)
                    {
                        message = newCount + " new recipes added.";
                    }
                    else
                    {
                        message = newCount + " new recipe added.";
                    }
                }

                return message;
            }

            @Override
            public void onError(Throwable e)
            {
                Context context = contextRef.get();
                if(context != null)
                {
                    Toast.makeText(context, "Error retrieving recipes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                LogsManager.log(CLASS_NAME, "onError", "Error retrieving recipes. Error: " + e.getMessage(), e);
            }
        };
    }

    /**
     * Saves the given lists of recipes to the local database.
     *
     * @param recipeList the recipe lists to be stored
     * @return true on success, else false
     */
    private boolean saveRecipesToDisk(final List<Recipe> recipeList)
    {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        try
        {
            db.beginTransaction();
            // Delete recipes. To ensure no duplicates, if existing recipes are modified in the server.
            this.deleteQuery(db);

            // Iterate each recipe of a particular category
            recipeList.forEach(recipe -> insertRecipeToDatabase(db, recipe));

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
            db.close();
            this.dbHelper.close();
        }

        LogsManager.log(CLASS_NAME, "saveToDisk", "");

        return true;
    }

    private void insertRecipeToDatabase(SQLiteDatabase db, Recipe recipe)
    {
        long recipeId = insertRecipeDetailsToDatabase(db, recipe);

        List<Ingredient> ingredientList = recipe.getIngredients().getIngredientsList();
        IntConsumer insertIngredientToDatabase = count -> insertIngredientToDatabase(count, db, ingredientList, recipeId);
        IntStream.range(0, ingredientList.size()).forEach(insertIngredientToDatabase);

        List<String> instructionsList = recipe.getInstructions().getInstructionsList();
        IntConsumer insertInstructionToDatabase = count -> insertInstructionToDatabase(count, db, instructionsList, recipeId);
        IntStream.range(0, instructionsList.size()).forEach(insertInstructionToDatabase);
    }

    private long insertRecipeDetailsToDatabase(SQLiteDatabase db, Recipe recipe)
    {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(ColumnRecipe.title.name(), recipe.getTitle());
        recipeValues.put(ColumnRecipe.category.name(), Categories.getId(recipe.getCategory()));
        recipeValues.put(ColumnRecipe.preparation_time.name(), recipe.getPreparationTime());
        recipeValues.put(ColumnRecipe.servings.name(), recipe.getServings());
        recipeValues.put(ColumnRecipe.description.name(), recipe.getDescription());
        recipeValues.put(ColumnRecipe.date_in.name(), FastDateFormat.getInstance(DATE_FORMAT_LONG, Locale.getDefault()).format(this.curDate));
        return db.insert(TABLE_RECIPE, null, recipeValues);
    }

    private void insertIngredientToDatabase(int count, SQLiteDatabase db, List<Ingredient> ingredientList, long recipeId)
    {
        ContentValues ingredientsValues = new ContentValues();
        ingredientsValues.put(ColumnIngredients.recipe_id.name(), recipeId);
        ingredientsValues.put(ColumnIngredients.quantity.name(), ingredientList.get(count).getQuantity());
        ingredientsValues.put(ColumnIngredients.measurement.name(), ingredientList.get(count).getMeasurement());
        ingredientsValues.put(ColumnIngredients.ingredient.name(), ingredientList.get(count).getIngredient());
        ingredientsValues.put(ColumnIngredients.comment_.name(), ingredientList.get(count).getComment());
        ingredientsValues.put(ColumnIngredients.count.name(), count + 1);

        db.insert(TABLE_INGREDIENTS, null, ingredientsValues);
    }

    private void insertInstructionToDatabase(int count, SQLiteDatabase db, List<String> instructionsList, long recipeId)
    {
        ContentValues instructionsValues = new ContentValues();
        instructionsValues.put(ColumnInstructions.recipe_id.name(), recipeId);
        instructionsValues.put(ColumnInstructions.instruction.name(), instructionsList.get(count));
        instructionsValues.put(ColumnInstructions.count.name(), count + 1);

        db.insert(TABLE_INSTRUCTIONS, null, instructionsValues);
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
            // Parse String to Date, to be able to format properly.
            Date date = FastDateFormat.getInstance(DATE_FORMAT_LONG, Locale.getDefault()).parse(lastUpdatedDate);
            lastUpdatedDate = FastDateFormat.getInstance(format, Locale.getDefault()).format(date);
        }
        catch(ParseException e)
        {
            LogsManager.log(CLASS_NAME, "getLastUpdated", e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

        LogsManager.log(CLASS_NAME, "getLastUpdated", "lastUpdatedDate=" + lastUpdatedDate);

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

        Categories.getCategoriesMap().entrySet().forEach(entry -> putRecipeCountOfCategoryToMap(map, entry, db, whereClause));

        LogsManager.log(CLASS_NAME, "getRecipesCount", "values_size=" + map.values().size());

        return map;
    }

    private void putRecipeCountOfCategoryToMap(Map<String, Integer> map, Map.Entry<Integer, String> entry, SQLiteDatabase db, String whereClause)
    {
        try(Cursor cursor = db.query(TABLE_RECIPE, COLUMN_COUNT, whereClause, new String[] { entry.getKey().toString() }, null, null, null))
        {
            if(cursor.moveToFirst())
            {
                map.put(entry.getValue(), cursor.getInt(0));
            }
        }
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

        LogsManager.log(CLASS_NAME, "getRecipesFromDisk", "category=" + selectedCategory);

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

        String[] ingredientsColumns = new String[] { ColumnIngredients.quantity.name(), ColumnIngredients.measurement.name(),
                ColumnIngredients.ingredient.name(), ColumnIngredients.comment_.name() };
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
                    Ingredient ingredient = new Ingredient()
                                                .setQuantity(ingredientCursor.getDouble(0))
                                                .setMeasurement(ingredientCursor.getString(1))
                                                .setIngredient(ingredientCursor.getString(2))
                                                .setComment(ingredientCursor.getString(3));
                    ingredients.addIngredient(ingredient);
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

        return new Recipe().setId(id).setTitle(title).setCategory(category).setServings(servings)
                .setPreparationTime(preparationTime).setDescription(description)
                .setIngredients(ingredients).setInstructions(instructions);
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
     * Clears all observer in the composite disposable.
     * Uses clear because the CompositeDisposable is static and is used throughout the life of the application.
     */
    public static void clearRecipesWebObserver()
    {
        if(!compositeDisposable.isDisposed())
        {
            compositeDisposable.clear();
        }
    }
}
