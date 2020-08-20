package com.example.softsquared_5.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.softsquared_5.R;
import com.example.softsquared_5.WeatherImageUtils;

import java.util.Date;

public class HourlyWeatherView extends LinearLayout {
    private Context mContext;
    private String weather;
    private int hour, temperature;

    public HourlyWeatherView(Context context, long time, String weather, int temperature) {
        super(context);
        this.mContext = context;
        this.weather = weather;
        Date date = new Date(time * 1000);
        this.hour = date.getHours();
        this.temperature = temperature;

        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.view_hourly_weather, this, true);

        TextView tv_hour = findViewById(R.id.textView_hourly_hour);
        TextView tv_temp = findViewById(R.id.textView_hourly_temp);
        ImageView iv_weather = findViewById(R.id.imageView_hourly_weather);

        tv_hour.setText(hour+"시");
        tv_temp.setText(temperature+"ºC");

        boolean day = hour >= 6 && hour <= 19;

        WeatherImageUtils.updateWeatherState(weather, iv_weather, day);
    }
}
