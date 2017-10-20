package com.example.kelanaeka.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kelanaeka on 10/3/2017.
 */

public class StepsContent extends Fragment{
    public static final String ARG_STEP_ID = "step_id";
    public static final String ARG_S_DESC = "s_desc";
    public static final String ARG_DESC = "desc";
    public static final String ARG_VID_URL = "vid_url";
    public static final String ARG_STEP_T = "step_t";
    public static final String ARG_MAX_STEP = "max_step";
    public static final String ARG_TWO_PANE = "two_pane";
    private static final String TAG = StepsContent.class.getSimpleName();
    public int stepsId;
    public String shortDesc;
    public String desc;
    public String vidUrl;
    public String thumbUrl;
    public int maxSteps;
    private SimpleExoPlayer mExoPlayer;
    private boolean mTwoPane = false;
    OnButtonClickListener mCallback;
    Uri videoUri = null;
    long pos;
    private static final String EXO_STATE_KEY = "savedstate";

    @BindView(R.id.step_video) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.step_description) TextView descTextView;
    @BindView(R.id.next_bt) Button nextBt;
    @BindView(R.id.prev_bt) Button prevBt;
    @BindView(R.id.steps_bt) Button stepsBt;

    public interface OnButtonClickListener {
        void onPrevClicked(int stepsId);
        void onNextClicked(int stepsId);
        void onStepsClicked();
    }

    public StepsContent(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(getArguments().containsKey(ARG_TWO_PANE)) {
            mTwoPane = getArguments().getBoolean(ARG_TWO_PANE);
            Log.v(TAG, "two pane UI...");
        }

        if(!mTwoPane) {
            try {
                mCallback = (OnButtonClickListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(ARG_STEP_ID))
            stepsId = getArguments().getInt(ARG_STEP_ID);

        if(getArguments().containsKey(ARG_S_DESC))
            shortDesc = getArguments().getString(ARG_S_DESC);

        if(getArguments().containsKey(ARG_DESC))
            desc = getArguments().getString(ARG_DESC);

        if(getArguments().containsKey(ARG_VID_URL))
            vidUrl = getArguments().getString(ARG_VID_URL);

        if(getArguments().containsKey(ARG_STEP_T))
            thumbUrl = getArguments().getString(ARG_STEP_T);

        if(getArguments().containsKey(ARG_MAX_STEP))
            maxSteps = getArguments().getInt(ARG_MAX_STEP);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.steps_content, container, false);

        ButterKnife.bind(this, rootView);

        if(mTwoPane){
            nextBt.setVisibility(View.INVISIBLE);
            prevBt.setVisibility(View.INVISIBLE);
            stepsBt.setVisibility(View.INVISIBLE);

        }

        if(savedInstanceState != null)
            pos = savedInstanceState.getLong(EXO_STATE_KEY);

        if(stepsId < 1)
            prevBt.setVisibility(View.INVISIBLE);

        if(stepsId == maxSteps - 1)
            nextBt.setVisibility(View.INVISIBLE);

        prevBt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mCallback.onPrevClicked(stepsId);
            }
        });

        nextBt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mCallback.onNextClicked(stepsId);
            }
        });

        stepsBt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mCallback.onStepsClicked();
            }
        });

        descTextView.setText(desc);


        try {
            URL videoUrl = new URL(vidUrl);
            videoUri = Uri.parse(videoUrl.toURI().toString());
        } catch (MalformedURLException e    ) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        initializePlayer(videoUri);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mExoPlayer  != null){
            pos = mExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(videoUri != null){
            initializePlayer(videoUri);
        }
    }

    private void initializePlayer(Uri mVideoUri){
        if(mExoPlayer == null){
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            String userAgent = Util.getUserAgent(getContext(), "RecipeSteps");
            MediaSource mediaSource = new ExtractorMediaSource(mVideoUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            if(pos != C.TIME_UNSET)
                mExoPlayer.seekTo(pos);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }
    }

    private void releasePlayer(){
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXO_STATE_KEY, pos);
    }

}
