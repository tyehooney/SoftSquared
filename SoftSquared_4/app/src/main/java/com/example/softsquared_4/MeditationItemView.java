package com.example.softsquared_4;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class MeditationItemView extends LinearLayout {

    private Context mContext;
    private String title;
    private Drawable imageResource;
    private int backgroundResource, textColorResource;

    private LinearLayout linearLayout_item;
    private TextView textView_title;
    private ImageView imageView_item;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MeditationItemView(Context context, String title, Drawable imageResource,
                              int backgroundColorResource, int textColorResource){
        super(context, (AttributeSet)null);
        this.mContext = context;
        this.title = title;
        this.imageResource = imageResource;
        this.backgroundResource = backgroundColorResource;
        this.textColorResource = textColorResource;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.view_item_meditation, this, true);

        linearLayout_item = findViewById(R.id.linearLayout_item);
        textView_title = findViewById(R.id.textView_title);
        imageView_item = findViewById(R.id.imageView_item);

        GradientDrawable bgShape = (GradientDrawable) linearLayout_item.getBackground();
        bgShape.setColor(backgroundResource);

        textView_title.setText(title);
        textView_title.setTextColor(textColorResource);

        imageView_item.setImageDrawable(imageResource);
        imageView_item.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView_item.setBackground(new ShapeDrawable(new OvalShape()));
        imageView_item.setClipToOutline(true);
    }
}
