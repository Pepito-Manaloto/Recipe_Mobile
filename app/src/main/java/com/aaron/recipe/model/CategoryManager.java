package com.aaron.recipe.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.response.ResponseCategory;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.aaron.recipe.model.MySQLiteHelper.ColumnCategories;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_CATEGORIES;

/**
 * Handles the web call to retrieve recipes in JSON object representation. Handles the data storage of recipes.
 */
public class CategoryManager
{
    public static final String CLASS_NAME = CategoryManager.class.getSimpleName();

    private static final AtomicBoolean IS_UPDATING = new AtomicBoolean(false);
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MySQLiteHelper dbHelper;
    private HttpClient httpClient;
    private WeakReference<Context> contextRef;

    /**
     * Constructor initializes the url.
     *
     * @param context
     *            the caller activity
     */
    public CategoryManager(final Context context)
    {
        this.dbHelper = new MySQLiteHelper(context);
        this.httpClient = new HttpClient(context.getString(R.string.url_address_default));
        this.contextRef = new WeakReference<>(context);
    }

    /**
     * Retrieves the categories from the server, then update cache and database.
     */
    public void updateCategories(Action doFinally)
    {
        startUpdating();

        Disposable disposable = httpClient.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(doFinally)
                .subscribeWith(getCategoriesFromWebObserver());

        compositeDisposable.add(disposable);
    }

    private DisposableSingleObserver<List<ResponseCategory>> getCategoriesFromWebObserver()
    {
        return new DisposableSingleObserver<List<ResponseCategory>>()
        {
            @Override
            public void onSuccess(List<ResponseCategory> response)
            {
                boolean saved = saveCategories(response);
                if(saved)
                {
                    LogsManager.log(CLASS_NAME, "onSuccess", "Categories = " + Categories.getCategories());
                }
                else
                {
                    Context context = contextRef.get();
                    if(context != null)
                    {
                        Toast.makeText(context, "Error saving categories.", Toast.LENGTH_LONG).show();
                    }
                    LogsManager.log(CLASS_NAME, "onSuccess", "Failed saving categories.");
                }
            }

            @Override
            public void onError(Throwable e)
            {
                Context context = contextRef.get();
                if(context != null)
                {
                    Toast.makeText(context, context.getString(R.string.error_retrieving_categories), Toast.LENGTH_LONG).show();
                }

                LogsManager.log(CLASS_NAME, "onError", "Error retrieving categories. Error: " + e.getMessage(), e);
            }
        };

    }

    /**
     * Store categories in cache and persist to the database.
     *
     * @param responseCategories the categories response to save
     */
    private boolean saveCategories(List<ResponseCategory> responseCategories)
    {
        if(responseCategories != null && !responseCategories.isEmpty())
        {
            saveCategoriesInCache(responseCategories);
            saveCategoriesInDatabase(responseCategories);

            return true;
        }

        return false;
    }

    /**
     * Store categories in cache.
     *
     * @param responseCategories the categories response to save
     */
    private void saveCategoriesInCache(List<ResponseCategory> responseCategories)
    {
        Consumer<ResponseCategory> putResponseCategoryToCache = (category) ->
        {
            Categories.getCategoriesMap().put(category.getId(), category.getName());
            Categories.getCategories().add(category.getName());
        };
        responseCategories.forEach(putResponseCategoryToCache);
    }

    /**
     * Persists the category list to the database.
     *
     * @param responseCategories the categories response to save
     */
    private void saveCategoriesInDatabase(List<ResponseCategory> responseCategories)
    {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues categoryValues = new ContentValues();

        try
        {
            db.beginTransaction();

            // Delete categories to insert latest data
            db.delete(TABLE_CATEGORIES, null, null);

            Consumer<ResponseCategory> insertResponseCategoryToDatabase = (category) ->
            {
                categoryValues.put(ColumnCategories.id.name(), category.getId());
                categoryValues.put(ColumnCategories.name.name(), category.getName());

                db.insert(TABLE_CATEGORIES, null, categoryValues);
            };
            responseCategories.forEach(insertResponseCategoryToDatabase);

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
            db.close();
            this.dbHelper.close();
        }
    }

    /**
     * Clears all observer in the composite disposable.
     * Uses clear because the CompositeDisposable is static and is used throughout the life of the application.
     */
    public static void clearCategoriesWebObserver()
    {
        if(!compositeDisposable.isDisposed())
        {
            compositeDisposable.clear();
        }
    }

    /**
     * Store categories in cache.
     *
     * @param categoriesArray the categories array
     */
    public void saveCategoriesInCache(SparseArray<String> categoriesArray)
    {
        int length = categoriesArray.size();
        for(int i = 0; i < length; i++)
        {
            int id = categoriesArray.keyAt(i);
            String category = categoriesArray.get(id);
            Categories.getCategoriesMap().put(id, category);
            Categories.getCategories().add(category);
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

        LogsManager.log(CLASS_NAME, "getCategoriesFromDisk", "length=" + array.size());

        return array;
    }

    private static void startUpdating()
    {
        IS_UPDATING.set(true);
    }

    public static void doneUpdating()
    {
        IS_UPDATING.set(false);
    }

    public static boolean isNotUpdating()
    {
        return !IS_UPDATING.get();
    }
}
