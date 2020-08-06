package com.hoonhooney.softsquared_3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    String[] permission_list = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

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

        checkPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        showDB("last_edited desc");

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
                byte[] photo = cursor.getBlob(cursor.getColumnIndex("photo"));
                String lastEdited = cursor.getString(cursor.getColumnIndex("last_edited"));

                Note note = new Note(title, details);
                note.setId(id);
                note.setPhoto(photo);
                note.setLastEdited(format.parse(lastEdited));
                noteList.add(note);
            } catch (ParseException e){
                e.printStackTrace();
            }
        }

        noteListAdapter.notifyDataSetChanged();
    }

    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]== PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(getApplicationContext(),"앱 권한 설정이 필요합니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}