package com.example.softsquared_41.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.softsquared_41.R;
import com.example.softsquared_41.database.DBOpenHelper;

public class SavePopupActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_save_record);

        this.setFinishOnTouchOutside(false);

        final EditText et_name = (EditText)findViewById(R.id.editText_save_name);
        Button btn_save = (Button)findViewById(R.id.button_save_save);
        Button btn_exit = (Button)findViewById(R.id.button_save_exit);

        final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        et_name.requestFocus();
        imm.showSoftInput(et_name, 0);

        Intent intent = getIntent();
        final int level = intent.getIntExtra("level", 0);
        final int time = intent.getIntExtra("time", 0);

        et_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER){
                    imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString();
                if (name.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SavePopupActivity.this);
                    builder.setMessage("Please text your name!")
                            .setPositiveButton("OK", null)
                            .create().show();
                }else{
                    //saving record to db...
                    imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0);

                    DBOpenHelper dbHelper = new DBOpenHelper(SavePopupActivity.this);
                    dbHelper.open();
                    dbHelper.create();
                    dbHelper.insertColumn(name, level, time);

                    Toast.makeText(SavePopupActivity.this, "Success : saving new record", Toast.LENGTH_SHORT).show();
                    dbHelper.close();
                    finish();
                }
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
