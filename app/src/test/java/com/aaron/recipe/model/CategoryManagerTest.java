package com.aaron.recipe.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.aaron.recipe.R;
import com.aaron.recipe.RobolectricTest;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.response.ResponseCategory;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static com.aaron.recipe.model.MySQLiteHelper.TABLE_CATEGORIES;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CategoryManagerTest extends RobolectricTest
{
    private HttpClient httpClient;
    private CategoryManager manager;

    private MySQLiteHelper dbHelperTest;
    private CompositeDisposable compositeDisposable;
    private int disposables;
    private AtomicBoolean isUpdating;

    @Before
    public void initialize() throws IllegalAccessException
    {
        // override Schedulers.io()
        RxJavaPlugins.setIoSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        // override AndroidSchedulers.mainThread()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        dbHelperTest = new MySQLiteHelper(getContext());
        manager = new CategoryManager(getContext());

        httpClient = mock(HttpClient.class);
        FieldUtils.writeField(manager, "httpClient", httpClient, true);

        compositeDisposable = (CompositeDisposable) FieldUtils.readStaticField(CategoryManager.class, "compositeDisposable", true);
        isUpdating = (AtomicBoolean) FieldUtils.readStaticField(CategoryManager.class, "IS_UPDATING", true);

        disposables = compositeDisposable.size();
    }

    @After
    public void cleanUp()
    {
        compositeDisposable.clear();
        isUpdating.set(false);
        Categories.getCategories().clear();
        Categories.getCategoriesMap().clear();
        dbHelperTest.close();
    }

    @Test
    public void givenActionAndResponse_whenUpdateCategories_thenShouldStartUpdatingAndSaveCategoriesAndRunActionAndDisposeObserver() throws Exception
    {
        Action action = mock(Action.class);
        List<ResponseCategory> response = givenResponseCategoryList();
        when(httpClient.getCategories()).thenReturn(Single.just(response));

        manager.updateCategories(action);

        thenShouldStartUpdatingAndSaveCategoriesAndExecuteTheActionAndAddObserverToCompositeDisposable(response, action);
    }

    @Test
    public void givenActionAndEmptyResponse_whenUpdateCategories_thenShouldStartUpdatingAndHandleEmptyResponseAndRunActionAndDisposeObserver()
            throws Exception
    {
        Action action = mock(Action.class);
        when(httpClient.getCategories()).thenReturn(Single.just(Collections.emptyList()));

        manager.updateCategories(action);

        thenShouldStartUpdatingAndHandleEmptyResponseAndRunActionAndDisposeObserver(action);
    }

    @Test
    public void givenActionAndThrowExceptn_whenUpdateCategories_thenShouldStartUpdatingAndHandleExceptnAndRunActionAndDisposeObserver()
            throws Exception
    {
        Action action = mock(Action.class);
        when(httpClient.getCategories()).thenReturn(Single.error(Exception::new));

        manager.updateCategories(action);

        thenShouldStartUpdatingAndHandleExceptionAndRunActionAndDisposeObserver(action);
    }

    @Test
    public void givenNotDisposedCompositeDisposable_whenClearCategoriesWebObserver_thenShouldClearTheCompositeDisposable()
    {
        compositeDisposable.addAll(mock(Disposable.class), mock(Disposable.class), mock(Disposable.class));

        CategoryManager.clearCategoriesWebObserver();

        assertEquals(0, compositeDisposable.size());
    }

    @Test
    public void givenCategoriesInDisk_whenGetCategoriesFromDisk_thenShouldReturnCategoriesArray()
    {
        SparseArray<String> categoriesFromDatabase = givenCategoriesInDisk();

        SparseArray<String> categories = manager.getCategoriesFromDisk();

        thenShouldReturnCategoriesArray(categoriesFromDatabase, categories);
    }

    @Test
    public void givenIsUpdating_whenDoneUpdating_thenShouldSetIsUpdatingFlagToFalse()
    {
        isUpdating.set(true);

        CategoryManager.doneUpdating();

        assertFalse(isUpdating.get());
    }

    @Test
    public void givenIsUpdating_whenIsNotUpdating_thenShouldReturnFalse()
    {
        isUpdating.set(true);

        assertFalse(CategoryManager.isNotUpdating());
    }

    @Test
    public void givenNotUpdating_whenIsNotUpdating_thenShouldReturnTrue()
    {
        isUpdating.set(false);

        assertTrue(CategoryManager.isNotUpdating());
    }

    private List<ResponseCategory> givenResponseCategoryList()
    {
        int size = 5;
        List<ResponseCategory> response = new ArrayList<>(size);
        IntStream.range(0, size).mapToObj(this::newResponseCategory).forEach(response::add);

        return response;
    }

    private ResponseCategory newResponseCategory(int id)
    {
        ResponseCategory category = new ResponseCategory();
        category.setId(id);
        category.setName(randomAlphabetic(7));

        return category;
    }

    private SparseArray<String> givenCategoriesInDisk()
    {
        int size = 8;
        SparseArray<String> array = new SparseArray<>();
        IntStream.range(0, size).forEach(id -> array.append(id, randomAlphabetic(7)));

        try(SQLiteDatabase db = dbHelperTest.getWritableDatabase())
        {
            for(int i = 0; i < size; i++)
            {
                String sql = String.format("INSERT INTO %s(id, name) VALUES('%s', '%s')", TABLE_CATEGORIES, array.keyAt(i), array.valueAt(i));
                db.execSQL(sql);
            }
        }

        return array;
    }

    private void thenShouldStartUpdatingAndHandleEmptyResponseAndRunActionAndDisposeObserver(Action action) throws Exception
    {
        assertTrue(isUpdating.get());

        assertEquals(ShadowToast.getTextOfLatestToast(), "Error saving categories.");

        verify(action, times(1)).run();

        assertEquals(disposables + 1, compositeDisposable.size());
    }

    private void thenShouldStartUpdatingAndHandleExceptionAndRunActionAndDisposeObserver(Action action) throws Exception
    {
        assertTrue(isUpdating.get());

        assertEquals(ShadowToast.getTextOfLatestToast(), getContext().getString(R.string.error_retrieving_categories));

        verify(action, times(1)).run();

        assertEquals(disposables + 1, compositeDisposable.size());
    }

    private void thenShouldReturnCategoriesArray(SparseArray<String> categoriesFromDatabase, SparseArray<String> categories)
    {
        assertEquals(categoriesFromDatabase.size(), categories.size());

        int size = categoriesFromDatabase.size();

        for(int i = 0; i < size; i++)
        {
            assertEquals(categoriesFromDatabase.keyAt(i), categories.keyAt(i));
            assertEquals(categoriesFromDatabase.valueAt(i), categories.valueAt(i));
        }
    }

    private void thenShouldStartUpdatingAndSaveCategoriesAndExecuteTheActionAndAddObserverToCompositeDisposable(List<ResponseCategory> response, Action action)
            throws Exception
    {
        assertTrue(isUpdating.get());

        assertCategoriesAreSavedInCacheAndDisk(response);

        verify(action, times(1)).run();

        assertEquals(disposables + 1, compositeDisposable.size());
    }

    private void assertCategoriesAreSavedInCacheAndDisk(List<ResponseCategory> response)
    {
        Set<Integer> categoriesIds = response.stream().map(ResponseCategory::getId).collect(Collectors.toSet());
        List<String> categoriesList = response.stream().map(ResponseCategory::getName).collect(Collectors.toList());

        Map<Integer, String> categoriesFromDisk = getCategoriesFromDisk();
        assertEquals(categoriesIds, categoriesFromDisk.keySet());
        assertThat(categoriesList, containsInAnyOrder(categoriesFromDisk.values().toArray()));

        assertEquals(categoriesIds, Categories.getCategoriesMap().keySet());
        assertThat(categoriesList, containsInAnyOrder(Categories.getCategoriesMap().values().toArray()));
        assertThat(categoriesList, containsInAnyOrder(Categories.getCategories().toArray()));
    }

    private Map<Integer, String> getCategoriesFromDisk()
    {
        Map<Integer, String> map = new HashMap<>();
        try(SQLiteDatabase db = dbHelperTest.getReadableDatabase())
        {
            try(Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s", TABLE_CATEGORIES), null))
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        map.put(cursor.getInt(0), cursor.getString(1));
                    } while(cursor.moveToNext());
                }
            }
        }

        return map;
    }
}
