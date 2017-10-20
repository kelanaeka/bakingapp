package com.example.kelanaeka.bakingapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by kelanaeka on 10/10/2017.
 */

public class RecipeContentProvider extends ContentProvider{
    public static final int RECIPE_URI = 100;
    public static final int RECIPE_URI_ID = 111;
    private RecipeDbHelper mRecipeDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = RecipeContentProvider.class.getSimpleName();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPE, RECIPE_URI);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPE + "/#", RECIPE_URI_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        mRecipeDbHelper = new RecipeDbHelper(ctx);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mRecipeDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match){
            case RECIPE_URI:
                returnCursor = db.query(
                        RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case RECIPE_URI_ID:
                String id = uri.getPathSegments().get(1);
                returnCursor = db.query(
                        RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case RECIPE_URI:
                long id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
                if(id > 0)
                    returnUri = ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI, id);
                else
                    throw new android.database.SQLException("Failed to insert a row into " + uri);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int recipeDeleted;
        switch(match){
            case RECIPE_URI:
                recipeDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(recipeDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return recipeDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int insertCount = 0;

        switch(match){
            case RECIPE_URI:
                db.beginTransaction();
                for(ContentValues value:values){
                    long id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, value);
                    if(id > 0)
                        insertCount++;
                    else
                        throw new android.database.SQLException("Failed to insert a row into " + uri);
                }

                db.setTransactionSuccessful();
                db.endTransaction();

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return insertCount;
    }
}
