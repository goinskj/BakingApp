package com.example.android.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Step;

import java.util.List;

public class RecipeMasterAdapter extends RecyclerView.Adapter<RecipeMasterAdapter.RecipeMasterViewHolder> {

    private List<Step> mSteps;

    private final Context mContext;

    final private RecipeMasterAdapterOnClickHandler mClickHandler;

    public interface RecipeMasterAdapterOnClickHandler {
        void onClick(Bundle itemData);
    }

    public RecipeMasterAdapter(RecipeMasterAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    public class RecipeMasterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mMasterItemTextView;

        public RecipeMasterViewHolder(View view) {
            super(view);
            mMasterItemTextView = (TextView) view.findViewById(R.id.tv_master_item_name);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Bundle mBundle = new Bundle();
            //figure out if we are in the ingredients tab or step tab
            mBundle.putInt("STEP_ID", mSteps.get(adapterPosition).getId());
            mBundle.putInt("RECIPE_ID_STEP", mSteps.get(adapterPosition).getRecipeId());
            mBundle.putInt("STEP_LAST_ID", ((mSteps.size()) - 1));
            mClickHandler.onClick(mBundle);
        }
    }

    @NonNull
    @Override
    public RecipeMasterAdapter.RecipeMasterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.master_item_layout, viewGroup, shouldAttachToParentImmediately);
        return new RecipeMasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeMasterAdapter.RecipeMasterViewHolder recipeMasterViewHolder, int position) {
        String stepsMasterItemName = mSteps.get(position).getShortDescription();
        if (mContext != null) {
            recipeMasterViewHolder.mMasterItemTextView.setText(stepsMasterItemName);
        }

    }

    @Override
    public int getItemCount() {
        if (null == mSteps) return 0;
        return (mSteps.size());
    }

    public void setStepsData(List<Step> stepsData) {
        mSteps = stepsData;
        notifyDataSetChanged();
    }
}
