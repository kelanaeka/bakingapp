package com.example.kelanaeka.bakingapp;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

/**
 * Created by kelanaeka on 9/30/2017.
 */

public final class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();

    public interface Recipe {
        @GET("/topher/2017/May/59121517_baking/baking.json")
        Call<String> getRecipe();
    }

    public static String getResponseFromHttp(String url) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        Recipe recipe = retrofit.create(Recipe.class);

        Call<String> call = recipe.getRecipe();
        String recipeJson = call.execute().body();

        return recipeJson;
    }
}
