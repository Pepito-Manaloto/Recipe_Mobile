package com.aaron.recipe.model;

import android.content.Context;

import com.aaron.recipe.R;
import com.aaron.recipe.response.ResponseCategory;
import com.aaron.recipe.response.ResponseRecipes;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Aaron on 9/24/2016.
 */
public class HttpClient
{
    private static final String CLASS_NAME = HttpClient.class.getSimpleName();
    private static final int DEFAUT_TIMEOUT = 10;
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_VALUE = new String(Hex.encodeHex(DigestUtils.md5("aaron")));
    private static final String BASE_URL = "http://%s/Recipe/web_service/";

    private static OkHttpClient okHttpClient;
    private static RecipeService service;

    public HttpClient(Context context)
    {
        initializeRetrofit(context.getString(R.string.url_address_default));
    }

    private void initializeRetrofit(String hostname)
    {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAUT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAUT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAUT_TIMEOUT, TimeUnit.SECONDS)
                .pingInterval(DEFAUT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(this::authorizationHeaderInterceptor)
                .build();

        reinitializeRetrofit(hostname);
    }

    /**
     * Sets the retrofit http client.
     */
    public static void reinitializeRetrofit(String hostname)
    {
        String baseUrl = String.format(BASE_URL, hostname);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient).addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        service = retrofit.create(RecipeService.class);

        LogsManager.log(CLASS_NAME, "reinitializeRetrofit", "New BaseUrl=" + baseUrl);
    }

    private okhttp3.Response authorizationHeaderInterceptor(Interceptor.Chain chain) throws IOException
    {
        Request request = chain.request().newBuilder()
                .addHeader(AUTHORIZATION, AUTHORIZATION_VALUE)
                .build();

        return chain.proceed(request);
    }

    /**
     * Performs a GET request to /categories.
     *
     * @return Single<List<ResponseCategory>>
     */
    public Single<List<ResponseCategory>> getCategories()
    {
        return service.getCategories();
    }

    /**
     * Performs a GET request to /recipes.
     *
     * @param lastUpdated the last updated query parameter
     * @return Single<ResponseRecipes>
     */
    public Single<ResponseRecipes> getRecipes(String lastUpdated)
    {
        return service.getRecipes(lastUpdated);
    }
}
