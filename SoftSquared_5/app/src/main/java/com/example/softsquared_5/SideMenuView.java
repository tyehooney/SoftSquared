package com.example.softsquared_5;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class SideMenuView extends LinearLayout {
    private Context mContext;
    private LinearLayout ll_other_locations;
    private RoundImageView iv_profile;
    private ImageView btn_back, btn_add;
    private TextView tv_nickname, btn_logout;

    private long id;
    private String nickname, imgUrl;

    private List<String> locationList = new ArrayList<>();

    public EventListener listener;

    public void setEventListener(EventListener l){listener = l;}

    public interface EventListener{
        void btnCancel();
        void btnLogout();
    }

    public SideMenuView(Context context, long id, String nickname, @Nullable String imgUrl) {
        super(context);
        this.mContext = context;

        this.id = id;
        this.nickname = nickname;
        this.imgUrl = imgUrl;

        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.view_side_menu, this, true);

        ll_other_locations = findViewById(R.id.linearLayout_other_locations);

        iv_profile = findViewById(R.id.imageView_profile);
        iv_profile.setRectRadius(50.f);
        if (imgUrl != null)
            Glide.with(mContext).load(imgUrl).into(iv_profile);

        btn_back = findViewById(R.id.button_menu_back);
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.btnCancel();
            }
        });
        btn_add = findViewById(R.id.imageView_add_location);

        tv_nickname = findViewById(R.id.textView_menu_nickname);
        tv_nickname.setText(nickname+"님");

        btn_logout = findViewById(R.id.textView_logout);
        btn_logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.btnLogout();
            }
        });

        getLocations();
    }

    private void getLocations(){}
}
