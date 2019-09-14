package baking.example.android.bakingapp.data;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StepsConverter {

    @TypeConverter // note this annotation
    public String fromStepsList(List<Step> steps) {
        if (steps == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Step>>() {
        }.getType();
        String json = gson.toJson(steps, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<Step> toStepsList(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Step>>() {
        }.getType();
        List<Step> steps = gson.fromJson(optionValuesString, type);
        return steps;
    }
}
