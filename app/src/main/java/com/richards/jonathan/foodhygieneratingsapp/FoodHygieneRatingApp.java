package com.richards.jonathan.foodhygieneratingsapp;

import android.app.Application;
import android.content.Context;

public class FoodHygieneRatingApp extends Application {

    private static FoodHygieneRatingApp instance;

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance= this;
        super.onCreate();
    }
}
