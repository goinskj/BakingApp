package com.example.android.bakingapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class RecipeDao {

    @Insert(onConflict = REPLACE)
    public abstract void insertRecipe(Recipe recipe);

    @Insert(onConflict = REPLACE)
    public abstract void insertIngredients(List<Ingredient> ingredients);

    @Insert(onConflict = REPLACE)
    public abstract void insertStepsList(List<Step> steps);

    public void insertRecipeWithIngredientsAndSteps(Recipe recipe) {
        // loop thru ingredients and steps to make sure the recipe id is set before we insert
        List<Step> steps = recipe.getSteps();
        List<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).setRecipeId(recipe.getId());
        }
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.get(i).setRecipeId(recipe.getId());
        }
        insertRecipe(recipe);
        insertIngredients(ingredients);
        insertStepsList(steps);
    }

    @Query("SELECT * FROM Recipe WHERE id =:id")
    public abstract Recipe getRecipe(int id);

    @Query("SELECT * FROM Ingredient WHERE recipeId =:recipeId")
    public abstract List<Ingredient> loadAllIngredients(int recipeId);

    @Query("SELECT * FROM Step WHERE recipeId =:recipeId")
    public abstract List<Step> loadAllSteps(int recipeId);

    @Query("SELECT * FROM Recipe ORDER BY id")
    public abstract List<Recipe> loadAllRecipes();

    public Recipe getRecipeWithIngredientsAndSteps(int recipeId) {
        Recipe recipe = getRecipe(recipeId);
//        List<Ingredient> ingredients = loadAllIngredients(id);
        List<Step> steps = loadAllSteps(recipeId);
//        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        return recipe;
    }
}
