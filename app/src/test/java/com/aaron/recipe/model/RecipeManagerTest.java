package com.aaron.recipe.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aaron.recipe.RobolectricTest;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.Ingredient;
import com.aaron.recipe.bean.Ingredients;
import com.aaron.recipe.bean.Instructions;
import com.aaron.recipe.bean.Recipe;
import com.aaron.recipe.response.ResponseIngredient;
import com.aaron.recipe.response.ResponseInstruction;
import com.aaron.recipe.response.ResponseRecipe;
import com.aaron.recipe.response.ResponseRecipes;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowToast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static com.aaron.recipe.bean.Categories.DEFAULT;
import static com.aaron.recipe.model.MySQLiteHelper.ColumnInstructions;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_INGREDIENTS;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_INSTRUCTIONS;
import static com.aaron.recipe.model.MySQLiteHelper.TABLE_RECIPE;
import static com.aaron.recipe.model.RecipeManager.DATE_FORMAT_DATABASE;
import static com.aaron.recipe.model.RecipeManager.DATE_FORMAT_SHORT_24;
import static com.aaron.recipe.model.RecipeManager.DEFAULT_LAST_UPDATED;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.nonNull;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeManagerTest extends RobolectricTest
{
    private static final int RECIPES_SIZE = 8;
    private static final int INGREDIENTS_SIZE = 5;
    private static final int INSTRUCTIONS_SIZE = INGREDIENTS_SIZE;
    private static final LocalDateTime NOW = now();

    private HttpClient httpClient;
    private RecipeManager manager;

    private MySQLiteHelper dbHelperTest;
    private CompositeDisposable compositeDisposable;
    private int disposables;

    @Before
    public void initialize() throws IllegalAccessException
    {
        // override Schedulers.io()
        RxJavaPlugins.setIoSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        // override AndroidSchedulers.mainThread()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        dbHelperTest = new MySQLiteHelper(getContext());
        manager = new RecipeManager(getContext());

        httpClient = mock(HttpClient.class);
        FieldUtils.writeField(manager, "httpClient", httpClient, true);

        compositeDisposable = (CompositeDisposable) FieldUtils.readStaticField(RecipeManager.class, "compositeDisposable", true);

        disposables = compositeDisposable.size();
    }

    @After
    public void cleanUp()
    {
        Categories.getCategoriesMap().clear();
        compositeDisposable.clear();
        dbHelperTest.close();
    }

    @Test
    public void givenActionAndConsumerAndResponses_whenUpdateRecipesFromWeb_thenShouldSaveRecipesCallActionAndConsumerAndDisposeObserver() throws Exception
    {
        Action action = mock(Action.class);
        Consumer<ArrayList<Recipe>> consumer = mock(Consumer.class);
        ResponseRecipes response = givenResponseRecipes(10);
        when(httpClient.getRecipes(anyString())).thenReturn(Single.just(response));

        manager.updateRecipesFromWeb(action, consumer);

        String message = response.getRecipeList().size() + " new recipes added.";
        thenShouldSaveRecipesCallActionAndConsumerAndDisposeObserver(action, consumer, response, message);
    }

    @Test
    public void givenActionAndConsumerAndResponse_whenUpdateRecipesFromWeb_thenShouldSaveRecipesCallActionAndConsumerAndDisposeObserver() throws Exception
    {
        Action action = mock(Action.class);
        Consumer<ArrayList<Recipe>> consumer = mock(Consumer.class);
        ResponseRecipes response = givenResponseRecipes(1);
        when(httpClient.getRecipes(anyString())).thenReturn(Single.just(response));

        manager.updateRecipesFromWeb(action, consumer);

        String message = response.getRecipeList().size() + " new recipe added.";
        thenShouldSaveRecipesCallActionAndConsumerAndDisposeObserver(action, consumer, response, message);
    }

    @Test
    public void givenActionAndConsumerAndEmptyResponse_whenUpdateRecipesFromWeb_thenShouldCallActionAndConsumerAndDisposeObserver() throws Exception
    {
        Action action = mock(Action.class);
        Consumer<ArrayList<Recipe>> consumer = mock(Consumer.class);
        when(httpClient.getRecipes(anyString())).thenReturn(Single.just(givenResponseRecipes(0)));

        manager.updateRecipesFromWeb(action, consumer);

        String message = "No new recipes available.";
        thenShouldCallActionAndConsumerAndAddCompositeToDisposable(action, consumer, message);
    }

    @Test
    public void givenActionAndConsumerAndDbException_whenUpdateRecipesFromWeb_thenShouldCallActionAndConsumerAndDisposeObserver() throws Exception
    {
        Action action = mock(Action.class);
        Consumer<ArrayList<Recipe>> consumer = mock(Consumer.class);
        when(httpClient.getRecipes(anyString())).thenReturn(Single.just(givenResponseRecipes(1)));

        RecipeManager spyManager = mockDatabaseInsertException();
        spyManager.updateRecipesFromWeb(action, consumer);

        String message = "Failed saving to disk.";
        thenShouldHandleExceptnAndCallActionAndDisposeObserver(action, consumer, message);
    }

    @Test
    public void givenActionAndConsumerAndExceptn_whenUpdateRecipesFromWeb_thenShouldHandleExceptnAndCallActionAndDisposeObserver() throws Exception
    {
        Action action = mock(Action.class);
        Consumer<ArrayList<Recipe>> consumer = mock(Consumer.class);
        String exceptionMessage = randomAlphabetic(10);
        when(httpClient.getRecipes(anyString())).thenReturn(Single.error(() -> new Exception(exceptionMessage)));

        manager.updateRecipesFromWeb(action, consumer);

        thenShouldHandleExceptnAndCallActionAndDisposeObserver(action, consumer, exceptionMessage);
    }

    @Test
    public void givenRecipesInDiskAndFormat_whenGetLastUpdated_thenShouldFormattedLastUpdated()
    {
        givenRecipesInDisk();
        String format = DATE_FORMAT_SHORT_24;

        String lastUpdated = manager.getLastUpdated(format);

        assertEquals(ofPattern(format).format(NOW), lastUpdated);
    }

    @Test
    public void givenNoRecipesInDisk_whenGetLastUpdated_thenShouldReturnDefaultLastUpdated()
    {
        String lastUpdated = manager.getLastUpdated(DATE_FORMAT_DATABASE);

        assertEquals(DEFAULT_LAST_UPDATED, lastUpdated);
    }

    @Test
    public void givenRecipesInDisk_whenGetRecipesCount_thenShouldRecipesCountPerCategory()
    {
        List<Recipe> recipes = givenRecipesInDisk();

        Map<String, Integer> recipesCountFromDisk = manager.getRecipesCount();

        // Collectors.counting() is not used, because it returns a Long type
        Map<String, Integer> recipesCount = recipes.stream().collect(Collectors.groupingBy(Recipe::getCategory, Collectors.summingInt(i -> 1)));
        assertTrue(recipesCount.equals(recipesCountFromDisk));
    }

    @Test
    public void givenRecipesInDiskAndAllCategory_whenGetRecipesFromDisk_thenShouldGetAllRecipes()
    {
        List<Recipe> recipes = givenRecipesInDisk();

        ArrayList<Recipe> recipesFromDisk = manager.getRecipesFromDisk(DEFAULT);

        assertEquals(recipes.size(), recipesFromDisk.size());
        assertThat(recipes, containsInAnyOrder(recipesFromDisk.toArray()));
    }

    @Test
    public void givenRecipesInDiskAndCategory_whenGetRecipesFromDisk_thenShouldGetRecipesOfTheSameCategory()
    {
        List<Recipe> recipes = givenRecipesInDisk();
        String category = Categories.getCategoriesMap().get(1);

        ArrayList<Recipe> recipesFromDisk = manager.getRecipesFromDisk(category);

        List<Recipe> recipesOfCategory = recipes.stream().filter(r -> r.getCategory().equals(category)).collect(Collectors.toList());
        assertEquals(recipesOfCategory.size(), recipesFromDisk.size());
        assertThat(recipesOfCategory, containsInAnyOrder(recipesFromDisk.toArray()));
    }

    @Test
    public void givenRecipesInDisk_whenDeleteRecipeFromDisk_thenShouldDeleteAllRecipesInDisk()
    {
        givenRecipesInDisk();

        manager.deleteRecipeFromDisk();

        assertTrue(getRecipesFromDisk().isEmpty());
    }

    @Test
    public void givenNotDisposedCompositeDisposable_whenClearRecipesWebObserver_thenShouldClearTheCompositeDisposable()
    {
        compositeDisposable.addAll(mock(Disposable.class), mock(Disposable.class), mock(Disposable.class));

        RecipeManager.clearRecipesWebObserver();

        assertEquals(0, compositeDisposable.size());
    }

    private ResponseRecipes givenResponseRecipes(int size)
    {
        ResponseRecipes response = new ResponseRecipes();
        response.setRecentlyAddedCount(size);

        List<ResponseRecipe> recipeList = new ArrayList<>();
        IntStream.range(0, size).mapToObj(this::newResponseRecipe).forEach(recipeList::add);
        response.setRecipeList(recipeList);

        return response;
    }

    private ResponseRecipe newResponseRecipe(int id)
    {
        ResponseRecipe recipe = new ResponseRecipe();
        recipe.setTitle(randomUUID().toString());
        recipe.setCategory(randomAlphabetic(7));
        recipe.setDescription(randomAlphabetic(7));
        recipe.setPreparationTime(nextInt());
        recipe.setServings(nextInt());
        recipe.setIngredientList(createIngredientsList());
        List<ResponseInstruction> instructions = IntStream.range(0, 5).mapToObj(Integer::toString).map(s -> s.concat(randomAlphabetic(7)))
                .map(ResponseInstruction::new).collect(Collectors.toList());
        recipe.setInstructionList(instructions);

        // Also add to cache (used by the manager to insert the category ID of the recipe
        Categories.getCategoriesMap().put(id, recipe.getCategory());

        return recipe;
    }

    private List<ResponseIngredient> createIngredientsList()
    {
        List<ResponseIngredient> ingredients = new ArrayList<>();

        for(int i = 0; i < 5; i++)
        {
            ResponseIngredient ingredient = new ResponseIngredient();
            ingredient.setQuantity(nextDouble(0.125, 10.75));
            ingredient.setMeasurement(randomAlphabetic(15));
            ingredient.setIngredient(randomAlphabetic(15));
            ingredient.setComment(randomAlphabetic(15));
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    private RecipeManager mockDatabaseInsertException() throws IllegalAccessException
    {
        RecipeManager spyManager = spy(manager);
        when(spyManager.getLastUpdated(anyString())).thenReturn(DEFAULT_LAST_UPDATED);

        SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);
        when(mockedSQLiteDatabase.insert(anyString(), isNull(), any(ContentValues.class))).thenThrow(new RuntimeException("FUCK YOU!"));

        MySQLiteHelper mockedSQLiteHelper = mock(MySQLiteHelper.class);
        when(mockedSQLiteHelper.getWritableDatabase()).thenReturn(mockedSQLiteDatabase);

        FieldUtils.writeField(spyManager, "dbHelper", mockedSQLiteHelper, true);
        FieldUtils.writeField(spyManager, "httpClient", httpClient, true);

        return spyManager;
    }

    private List<Recipe> givenRecipesInDisk()
    {
        int size = RECIPES_SIZE;
        int categoryId = 0;
        List<Recipe> recipeList = new ArrayList<>(size);

        try(SQLiteDatabase db = dbHelperTest.getWritableDatabase())
        {
            String insertRecipeQuery = "INSERT INTO %s(title, category_id, preparation_time, description, servings, date_in) VALUES('%s', %s, %s, '%s', %s, '%s')";

            for(int i = 0; i < size; i++)
            {
                Recipe recipe = createNewRecipe(categoryId);
                String sql = String.format(insertRecipeQuery, TABLE_RECIPE, recipe.getTitle(), Categories.getId(recipe.getCategory()),
                        recipe.getPreparationTime(), recipe.getDescription(), recipe.getServings(), NOW.format(ofPattern(DATE_FORMAT_DATABASE)));
                db.execSQL(sql);

                int id = getIdOfLastInserted(db);
                recipe.setId(id);
                insertIngredientsToDisk(id, db, recipe.getIngredients().getIngredientsList());
                insertInstructionsToDisk(id, db, recipe.getInstructions().getInstructionsList());

                categoryId = i / 2;
                recipeList.add(recipe);
            }
        }

        return recipeList;
    }

    private int getIdOfLastInserted(SQLiteDatabase db)
    {
        String getIdOfLastInsertedQuery = "SELECT last_insert_rowid()";
        Cursor cursor = db.rawQuery(getIdOfLastInsertedQuery, null);
        if(nonNull(cursor))
        {
            try
            {
                if(cursor.moveToFirst())
                {
                    return cursor.getInt(0);
                }
            }
            finally
            {
                cursor.close();
            }
        }

        throw new AssertionError("Error getting ID of last inserted test data");
    }

    private Recipe createNewRecipe(int categoryId)
    {
        Recipe recipe = new Recipe();
        recipe.setTitle(randomAlphabetic(10));
        Categories.getCategoriesMap().put(categoryId, String.valueOf(categoryId).concat("-value"));
        recipe.setCategory(Categories.getCategoriesMap().get(categoryId));
        recipe.setDescription(randomAlphabetic(30));
        recipe.setServings(nextInt());
        recipe.setPreparationTime(nextInt());
        recipe.setIngredients(createNewIngredients(recipe.getTitle()));
        recipe.setInstructions(createNewInstructions(recipe.getTitle()));

        return recipe;
    }

    private Ingredients createNewIngredients(String title)
    {
        int size = INGREDIENTS_SIZE;
        Ingredients ingredients = new Ingredients(title, size);
        for(int i = 0; i < size; i++)
        {
            ingredients.addIngredient(new Ingredient()
                    .setQuantity(Math.round(nextDouble() * 100.0) / 100.0) // Limit to two decimal places to prevent assertion error
                    .setIngredient(randomAlphabetic(10))
                    .setMeasurement(randomAlphabetic(4))
                    .setComment(randomAlphabetic(30)));
        }

        return ingredients;
    }

    private Instructions createNewInstructions(String title)
    {
        int size = INSTRUCTIONS_SIZE;
        Instructions instructions = new Instructions(title, size);
        IntStream.range(0, size).mapToObj(i -> randomAlphabetic(20)).forEach(instructions::addInstruction);

        return instructions;
    }

    private void insertIngredientsToDisk(int id, SQLiteDatabase db, ArrayList<Ingredient> ingredientsList)
    {
        String insertIngredientQuery = "INSERT INTO %s(recipe_id, quantity, measurement, ingredient, comment_, count) VALUES('%s', %s, '%s', '%s', '%s', %s)";
        for(int i = 0; i < INGREDIENTS_SIZE; i++)
        {
            String sql = String.format(insertIngredientQuery, TABLE_INGREDIENTS, id, ingredientsList.get(i).getQuantity(),
                    ingredientsList.get(i).getMeasurement(), ingredientsList.get(i).getIngredient(), ingredientsList.get(i).getComment(), i);
            db.execSQL(sql);
        }
    }

    private void insertInstructionsToDisk(int id, SQLiteDatabase db, ArrayList<String> instructionsList)
    {
        String insertIngredientQuery = "INSERT INTO %s(recipe_id, instruction, count) VALUES(%s, '%s', %s)";
        for(int i = 0; i < INSTRUCTIONS_SIZE; i++)
        {
            String sql = String.format(insertIngredientQuery, TABLE_INSTRUCTIONS, id, instructionsList.get(i), i);
            db.execSQL(sql);
        }
    }

    private void thenShouldCallActionAndConsumerAndAddCompositeToDisposable(Action action, Consumer<ArrayList<Recipe>> consumer, String message)
            throws Exception
    {
        verify(consumer, times(1)).accept(any(ArrayList.class));
        verify(action, times(1)).run();

        List<Recipe> recipesFromDisk = getRecipesFromDisk();
        assertTrue(recipesFromDisk.isEmpty());

        assertEquals(message, ShadowToast.getTextOfLatestToast());

        assertEquals(disposables + 1, compositeDisposable.size());
    }

    private void thenShouldSaveRecipesCallActionAndConsumerAndDisposeObserver(Action action, Consumer<ArrayList<Recipe>> consumer,
            ResponseRecipes response, String message)
            throws Exception
    {
        verify(consumer, times(1)).accept(any(ArrayList.class));
        verify(action, times(1)).run();

        assertRecipesSavedInDisk(response);

        assertEquals(message, ShadowToast.getTextOfLatestToast());

        assertEquals(disposables + 1, compositeDisposable.size());
    }

    private void assertRecipesSavedInDisk(ResponseRecipes response)
    {
        List<Recipe> recipesFromDisk = getRecipesFromDisk();
        List<ResponseRecipe> recipes = response.getRecipeList();

        for(ResponseRecipe recipe : recipes)
        {
            Recipe recipeFromDisk = getRecipeFromDiskFromList(recipe.getTitle(), recipesFromDisk);
            assertEquals(recipe.getTitle(), recipeFromDisk.getTitle());
            assertEquals(recipe.getCategory(), recipeFromDisk.getCategory());
            assertEquals(recipe.getDescription(), recipeFromDisk.getDescription());
            assertEquals(recipe.getPreparationTime(), recipeFromDisk.getPreparationTime());
            assertEquals(recipe.getServings(), recipeFromDisk.getServings());
            int ingredientsSize = recipe.getIngredientList().size();
            assertEquals(ingredientsSize, recipeFromDisk.getIngredients().getIngredientsList().size());
            assertThat(recipe.getIngredientList().stream().map(ResponseIngredient::getQuantity).collect(Collectors.toList()),
                    containsInAnyOrder(recipeFromDisk.getIngredients().getIngredientsList().stream().map(Ingredient::getQuantity).toArray()));
            assertThat(recipe.getIngredientList().stream().map(ResponseIngredient::getMeasurement).collect(Collectors.toList()),
                    containsInAnyOrder(recipeFromDisk.getIngredients().getIngredientsList().stream().map(Ingredient::getMeasurement).toArray()));
            assertThat(recipe.getIngredientList().stream().map(ResponseIngredient::getIngredient).collect(Collectors.toList()),
                    containsInAnyOrder(recipeFromDisk.getIngredients().getIngredientsList().stream().map(Ingredient::getIngredient).toArray()));
            assertThat(recipe.getIngredientList().stream().map(ResponseIngredient::getComment).collect(Collectors.toList()),
                    containsInAnyOrder(recipeFromDisk.getIngredients().getIngredientsList().stream().map(Ingredient::getComment).toArray()));

            int instructionsSize = recipe.getInstructionList().size();
            assertEquals(instructionsSize, recipeFromDisk.getInstructions().getInstructionsList().size());
            assertThat(recipe.getInstructionList().stream().map(ResponseInstruction::getInstruction).collect(Collectors.toList()),
                    containsInAnyOrder(recipeFromDisk.getInstructions().getInstructionsList().toArray()));
        }
    }

    private Recipe getRecipeFromDiskFromList(String title, List<Recipe> recipesFromDisk)
    {
        List<Recipe> recipes = recipesFromDisk.stream().filter(recipe -> recipe.getTitle().equals(title)).collect(Collectors.toList());
        assertEquals(1, recipes.size());

        return recipes.get(0);
    }

    private List<Recipe> getRecipesFromDisk()
    {
        List<Recipe> list = new ArrayList<>();
        try(SQLiteDatabase db = dbHelperTest.getReadableDatabase())
        {
            try(Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s", TABLE_RECIPE), null))
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        int id = cursor.getInt(0);

                        list.add(new Recipe().setId(id)
                                .setTitle(cursor.getString(1))
                                .setCategory(Categories.getCategoriesMap().get(cursor.getInt(2))) // get the name from cache
                                .setPreparationTime(cursor.getInt(3))
                                .setDescription(cursor.getString(4))
                                .setServings(cursor.getInt(5))
                                .setIngredients(getIngredientsFromDisk(id))
                                .setInstructions(getInstructionsFromDisk(id)));
                    } while(cursor.moveToNext());
                }
            }
        }

        return list;
    }

    private Ingredients getIngredientsFromDisk(int id)
    {
        List<Ingredient> list = new ArrayList<>();

        try(SQLiteDatabase db = dbHelperTest.getReadableDatabase())
        {
            try(Cursor cursor = db.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s = ?", TABLE_INGREDIENTS, ColumnInstructions.recipe_id.name()),
                    new String[] { String.valueOf(id) }))
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        list.add(new Ingredient()
                                .setQuantity(cursor.getDouble(1))
                                .setMeasurement(cursor.getString(2))
                                .setIngredient(cursor.getString(3))
                                .setComment(cursor.getString(4)));
                    } while(cursor.moveToNext());
                }
            }
        }

        return new Ingredients("", list);
    }

    private Instructions getInstructionsFromDisk(int id)
    {
        List<String> list = new ArrayList<>();

        try(SQLiteDatabase db = dbHelperTest.getReadableDatabase())
        {
            try(Cursor cursor = db.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s = ?", TABLE_INSTRUCTIONS, ColumnInstructions.recipe_id.name()),
                    new String[] { String.valueOf(id) }))
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        list.add(cursor.getString(1));
                    } while(cursor.moveToNext());
                }
            }
        }

        return new Instructions("", list);
    }

    private void thenShouldHandleExceptnAndCallActionAndDisposeObserver(Action action, Consumer<ArrayList<Recipe>> consumer, String message) throws Exception
    {
        verify(consumer, times(0)).accept(any(ArrayList.class));
        verify(action, times(1)).run();

        assertEquals(ShadowToast.getTextOfLatestToast(), "Error retrieving recipes: " + message);

        assertEquals(disposables + 1, compositeDisposable.size());
    }
}
