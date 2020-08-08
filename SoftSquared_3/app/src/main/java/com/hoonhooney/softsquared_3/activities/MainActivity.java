package com.hoonhooney.softsquared_3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
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

    private String sortBy = "last_edited desc";

    private ListView listView_notes;
    private ImageView button_add, button_search, button_sort, imageView_logo;
    private TextView textView_if_none;
    private EditText editText_search;
    private boolean searchShown = false;
    private InputMethodManager imm;

    private DBOpenHelper dbHelper;

    private List<Note> noteList = new ArrayList<>();
    private List<Note> copyList = new ArrayList<>();
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
        button_search = findViewById(R.id.button_search);
        button_sort = findViewById(R.id.button_sort);

        imageView_logo = findViewById(R.id.imageView_logo_small);

        textView_if_none = findViewById(R.id.textView_if_none);

        editText_search = findViewById(R.id.editText_search);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

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

        showDB(sortBy);

        if (noteList.isEmpty()){
            textView_if_none.setVisibility(View.VISIBLE);
        } else{
            textView_if_none.setVisibility(View.GONE);
        }
<<<<<<< HEAD
    }

    private void setListeners(){
=======

>>>>>>> parent of 155554e... timestamp  format 수정,  textView 전부 안보이는 현상 수정
        //정렬 방식 변경
        button_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);

                getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.sort_last_edited:
                                sortBy = "last_edited desc";
                                break;
                            case R.id.sort_first_created:
                                sortBy = "_id";
                                break;
                            case R.id.sort_title_text:
                                sortBy = "title";
                                break;
                            default:
                                break;
                        }

                        Toast.makeText(MainActivity.this,
                                menuItem.getTitle()+"으로 정렬", Toast.LENGTH_SHORT).show();
                        showDB(sortBy);
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        //추가
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        //검색
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchShown = !searchShown;
                if (searchShown){
                    editText_search.setVisibility(View.VISIBLE);
                    //자동으로 키보드 보이기
                    editText_search.requestFocus();
                    imm.showSoftInput(editText_search, 0);
                }else{
                    editText_search.setText("");
                    editText_search.setVisibility(View.GONE);
                    imm.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
                }
            }
        });

        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editText_search.getText().toString();
                search(text);
            }
        });

        editText_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER){
                    imm.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
                }
                return false;
            }
        });

        //맨 위로 스크롤
        imageView_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Note note : noteList)
                    note.setFocused(false);
                noteListAdapter.notifyDataSetChanged();
                listView_notes.smoothScrollToPosition(0);
            }
        });
    }

    private void showDB(String base) {
        Cursor cursor = dbHelper.sortColumn(base);
        Log.d(TAG, "DB size: "+cursor.getCount());

<<<<<<< HEAD
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
=======
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
>>>>>>> parent of 155554e... timestamp  format 수정,  textView 전부 안보이는 현상 수정

        noteList.clear();
        copyList.clear();

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

        copyList.addAll(noteList);

        noteListAdapter.notifyDataSetChanged();
    }

    //검색
    private void search(String str){
        noteList.clear();

        if (str.length() == 0)
            showDB("last_edited desc");
        else{

            for (Note note : copyList){
                if ((note.getTitle().toLowerCase().contains(str) ||
                        note.getDetails().toLowerCase().contains(str)) &&
                        !noteList.contains(note)){
                    noteList.add(note);
                }
            }

            noteListAdapter.notifyDataSetChanged();
        }
    }

    //권한 확인
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dbHelper.close();
    }
}