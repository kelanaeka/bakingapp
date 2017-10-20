package com.example.kelanaeka.bakingapp.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kelanaeka on 10/10/2017.
 */

public class RecipeContract {
    public static final String AUTHORITY = "com.example.kelanaeka.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_RECIPE = "recipe";
    public static final long INVALID_RECIPE_ID = -1;

    public static final class RecipeEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

        public static final String TABLE_NAME = "recipe";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_JSON = "json";
    }

}
