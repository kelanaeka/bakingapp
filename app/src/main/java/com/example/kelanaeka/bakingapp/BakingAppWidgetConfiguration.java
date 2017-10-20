package com.example.kelanaeka.bakingapp;

import android.app.Activity;
import android.app.IntentService;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kelanaeka.bakingapp.provider.RecipeContract;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kelanaeka.bakingapp.provider.RecipeContract.BASE_CONTENT_URI;
import static com.example.kelanaeka.bakingapp.provider.RecipeContract.PATH_RECIPE;

/**
 * Created by kelanaeka on 10/11/2017.
 */

public class BakingAppWidgetConfiguration extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    ArrayAdapter<String> aAdapter;
    ArrayList<RecipeData> recipeDataArrayList = new ArrayList<>();
    private int mBakingAppWidgetId;
    private static final int RECIPE_LOADER_ID = 100;
    private static final String TAG = BakingAppWidgetConfiguration.class.getSimpleName();

    @BindView(R.id.recipe_lv) ListView recipeListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_configuration);
        ButterKnife.bind(this);

        mBakingAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
            mBakingAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if(mBakingAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            finish();

        aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
        recipeListView.setAdapter(aAdapter);

        getLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);
        setResult(RESULT_CANCELED);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri recipeUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();
        return new CursorLoader(this, recipeUri, null, null, null, RecipeContract.RecipeEntry._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null)
            Log.v(TAG, "Cursor size: " + data.getCount());

        int i = 0;
        while(data.moveToNext()){
            int jsonIndex = data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_JSON);
            String jsonContent = data.getString(jsonIndex);
            RecipeData recipeData = new Gson().fromJson(jsonContent, RecipeData.class);
            aAdapter.add(recipeData.getRecipeName());
            recipeDataArrayList.add(recipeData);
        }

        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecipeData recipeData = recipeDataArrayList.get(position);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.baking_app_widget);
                views.setTextViewText(R.id.recipe_title_tv, recipeData.getRecipeName());

                String ingredients = "";
                for(int i = 0; i < recipeData.getIngredientsLength(); i++) {
                    ingredients = ingredients + Integer.toString(recipeData.getIngredients(i).getQty()) + " " +
                            recipeData.getIngredients(i).getMeasure() + " " +
                            recipeData.getIngredients(i).getIngredient() + "\n";
                }

                views.setTextViewText(R.id.recipe_list_tv, ingredients);

                Intent intent = new Intent(getApplicationContext(), RecipeListActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                views.setOnClickPendingIntent(R.id.bakingapp_widget_launcher, pendingIntent);
                views.setOnClickPendingIntent(R.id.recipe_title_tv, pendingIntent);

                appWidgetManager.updateAppWidget(mBakingAppWidgetId, views);
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mBakingAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
