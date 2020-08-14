package com.example.softsquared_41;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.softsquared_41.activities.MainActivity;
import com.example.softsquared_41.activities.RecordsActivity;

public class GameOverDialog {
    private Context mContext;

    public GameOverDialog(Context context){
        this.mContext = context;
    }

    public void set(final int level, final int time){

        final Dialog dialog = new Dialog(mContext);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_game_over);

        dialog.setCancelable(false);
        dialog.show();

        TextView tv_level = (TextView)dialog.findViewById(R.id.textView_record_level);
        TextView tv_time = (TextView)dialog.findViewById(R.id.textView_record_time);

        Button btn_restart = (Button)dialog.findViewById(R.id.button_record_restart);
        Button btn_save = (Button)dialog.findViewById(R.id.button_record_save);
        Button btn_exit = (Button)dialog.findViewById(R.id.button_record_exit);

        tv_level.setText("Level : "+level);

        int mSec = time % 100;
        int sec = (time / 100) % 60;
        int min = (time / 100) / 60;

        String timeToString = String.format("%02d:%02d:%02d", min, sec, mSec);
        tv_time.setText("time : "+timeToString);

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).resetGame();
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saving record to db...
                Intent intent = new Intent(mContext, RecordsActivity.class);
                intent.putExtra("saving", true);
                intent.putExtra("level", level);
                intent.putExtra("time", time);
                mContext.startActivity(intent);
                dialog.dismiss();
                ((MainActivity)mContext).finish();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((MainActivity)mContext).finish();
            }
        });
    }
}
