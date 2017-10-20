package com.example.kelanaeka.bakingapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kelanaeka on 10/7/2017.
 */

public class RecipeStepsActivity extends AppCompatActivity{
    private StepsRecyclerViewAdapter stepsAdapter;
    public static final String ARG_ITEM_ID = "item_id";
    RecipeData mItem;
    String recipeDataJson;

    @BindView(R.id.recipe_list) View recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);
        recipeDataJson = getIntent().getStringExtra(ARG_ITEM_ID);
        mItem = new Gson().fromJson(recipeDataJson, RecipeData.class);

        //make sure recyclerView variable is not null
        assert recyclerView != null;
        stepsAdapter = new StepsRecyclerViewAdapter(this, mItem);
        ((RecyclerView) recyclerView).setAdapter(stepsAdapter);
    }

    public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.ViewHolder>{

        private final RecipeData mItem;
        Context ctx;

        public StepsRecyclerViewAdapter(Context ctx, RecipeData mItem){
            this.ctx = ctx;
            this.mItem = mItem;
        }

        @Override
        public StepsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.steps_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(StepsRecyclerViewAdapter.ViewHolder holder, int position) {
            if(mItem.getSteps(position).getThumbnailUrl().length() > 0)
                Picasso.with(ctx).load(mItem.getSteps(position).getThumbnailUrl()).into(holder.stepThumbView);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.stepThumbView.setImageDrawable(getResources().getDrawable(R.drawable.cook_48, null));
            } else
                holder.stepThumbView.setImageResource(R.drawable.cook_48);

            holder.stepsItem.setText(mItem.getSteps(position).getShortDescription());
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

                Bundle args = new Bundle();
                args.putInt(StepsContent.ARG_STEP_ID, mItem.getSteps(pos).getId());
                args.putString(StepsContent.ARG_S_DESC, mItem.getSteps(pos).getShortDescription());
                args.putString(StepsContent.ARG_DESC, mItem.getSteps(pos).getDescription());
                args.putString(StepsContent.ARG_VID_URL, mItem.getSteps(pos).getVideoUrl());
                args.putString(StepsContent.ARG_STEP_T, mItem.getSteps(pos).getThumbnailUrl());
                args.putInt(StepsContent.ARG_MAX_STEP, mItem.getStepsLength());
                args.putBoolean(StepsContent.ARG_TWO_PANE, true);

                StepsContent stepsContent = new StepsContent();
                stepsContent.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, stepsContent)
                        .commit();
            }
        }
    }
}
