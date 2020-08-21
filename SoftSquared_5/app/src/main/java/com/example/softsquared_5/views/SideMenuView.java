package com.example.softsquared_5.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.softsquared_5.R;
import com.example.softsquared_5.SearchPlaceDialog;
import com.example.softsquared_5.activities.LocationWeatherActivity;
import com.example.softsquared_5.db.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SideMenuView extends LinearLayout {
    private Context mContext;
    private LinearLayout ll_other_locations;
    private RoundImageView iv_profile;
    private ImageView btn_back, btn_add;
    private TextView tv_nickname, btn_share, btn_logout;

    private long id;
    private String nickname, imgUrl;

    private List<String> locationList = new ArrayList<>();

    public EventListener listener;

    private DBOpenHelper dbHelper;

    public void setEventListener(EventListener l){listener = l;}

    public interface EventListener{
        void btnCancel();
        void btnShare();
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
        btn_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchPlaceDialog(mContext, id).set();
            }
        });

        tv_nickname = findViewById(R.id.textView_menu_nickname);
        tv_nickname.setText(nickname+"님");

        btn_share = findViewById(R.id.textView_share);
        btn_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.btnShare();
            }
        });

        btn_logout = findViewById(R.id.textView_logout);
        btn_logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.btnLogout();
            }
        });

        dbHelper = new DBOpenHelper(mContext);

        getLocations();
    }

    private void getLocations(){
        ll_other_locations.removeAllViews();
        locationList.clear();

        dbHelper.open();
        dbHelper.create();

        Cursor cursor = dbHelper.sortColumn(id, "name");

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lon = cursor.getDouble(cursor.getColumnIndex("lon"));
            Log.d("db", name);
            addLocation(name, lat, lon);
        }

        dbHelper.close();
    }

    private void addLocation(final String location, final double lat, final double lon){
        final TextView tv_location = new TextView(mContext);
        tv_location.setText(location);
        tv_location.setTextColor(mContext.getResources().getColor(R.color.font_black));
        tv_location.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LocationWeatherActivity.class);
                intent.putExtra("place", location);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                mContext.startActivity(intent);
            }
        });
        tv_location.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("지역 삭제")
                        .setMessage("해당 지역을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbHelper.open();
                                dbHelper.create();
                                dbHelper.deleteColumn(id, location);
                                dbHelper.close();

                                ll_other_locations.removeView(tv_location);
                                ll_other_locations.invalidate();
                            }
                        }).setNegativeButton("아니오", null).create().show();
                return true;
            }
        });
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        tv_location.setLayoutParams(params);
        ll_other_locations.addView(tv_location);
        ll_other_locations.invalidate();
    }
}
