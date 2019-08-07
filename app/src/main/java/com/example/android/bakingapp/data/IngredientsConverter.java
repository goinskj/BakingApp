package com.example.android.bakingapp.data;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class IngredientsConverter {

    @TypeConverter // note this annotation
    public String fromIngredientsList(List<Ingredient> ingredients) {
        if (ingredients == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Ingredient>>() {
        }.getType();
        String json = gson.toJson(ingredients, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<Ingredient> toIngredientsList(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Ingredient>>() {
        }.getType();
        List<Ingredient> ingredients = gson.fromJson(optionValuesString, type);
        return ingredients;
    }
}
