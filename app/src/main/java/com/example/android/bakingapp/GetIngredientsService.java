package com.example.android.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.android.bakingapp.data.AppDatabase;
import com.example.android.bakingapp.data.Ingredient;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class GetIngredientsService extends IntentService {

    private AppDatabase mDb;

    public static final String ACTION_GET_INGREDIENTS = "com.example.android.bakingapp.action.GET_INGREDIENTS";

    public static final String EXTRA_RECIPE_ID = "com.example.android.bakingapp.extra.RECIPE_ID";;

    public GetIngredientsService() {
        super("GetIngredientsService");
    }

    /**
     * Starts this service to perform GetIngredients action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionGetIngredients(Context context, int recipeId) {
        Intent intent = new Intent(context, GetIngredientsService.class);
        intent.setAction(ACTION_GET_INGREDIENTS);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        context.startService(intent);
    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_INGREDIENTS.equals(action)) {
                int recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, 1);
                handleActionGetIngredients(recipeId);
            }
        }
    }

    /**
     * Handle action WaterPlant in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetIngredients(int recipeId) {
        mDb = AppDatabase.getInstance(getApplicationContext());
        final List<Ingredient> ingredients = mDb.recipeDao().getRecipe(recipeId).getIngredients();
        StringBuilder ingredientsList = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            String quantity = ingredients.get(i).getQuantity();
            String measure = ingredients.get(i).getMeasure();
            String ingredient = ingredients.get(i).getIngredient();

            String string = "\n" + quantity + " " + measure + " " + ingredient + "\n";
            ingredientsList.append(string);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                BakingWidgetProvider.class));
        //Now update all widgets
        BakingWidgetProvider.updateIngredientWidgets(this, appWidgetManager, appWidgetIds,
                ingredientsList.toString());
    }

}
