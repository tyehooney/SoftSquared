package com.example.softsquared_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 3000);
    }

    private class SplashHandler implements Runnable{
        @Override
        public void run() {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}