package com.example.kelanaeka.bakingapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kelanaeka on 10/1/2017.
 */

public class RecipeStepsFragment extends Fragment {
    private StepsListRecyclerviewAdapter stepsListRecyclerviewAdapter;
    public static final String ARG_ITEM_ID = "item_id";
    private RecipeData mItem;
    private static final String TAG = RecipeStepsFragment.class.getSimpleName();
    private static final String BT_TAG = "button";
    OnButtonClickListener mCallback;
    private RecyclerView stepsRv;
    private static final String STATE_KEY = "savedstate";
    private static final String DATA_KEY = "saveddata";
    int tagCount = 0;
    String recipeDataJson;

    public interface OnButtonClickListener {
        void onButtonClicked(int pos);
    }

    public RecipeStepsFragment() {
        //setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnButtonClickListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mItem = savedInstanceState.getParcelable(DATA_KEY);
            Parcelable savedState = savedInstanceState.getParcelable(STATE_KEY);
            stepsRv.getLayoutManager().onRestoreInstanceState(savedState);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            Log.v(TAG, "wkwk ok get saved state...");
            mItem = savedInstanceState.getParcelable(DATA_KEY);
        } else if (getArguments().containsKey(ARG_ITEM_ID)) {
            Log.v(TAG, "wkwk no saved state sorry...");
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            recipeDataJson = getArguments().getString(ARG_ITEM_ID);
            mItem = new Gson().fromJson(recipeDataJson, RecipeData.class);
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipe_steps, container, false);

        assert rootView != null;
        stepsRv = (RecyclerView) rootView;

        stepsListRecyclerviewAdapter = new StepsListRecyclerviewAdapter(getContext(), mItem);
        stepsRv.setAdapter(stepsListRecyclerviewAdapter);

        if(savedInstanceState != null){
            mItem = savedInstanceState.getParcelable(DATA_KEY);
            Parcelable savedState = savedInstanceState.getParcelable(STATE_KEY);
            stepsRv.getLayoutManager().onRestoreInstanceState(savedState);
        }

        return stepsRv;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "wkwk we're entering on pause...");
    }

    public class StepsListRecyclerviewAdapter extends RecyclerView.Adapter<StepsListRecyclerviewAdapter.ViewHolder>{

        private final RecipeData mItem;
        Context ctx;

        public StepsListRecyclerviewAdapter(Context ctx, RecipeData mItem) {
            this.mItem = mItem;
            this.ctx = ctx;
        }

        @Override
        public StepsListRecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.steps_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(mItem.getSteps(position).getThumbnailUrl().length() > 0)
                Picasso.with(ctx).load(mItem.getSteps(position).getThumbnailUrl()).into(holder.stepThumbView);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.stepThumbView.setImageDrawable(getResources().getDrawable(R.drawable.cook_48, null));
            } else
                holder.stepThumbView.setImageResource(R.drawable.cook_48);

            holder.stepsItem.setText(mItem.getSteps(position).getShortDescription());

            //for testing
            holder.stepsItem.setTag(BT_TAG + Integer.toString(tagCount));
            tagCount++;
            Log.v(TAG, holder.stepsItem.getTag().toString());
        }

        @Override
        public int getItemCount() {
            return mItem.getStepsLength();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public final View mView;

            @BindView(R.id.step_thumb) ImageView stepThumbView;
            @BindView(R.id.steps_item) Button stepsItem;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                ButterKnife.bind(this, mView);
                stepsItem.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                mCallback.onButtonClicked(pos);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Parcelable savedState = stepsRv.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_KEY, savedState);
        outState.putParcelable(DATA_KEY, mItem);

        Log.v(TAG, "wkwk saving state...");

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            Log.v(TAG, "wkwk restoring state...");
            mItem = savedInstanceState.getParcelable(DATA_KEY);
            Parcelable savedState = savedInstanceState.getParcelable(STATE_KEY);
            stepsRv.getLayoutManager().onRestoreInstanceState(savedState);
        }
    }
}
