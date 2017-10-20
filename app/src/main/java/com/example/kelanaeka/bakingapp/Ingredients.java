package com.example.kelanaeka.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kelanaeka on 10/14/2017.
 */

public class Ingredients implements Parcelable{
    private int qty;
    private String measure;
    private String ingredient;

    Ingredients(int qty, String measure, String ingredient){
        this.qty = qty;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    protected Ingredients(Parcel in) {
        qty = in.readInt();
        measure = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<Ingredients> CREATOR = new Creator<Ingredients>() {
        @Override
        public Ingredients createFromParcel(Parcel in) {
            return new Ingredients(in);
        }

        @Override
        public Ingredients[] newArray(int size) {
            return new Ingredients[size];
        }
    };

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getQty() {
        return qty;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(qty);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }
}
