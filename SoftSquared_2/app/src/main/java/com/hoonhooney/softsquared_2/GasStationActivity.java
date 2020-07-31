package com.hoonhooney.softsquared_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GasStationActivity extends AppCompatActivity {
    static final String TAG = "Activity2";

    private int fuel;

    private TextView textView_fuel_status;
    private EditText editText_fuel;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_station);
        Log.d(TAG, "onCreate");

        textView_fuel_status = findViewById(R.id.textView_fuel_status);

        editText_fuel = findViewById(R.id.editText_fuel);

        sharedPreferences = getSharedPreferences("state_data", MODE_PRIVATE);

        fuel = sharedPreferences.getInt("fuel", 0);
        textView_fuel_status.setText("현재 연료 : "+fuel+"km");
    }

//    연료 채우기
    public void chargeFuel(View view){
        fuel = fuel + Integer.parseInt(editText_fuel.getText().toString());
        editText_fuel.setText("");
        textView_fuel_status.setText("현재 연료 : "+fuel+"km");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("fuel", fuel);
        editor.apply();

        Toast.makeText(this, "주입 완료 : 현재 "+fuel+"km를 달릴 수 있습니다.", Toast.LENGTH_LONG).show();
    }
}