package com.example.softsquared_5;

import android.app.Activity;
import android.os.Build;
import android.widget.ImageView;

public class WeatherImageUtils {

    public static void updateWeatherState(String weather, ImageView iv, boolean day){
        switch (weather){
            case "Clear":
                int drawableResource = day ? R.drawable.weather_sun
                        : R.drawable.weather_night;
                iv.setImageResource(drawableResource);
                break;
            case "Rain":
                iv.setImageResource(R.drawable.weather_umbrella);
                break;
            case "Drizzle":
                iv.setImageResource(R.drawable.weather_rainy);
                break;
            case "Thunderstorm":
                iv.setImageResource(R.drawable.weather_thunder);
                break;
            case "Snow":
                iv.setImageResource(R.drawable.weather_snowy);
                break;
            case "Mist":
            case "Fog":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Sand":
                iv.setImageResource(R.drawable.weather_fog);
                break;
            case "Tornado":
                iv.setImageResource(R.drawable.weather_storm);
                break;
            case "Clouds":
                iv.setImageResource(R.drawable.weather_cloudy);
                break;
        }
    }

    public static void setStatusBarColor(Activity activity, int colorId) {
        int color = activity.getResources().getColor(colorId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }
}
