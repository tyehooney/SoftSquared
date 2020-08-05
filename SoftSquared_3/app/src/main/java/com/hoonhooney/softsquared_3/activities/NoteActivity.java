package com.hoonhooney.softsquared_3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hoonhooney.softsquared_3.Note;
import com.hoonhooney.softsquared_3.R;
import com.hoonhooney.softsquared_3.database.DBOpenHelper;

import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private ImageView button_back, button_save;
    private EditText editText_title, editText_details;
    private long id;
    private boolean edit = false;
    private DBOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        button_back = findViewById(R.id.button_back);
        button_save = findViewById(R.id.button_save);
        editText_title = findViewById(R.id.editText_title);
        editText_details = findViewById(R.id.editText_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            edit = bundle.getBoolean("edit", false);
            if (edit){
                id = bundle.getLong("id");
                editText_title.setText(bundle.getString("title", ""));
                editText_details.setText(bundle.getString("details", ""));
            }
        }

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Saving Note...
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setTitle("저장")
                        .setMessage("해당 노트를 저장하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbHelper = new DBOpenHelper(NoteActivity.this);
                                dbHelper.open();

                                String title = editText_title.getText().toString();
                                String details = editText_details.getText().toString();

                                if(edit){
                                    dbHelper.updateColumn(id, title, details, new Date(System.currentTimeMillis()));
                                }
                                else{
                                    dbHelper.insertColumn(System.currentTimeMillis(), title, details, new Date(System.currentTimeMillis()));
                                }

                                Toast.makeText(NoteActivity.this, "저장 완료!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).setNegativeButton("아니오", null)
                        .create()
                        .show();
            }
        });
    }
}