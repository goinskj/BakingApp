package baking.example.android.bakingapp.utilities;

import baking.example.android.bakingapp.data.Ingredient;
import baking.example.android.bakingapp.data.Recipe;
import baking.example.android.bakingapp.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movies over various sort criteria.
     * <p/>
     *
     * @param recipeJsonStr JSON response from server
     *
     * @return Array of Strings describing movie data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */

    public static List<Recipe> getRecipeListFromJson(String recipeJsonStr)
            throws JSONException {

        /* String array to hold each recipe item */
        List<Recipe> parsedRecipeData = new ArrayList<>();

        JSONArray recipeArray = new JSONArray(recipeJsonStr);

        if (recipeArray != null) {

            for (int i = 0; i < recipeArray.length(); i++) {

                JSONObject item = recipeArray.getJSONObject(i);

                int id = item.getInt("id");
                String name = item.getString("name");
                List<Ingredient> ingredients =
                        getIngredientsListFromJson(item.getJSONArray("ingredients"), id);
                List<Step> steps = getStepsFromJson(item.getJSONArray("steps"), id);
                int servings = item.optInt("servings");
                String imageUrl = item.optString("image");

                parsedRecipeData.add(new Recipe(id, name, servings, imageUrl, ingredients, steps));
            }

        } else {
            return null;
        }

        return parsedRecipeData;

    }

    public static List<Ingredient> getIngredientsListFromJson(JSONArray ingredientsArray, int id) {

        List<Ingredient> ingredients = new ArrayList<>();

        if (ingredientsArray != null) {
            for (int i = 0; i < ingredientsArray.length() - 1; i++) {
                try {
                    JSONObject ingredientItem = ingredientsArray.getJSONObject(i);
                    int recipeId = id;
                    String quantity = ingredientItem.getString("quantity");
                    String measure = ingredientItem.getString("measure");
                    String ingredient = ingredientItem.getString("ingredient");

                    ingredients.add(new Ingredient(recipeId, ingredient, quantity, measure));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {

            return null;

        }

        return ingredients;
    }

    public static List<Step> getStepsFromJson(JSONArray stepsArray, int sourceId) {

        List<Step> parsedSteps = new ArrayList<Step>();

        if (stepsArray != null) {
            for (int i = 0; i < stepsArray.length(); i++) {
                try {

                    JSONObject stepItem = stepsArray.getJSONObject(i);

                    int id = i;
                    int recipeId = sourceId;
                    String shortDescription = stepItem.getString("shortDescription");
                    String description = stepItem.getString("description");
                    //possible null items below
                    String videoUrl = stepItem.optString("videoURL");
                    String thumbnailUrl = stepItem.optString("thumbnailURL");

                    parsedSteps.add(new Step(id, recipeId, shortDescription, description, videoUrl,
                            thumbnailUrl));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            return null;
        }

        return parsedSteps;
    }

}
