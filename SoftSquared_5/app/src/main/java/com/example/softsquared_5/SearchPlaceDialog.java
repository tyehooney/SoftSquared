package com.example.softsquared_5;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.softsquared_5.activities.LocationWeatherActivity;
import com.example.softsquared_5.db.DBOpenHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPlaceDialog {
    private Context mContext;
    private long id;

    public SearchPlaceDialog(Context context, long id){
        this.mContext = context;
        this.id = id;
    }

    public void set(){
        final Dialog dialog = new Dialog(mContext);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_search_place);

        dialog.show();

        final LinearLayout ll_result = dialog.findViewById(R.id.linearLayout_results);
        final EditText et_search = dialog.findViewById(R.id.editText_search_place);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                    return false;
                return true;
            }
        });
        Button btn_search = dialog.findViewById(R.id.button_search);

        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        et_search.requestFocus();
        imm.showSoftInput(et_search, 0);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_result.removeAllViews();

                String searchingPlace = et_search.getText().toString();

                RetrofitService searchService = MyRetrofit.getInstance().getPlaceService();

                Call<JsonObject> call =
                        searchService.getPlaces(searchingPlace, "textquery", mContext.getString(R.string.search_fields),
                                mContext.getString(R.string.google_cp_app_key));

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()){
                            JsonObject object = response.body();
                            if (object != null){
                                JsonArray candidates = object.getAsJsonArray("candidates");
                                for (int i = 0; i < candidates.size(); i++) {
                                    JsonObject candidate = candidates.get(i).getAsJsonObject();
                                    final String name = candidate.get("name").getAsString();
                                    JsonObject geo = candidate.get("geometry").getAsJsonObject()
                                            .get("location").getAsJsonObject();
                                    final double lat = geo.get("lat").getAsDouble();
                                    final double lon = geo.get("lng").getAsDouble();

                                    TextView tv_result = new TextView(mContext);
                                    tv_result.setText(name + "\n("+candidate.get("formatted_address")
                                            .getAsString()+")");
                                    tv_result.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //db에 저장
                                            DBOpenHelper dbHelper = new DBOpenHelper(mContext);
                                            dbHelper.open();
                                            dbHelper.create();
                                            dbHelper.insertColumn(id, name, lat, lon);
                                            dbHelper.close();
                                            //새 activity 호출
                                            Intent intent = new Intent(mContext, LocationWeatherActivity.class);
                                            intent.putExtra("place", name);
                                            intent.putExtra("lat", lat);
                                            intent.putExtra("lon", lon);
                                            mContext.startActivity(intent);
                                            imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                                            dialog.dismiss();
                                        }
                                    });

                                    ll_result.addView(tv_result);
                                    ll_result.invalidate();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        });
    }
}
