package com.example.android.bakingapp.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Step;
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

public class StepFragment extends Fragment {

    private static final String TAG = StepFragment.class.getSimpleName();

    // Final Strings to store state information about the list of images and list index
    public static final String LAST_STEP_ID = "last_step_id";
    public static final String LIST_INDEX = "list_index";
    private static final String DESCRIPTION = "description";
    private static final String VIDEO_URL = "video_url";


    private TextView mDescriptionTextView;

    private Button mNextButton;

    private Button mPrevButton;

    private SimpleExoPlayer mExoPlayer;
    
    private SimpleExoPlayerView mPlayerView;

    private CardView videoCardView;

    //step data items
    private String mVideo_url;

    private int mListIndex;

    private Integer mLastStepId;

    private String mDescription;

    private boolean mTwoPane;

    // Define a new interface OnNextClickListener that triggers a next step callback in the host activity
    OnNextClickListener mNextCallback;

    // Define a new interface OnPrevClickListener that triggers a previous step callback in the host activity
    OnPrevClickListener mPrevCallback;

    // OnNextClickListener interface, calls a method in the host activity named onNextSelected
    public interface OnNextClickListener {
        void onNextSelected(int nextStepId);
    }

    // OnPrevClickListener interface, calls a method in the host activity named onPrevSelected
    public interface OnPrevClickListener {
        void onPrevSelected(int prevStepId);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mNextCallback = (OnNextClickListener) context;
            mPrevCallback = (OnPrevClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnNextClickListener and OnPrevClickListener");
        }
    }

    public StepFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Load the saved state (the list of images and list index) if there is one
        if(savedInstanceState != null) {
            mTwoPane = savedInstanceState.getBoolean("PANE");

            if (mTwoPane) {
                mListIndex = savedInstanceState.getInt(LIST_INDEX);
                mDescription = savedInstanceState.getString(DESCRIPTION);
                mVideo_url = savedInstanceState.getString(VIDEO_URL);
            } else {
                mLastStepId = savedInstanceState.getInt(LAST_STEP_ID);
                mListIndex = savedInstanceState.getInt(LIST_INDEX);
                mDescription = savedInstanceState.getString(DESCRIPTION);
                mVideo_url = savedInstanceState.getString(VIDEO_URL);
            }

        }

        final View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        // Initialize the player view.
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        mDescriptionTextView = (TextView) rootView.findViewById(R.id.tv_description);

        mNextButton = (Button) rootView.findViewById(R.id.btn_next_step);

        mPrevButton = (Button) rootView.findViewById(R.id.btn_previous_step);

        mDescriptionTextView.setText(mDescription);

        videoCardView = rootView.findViewById(R.id.media_card);

        initializePlayer(mVideo_url);

        if(mLastStepId != null){

            mTwoPane = false;

            initButtons(mListIndex, mLastStepId);

            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Increment position as long as the index remains <= the size of the image ids list
                    if(mListIndex < mLastStepId) {
                        mListIndex++;
                    } else {
                        // The end of list has been reached, so return to beginning index
                        mListIndex = 0;
                    }
                    // Set the image resource to the new list item
                    mNextCallback.onNextSelected(mListIndex);

                }
            });

            mPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Increment position as long as the index remains <= the size of the image ids list
                    if(mListIndex <= mLastStepId) {
                        mListIndex--;
                    } else if (mListIndex < 0){
                        // The end of list has been reached, so return to beginning index
                        mListIndex = 0;
                    }
                    // Set the image resource to the new list item
                    mPrevCallback.onPrevSelected(mListIndex);
                }
            });

        } else {

            disableNavigationButton();
            Log.v(TAG, "This fragment is in tablet mode");

        }

        return rootView;

    }

    public void initButtons(int listIndex, Integer lastStepId) {

        if (listIndex < lastStepId && listIndex != 0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPrevButton.setVisibility(View.VISIBLE);
        } else if (listIndex == lastStepId) {
            mNextButton.setVisibility(View.INVISIBLE);
            mPrevButton.setVisibility(View.VISIBLE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
            mPrevButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(String mediaUri) {
        if (mExoPlayer == null) {
            if (!TextUtils.isEmpty(mediaUri)) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
                mPlayerView.setPlayer(mExoPlayer);
                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(getContext(), "BakingApp");
                MediaSource mediaSource = new ExtractorMediaSource(
                        Uri.parse(mediaUri),
                        new DefaultDataSourceFactory(getContext(), userAgent),
                        new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(true);
            } else {
                videoCardView.setVisibility(View.INVISIBLE);
                mPlayerView.setVisibility(View.INVISIBLE);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mExoPlayer != null) {
          // Release the player when it is not needed
            mExoPlayer.release();
        }

    }

    public void setStepData(Step step) {

        mVideo_url = step.getVideoUrl();

        mListIndex = step.getId();

        mDescription = step.getDescription();

    }

    public void setLastStepId(Integer lastStepId) {
        mLastStepId = lastStepId;
    }

    public void disableNavigationButton() {
        mTwoPane = true;
        mNextButton.setVisibility(View.INVISIBLE);
        mPrevButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
         if (mTwoPane) {
             Log.d("PANE", "2");
             currentState.putBoolean("PANE", mTwoPane);
             currentState.putInt(LIST_INDEX, mListIndex);
             currentState.putString(DESCRIPTION, mDescription);
             currentState.putString(VIDEO_URL, mVideo_url);

         } else {
             Log.d("PANE", "1");
             currentState.putBoolean("PANE", mTwoPane);
             currentState.putInt(LAST_STEP_ID, mLastStepId);
             currentState.putInt(LIST_INDEX, mListIndex);
             currentState.putString(DESCRIPTION, mDescription);
             currentState.putString(VIDEO_URL, mVideo_url);
         }

    }
}
