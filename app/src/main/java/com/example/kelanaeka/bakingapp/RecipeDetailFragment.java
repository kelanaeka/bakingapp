package com.example.kelanaeka.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_TWO_PANE = "two_pane";
    private boolean mTwoPane = false;
    private RecipeData mItem;
    OnButtonClickListener mCallback;
    private static final String STATE_KEY = "savedstate";

    @BindView(R.id.view_steps_bt) Button button;
    @BindView(R.id.recipe_title_tv) TextView recipeTitleTv;
    @BindView(R.id.recipe_detail) TextView ingredientTv;

    public interface OnButtonClickListener {
        void onButtonClicked(RecipeData mItem);
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(getArguments().containsKey(ARG_TWO_PANE))
            mTwoPane = getArguments().getBoolean(ARG_TWO_PANE);

        if(mTwoPane) {
            try {
                mCallback = (OnButtonClickListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String recipeDataJson = getArguments().getString(ARG_ITEM_ID);
            mItem = new Gson().fromJson(recipeDataJson, RecipeData.class);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (mItem != null) {
            if(mTwoPane) {
                recipeTitleTv.setVisibility(View.VISIBLE);
                recipeTitleTv.setText(mItem.getRecipeName());

                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onButtonClicked(mItem);
                    }
                });

            }

            String serving = "SERVINGS: " + mItem.getServings() + "\n\n";
            String ingredient = "INGREDIENTS:\n";
            for(int i = 0;i < mItem.getIngredientsLength();i++){
                ingredient = ingredient + Integer.toString(mItem.getIngredients(i).getQty()) + " ";
                ingredient = ingredient + mItem.getIngredients(i).getMeasure() + " ";
                ingredient = ingredient + mItem.getIngredients(i).getIngredient() + "\n";
            }

            ingredientTv.setText(serving + ingredient);
        }

        return rootView;
    }

}
