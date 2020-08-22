package com.example.softsquared_5.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.softsquared_5.views.HourlyWeatherView;
import com.example.softsquared_5.retrofit.MyRetrofit;
import com.example.softsquared_5.R;
import com.example.softsquared_5.retrofit.RetrofitService;
import com.example.softsquared_5.views.SideMenuView;
import com.example.softsquared_5.WeatherImageUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private LinearLayout ll_background, ll_hourly, ll_tips;
    private TextView tv_today, tv_location, tv_temperature, tv_temp_feels_like,
            tv_temp_max, tv_temp_min, tv_humidity, tv_wind,
            tv_weather, tv_outer, tv_top, tv_bottoms;
    private ImageView iv_weather, iv_outer, iv_top, iv_bottoms, btn_menu, btn_refresh;
    private ViewGroup viewLayout, sideLayout;
    private RelativeLayout rl_progressBar;
    private Animation fadeOut;

    private int sbColor;

    private long backKeyPressedTime = 0;

    private LocationManager lm;
    private double lat = 37.55, lon = 126.32;
    private boolean day = true;

    private long userId;
    private String userNickname, profileImgUrl;

    private boolean menuShowing = false;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getLongExtra("id", 0);
        userNickname = intent.getStringExtra("nickname");
        profileImgUrl = intent.getStringExtra("profileImg");

        if (userId == 0 || userNickname == null) {
            Toast.makeText(this, "로그인 정보를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        preferences = getSharedPreferences("timezone", 0);

        setViews();

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            getCurrentLocationInfo();
        }
    }

    @Override
    public void onBackPressed() {
        if (menuShowing) {
            closeMenu();
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면\n앱이 종료됩니다."
                        , Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getCurrentLocationInfo();
        }
    }

    private void setViews() {
        ll_background = findViewById(R.id.linearLayout_main_bg);
        ll_hourly = findViewById(R.id.linearLayout_hourly);
        ll_tips = findViewById(R.id.linearLayout_tips);

        tv_today = findViewById(R.id.textView_today);
        tv_location = findViewById(R.id.textView_location);
        tv_weather = findViewById(R.id.textView_weather);
        tv_temperature = findViewById(R.id.textView_temperature);
        tv_temp_feels_like = findViewById(R.id.textView_temp_feels_like);
        tv_humidity = findViewById(R.id.textView_humidity);
        tv_wind = findViewById(R.id.textView_wind);
        tv_temp_max = findViewById(R.id.textView_temp_max);
        tv_temp_min = findViewById(R.id.textView_temp_min);
        tv_outer = findViewById(R.id.textView_outer);
        tv_top = findViewById(R.id.textView_top);
        tv_bottoms = findViewById(R.id.textView_bottoms);

        iv_weather = findViewById(R.id.imageView_weather);
        iv_outer = findViewById(R.id.imageView_outer);
        iv_top = findViewById(R.id.imageView_top);
        iv_bottoms = findViewById(R.id.imageView_bottoms);

        btn_menu = findViewById(R.id.imageView_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });
        btn_refresh = findViewById(R.id.imageView_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocationInfo();
            }
        });

        viewLayout = findViewById(R.id.fl_slide);
        sideLayout = findViewById(R.id.view_sildemenu);

        addSideMenu();

        rl_progressBar = findViewById(R.id.relativeLayout_progress);
    }

    private void addSideMenu() {
        sideLayout.removeAllViews();

        SideMenuView sideMenu = new SideMenuView(MainActivity.this, userId, userNickname, profileImgUrl);

        sideLayout.addView(sideMenu);

        sideMenu.setEventListener(new SideMenuView.EventListener() {
            @Override
            public void btnCancel() {
                closeMenu();
            }

            @Override
            public void btnShare() {
                kakaolink();
            }

            @Override
            public void btnLogout() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("로그아웃")
                        .setMessage("로그아웃하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                    @Override
                                    public void onCompleteLogout() {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).setNegativeButton("아니오", null).create().show();
            }
        });
    }

    public void showMenu() {
        menuShowing = true;
        Animation slide = AnimationUtils.loadAnimation(MainActivity.this, R.anim.sidemenu_show);
        sideLayout.startAnimation(slide);
        viewLayout.setVisibility(View.VISIBLE);
        viewLayout.setEnabled(true);
        Animation fade = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
        viewLayout.startAnimation(fade);

        WeatherImageUtils.setStatusBarColor(MainActivity.this, R.color.colorPrimaryDark);
    }

    public void closeMenu() {
        menuShowing = false;

        Animation slide = AnimationUtils.loadAnimation(MainActivity.this, R.anim.sidemenu_hidden);
        sideLayout.startAnimation(slide);
        Animation fade = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        viewLayout.startAnimation(fade);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewLayout.setVisibility(View.GONE);
                viewLayout.setEnabled(false);
                WeatherImageUtils.setStatusBarColor(MainActivity.this, sbColor);
            }
        }, 400);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addSideMenu();
        getCurrentLocationInfo();
    }

    private void getCurrentLocationInfo() {
        if (sbColor != 0)
            rl_progressBar.setBackground(ll_background.getBackground());
        rl_progressBar.setVisibility(View.VISIBLE);

        ll_hourly.removeAllViews();
        ll_tips.removeAllViews();

        fadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationListener mLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    //여기서 위치값이 갱신되면 이벤트가 발생한다.
                    //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
                    lat = location.getLatitude();
                    lon = location.getLongitude();

                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(lat, lon, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list != null){
                        if (list.size() == 0)
                            tv_location.setText("위치에 해당하는 주소 없음");
                        else{
                            Address address = list.get(0);
                            String locality = address.getLocality() == null ? "" : address.getLocality();
                            tv_location.setText(address.getCountryName()+" "
                                    +address.getAdminArea()+" "
                                    +locality);
                        }
                    }

                    getWeather(lat, lon);
                }
                public void onProviderDisabled(String provider) {
                    // Disabled시
                    Log.d("test", "onProviderDisabled, provider:" + provider);
                }

                public void onProviderEnabled(String provider) {
                    // Enabled시
                    Log.d("test", "onProviderEnabled, provider:" + provider);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // 변경시
                    Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
                }
            };

            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);
            lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null);
        }

    }

    private void getWeather(double latitude, double longitude){
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        final Date currentDate = new Date();
        tv_today.setText(format.format(currentDate));

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
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong("offset", timezoneOffset);
                        editor.apply();

                        //현재 날씨
                        JsonObject current = object.getAsJsonObject("current");
                        if (current != null){
                            float temp_current = current.get("temp").getAsFloat() - 273;
                            float temp_feels_like = current.get("feels_like").getAsFloat() - 273;
                            int humidity = current.get("humidity").getAsInt();
                            float wind = current.get("wind_speed").getAsFloat();

                            long sunrise = current.get("sunrise").getAsLong();
                            long sunset = current.get("sunset").getAsLong();
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

                                TextView tv_tip = new TextView(MainActivity.this);
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

                                TextView tv_tip = new TextView(MainActivity.this);
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

                            if (mainWeather.equals("Rain") || mainWeather.equals("Drizzle")
                                    || mainWeather.equals("Thunderstorm") || mainWeather.equals("Tornado")){
                                ll_background.setBackground(day ? getDrawable(R.drawable.bg_rain) : getDrawable(R.drawable.bg_rain_night));
                                sbColor = day? R.color.sb_rain : R.color.sb_rain_night;

                                TextView tv_tip = new TextView(MainActivity.this);
                                if (mainWeather.equals("Rain") || mainWeather.equals("Drizzle")){
                                    tv_tip.setText("* 외출 시 우산 꼭 챙기세요!");
                                }else{
                                    tv_tip.setText("밖이 위험하니 가능하면 장시간 실외활동은 삼가해주세요!");
                                }
                                tv_tip.setTextColor(getResources().getColor(R.color.font_white));
                                tv_tip.setTypeface(Typeface.createFromAsset(getAssets(), "font/js_dongkang.otf"));
                                tv_tip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                ll_tips.addView(tv_tip);
                            }
                            WeatherImageUtils.setStatusBarColor(MainActivity.this, sbColor);
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

                                if (max - min > 10){
                                    TextView tv_tip = new TextView(MainActivity.this);
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
                                    int temp = hour.get("temp").getAsInt() - 273;
                                    JsonArray hourlyWeather = hour.getAsJsonArray("weather");
                                    if (hourlyWeather != null){
                                        String weather = hourlyWeather.get(0).getAsJsonObject()
                                                .get("main").getAsString();
                                        HourlyWeatherView hwv = new HourlyWeatherView(MainActivity.this,
                                                time, weather, temp);
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

    private void kakaolink(){
        String text = userNickname+"님이 있는 곳의 날씨정보입니다.\n" +
                "위치 : "+tv_location.getText().toString()+"\n" +
                "현재 날씨 : "+tv_temperature.getText().toString()+"ºC, "+tv_weather.getText().toString()+"\n" +
                tv_temp_min.getText().toString()+", "+tv_temp_max.getText().toString()+"\n\n"+
                "이곳에 지내기에 적당한 옷차림은 다음과 같습니다.\n" +
                "겉옷 : "+tv_outer.getText().toString()+"\n" +
                "상의 : "+tv_top.getText().toString()+"\n" +
                "하의 : "+tv_bottoms.getText().toString();

        TextTemplate params = TextTemplate.newBuilder(text, LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com").build()).build();
        Map<String, String> serverCallbackArgs = new HashMap<String, String>();

        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }
}