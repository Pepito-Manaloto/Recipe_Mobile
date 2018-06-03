package com.aaron.recipe.model;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class LogsManagerTest
{
    private LogsManager logsManager = new LogsManager();

    @Test
    public void givenListOfLogsAndSearchWord_whenGetLogs_thenShouldReturnAllLogsContainingTheSearchWord() throws IllegalAccessException
    {
        StringBuilder listOfLogs = (StringBuilder) FieldUtils.readStaticField(LogsManager.class, "logs", true);
        List<String> list = givenListOfLogs();
        listOfLogs.append(list.stream().collect(Collectors.joining(System.lineSeparator())));
        String searchWord = "Recipe";

        String logs = logsManager.getLogs(searchWord);

        String expected = list.get(0)
                .concat(System.lineSeparator())
                .concat(list.get(3))
                .concat(System.lineSeparator())
                .concat(list.get(6))
                .concat(System.lineSeparator());

        assertEquals(expected, logs);
    }

    @Test
    public void givenListOfLogs_whenGetLogs_thenShouldReturnAllLogs() throws IllegalAccessException
    {
        StringBuilder listOfLogs = (StringBuilder) FieldUtils.readStaticField(LogsManager.class, "logs", true);
        List<String> list = givenListOfLogs();
        listOfLogs.append(list.stream().collect(Collectors.joining(System.lineSeparator())));

        String logs = logsManager.getLogs();

        assertEquals(listOfLogs.toString(), logs);
    }

    private List<String> givenListOfLogs()
    {
        List<String> list = new ArrayList<>();
        list.add("05-08 14:37:39.183 4178-4178/com.aaron.recipe D/Recipe: RecipeListFragment: onCreateView.");
        list.add("05-08 14:37:38.152 4178-4178/? D/NetworkSecurityConfig: No Network Security Config specified, using platform default");
        list.add("05-08 14:37:37.396 4178-4178/? W/zygote: Unexpected CPU variant for X86 using defaults: x86");
        list.add("05-08 14:37:39.190 4178-4178/com.aaron.recipe D/Recipe: RecipeListFragment: onResume");
        list.add("05-08 14:38:58.814 1713-1735/system_process E/memtrack: Couldn't load memtrack module");
        list.add("05-08 14:37:39.534 4178-4183/com.aaron.recipe I/zygote: After code cache collection, code=39KB, data=42KB");
        list.add("05-08 14:37:39.183 4178-4178/com.aaron.recipe D/Recipe: RecipeListFragment: onCreateView.");

        return list;
    }
}
