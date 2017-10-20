package com.example.kelanaeka.bakingapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.kelanaeka.bakingapp.provider.RecipeContract;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity implements RecipeDetailFragment.OnButtonClickListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final int ID_LOADER = 22;
    private SimpleItemRecyclerViewAdapter recipeAdapter;
    private String recipeDataJson;
    private ArrayList<RecipeData> recipeDataList;
    private static final String TAG = RecipeListActivity.class.getSimpleName();
    private static final String STATE_KEY = "savedstate";
    private static final String DATA_KEY = "saveddata";
    private static final String LISTDATA_KEY = "savedlistdata";

    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingPb;
    @BindView(R.id.tv_error_message_display) TextView mErrorTv;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.recipe_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);
        mErrorTv.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
        }

        //make sure recyclerView variable is not null
        assert recyclerView != null;

        recipeAdapter = new SimpleItemRecyclerViewAdapter();
        ((RecyclerView) recyclerView).setAdapter(recipeAdapter);

        if(savedInstanceState != null && savedInstanceState.containsKey(LISTDATA_KEY)) {
            //recipeDataJson = savedInstanceState.getString(DATA_KEY);
            recipeDataList = savedInstanceState.getParcelableArrayList(LISTDATA_KEY);
            //ArrayList<RecipeData> recipeDataList = jsonToRecipeData(recipeDataJson);
            recipeAdapter.setRecipeData(recipeDataList);
        } else {
            loadRecipeData();
        }
    }

    @Override
    public void onButtonClicked(RecipeData mItem) {
        Intent intent = new Intent(this, RecipeStepsActivity.class);
        intent.putExtra(RecipeStepsActivity.ARG_ITEM_ID, new Gson().toJson(mItem));
        startActivity(intent);
    }

    //private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    //    ArrayList<RecipeData> recipeDataList = new ArrayList<RecipeData>();
    //    loadRecipeData();

    //    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter());
    //}

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<RecipeData> mValues;

        public SimpleItemRecyclerViewAdapter() {
        }

        public void setRecipeData(ArrayList<RecipeData> items) {
            mValues = items;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(Integer.toString(mValues.get(position).getRecipeId()));
            holder.mContentView.setText(mValues.get(position).getRecipeName());

            if(mValues.get(position).getImageUrl().length() > 0)
                Picasso.with(getApplicationContext()).load(mValues.get(position).getImageUrl()).into(holder.mImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, new Gson().toJson(holder.mItem));
                        arguments.putBoolean(RecipeDetailFragment.ARG_TWO_PANE, mTwoPane);

                        RecipeDetailFragment fragment = new RecipeDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.recipe_detail_container, fragment)
                                .commit();


                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeDetailActivity.class);
                        intent.putExtra(RecipeDetailActivity.ARG_ITEM_ID, new Gson().toJson(holder.mItem));
                        //Integer.toString(holder.mItem.getRecipeId())
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mValues == null) return 0;
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public RecipeData mItem;

            @BindView(R.id.id) TextView mIdView;
            @BindView(R.id.content) TextView mContentView;
            @BindView(R.id.recipe_pict) ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                ButterKnife.bind(this, mView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public void loadRecipeData(){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> recipeLoader = loaderManager.getLoader(ID_LOADER);
        if(recipeLoader == null) {
            loaderManager.initLoader(ID_LOADER, null, recipeDataLoader);
        } else {
            loaderManager.destroyLoader(ID_LOADER);
            loaderManager.restartLoader(ID_LOADER, null, recipeDataLoader);
        }
    }

    private LoaderManager.LoaderCallbacks<String> recipeDataLoader =
            new LoaderManager.LoaderCallbacks<String>() {
                @Override
                public Loader<String> onCreateLoader(int id, Bundle args) {
                    return new AsyncTaskLoader<String>(getApplicationContext()) {
                        String recipeDataStr;

                        @Override
                        protected void onStartLoading() {
                            mLoadingPb.setVisibility(View.VISIBLE);

                            if(recipeDataStr != null)
                                deliverResult(recipeDataStr);
                            else
                                forceLoad();
                        }

                        @Override
                        public String loadInBackground() {
                            String recipeUrl = getString(R.string.recipe_url);

                            try {
                                return NetworkUtil.getResponseFromHttp(recipeUrl);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        public void deliverResult(String data) {
                            mLoadingPb.setVisibility(View.INVISIBLE);
                            recipeDataStr = data;
                            super.deliverResult(data);
                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<String> loader, String data) {
                    mLoadingPb.setVisibility(View.INVISIBLE);
                    if(data != null) {
                        recipeDataJson = data;
                        recipeDataList = jsonToRecipeData(data);
                        recipeAdapter.setRecipeData(recipeDataList);

                        //insert into db
                        if(recipeDataList.size() > 0) {
                            long insertCount = 0;
                            int deleteCount = 0;
                            try {
                                //empty table
                                deleteCount = getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI, null, null);
                                Log.v(TAG, "Records deleted: " + deleteCount);

                                //insert
                                int i = 0;
                                Gson gsonHandler = new Gson();
                                ContentValues[] valueArray = new ContentValues[recipeDataList.size()];
                                for(RecipeData recipeData:recipeDataList) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(RecipeContract.RecipeEntry.COLUMN_JSON, gsonHandler.toJson(recipeData));
                                    valueArray[i] = contentValues;
                                    i++;
                                }

                                insertCount = getContentResolver().bulkInsert(RecipeContract.RecipeEntry.CONTENT_URI, valueArray);
                                Log.v(TAG, "Number of record inserted: " + insertCount);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    } else {
                        mErrorTv.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoaderReset(Loader<String> loader) {

                }

            };

    private ArrayList<RecipeData> jsonToRecipeData(String data) {
        try {
            ArrayList<RecipeData> tempRecipeDataList = new ArrayList<>();
            JSONArray recipeDataJsonArray = new JSONArray(data);
            for(int i = 0;i < recipeDataJsonArray.length();i++){
                RecipeData tempRecipeData = new RecipeData();
                tempRecipeData.setRecipeId(recipeDataJsonArray.getJSONObject(i).getInt("id"));
                tempRecipeData.setRecipeName(recipeDataJsonArray.getJSONObject(i).getString("name"));
                tempRecipeData.setServings(recipeDataJsonArray.getJSONObject(i).getInt("servings"));
                tempRecipeData.setImageUrl(recipeDataJsonArray.getJSONObject(i).getString("image"));

                JSONArray ingredients = recipeDataJsonArray.getJSONObject(i).getJSONArray("ingredients");
                ArrayList<Ingredients> tempIngredientArrayList = new ArrayList<>();
                for(int j = 0;j < ingredients.length();j++){
                    int qty = ingredients.getJSONObject(j).getInt("quantity");
                    String measure = ingredients.getJSONObject(j).getString("measure");
                    String ingredient = ingredients.getJSONObject(j).getString("ingredient");
                    Ingredients tempIngredients = new Ingredients(qty, measure, ingredient);
                    tempIngredientArrayList.add(tempIngredients);
                }
                tempRecipeData.setIngredients(tempIngredientArrayList);

                JSONArray steps = recipeDataJsonArray.getJSONObject(i).getJSONArray("steps");
                ArrayList<Steps> tempStepsArrayList = new ArrayList<>();
                for(int k = 0;k < steps.length();k++){
                    int id = steps.getJSONObject(k).getInt("id");
                    String shortDescription = steps.getJSONObject(k).getString("shortDescription");
                    String description = steps.getJSONObject(k).getString("description");
                    String videoUrl = steps.getJSONObject(k).getString("videoURL");
                    String thumbnailUrl = steps.getJSONObject(k).getString("thumbnailURL");
                    Steps tempSteps = new Steps(id, shortDescription, description, videoUrl, thumbnailUrl);
                    tempStepsArrayList.add(tempSteps);
                }
                tempRecipeData.setSteps(tempStepsArrayList);

                tempRecipeDataList.add(tempRecipeData);
            }
            return tempRecipeDataList;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Parcelable savedState = ((RecyclerView) recyclerView).getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_KEY, savedState);
        outState.putString(DATA_KEY, recipeDataJson);
        outState.putParcelableArrayList(LISTDATA_KEY, recipeDataList);

        Log.v(TAG, "wkwk mainactivity state saved...");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Parcelable savedState = savedInstanceState.getParcelable(STATE_KEY);
        ((RecyclerView) recyclerView).getLayoutManager().onRestoreInstanceState(savedState);
    }
}

