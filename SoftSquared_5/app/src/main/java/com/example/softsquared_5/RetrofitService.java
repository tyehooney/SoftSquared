package com.example.softsquared_5;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("data/2.5/onecall")
    Call<JsonObject> getCurrentWeather(@Query("lat")double lat,
                                             @Query("lon")double lon,
                                             @Query("appid")String appKey);
}