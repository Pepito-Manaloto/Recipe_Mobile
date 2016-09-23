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
    public static final String[] COLUMN_COUNT = new String[]{"COUNT(*)",};

    /**
     * The database's recipe table column names.
     */
    public enum ColumnRecipe
    {
        id,
        title,
        category,
        preparation_time,
        description,
        servings,
        date_in,
    }

    /**
     * The database's ingredients table column names.
     */
    public enum ColumnIngredients
    {
        title,
        quantity,
        measurement,
        ingredient,
        comment_,
        count,
    }

    /**
     * The database's instructions table column names.
     */
    public enum ColumnInstructions
    {
        title,
        instruction,
        count,
    }

    private static final String CREATE_TABLE_RECIPE = "CREATE TABLE " + TABLE_RECIPE +
                                                      "(" + 
                                                       ColumnRecipe.title.name() + " TEXT PRIMARY KEY, " +
                                                       ColumnRecipe.category.name() + " TEXT NOT NULL, " +
                                                       ColumnRecipe.preparation_time.name() + " INTEGER NOT NULL, " +
                                                       ColumnRecipe.description.name() + " TEXT NOT NULL, " +
                                                       ColumnRecipe.servings.name() + " INTEGER NOT NULL, " +
                                                       ColumnRecipe.date_in.name() + " TEXT NOT NULL" +
                                                      ");";

    private static final String CREATE_TABLE_INGREDIENTS = "CREATE TABLE " + TABLE_INGREDIENTS +
                                                       "(" + 
                                                        ColumnIngredients.title.name() + " TEXT NOT NULL, " +
                                                        ColumnIngredients.quantity.name() + " REAL NOT NULL, " +
                                                        ColumnIngredients.measurement.name() + " TEXT NOT NULL, " +
                                                        ColumnIngredients.ingredient.name() + " TEXT NOT NULL, " +
                                                        ColumnIngredients.comment_.name() + " TEXT NOT NULL, " +
                                                        ColumnIngredients.count.name() + " INTEGER NOT NULL, " +
                                                        "UNIQUE(" + ColumnIngredients.title.name() + ", " + ColumnIngredients.ingredient.name() + ") ON CONFLICT REPLACE, " +
                                                        "FOREIGN KEY (title) REFERENCES " + TABLE_RECIPE + "(title) ON UPDATE CASCADE ON DELETE CASCADE" +
                                                       ");";

    private static final String CREATE_TABLE_INSTRUCTIONS = "CREATE TABLE " + TABLE_INSTRUCTIONS +
                                                       "(" + 
                                                        ColumnInstructions.title.name() + " TEXT NOT NULL, " +
                                                        ColumnInstructions.instruction.name() + " TEXT NOT NULL, " +
                                                        ColumnInstructions.count.name() + " INTEGER NOT NULL, " +
                                                        "UNIQUE(" + ColumnInstructions.title.name() + ", " + ColumnInstructions.instruction.name() + ") ON CONFLICT REPLACE, " +
                                                        "FOREIGN KEY (title) REFERENCES " + TABLE_RECIPE + "(title) ON UPDATE CASCADE ON DELETE CASCADE" +
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

        database.execSQL(CREATE_TABLE_RECIPE);
        database.execSQL(CREATE_TABLE_INGREDIENTS);
        database.execSQL(CREATE_TABLE_INSTRUCTIONS);
    }

    /**
     * Called if the version given in the constructor is higher than the existing database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        /**
         * TODO (1) store db contents in temp --- NOT YET
         *      (2) drop db --- IMPLEMENTED
         *      (3) create new db --- IMPLEMENTED 
         *      (4) insert temp data in new db --- NOT YET
         */ 
        database.execSQL("DROP IF TABLE EXISTS " + TABLE_RECIPE);
        this.onCreate(database);
    }

}
