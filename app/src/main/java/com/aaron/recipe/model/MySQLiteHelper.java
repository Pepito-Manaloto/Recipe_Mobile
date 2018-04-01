package com.aaron.recipe.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Model for creating and updating the database.
 */
public class MySQLiteHelper extends SQLiteOpenHelper
{
    public static final String TAG = "MySQLiteHelper";
    public static final String DATABASE_NAME = "aaron_recipe.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_RECIPE = "recipe";
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_INSTRUCTIONS = "instructions";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String[] COLUMN_COUNT = new String[] { "COUNT(*)", };

    /**
     * The database's recipe table column names.
     */
    public enum ColumnRecipe
    {
        id, title, category, preparation_time, description, servings, date_in,
    }

    /**
     * The database's ingredients table column names.
     */
    public enum ColumnIngredients
    {
        recipe_id, quantity, measurement, ingredient, comment_, count,
    }

    /**
     * The database's instructions table column names.
     */
    public enum ColumnInstructions
    {
        recipe_id, instruction, count,
    }

    /**
     * The database's categories table column names.
     */
    public enum ColumnCategories
    {
        id, name,
    }

    private static final String CREATE_TABLE_RECIPE = "CREATE TABLE " + TABLE_RECIPE +
            "(" +
            ColumnRecipe.id.name() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ColumnRecipe.title.name() + " TEXT UNIQUE NOT NULL, " +
            ColumnRecipe.category.name() + " INTEGER NOT NULL, " +
            ColumnRecipe.preparation_time.name() + " INTEGER NOT NULL, " +
            ColumnRecipe.description.name() + " TEXT NOT NULL, " +
            ColumnRecipe.servings.name() + " INTEGER NOT NULL, " +
            ColumnRecipe.date_in.name() + " TEXT NOT NULL, " +
            "FOREIGN KEY (category) REFERENCES " + TABLE_CATEGORIES + "(id)" +
            ");";

    private static final String CREATE_TABLE_INGREDIENTS = "CREATE TABLE " + TABLE_INGREDIENTS +
            "(" +
            ColumnIngredients.recipe_id.name() + " INTEGER NOT NULL, " +
            ColumnIngredients.quantity.name() + " REAL NOT NULL, " +
            ColumnIngredients.measurement.name() + " TEXT NOT NULL, " +
            ColumnIngredients.ingredient.name() + " TEXT NOT NULL, " +
            ColumnIngredients.comment_.name() + " TEXT NOT NULL, " +
            ColumnIngredients.count.name() + " INTEGER NOT NULL, " +
            "FOREIGN KEY (recipe_id) REFERENCES " + TABLE_RECIPE + "(id) ON UPDATE CASCADE ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TABLE_INSTRUCTIONS = "CREATE TABLE " + TABLE_INSTRUCTIONS +
            "(" +
            ColumnInstructions.recipe_id.name() + " INTEGER NOT NULL, " +
            ColumnInstructions.instruction.name() + " TEXT NOT NULL, " +
            ColumnInstructions.count.name() + " INTEGER NOT NULL, " +
            "FOREIGN KEY (recipe_id) REFERENCES " + TABLE_RECIPE + "(id) ON UPDATE CASCADE ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES +
            "(" +
            ColumnCategories.id.name() + " INTEGER PRIMARY KEY," +
            ColumnCategories.name.name() + " TEXT UNIQUE NOT NULL" +
            ");";

    /**
     * Default constructor.
     */
    public MySQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called if the database name given in the constructor does not exists.
     */
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        Log.d(LogsManager.TAG, "MySQLiteHelper: onCreate. query=" + CREATE_TABLE_RECIPE);
        Log.d(LogsManager.TAG, "MySQLiteHelper: onCreate. query=" + CREATE_TABLE_INGREDIENTS);
        Log.d(LogsManager.TAG, "MySQLiteHelper: onCreate. query=" + CREATE_TABLE_INSTRUCTIONS);
        Log.d(LogsManager.TAG, "MySQLiteHelper: onCreate. query=" + CREATE_TABLE_CATEGORIES);

        try
        {
            database.beginTransaction();

            database.execSQL(CREATE_TABLE_RECIPE);
            database.execSQL(CREATE_TABLE_INGREDIENTS);
            database.execSQL(CREATE_TABLE_INSTRUCTIONS);
            database.execSQL(CREATE_TABLE_CATEGORIES);

            database.setTransactionSuccessful();
        }
        finally
        {
            database.endTransaction();
        }
    }

    /**
     * Called if the version given in the constructor is higher than the existing database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        /**
         * TODO
         * (1) store db contents in temp --- NOT YET
         * (2) drop db --- IMPLEMENTED
         * (3) create new db --- IMPLEMENTED
         * (4) insert temp data in new db --- NOT YET
         */
        try
        {
            database.beginTransaction();
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTIONS);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            database.setTransactionSuccessful();
        }
        finally
        {
            database.endTransaction();
        }

        this.onCreate(database);
    }

}
