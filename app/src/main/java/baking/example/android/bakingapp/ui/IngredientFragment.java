package baking.example.android.bakingapp.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import baking.example.android.bakingapp.data.Ingredient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import baking.example.android.bakingapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

public class IngredientFragment extends Fragment {

    private static final String TAG = IngredientFragment.class.getSimpleName();

    // Final Strings to store state information about the list of images and list index
    public static final String INGREDIENTS_LIST = "ingredients_list";

    @BindView(R.id.tv_ingredients) TextView mIngredientsTextView;

    private List<Ingredient> mIngredients;

    public IngredientFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(getActivity()); // bind butterknife after setContectView(..)

        // Load the saved state (the list of images and list index) if there is one
        if(savedInstanceState != null) {
            mIngredients = savedInstanceState.getParcelableArrayList(INGREDIENTS_LIST);
        }

        final View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);

        StringBuilder ingredientsList = new StringBuilder();

        for (int i = 0; i < mIngredients.size(); i++) {
            String quantity = mIngredients.get(i).getQuantity();
            String measure = mIngredients.get(i).getMeasure();
            String ingredient = mIngredients.get(i).getIngredient();

            String string = "\n" + quantity + " " + measure + " " + ingredient + "\n";
            ingredientsList.append(string);
        }

        mIngredientsTextView.setText(ingredientsList.toString());

        return rootView;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putParcelableArrayList(INGREDIENTS_LIST, (ArrayList<? extends Parcelable>) mIngredients);
    }

}
