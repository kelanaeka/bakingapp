package com.example.kelanaeka.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kelanaeka on 9/30/2017.
 */

public class RecipeData implements Parcelable{
    private int recipeId;
    private String recipeName;
    private int servings;
    private String imageUrl;
    private ArrayList<Ingredients> ingredients;
    private ArrayList<Steps> steps;

    public RecipeData(){}

    protected RecipeData(Parcel in) {
        recipeId = in.readInt();
        recipeName = in.readString();
        servings = in.readInt();
        imageUrl = in.readString();
        ingredients = in.readArrayList(Ingredients.class.getClassLoader());
        steps = in.readArrayList(Steps.class.getClassLoader());
    }

    public static final Creator<RecipeData> CREATOR = new Creator<RecipeData>() {
        @Override
        public RecipeData createFromParcel(Parcel in) {
            return new RecipeData(in);
        }

        @Override
        public RecipeData[] newArray(int size) {
            return new RecipeData[size];
        }
    };

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setImageUrl(String image) {
        this.imageUrl = image;
    }

    public void setIngredients(ArrayList<Ingredients> ingredients) {this.ingredients = ingredients;}

    public void setSteps(ArrayList<Steps> steps) {this.steps = steps;}

    public int getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public int getServings() {
        return servings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getIngredientsLength() { return this.ingredients.size(); }

    public Ingredients getIngredients(int pos) {
        return this.ingredients.get(pos);
    }

    public int getStepsLength() { return this.steps.size(); }

    public Steps getSteps(int pos) {
        return this.steps.get(pos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recipeId);
        dest.writeString(recipeName);
        dest.writeInt(servings);
        dest.writeString(imageUrl);
        dest.writeList(ingredients);
        dest.writeList(steps);
    }
}
