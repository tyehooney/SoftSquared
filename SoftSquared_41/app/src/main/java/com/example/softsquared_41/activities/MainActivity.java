package com.example.softsquared_41.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.softsquared_41.GameOverDialog;
import com.example.softsquared_41.R;
import com.example.softsquared_41.StoneView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int OPPOSITE = -1;
    private static final int STRAIGHT = 1;
    private static final int LEFT = 0;

    private RelativeLayout rl_game;
    private TextView tv_level, tv_timer, tv_first;
    private Button btn_stack;
    private ImageView btn_restart, btn_pause;

    private CountdownThread cdThread = null;
    private CountThread countThread = null;
    private StackThread stackThread = null;
    private Handler cdHandler, countHandler, stackHandler;

    private boolean playing = false;
    private boolean running = false;

    private int runningTime = 0;
    private int level = 1;

    private StoneView currentStone;
    private int lastXOfStone, lastWidthOfStone;
    private int distance = 0;
    private int direction = 1;

    private int deviceWidth;

    private boolean first;

    private List<StoneView> stones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rl_game = findViewById(R.id.relativeLayout_game);
        tv_level = findViewById(R.id.textView_level);
        tv_timer = findViewById(R.id.textView_timer);
        tv_first = findViewById(R.id.textView_first);

        btn_stack = findViewById(R.id.button_stack);
        btn_restart = findViewById(R.id.button_restart);
        btn_pause = findViewById(R.id.button_pause);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        deviceWidth = dm.widthPixels;

        first = true;

        setFirstStone();
        setHandlers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cdThread != null)
            cdThread.interrupt();

        btn_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
        btn_pause.setVisibility(View.VISIBLE);
        btn_stack.setEnabled(false);
        tv_first.setText("Pause");
        tv_first.setVisibility(View.VISIBLE);

        if (countThread != null)
            countThread.interrupt();
        if (stackThread != null)
            stackThread.interrupt();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(){
        //게임 시작 / 스택
        btn_stack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if (!playing && !running){
                        cdThread = new CountdownThread();
                        cdThread.start();
                        btn_stack.setEnabled(false);
                    }else{
                        //stacking
                        stackThread.interrupt();
                        //if success
                        int realX = currentStone.getFirstDirection() == LEFT ?
                                distance - currentStone.getStoneWidth() : deviceWidth - distance;
                        if (realX > lastXOfStone+lastWidthOfStone || realX+currentStone.getStoneWidth() < lastXOfStone){
                            //gameOver
                            countThread.interrupt();
                            playing = false;

                            tv_first.setVisibility(View.VISIBLE);
                            tv_first.setText("oops...");

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    GameOverDialog dialog = new GameOverDialog(MainActivity.this);
                                    dialog.set(level, runningTime);
                                }
                            }, 1000);
                        }else{
                            lastXOfStone = realX;
                            lastWidthOfStone = currentStone.getStoneWidth();
                            stones.add(currentStone);
                            level++;
                            distance = 0;
                            tv_level.setText("Level "+level);
                            callStone(level);
                        }
                    }
                }
                return false;
            }
        });

        //일시 정지 / 재생
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running){
                    btn_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                    tv_first.setText("Pause");
                    tv_first.setVisibility(View.VISIBLE);
                    if (countThread != null)
                        countThread.interrupt();
                    if (stackThread != null)
                        stackThread.interrupt();
                }else{
                    running = true;
                    cdThread = new CountdownThread();
                    cdThread.start();
                    btn_pause.setImageResource(R.drawable.ic_baseline_pause);
                }
                btn_stack.setEnabled(false);
            }
        });

        //리셋
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void setHandlers(){
        //카운트다운(게임 시작)
        cdHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if (msg.arg1 > 0){
                    tv_first.setText(msg.arg1+"");
                    btn_pause.setVisibility(View.GONE);
                }else if(msg.arg1 == 0){
                    tv_first.setText("Start!");
                    btn_pause.setVisibility(View.GONE);
                }else{
                    playing = true;
                    running = true;

                    countThread = new CountThread();
                    countThread.start();

                    if (level == 1 && first)
                        callStone(level);
                    else{
                        stackThread = new StackThread();
                        stackThread.start();
                    }

                    tv_first.setVisibility(View.GONE);
                    btn_stack.setEnabled(true);
                    btn_pause.setImageResource(R.drawable.ic_baseline_pause);
                    btn_pause.setVisibility(View.VISIBLE);
                }
            }
        };

        //게임 경과 시간
        countHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if (running){
                    int mSec = msg.arg1 % 100;
                    int sec = (msg.arg1 / 100) % 60;
                    int min = (msg.arg1 / 100) / 60;

                    String time = String.format("%02d:%02d:%02d", min, sec, mSec);
                    tv_timer.setText(time);
                }else{
                    btn_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
                }
            }
        };

        //쌓기
        stackHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if (running){
                    currentStone.setStone(msg.arg1);
                }
            }
        };
    }

    private void setFirstStone(){
        currentStone = new StoneView(this, 0);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        currentStone.setId(View.generateViewId());
        rl_game.addView(currentStone, params);
        rl_game.invalidate();

        lastXOfStone = (deviceWidth/2) - (currentStone.getStoneWidth()/2);
        lastWidthOfStone = currentStone.getStoneWidth();

        stones.add(currentStone);
    }

    private void callStone(int level){
        if (stones.size() > 10){
            StoneView secondBottom = stones.get(1);
            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) secondBottom.getLayoutParams();
            lp.removeRule(RelativeLayout.ABOVE);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            secondBottom.setLayoutParams(lp);

            rl_game.removeView(stones.remove(0));
        }

        StoneView stone = new StoneView(this, level);
        stone.setId(View.generateViewId());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, currentStone.getId());
        stone.setLayoutParams(params);
        rl_game.invalidate();
        rl_game.addView(stone);

        currentStone = stone;

        first = false;

        stackThread = new StackThread();
        stackThread.start();
    }

    public void resetGame(){
        tv_first.setText("click 'STACK!' button below\nif you're ready");
        tv_first.setVisibility(View.VISIBLE);
        tv_timer.setText("00:00:00");
        tv_level.setText("Level 1");
        level = 1;
        runningTime = 0;
        distance = 0;
        btn_pause.setVisibility(View.GONE);

        if (running)
            running = false;
        if (playing)
            playing = false;

        if (cdThread != null && !cdThread.isInterrupted())
            cdThread.interrupt();
        if (stackThread != null && !stackThread.isInterrupted())
            stackThread.interrupt();

        for (int i = 0; i < stones.size(); i++) {
            rl_game.removeView(stones.get(i));
        }
        if (!stones.contains(currentStone)) {
            rl_game.removeView(currentStone);
        }

        rl_game.invalidate();
        stones.clear();

        first = true;

        setFirstStone();

        btn_stack.setEnabled(true);
    }

//    Threads
    class CountdownThread extends Thread{
        public void run(){
            int count = 3;
            while(count >= -1 && !isInterrupted()){
                try {
                    Message msg = cdHandler.obtainMessage();
                    msg.arg1 = count;
                    cdHandler.sendMessage(msg);
                    count--;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
            cdHandler.removeMessages(0);
        }
    }

    class CountThread extends Thread{
        public void run(){
            while(playing && !isInterrupted()){
                try {
                    Message msg = new Message();
                    msg.arg1 = runningTime++;
                    countHandler.sendMessage(msg);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            running = false;
            countHandler.removeMessages(0);
        }
    }

    class StackThread extends Thread{
        public void run(){
            while(playing && running && !isInterrupted()){
                try {
                    if (distance < 0 || distance < currentStone.getStoneWidth())
                        direction = STRAIGHT;
                    else if(distance > deviceWidth)
                        direction = OPPOSITE;

                    int velocity = 6 + (level/10);

                    distance += (velocity * direction);

                    Message msg = new Message();
                    msg.arg1 = distance;
                    stackHandler.sendMessage(msg);

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            stackHandler.removeMessages(0);
        }
    }
}