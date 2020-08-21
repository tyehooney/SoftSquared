package com.example.softsquared_5.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.softsquared_5.R;
import com.example.softsquared_5.WeatherImageUtils;

import java.util.Calendar;
import java.util.Date;

public class HourlyWeatherView extends LinearLayout {
    private Context mContext;
    private String weather, date;
    private int hour, temperature;

    public HourlyWeatherView(Context context, long time, String weather, int temperature) {
        super(context);
        this.mContext = context;
        this.weather = weather;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.date = (calendar.get(Calendar.MONTH))+1+"/"+calendar.get(Calendar.DAY_OF_MONTH);
        this.temperature = temperature;

        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.view_hourly_weather, this, true);

        TextView tv_date = findViewById(R.id.textView_hourly_date);
        TextView tv_hour = findViewById(R.id.textView_hourly_hour);
        TextView tv_temp = findViewById(R.id.textView_hourly_temp);
        ImageView iv_weather = findViewById(R.id.imageView_hourly_weather);

        if (hour >= 0 && hour < 3){
            tv_date.setText(date);
            tv_date.setVisibility(VISIBLE);
        }else
            tv_date.setVisibility(INVISIBLE);

        tv_hour.setText(hour+"시");
        tv_temp.setText(temperature+"ºC");

        boolean day = hour >= 6 && hour <= 19;

        WeatherImageUtils.updateWeatherState(weather, iv_weather, day);
    }
}
