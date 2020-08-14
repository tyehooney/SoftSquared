package com.example.softsquared_4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MeditationActivity extends AppCompatActivity {
    private LinearLayout linearLayout_sounds;
    private TextView textView_title;
    private ImageView imageView_background, button_play, button_timer, button_add;
    private SeekBar seekBar_volume;

    private MediaPlayer mainMusic;
    private List<MediaPlayer> sounds = new ArrayList<>();
    private boolean playing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        linearLayout_sounds = findViewById(R.id.linearLayout_sounds);
        textView_title = findViewById(R.id.textView_meditation_title);
        imageView_background = findViewById(R.id.imageView_background);
        button_play = findViewById(R.id.button_play);
        button_timer = findViewById(R.id.button_timer);
        button_add = findViewById(R.id.button_add);
        seekBar_volume = findViewById(R.id.seekBar_volume);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String code = intent.getStringExtra("code");
        int bgResource = intent.getIntExtra("background", 0);
        textView_title.setText(title);
        imageView_background.setImageResource(bgResource);

        Resources res = getResources();

        mainMusic = MediaPlayer.create(MeditationActivity.this, res.getIdentifier(code, "raw", getPackageName()));
        mainMusic.setLooping(true);
        sounds.add(mainMusic);

        int i = 1;
        while (res.getIdentifier(code+i, "raw", getPackageName()) != 0){
            int soundId = res.getIdentifier(code+i, "raw", getPackageName());
            MediaPlayer sound = MediaPlayer.create(MeditationActivity.this, soundId);
            sound.setLooping(true);
            sounds.add(sound);

            int buttonId = res.getIdentifier("sound"+i, "drawable", getPackageName());
            ImageView button_sound = new ImageView(MeditationActivity.this);
            button_sound.setImageDrawable(res.getDrawable(buttonId));
            button_sound.setMaxWidth(100);
            button_sound.setMaxHeight(100);
            //TODO : add onClickListener
            linearLayout_sounds.addView(button_sound);
            i++;
        }

        if (playing){
            for (MediaPlayer sound : sounds){
                if (!sound.isPlaying())
                    sound.start();
            }
        }

        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playing = !playing;

                if (playing){
                    for(MediaPlayer sound : sounds){
                        if (!sound.isPlaying())
                            sound.start();
                    }
                    button_play.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }else{
                    for(MediaPlayer sound : sounds){
                        if (sound.isPlaying())
                            sound.pause();
                    }
                    button_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (MediaPlayer sound : sounds){
            if (sound.isPlaying()){
                sound.stop();
                sound.release();
            }
        }
    }
}