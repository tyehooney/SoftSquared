package com.example.softsquared_41;

import android.content.Context;
import android.util.TypedValue;

public class ChangeUtil {
    public static int changeToDp(Context context, int num){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                num,
                context.getResources().getDisplayMetrics()
        );
    }
}
