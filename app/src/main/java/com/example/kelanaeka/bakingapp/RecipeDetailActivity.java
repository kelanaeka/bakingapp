package com.example.kelanaeka.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeStepsFragment.OnButtonClickListener, StepsContent.OnButtonClickListener{
    RecipeData mItem;
    String recipeDataJson;
    NestedScrollView recipeDetailNscv;
    public static final String ARG_ITEM_ID = "item_id";
    private static final String STEPS_TAG = "stepstag";
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    RecipeDetailFragment fragment;
    RecipeStepsFragment stepsFragment;
    private static final String FRAGMENT_KEY = "fragmentkey";
    private static final String STEPSFRAGMENT_KEY = "stepsfragmentkey";

    @BindView(R.id.detail_toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recipeDataJson = getIntent().getStringExtra(ARG_ITEM_ID);
        mItem = new Gson().fromJson(recipeDataJson, RecipeData.class);

        if(appBarLayout != null)
            appBarLayout.setTitle(mItem.getRecipeName());

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        Bundle arguments = new Bundle();
        arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, recipeDataJson);

        if (savedInstanceState == null) {
            Log.v(TAG, "there is no saved instance");
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            /*fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();*/

            stepsFragment = new RecipeStepsFragment();
            stepsFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_steps, stepsFragment, STEPS_TAG)
                    .commit();

        } /*else {
            Log.v(TAG, "wkwk activity get saved state...");
            if(savedInstanceState.containsKey(FRAGMENT_KEY)) {
                Log.v(TAG, "Saved instance: " + FRAGMENT_KEY);
                fragment = (RecipeDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            }

            if(savedInstanceState.containsKey(STEPSFRAGMENT_KEY)) {
                Log.v(TAG, "Saved instance: " + STEPSFRAGMENT_KEY);
                stepsFragment = (RecipeStepsFragment) getSupportFragmentManager().getFragment(savedInstanceState, STEPSFRAGMENT_KEY);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_steps, stepsFragment, STEPS_TAG)
                        .commit();
            }
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onButtonClicked(int pos) {
        Bundle args = new Bundle();
        args.putInt(StepsContent.ARG_STEP_ID, mItem.getSteps(pos).getId());
        args.putString(StepsContent.ARG_S_DESC, mItem.getSteps(pos).getShortDescription());
        args.putString(StepsContent.ARG_DESC, mItem.getSteps(pos).getDescription());
        args.putString(StepsContent.ARG_VID_URL, mItem.getSteps(pos).getVideoUrl());
        args.putString(StepsContent.ARG_STEP_T, mItem.getSteps(pos).getThumbnailUrl());
        args.putInt(StepsContent.ARG_MAX_STEP, mItem.getStepsLength());

        StepsContent stepsContent = new StepsContent();
        stepsContent.setArguments(args);

        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_detail_container, stepsContent)
                .commit();*/

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(STEPS_TAG);
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onPrevClicked(int stepsId) {
        int pos = stepsId - 1;
        Bundle args = new Bundle();
        args.putInt(StepsContent.ARG_STEP_ID, mItem.getSteps(pos).getId());
        args.putString(StepsContent.ARG_S_DESC, mItem.getSteps(pos).getShortDescription());
        args.putString(StepsContent.ARG_DESC, mItem.getSteps(pos).getDescription());
        args.putString(StepsContent.ARG_VID_URL, mItem.getSteps(pos).getVideoUrl());
        args.putString(StepsContent.ARG_STEP_T, mItem.getSteps(pos).getThumbnailUrl());
        args.putInt(StepsContent.ARG_MAX_STEP, mItem.getStepsLength());

        StepsContent stepsContent = new StepsContent();
        stepsContent.setArguments(args);

        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_detail_container, stepsContent)
                .commit();*/
    }

    @Override
    public void onNextClicked(int stepsId) {
        int pos = stepsId + 1;
        Bundle args = new Bundle();
        args.putInt(StepsContent.ARG_STEP_ID, mItem.getSteps(pos).getId());
        args.putString(StepsContent.ARG_S_DESC, mItem.getSteps(pos).getShortDescription());
        args.putString(StepsContent.ARG_DESC, mItem.getSteps(pos).getDescription());
        args.putString(StepsContent.ARG_VID_URL, mItem.getSteps(pos).getVideoUrl());
        args.putString(StepsContent.ARG_STEP_T, mItem.getSteps(pos).getThumbnailUrl());
        args.putInt(StepsContent.ARG_MAX_STEP, mItem.getStepsLength());

        StepsContent stepsContent = new StepsContent();
        stepsContent.setArguments(args);

        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_detail_container, stepsContent)
                .commit();*/
    }

    @Override
    public void onStepsClicked() {
        Bundle arguments = new Bundle();
        arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, recipeDataJson);

        /*fragment = new RecipeDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_detail_container, fragment)
                .commit();*/

        stepsFragment = new RecipeStepsFragment();
        stepsFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.recipe_detail_steps, stepsFragment, STEPS_TAG)
                .commit();
    }

/*    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.v(TAG, "wkwk detail activity save state...");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(fragment != null) {
            Log.v(TAG, "wkwk detail activity save fragment state...");
            getSupportFragmentManager().putFragment(outState, FRAGMENT_KEY, fragment);
        }

        if(stepsFragment != null) {
            Log.v(TAG, "wkwk detail activity save steps fragment state...");
            getSupportFragmentManager().putFragment(outState, STEPSFRAGMENT_KEY, stepsFragment);
        }
    }*/
}
