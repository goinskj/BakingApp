package com.example.android.bakingapp.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.GetIngredientsService;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.AppDatabase;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.utilities.AppExecutors;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.example.android.bakingapp.utilities.RecipeJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<List<Recipe>>{

    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
     * to any number you like, as long as you use the same variable name.
     */
    private static final int RECIPE_LOADER = 22;

    private RecipeAdapter mAdapter;

    private List<Recipe> mRecipes;

    private List<Recipe> mCachedRecipes;

    private RecyclerView mRecyclerView;

    Parcelable savedRecyclerLayoutState;

    private LinearLayoutManager mLayoutManager;

    private GridLayoutManager mGridLayoutManager;

    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    private static final String RECIPES_PARCELABLE = "recipes_parcelable";

    private AppDatabase mDb;

    /* This TextView is used to display errors and will be hidden if there are no errors */
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;

    /*
     * The ProgressBar that will indicate to the user that we are loading data. It will be
     * hidden when no data is loading.
     *
     * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
     * circle. We didn't make the rules (or the names of Views), we just follow them.
     */
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); // bind butterknife after setContectView(..)

        mDb = AppDatabase.getInstance(getApplicationContext());

        mAdapter = new RecipeAdapter(MainActivity.this,this);

        if (findViewById(R.id.rv_Recipes_Tablet) != null) {

            // Set the RecyclerView to its corresponding view
            mRecyclerView = findViewById(R.id.rv_Recipes_Tablet);
            /*
             * GridLayoutManager can support a grid like layout for recyclerview.
             */
            mGridLayoutManager = new GridLayoutManager(this, 2);
            mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);

            mRecyclerView.setLayoutManager(mGridLayoutManager);

        } else {

            // Set the RecyclerView to its corresponding view
            mRecyclerView = findViewById(R.id.rv_Recipes);

            mLayoutManager = new LinearLayoutManager(this);

            // Set the layout for the RecyclerView to be a linear layout, which measures and
            // positions items within a RecyclerView into a linear list
            mRecyclerView.setLayoutManager(mLayoutManager);

        }

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the GridLayout
         */
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);

        if(savedInstanceState != null) {

            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mCachedRecipes = savedInstanceState.getParcelableArrayList(RECIPES_PARCELABLE);
            mAdapter.setRecipes(mCachedRecipes);
            mAdapter.notifyDataSetChanged();

        } else {
            /* Once all of our views are setup, we can load the movie data. */
            loadRecipeData();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Log.v(TAG, "In recipe mode while flipping screen");
        outState.putParcelableArrayList(RECIPES_PARCELABLE,
                (ArrayList<? extends Parcelable>) mCachedRecipes);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager()
                .onSaveInstanceState());
        super.onSaveInstanceState(outState);

    }


    @Override
    public void onItemClickListener(final int sourceRecipeId) {

        Context context = this;
        Class destinationClass = RecipeDetail.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("ID", sourceRecipeId);
        startActivity(intentToStartDetailActivity);

        //start widget stuff intent service
        GetIngredientsService.startActionGetIngredients(context, sourceRecipeId);

    }

    /**
     * This method will tell some background method to get the movie data in the background with
     * the sort key as a parameter.
     */
    private void loadRecipeData() {

        showRecipeDataView();

        LoaderManager loaderManager = getSupportLoaderManager();

        Loader<String> recipeLoader = loaderManager.getLoader(RECIPE_LOADER);

        if (recipeLoader == null) {
            loaderManager.initLoader(RECIPE_LOADER, null, this);
        } else {
            loaderManager.restartLoader(RECIPE_LOADER,null, this);
        }
    }

    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<List<Recipe>>(this) {

            // COMPLETED (5) Override onStartLoading
            @Override
            protected void onStartLoading() {
                /*
                 * When we initially begin loading in the background, we want to display the
                 * loading indicator to the user
                 */
                mLoadingIndicator.setVisibility(View.VISIBLE);

                // COMPLETED (8) Force a load
                forceLoad();
            }

            @Nullable
            @Override
            public List<Recipe> loadInBackground() {

                URL recipeRequestUrl = NetworkUtils.buildQueryUrl();

                try {
                    String jsonRecipeResponse = NetworkUtils
                            .getResponseFromHttpUrl(recipeRequestUrl);

                    List<Recipe> recipeList = RecipeJsonUtils
                            .getRecipeListFromJson(jsonRecipeResponse);

                    return recipeList;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, final List<Recipe> recipeList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (recipeList != null) {
            if (recipeList.size() > 0) {
                showRecipeDataView();
                for (int i = 0; i < recipeList.size(); i++) {
                    final int finalI = i;
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.recipeDao().insertRecipeWithIngredientsAndSteps(recipeList.get(finalI));
                        }
                    });

                }
                mRecipes = recipeList;
                mCachedRecipes = recipeList;
                mAdapter.setRecipes(recipeList);
            } else {
                showErrorMessage();
            }
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {

    }

    /**
     * This method will make the View for the product data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showRecipeDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    /**
     * This method will make the error message visible and hide the movie data
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


}
