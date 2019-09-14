package baking.example.android.bakingapp.ui;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import baking.example.android.bakingapp.R;
import baking.example.android.bakingapp.data.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds favorite data and the Context
    private List<Recipe> mRecipeEntries;
    private Context mContext;

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public RecipeAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_card, parent, false);

        return new RecipeViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {

        String recipeName = mRecipeEntries.get(position).getName();
        if (mContext != null) {
            holder.mRecipeName.setText(recipeName);
        }

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mRecipeEntries == null) {
            return 0;
        }
        return mRecipeEntries.size();
    }

    /**
     * When data changes, this method updates the list of favoriteEntries
     * and notifies the adapter to use the new values on it
     */
    public void setRecipes(List<Recipe> recipeEntries) {
        mRecipeEntries = recipeEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int recipeId);
    }

    // Inner class for creating ViewHolders
    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        public final TextView mRecipeName;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public RecipeViewHolder(View itemView) {
            super(itemView);

            mRecipeName = (TextView) itemView.findViewById(R.id.recipe_name_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            int recipeId = mRecipeEntries.get(adapterPosition).getId();
            mItemClickListener.onItemClickListener(recipeId);

        }
    }
}
