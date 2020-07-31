package com.hoonhooney.softsquared_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static final String TAG = "MainActivity";

    private String destination = "부산";
    private int distance = 325;
    private int fuel = 100;
    private boolean isGoing = false;

    private Button button_switch, button_gas_station, button_reset, button_change_destination;
    private TextView textView_distance, textView_fuel, textView_destination;

    private SharedPreferences sharedPreferences;

    private CarThread carThread;

    private Map<String, Integer> distanceMap;

//    onCreate : View 지정
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        button_switch = findViewById(R.id.button_switch);
        button_switch.setOnClickListener(this);

        button_gas_station = findViewById(R.id.button_gas_station);
        button_gas_station.setOnClickListener(this);

        button_reset = findViewById(R.id.button_reset);
        button_reset.setOnClickListener(this);

        button_change_destination = findViewById(R.id.button_change_destination);
        button_change_destination.setOnClickListener(this);

        textView_distance = findViewById(R.id.textView_distance);
        textView_fuel = findViewById(R.id.textView_fuel);
        textView_destination = findViewById(R.id.textView_destination);

        sharedPreferences = getSharedPreferences("state_data", MODE_PRIVATE);

        distanceMap = new HashMap<>();
        distanceMap.put("부산", 325);
        distanceMap.put("울산", 307);
        distanceMap.put("광주", 268);
        distanceMap.put("대전", 140);
        distanceMap.put("대구", 237);
        distanceMap.put("인천", 27);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

//    onResume : textView에 상태 업데이트
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");

        String newDestination = sharedPreferences.getString("destination", "부산");
        if(!destination.equals(newDestination)){
            destination = newDestination;
            distance = distanceMap.get(destination);
            fuel = 100;
        }

        textView_destination.setText("목적지 : "+destination);
        textView_distance.setText("남은 거리 : "+distance+"km");
        textView_fuel.setText("연료 : "+fuel+"km");
    }

//    onPause : 진행 중인 thread 정지시키기
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if(carThread != null && carThread.isAlive()){
            carThread.interrupt();
        }
    }

//    onStop : 연료 기록 저장
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("fuel", fuel);
        editor.apply();
    }

//    onDestroy : sharedPreference data 초기화
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        sharedPreferences.edit().clear().apply();
    }

//    다시 시작할 때 상태 정보 불러오기
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");

        fuel = sharedPreferences.getInt("fuel", 100);
    }

//    onClick
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_switch:

                if (isGoing){
                    isGoing = false;
                    button_switch.setText("출발");
                    carThread.interrupt();
                }else{
                    isGoing = true;
                    button_switch.setText("정지");
                    carThread = new CarThread();
                    carThread.start();
                }

                break;
            case R.id.button_gas_station:

                startActivity(new Intent(MainActivity.this, GasStationActivity.class));

                break;
            case R.id.button_reset:

                fuel = 100;
                distance = destination != null ? distanceMap.get(destination) : distanceMap.get("부산");

                if (carThread != null && carThread.isAlive())
                    carThread.interrupt();

                textView_distance.setText("남은 거리 : "+distance+"km");
                textView_fuel.setText("연료 : "+fuel+"km");

                break;

            case R.id.button_change_destination:

                startActivity(new Intent(this, DestinationPopupActivity.class));

                break;
        }
    }

//    달리는 차 thread
    class CarThread extends Thread{
        public void run(){
            while(fuel > 0 && !isInterrupted()){
                try {
                    fuel--;
                    distance--;

                    textView_distance.setText("남은 거리 : "+distance+"km");
                    textView_fuel.setText("연료 : "+fuel+"km");

                    Thread.sleep(100);

                    if(distance <= 0){
                        textView_distance.setText("목적지 도착!");
                        this.interrupt();
                        break;
                    }
                }catch (InterruptedException ie){
                    Thread.currentThread().interrupt();
                }catch (Exception e){
                    Log.e(TAG, "an Exception in the method CarThread.run() : "+e.toString());
                }
            }

            isGoing = false;
            button_switch.setText("출발");
        }
    }
}