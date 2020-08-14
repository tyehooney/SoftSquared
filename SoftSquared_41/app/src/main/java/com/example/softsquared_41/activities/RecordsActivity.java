package com.example.softsquared_41.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.softsquared_41.R;
import com.example.softsquared_41.Record;
import com.example.softsquared_41.RecordListAdapter;
import com.example.softsquared_41.database.DBOpenHelper;
import com.example.softsquared_41.database.Databases;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {
    private ListView lv_records;
    private TextView tv_no_records;
    private List<Record> records = new ArrayList<>();
    private RecordListAdapter adapter;

    private DBOpenHelper dbHelper;

    private boolean fromSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        lv_records = findViewById(R.id.listView_records);
        tv_no_records = findViewById(R.id.textView_no_records);

        dbHelper = new DBOpenHelper(RecordsActivity.this);

        Intent intent = getIntent();
        fromSave = intent.getBooleanExtra("saving", false);
        if (fromSave){
            int level = intent.getIntExtra("level", 0);
            int time = intent.getIntExtra("time", 0);

            Intent popupIntent = new Intent(RecordsActivity.this, SavePopupActivity.class);
            popupIntent.putExtra("level", level);
            popupIntent.putExtra("time", time);
            startActivity(popupIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new RecordListAdapter(RecordsActivity.this, records);
        lv_records.setAdapter(adapter);

        showRecords("level desc", "time asc");

        if (records.isEmpty()){
            lv_records.setVisibility(View.GONE);
            tv_no_records.setVisibility(View.VISIBLE);
        }else{
            lv_records.setVisibility(View.VISIBLE);
            tv_no_records.setVisibility(View.GONE);
        }
    }

    private void showRecords(String base, String secondBase){
        dbHelper.open();
        dbHelper.create();

        Cursor cursor = dbHelper.sortColumn(base+", "+secondBase);

        records.clear();

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(Databases.CreateDB.NAME));
            int level = cursor.getInt(cursor.getColumnIndex(Databases.CreateDB.LEVEL));
            String time = cursor.getString(cursor.getColumnIndex(Databases.CreateDB.TIME));

            Record record = new Record(name, level, time);
            records.add(record);
        }

        adapter.notifyDataSetChanged();
        dbHelper.close();
    }
}