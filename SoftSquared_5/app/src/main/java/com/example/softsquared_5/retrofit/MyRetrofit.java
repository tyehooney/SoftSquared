package com.example.softsquared_5.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetrofit {
    private static MyRetrofit ourInstance = new MyRetrofit();
    public static MyRetrofit getInstance(){
        return ourInstance;
    }

    private MyRetrofit(){}

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitService service = retrofit.create(RetrofitService.class);

    public RetrofitService getService(){
        return service;
    }

    Retrofit placeRetrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitService placeService = placeRetrofit.create(RetrofitService.class);

    public RetrofitService getPlaceService(){return placeService;}
}