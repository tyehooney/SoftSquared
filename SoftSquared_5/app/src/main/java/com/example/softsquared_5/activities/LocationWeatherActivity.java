package com.example.softsquared_5.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.softsquared_5.retrofit.MyRetrofit;
import com.example.softsquared_5.R;
import com.example.softsquared_5.retrofit.RetrofitService;
import com.example.softsquared_5.WeatherImageUtils;
import com.example.softsquared_5.views.HourlyWeatherView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationWeatherActivity extends AppCompatActivity {
    private LinearLayout ll_background, ll_hourly, ll_tips;
    private TextView tv_today, tv_location, tv_temperature, tv_temp_feels_like,
            tv_temp_max, tv_temp_min, tv_humidity, tv_wind,
            tv_weather, tv_outer, tv_top, tv_bottoms;
    private ImageView iv_weather, iv_outer, iv_top, iv_bottoms, btn_back, btn_refresh;
    private RelativeLayout rl_progressBar;
    private Animation fadeOut;

    private int sbColor;

    private String place;
    private double lat, lon;
    private boolean day = true;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_weather);

        Intent intent = getIntent();
        place = intent.getStringExtra("place");
        lat = intent.getDoubleExtra("lat", 0);
        lon = intent.getDoubleExtra("lon", 0);

        preferences = getSharedPreferences("timezone", 0);

        setViews();

        getCurrentLocationInfo();
    }

    private void setViews(){
        ll_background = findViewById(R.id.linearLayout_location_main_bg);
        ll_hourly = findViewById(R.id.linearLayout_location_hourly);
        ll_tips = findViewById(R.id.linearLayout_location_tips);

        tv_today = findViewById(R.id.textView_location_today);
        tv_location = findViewById(R.id.textView_other_location);
        tv_weather = findViewById(R.id.textView_location_weather);
        tv_temperature = findViewById(R.id.textView_location_temperature);
        tv_temp_feels_like = findViewById(R.id.textView_location_temp_feels_like);
        tv_humidity = findViewById(R.id.textView_location_humidity);
        tv_wind = findViewById(R.id.textView_location_wind);
        tv_temp_max = findViewById(R.id.textView_location_temp_max);
        tv_temp_min = findViewById(R.id.textView_location_temp_min);
        tv_outer = findViewById(R.id.textView_location_outer);
        tv_top = findViewById(R.id.textView_location_top);
        tv_bottoms = findViewById(R.id.textView_location_bottoms);

        iv_weather = findViewById(R.id.imageView_location_weather);
        iv_outer = findViewById(R.id.imageView_location_outer);
        iv_top = findViewById(R.id.imageView_location_top);
        iv_bottoms = findViewById(R.id.imageView_location_bottoms);

        btn_refresh = findViewById(R.id.imageView_location_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocationInfo();
            }
        });
        btn_back = findViewById(R.id.imageView_location_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rl_progressBar = findViewById(R.id.relativeLayout_location_progress);
    }

    private void getCurrentLocationInfo(){
        if (sbColor != 0)
            rl_progressBar.setBackground(ll_background.getBackground());
        rl_progressBar.setVisibility(View.VISIBLE);

        ll_hourly.removeAllViews();
        ll_tips.removeAllViews();

        fadeOut = AnimationUtils.loadAnimation(LocationWeatherActivity.this, R.anim.fade_out);

        tv_location.setText(place);

        getWeather(lat, lon);
    }

    private void getWeather(double latitude, double longitude){
        final SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        final long timezoneOffsetOfHere = preferences.getLong("offset", 0);

        RetrofitService retrofitService = MyRetrofit.getInstance().getService();

        Call<JsonObject> call =
                retrofitService.getCurrentWeather(latitude, longitude,
                        getString(R.string.weather_app_key), "kr");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    Log.d("weather api", "success, "+lat+", "+lon);

                    JsonObject object = response.body();
                    if (object != null){
                        long timezoneOffset = object.get("timezone_offset").getAsLong();
                        long realTime = ((System.currentTimeMillis()/1000)
                                - timezoneOffsetOfHere + timezoneOffset)*1000;
                        Date currentDate = new Date(realTime);
                        tv_today.setText(format.format(currentDate));

                        //현재 날씨
                        JsonObject current = object.getAsJsonObject("current");
                        if (current != null){
                            float temp_current = current.get("temp").getAsFloat() - 273;
                            float temp_feels_like = current.get("feels_like").getAsFloat() - 273;
                            int humidity = current.get("humidity").getAsInt();
                            float wind = current.get("wind_speed").getAsFloat();

                            long sunrise = current.get("sunrise").getAsLong() - timezoneOffsetOfHere + timezoneOffset;
                            long sunset = current.get("sunset").getAsLong() - timezoneOffsetOfHere + timezoneOffset;
                            long currentTime = currentDate.getTime() / 1000;
                            if (currentTime > sunrise && currentTime < sunset)
                                day = true;
                            else
                                day = false;

                            if (temp_feels_like > 30 || (temp_feels_like > 25 && humidity > 60)){
                                ll_background.setBackground(day ? getDrawable(R.drawable.bg_hot) : getDrawable(R.drawable.bg_hot_night));
                                sbColor = day? R.color.hot_1 : R.color.sb_hot_night;

                                iv_outer.setImageResource(R.drawable.cross);
                                tv_outer.setText("없음");

                                iv_top.setImageResource(R.drawable.polo);
                                tv_top.setText("반팔티 또는 셔츠");

                                iv_bottoms.setImageResource(R.drawable.shorts);
                                tv_bottoms.setText("반바지");

                                TextView tv_tip = new TextView(LocationWeatherActivity.this);
                                tv_tip.setText("* 날이 매우 덥기 때문에 장시간 야외활동은 자제해주세요!");
                                tv_tip.setTextColor(getResources().getColor(R.color.font_white));
                                tv_tip.setTypeface(Typeface.createFromAsset(getAssets(), "font/js_dongkang.otf"));
                                tv_tip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                ll_tips.addView(tv_tip);
                            } else if(temp_feels_like < 10){
                                ll_background.setBackground(day ? getDrawable(R.drawable.bg_cold) : getDrawable(R.drawable.bg_cold_night));
                                sbColor = day? R.color.sb_cold : R.color.sb_cold_night;

                                iv_outer.setImageResource(R.drawable.coat);
                                tv_outer.setText("코트 또는 패딩점퍼");

                                iv_top.setImageResource(R.drawable.hoodie);
                                tv_top.setText("두꺼운 긴팔");

                                iv_bottoms.setImageResource(R.drawable.trousers);
                                tv_bottoms.setText("긴바지");

                                TextView tv_tip = new TextView(LocationWeatherActivity.this);
                                tv_tip.setText("* 쌀쌀한 날씨이기 때문에 외출 시 옷을 따뜻하게 입으세요!");
                                tv_tip.setTextColor(getResources().getColor(R.color.font_white));
                                tv_tip.setTypeface(Typeface.createFromAsset(getAssets(), "font/js_dongkang.otf"));
                                tv_tip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                ll_tips.addView(tv_tip);
                            }else{
                                ll_background.setBackground(day ? getDrawable(R.drawable.bg_cool) : getDrawable(R.drawable.bg_cool_night));
                                sbColor = day? R.color.sb_cool : R.color.sb_cool_night;

                                if (temp_current > 25) {
                                    iv_outer.setImageResource(R.drawable.cross);
                                    tv_outer.setText("없음");

                                    iv_top.setImageResource(R.drawable.shirt);
                                    tv_top.setText("반팔 셔츠");

                                    iv_bottoms.setImageResource(R.drawable.shorts);
                                    tv_bottoms.setText("반바지");
                                } else{
                                    iv_outer.setImageResource(R.drawable.jacket);
                                    tv_outer.setText("가벼운 겉옷");

                                    iv_top.setImageResource(R.drawable.shirt_1);
                                    tv_top.setText("가벼운 셔츠 또는 티셔츠");

                                    iv_bottoms.setImageResource(R.drawable.trousers);
                                    tv_bottoms.setText("가벼운 바지");
                                }
                            }
                            WeatherImageUtils.setStatusBarColor(LocationWeatherActivity.this, sbColor);

                            tv_temperature.setText(Math.round(temp_current)+"");
                            tv_temp_feels_like.setText("체감 기온 : "+Math.round(temp_feels_like)+"ºC");
                            tv_humidity.setText(humidity+"%");
                            tv_wind.setText(wind+"m/s");

                            JsonArray currentMainWeather = current.get("weather").getAsJsonArray();
                            String mainWeather = currentMainWeather.get(0).getAsJsonObject()
                                    .get("main").getAsString();
                            WeatherImageUtils.updateWeatherState(mainWeather, iv_weather, day);
                            String description = currentMainWeather.get(0).getAsJsonObject()
                                    .get("description").getAsString();
                            tv_weather.setText(description);

                            if (mainWeather.equals("Rain") || mainWeather.equals("Drizzle")){
                                TextView tv_tip = new TextView(LocationWeatherActivity.this);
                                tv_tip.setText("* 외출 시 우산 꼭 챙기세요!");
                                tv_tip.setTextColor(getResources().getColor(R.color.font_white));
                                tv_tip.setTypeface(Typeface.createFromAsset(getAssets(), "font/js_dongkang.otf"));
                                tv_tip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                ll_tips.addView(tv_tip);
                            }
                        }

                        //오늘 최저, 최고 기온
                        JsonArray dailyArray = object.getAsJsonArray("daily");
                        if (dailyArray != null){
                            JsonObject day = dailyArray.get(0).getAsJsonObject();
                            JsonObject tempInADay = day.getAsJsonObject("temp");
                            if (tempInADay != null){
                                float max = tempInADay.get("max").getAsFloat() - 273;
                                float min = tempInADay.get("min").getAsFloat() - 273;

                                tv_temp_max.setText("최고 : "+Math.round(max)+"ºC");
                                tv_temp_min.setText("최저 : "+ Math.round(min)+"ºC");

                                if (max - min > 10 && min < 20){
                                    TextView tv_tip = new TextView(LocationWeatherActivity.this);
                                    tv_tip.setText("* 일교차가 심해서 장기간 밖에 머무르신다면 겉옷을 챙기세요!");
                                    tv_tip.setTextColor(getResources().getColor(R.color.font_white));
                                    tv_tip.setTypeface(Typeface.createFromAsset(getAssets(), "font/js_dongkang.otf"));
                                    tv_tip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                    ll_tips.addView(tv_tip);
                                }
                            }
                        }

                        //시간별 날씨
                        JsonArray hourlyArray = object.getAsJsonArray("hourly");
                        if (hourlyArray != null){
                            for (int i = 3; i < (hourlyArray.size()/2)+3; i += 3) {
                                JsonObject hour = hourlyArray.get(i).getAsJsonObject();
                                if (hour != null){
                                    long time = hour.get("dt").getAsLong();
                                    long timeThere = time - timezoneOffsetOfHere + timezoneOffset;
                                    int temp = hour.get("temp").getAsInt() - 273;

                                    JsonArray hourlyWeather = hour.getAsJsonArray("weather");
                                    if (hourlyWeather != null){
                                        String weather = hourlyWeather.get(0).getAsJsonObject()
                                                .get("main").getAsString();
                                        HourlyWeatherView hwv = new HourlyWeatherView(LocationWeatherActivity.this,
                                                timeThere, weather, temp);
                                        ll_hourly.addView(hwv);
                                    }
                                }
                            }
                        }
                    }
                }else{
                    Log.e("weather api", "fail to get info");
                }
                rl_progressBar.setAnimation(fadeOut);
                rl_progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("weather api", "fail to get info", t);
                rl_progressBar.setAnimation(fadeOut);
                rl_progressBar.setVisibility(View.GONE);
            }
        });
    }
}