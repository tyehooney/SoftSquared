package com.example.softsquared_41;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class StoneView extends FrameLayout {
    private static final int MAX_STONE_WIDTH = 150;
    private static final int MIN_STONE_WIDTH = 50;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private int direction;
    private Context mContext;
    private int start;

    private ImageView iv_stone;
    private int stoneWidth;

    public StoneView(Context context, int level) {
        super(context, (AttributeSet) null);
        this.mContext = context;

        LayoutInflater.from(mContext).inflate(R.layout.view_stone, this, true);
        iv_stone = findViewById(R.id.imageView_stone);
        LayoutParams params = (LayoutParams)iv_stone.getLayoutParams();

        int widthInDp = Math.max(MAX_STONE_WIDTH - (level*2), MIN_STONE_WIDTH);
        params.width = ChangeUtil.changeToDp(mContext, widthInDp);
        stoneWidth = params.width;

        direction = (int)((Math.random()*10000)%2);
        if (level == 0)
            params.gravity = Gravity.CENTER_HORIZONTAL;
        else{
            int changedWidth = ChangeUtil.changeToDp(mContext, widthInDp);
            start = changedWidth - (2 * changedWidth);
            if (direction == LEFT){
                params.gravity = Gravity.START;
                params.setMarginStart(start);
            }
            else if (direction == RIGHT){
                params.gravity = Gravity.END;
                params.setMarginEnd(start);
            }
        }

        iv_stone.setLayoutParams(params);
        invalidate();

        int num = (int)((Math.random()*10000)%5);
        iv_stone.setImageResource(getResources().getIdentifier("stone"+num, "drawable", mContext.getPackageName()));
    }

    public void setStone(int velocity){
        LayoutParams params = (LayoutParams) iv_stone.getLayoutParams();
        if (direction == LEFT){
            params.leftMargin = start + velocity;
        }else if (direction == RIGHT){
            params.rightMargin = start + velocity;
        }
        iv_stone.setLayoutParams(params);
        invalidate();
    }

    public int getStoneWidth(){
        return stoneWidth;
    }

    public int getFirstDirection(){return direction;}
}
