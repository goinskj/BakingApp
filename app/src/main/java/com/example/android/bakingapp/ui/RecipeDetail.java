package com.example.android.bakingapp.ui;

import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.AppDatabase;
import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Step;
import com.example.android.bakingapp.utilities.AppExecutors;

import java.util.List;

public class RecipeDetail extends AppCompatActivity implements RecipeMasterFragment.OnStepClickListener,
StepFragment.OnNextClickListener, StepFragment.OnPrevClickListener{

    private static final String TAG = RecipeDetail.class.getSimpleName();

    private static String SOURCE_RECIPE_ID_SAVED = "source_recipe_id_saved";

    private CardView mIngredientsTab;

    private AppDatabase mDb;

    private boolean mTwoPane;

    int sourceRecipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Only create new fragments when there is no previously saved state
        mDb = AppDatabase.getInstance(getApplicationContext());

        mIngredientsTab = (CardView) findViewById(R.id.ingredient_card);

        sourceRecipeId= getIntent().getIntExtra("ID", 0);

        if (findViewById(R.id.step_ingr_fragment) != null) {
            mTwoPane = true;
            mIngredientsTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<Ingredient> ingredients = mDb.recipeDao()
                                    .getRecipe(sourceRecipeId).getIngredients();
                            final IngredientFragment ingredientFragment = new IngredientFragment();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ingredientFragment.setIngredients(ingredients);
                                    // Add the fragment to its container using a FragmentManager and a Transaction
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.step_ingr_fragment, ingredientFragment)
                                            .commit();
                                }
                            });

                        }
                    });
                }
            });
        } else {
            mTwoPane = false;
            mIngredientsTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Class destinationClass = StepIngrDetail.class;
                    Bundle bundle = new Bundle();
                    bundle.putInt("RECIPE_ID_INGREDIENTS", sourceRecipeId);
                    Intent intentToStartDetailActivity = new Intent(getApplicationContext(), destinationClass);
                    intentToStartDetailActivity.putExtra("BUNDLE", bundle);
                    startActivity(intentToStartDetailActivity);
                }
            });
        }

        //only create fragments one time.
        if(savedInstanceState == null) {

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    final List<Step> steps = mDb.recipeDao().getRecipe(sourceRecipeId).getSteps();
                    // Create a new recycler list view Fragment that shows
                    final RecipeMasterFragment masterFragment = new RecipeMasterFragment();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            masterFragment.setSteps(steps);
                            // Add the fragment to its container using a FragmentManager and a Transaction
                            FragmentManager fragmentManager = getSupportFragmentManager();

                            fragmentManager.beginTransaction()
                                    .add(R.id.master_list_fragment, masterFragment)
                                    .commit();
                        }
                    });

                }
            });

        } else {
            sourceRecipeId = savedInstanceState.getInt(SOURCE_RECIPE_ID_SAVED);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SOURCE_RECIPE_ID_SAVED, sourceRecipeId);
    }

    // Define the behavior for onNextSelected
    public void onStepSelected(Bundle bundle) {

        if (mTwoPane) {
            final int stepId = bundle.getInt("STEP_ID");
            Log.v(TAG, String.valueOf(stepId));

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    final Step step = mDb.recipeDao().getRecipe(sourceRecipeId).getSteps().get(stepId);
                    final StepFragment stepFragment = new StepFragment();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stepFragment.setStepData(step);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.step_ingr_fragment, stepFragment)
                                    .commit();
                        }
                    });
                }
            });
        } else {
//            Log.d("PANE", "only 1 pane");
            Class destinationClass = StepIngrDetail.class;
            Intent intentToStartStepIngrDetailActivity = new Intent(this, destinationClass);
            intentToStartStepIngrDetailActivity.putExtra("BUNDLE",bundle);
            startActivity(intentToStartStepIngrDetailActivity);

        }

    }

    @Override
    public void onNextSelected(int nextStepId) {

    }

    @Override
    public void onPrevSelected(int prevStepId) {

    }
}
