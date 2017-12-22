package com.aaron.recipe.model;

/**
 * Handles the application logging(different from LogCat).
 */
public class LogsManager
{
    public static final String TAG = "Recipe";
    private static final StringBuilder logs = new StringBuilder();

    /**
     * Adds the given message to the string builder logs.
     *
     * @param text the message to add
     */
    public static void addToLogs(final String text)
    {
        logs.append(text).append("\n");
    }

    /**
     * Returns the StringBuilder object into a single String
     *
     * @return String
     */
    public String getLogs()
    {
        return logs.toString().substring(0, logs.length() - 1); // Removes trailing '\n' character
    }

    /**
     * Returns the filtered StringBuilder String
     *
     * @param keyWord the word used in filtering
     */
    public String getLogs(final String keyWord)
    {
        String lineSeparator = System.lineSeparator();
        String[] lines = logs.toString().split(lineSeparator);
        StringBuilder sb = new StringBuilder();

        for(String line : lines)
        {
            if(line.contains(keyWord))
            {
                sb.append(line).append(lineSeparator);
            }
        }

        return sb.toString();
    }

    /**
     * Saves the logs to the local disk.
     *
     * @return true on success, else false
     */
    public boolean saveToDisk()
    {
        /**
         * TODO: Implements functionalities
         */
        return false;
    }
}
