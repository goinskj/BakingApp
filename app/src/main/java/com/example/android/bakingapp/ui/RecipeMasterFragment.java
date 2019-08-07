package com.example.android.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Step;

import java.util.ArrayList;
import java.util.List;

public class RecipeMasterFragment extends Fragment implements
        RecipeMasterAdapter.RecipeMasterAdapterOnClickHandler {

    private static final String TAG = RecipeMasterFragment.class.getSimpleName();

    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    private static final String STEPS_PARCELABLE = "steps_parcelable";

    private RecyclerView mRecyclerView;

    private RecipeMasterAdapter mAdapter;

    private List<Step> mSteps;

    Parcelable savedRecyclerLayoutState;

    private LinearLayoutManager mLayoutManager;

    // Define a new interface OnNextClickListener that triggers a next step callback in the host activity
    OnStepClickListener mStepCallback;

    // OnNextClickListener interface, calls a method in the host activity named onNextSelected
    public interface OnStepClickListener {
        void onStepSelected(Bundle bundle);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mStepCallback = (RecipeMasterFragment.OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }

    public RecipeMasterFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Load the saved state (the list of images and list index) if there is one
        if(savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mSteps = savedInstanceState.getParcelableArrayList(STEPS_PARCELABLE);
        }

        // Inflate the recipe detail fragment layout
        // Inflate the Android-Me fragment layout
        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_detail_rv);

        mAdapter = new RecipeMasterAdapter(this, getContext());

        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setStepsData(mSteps);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setHasFixedSize(true);

        return rootView;

    }

    // Setter methods for keeping track of the list images this fragment can display and which image
    // in the list is currently being displayed

    public void setSteps(List<Step> steps) {
        mSteps = steps;
    }

    @Override
    public void onClick(Bundle stepData) {
        mStepCallback.onStepSelected(stepData);
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {

        Log.v(TAG, "In recipe mode while flipping screen");
        currentState.putParcelableArrayList(STEPS_PARCELABLE,
                (ArrayList<? extends Parcelable>) mSteps);
        currentState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager()
                .onSaveInstanceState());
        super.onSaveInstanceState(currentState);

    }

}
