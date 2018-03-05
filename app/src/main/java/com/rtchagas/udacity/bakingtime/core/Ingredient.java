package com.rtchagas.udacity.bakingtime.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.rtchagas.udacity.bakingtime.R;

import java.io.Serializable;

public class Ingredient implements Serializable {

    private final static long serialVersionUID = 1692478042905599652L;

    @SerializedName("quantity")
    private double quantity;

    @SerializedName("measure")
    private String measure;

    @SerializedName("ingredient")
    private String ingredient;

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @SuppressWarnings("defaultlocale")

    public static String getFormattedString(@NonNull Context context, @NonNull Ingredient ingredient) {

        // Get the measure
        String measure = ingredient.getMeasure().toLowerCase();

        // Get the quantity
        double rawQuantity = ingredient.getQuantity();
        String quantity = (rawQuantity % 1d > 0)
                ? String.format("%.1f", rawQuantity)
                : String.format("%.0f", rawQuantity);

        // Get the ingredient name
        String name = ingredient.getIngredient();

        return context.getString(R.string.ingredient_format, quantity, measure, name);
    }
}
