package baking.example.android.bakingapp.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Ingredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int recipeId;
    private String ingredient;
    private String quantity;
    private String measure;

    @Ignore
    public Ingredient(int recipeId, String ingredient, String quantity, String measure) {
        this.recipeId = recipeId;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measure = measure;

    }

    public Ingredient(int id, int recipeId, String ingredient, String quantity, String measure) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measure = measure;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Ingredient(Parcel in) {
        recipeId = in.readInt();
        ingredient = in.readString();
        quantity = in.readString();
        measure = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(recipeId);
        parcel.writeString(ingredient);
        parcel.writeString(quantity);
        parcel.writeString(measure);
    }
}
