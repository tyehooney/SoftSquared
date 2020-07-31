package com.hoonhooney.softsquared_2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//  도착지 선택하는 Popup Activity
public class DestinationPopupActivity extends Activity {
    private ListView listView_destinations;
    private String[] destinations = {"부산", "울산", "광주", "대전", "대구", "인천"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_destination_popup);

        listView_destinations = findViewById(R.id.listView_destinations);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, destinations);

        listView_destinations.setAdapter(adapter);

        listView_destinations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences sharedPreferences = getSharedPreferences("state_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("destination", destinations[i]);
                editor.apply();
                finish();
            }
        });
    }
}