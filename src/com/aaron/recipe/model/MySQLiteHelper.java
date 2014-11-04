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
    private static final String DATABASE_NAME = "aaron_recipe.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_RECIPE = "recipe";
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_INSTRUCTIONS = "instructions";
    public static final String[] COLUMN_COUNT = new String[]{"COUNT(*)",};

    /**
     * The database's column names.
     */
    public enum ColumnRecipe
    {
        id,
        title,
        category,
        preparation_time,
        description,
        servings,
        author,
        date_in,
    }

    private static final String CREATE_TABLE_RECIPE = "CREATE TABLE " + TABLE_RECIPE +
                                               "(" + 
                                                ColumnRecipe.id.name() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                                                ColumnRecipe.date_in.name() + " TEXT NOT NULL, " +

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

        database.execSQL(CREATE_TABLE_RECIPE);
    }

    /**
     * Called if the version given in the constructor is higher than the existing database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        /**
         * TODO (1) store db contents in temp
         *      (2) drop db --- IMPLEMENTED
         *      (3) create new db --- IMPLEMENTED 
         *      (4) insert temp data in new db
         */ 
        database.execSQL("DROP IF TABLE EXISTS " + TABLE_RECIPE);
        this.onCreate(database);
    }

}
