package com.example.softsquared_5;

import android.app.Activity;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class WeatherImageUtils {

    public static void updateWeatherState(String weather, ImageView iv, @Nullable TextView tv, boolean day){
        switch (weather){
            case "Clear":
                int drawableResource = day ? R.drawable.weather_sun
                        : R.drawable.weather_night;
                iv.setImageResource(drawableResource);
                if (tv != null)
                    tv.setText("맑음");
                break;
            case "Rain":
                iv.setImageResource(R.drawable.weather_umbrella);
                if (tv != null)
                    tv.setText("비");
                break;
            case "Drizzle":
                iv.setImageResource(R.drawable.weather_rainy);
                if (tv != null)
                    tv.setText("가랑비");
                break;
            case "Thunderstorm":
                iv.setImageResource(R.drawable.weather_thunder);
                if (tv != null)
                    tv.setText("벼락/돌풍");
                break;
            case "Snow":
                iv.setImageResource(R.drawable.weather_snowy);
                if (tv != null)
                    tv.setText("눈");
                break;
            case "Mist":
                iv.setImageResource(R.drawable.weather_fog);
                if (tv != null)
                    tv.setText("연무");
                break;
            case "Fog":
            case "Smoke":
            case "Haze":
                iv.setImageResource(R.drawable.weather_fog);
                if (tv != null)
                    tv.setText("안개");
                break;
            case "Dust":
                iv.setImageResource(R.drawable.weather_fog);
                if (tv != null)
                    tv.setText("먼지 많음");
                break;
            case "Sand":
                iv.setImageResource(R.drawable.weather_fog);
                if (tv != null)
                    tv.setText("황사");
                break;
            case "Tornado":
                iv.setImageResource(R.drawable.weather_storm);
                if (tv != null)
                    tv.setText("태풍");
                break;
            case "Clouds":
                iv.setImageResource(R.drawable.weather_cloudy);
                if (tv != null)
                    tv.setText("흐림");
        }
    }

    public static void setStatusBarColor(Activity activity, int colorId) {
        int color = activity.getResources().getColor(colorId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }
}
