package com.example.android.bakingapp.ui;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.AppDatabase;
import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Step;
import com.example.android.bakingapp.utilities.AppExecutors;

import java.util.List;



public class StepIngrDetail extends AppCompatActivity implements StepFragment.OnNextClickListener,
        StepFragment.OnPrevClickListener {

    private AppDatabase mDb;

    private static final String TAG = StepIngrDetail.class.getSimpleName();

    public static final String SAVED_RECIPE_ID = "saved_recipe_id";
    public static final String SAVED_LAST_STEP_ID = "saved_last_step_id";

    private FragmentManager fragmentManager;

    private int recipeId;

    private Integer lastStepId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_ingr_detail);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Bundle stepIngrBundle = getIntent().getBundleExtra("BUNDLE");

        // Only create new fragments when there is no previously saved state
        if(savedInstanceState == null) {

            fragmentManager = getSupportFragmentManager();

            if (stepIngrBundle.containsKey("RECIPE_ID_INGREDIENTS")) {

                recipeId = stepIngrBundle.getInt("RECIPE_ID_INGREDIENTS");
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<Ingredient> ingredients = mDb.recipeDao().getRecipe(recipeId).getIngredients();
                        final IngredientFragment ingredientFragment = new IngredientFragment();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ingredientFragment.setIngredients(ingredients);
                                // Add the fragment to its container using a FragmentManager and a Transaction
                                fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .add(R.id.step_ingr_fragment, ingredientFragment)
                                        .commit();
                            }
                        });

                    }
                });

            } else {

                recipeId = stepIngrBundle.getInt("RECIPE_ID_STEP");
                final int stepId = stepIngrBundle.getInt("STEP_ID");
                lastStepId = stepIngrBundle.getInt("STEP_LAST_ID");
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final Step step = mDb.recipeDao().getRecipe(recipeId).getSteps().get(stepId);
                        final StepFragment stepFragment = new StepFragment();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stepFragment.setStepData(step);
                                stepFragment.setLastStepId(lastStepId);
                                // Add the fragment to its container using a FragmentManager and a Transaction
                                fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .add(R.id.step_ingr_fragment, stepFragment)
                                        .commit();
                            }
                        });

                    }
                });

            }
        } else {
            recipeId = savedInstanceState.getInt(SAVED_RECIPE_ID);
            lastStepId = savedInstanceState.getInt(SAVED_LAST_STEP_ID);
        }

    }

    // Define the behavior for onNextSelected
    public void onNextSelected(final int nextStepId) {
        // Create a Toast that displays the position that was clicked
        Log.v(TAG, String.valueOf(nextStepId));

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Step step = mDb.recipeDao().getRecipe(recipeId).getSteps().get(nextStepId);
                final StepFragment nextStepFragment = new StepFragment();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextStepFragment.setStepData(step);
                        nextStepFragment.setLastStepId(lastStepId);
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.step_ingr_fragment, nextStepFragment)
                                .commit();
                    }
                });
            }
        });
    }

    // Define the behavior for onNextSelected
    public void onPrevSelected(final int prevStepId) {

        Log.v(TAG, String.valueOf(prevStepId));

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Step step = mDb.recipeDao().getRecipe(recipeId).getSteps().get(prevStepId);
                final StepFragment prevStepFragment = new StepFragment();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prevStepFragment.setStepData(step);
                        prevStepFragment.setLastStepId(lastStepId);
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.step_ingr_fragment, prevStepFragment)
                                .commit();
                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (lastStepId == null) {
            outState.putInt(SAVED_RECIPE_ID, recipeId);
        } else {
            outState.putInt(SAVED_RECIPE_ID, recipeId);
            outState.putInt(SAVED_LAST_STEP_ID, lastStepId);
        }

    }
}
