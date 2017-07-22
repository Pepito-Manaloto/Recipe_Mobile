package com.aaron.recipe.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aaron.recipe.R;
import com.aaron.recipe.bean.Categories;
import com.aaron.recipe.bean.ResponseRecipe;
import com.aaron.recipe.bean.Settings;
import com.aaron.recipe.fragment.SettingsFragment;
import com.aaron.recipe.model.HttpClient;
import com.aaron.recipe.model.LogsManager;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class CategoriesRetrieverThread extends AsyncTask<Void, Void, String>
{
    public static final String CLASS_NAME = CategoriesRetrieverThread.class.getSimpleName();
    private Context context;
    private SettingsFragment settingsFragment;
    private String url;

    private static final ArrayList<HttpClient.Header> header = new ArrayList<>(0);

    static
    {
        header.add(new HttpClient.Header("Authorization", new String(Hex.encodeHex(DigestUtils.md5("aaron")))));
    }

    public CategoriesRetrieverThread(SettingsFragment settingsFragment, Settings settings)
    {
        this(settingsFragment.getActivity(), settings);
        this.settingsFragment = settingsFragment;
    }

    public CategoriesRetrieverThread(Context context, Settings settings)
    {
        this.context = context;

        if(settings != null && settings.getServerURL() != null && !settings.getServerURL().isEmpty())
        {
            this.url = "http://" + settings.getServerURL() + this.context.getString(R.string.url_resource_categories);
        }
        else
        {
            this.url = "http://" + this.context.getString(R.string.url_address_default) + this.context.getString(R.string.url_resource_categories);
        }
    }

    /**
     * Retrieves data from server (also save to local disk) then returns the data, encapsulated in the intent, to RecipeListFragment.
     */
    @Override
    protected String doInBackground(Void... arg0)
    {
        try
        {
            HttpClient httpClient = new HttpClient();
            ResponseRecipe response = httpClient.get(url, header);

            if(response.getStatusCode() == HttpURLConnection.HTTP_OK)
            {
                String responseBody = response.getBody();
                if(StringUtils.isNotBlank(responseBody)) // Response body empty
                {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    Categories.initCategories(jsonArray);

                    Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate Categories = " + Categories.getCategories());
                    LogsManager.addToLogs(CLASS_NAME + ": onCreate Categories = " + Categories.getCategories());

                    return String.valueOf(HttpURLConnection.HTTP_OK);
                }
                else
                {
                    Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate Error initializing categories, response empty. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
                    LogsManager.addToLogs(CLASS_NAME + ": onCreate Error initializing categories, response empty. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
                }
            }
            else
            {
                Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate Error initializing categories, response not 200. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
                LogsManager.addToLogs(CLASS_NAME + ": onCreate Error initializing categories, response not 200. responseText=" + response.getText() + " responseCode=" + response.getStatusCode());
            }
        }
        catch(IOException e)
        {
            Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate. Error retrieving categories. Error: " + e.getMessage());
            LogsManager.addToLogs(CLASS_NAME + ": onCreate. Error retrieving categories. Error: " + e.getMessage());
        }
        catch(JSONException e)
        {
            Log.d(LogsManager.TAG, CLASS_NAME + ": onCreate. Error parsing categories response. Error: " + e.getMessage());
            LogsManager.addToLogs(CLASS_NAME + ": onCreate. Error parsing categories response. Error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Removes the dialog from screen, shows the result of the operation on toast, and sets the isUpdating flag to false.
     */
    @Override
    public void onPostExecute(String message)
    {
        if(this.settingsFragment != null)
        {
            this.settingsFragment.updateCategoriesSpinner();
        }

        if(StringUtils.isBlank(message))
        {
            Toast.makeText(this.context, this.context.getString(R.string.error_retrieving_categories), Toast.LENGTH_LONG).show();
        }
    }
}