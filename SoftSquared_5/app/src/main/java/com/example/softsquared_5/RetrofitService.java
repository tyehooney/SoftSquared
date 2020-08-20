package com.example.softsquared_5;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("data/2.5/onecall")
    Call<JsonObject> getCurrentWeather(@Query("lat")double lat,
                                             @Query("lon")double lon,
                                             @Query("appid")String appKey,
                                       @Query("lang")String language);

    @GET("maps/api/place/findplacefromtext/json")
    Call<JsonObject> getPlaces(@Query("input")String name,
                              @Query("inputtype")String inputType,
                              @Query("fields")String fields,
                              @Query("key")String appKey);
}