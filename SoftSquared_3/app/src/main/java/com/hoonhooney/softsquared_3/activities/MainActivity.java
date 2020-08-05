package com.hoonhooney.softsquared_3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hoonhooney.softsquared_3.Note;
import com.hoonhooney.softsquared_3.NoteListAdapter;
import com.hoonhooney.softsquared_3.R;
import com.hoonhooney.softsquared_3.database.DBOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ListView listView_notes;
    private ImageView button_add;
    private TextView textView_if_none;

    private DBOpenHelper dbHelper;

    private List<Note> noteList = new ArrayList<>();
    private NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView_notes = findViewById(R.id.listView_notes);
        button_add = findViewById(R.id.button_add);

        textView_if_none = findViewById(R.id.textView_if_none);

        dbHelper = new DBOpenHelper(MainActivity.this);
        dbHelper.open();
        dbHelper.create();

        noteListAdapter = new NoteListAdapter(this, noteList);
        listView_notes.setAdapter(noteListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        showDB("last_edited");

        if (noteList.isEmpty()){
            textView_if_none.setVisibility(View.VISIBLE);
        } else{
            textView_if_none.setVisibility(View.GONE);
        }

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });
    }

    private void showDB(String base) {
        Cursor cursor = dbHelper.sortColumn(base);
        Log.d(TAG, "DB size: "+cursor.getCount());

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        noteList.clear();

        while(cursor.moveToNext()){
            try{
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String details = cursor.getString(cursor.getColumnIndex("details"));
                String lastEdited = cursor.getString(cursor.getColumnIndex("last_edited"));

                Note note = new Note(title, details);
                note.setId(id);
                note.setLastEdited(format.parse(lastEdited));
                noteList.add(note);
            } catch (ParseException e){
                e.printStackTrace();
            }
        }

        noteListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }
}