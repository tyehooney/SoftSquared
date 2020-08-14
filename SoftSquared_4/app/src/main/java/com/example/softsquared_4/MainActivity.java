package com.example.softsquared_4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout_list;

    private String[] titles = {"싱그러운 아침", "평화로운 호수", "해돋이", "부드러운 피아노", "천국", "완벽한 비"};
    private Map<String, String> itemsMap = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout_list = findViewById(R.id.linearLayout_list);

        Resources res = getResources();

        itemsMap.put("싱그러운 아침", "morning");
        itemsMap.put("평화로운 호수", "lake");
        itemsMap.put("해돋이", "sunrise");
        itemsMap.put("부드러운 피아노", "piano");
        itemsMap.put("천국", "heaven");
        itemsMap.put("완벽한 비", "rain");

        for (final String title : titles) {
            final String code = itemsMap.get(title);
            final int drawableId = res.getIdentifier(code, "drawable", getPackageName());

            MeditationItemView itemView = new MeditationItemView(this, title,
                    res.getDrawable(drawableId),
                    res.getColor(res.getIdentifier(code, "color", getPackageName())),
                    res.getColor(res.getIdentifier(code+"Text","color",getPackageName())));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, MeditationActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("code", code);
                    intent.putExtra("background", drawableId);
                    startActivity(intent);
                }
            });

            linearLayout_list.addView(itemView);
        }
    }
}