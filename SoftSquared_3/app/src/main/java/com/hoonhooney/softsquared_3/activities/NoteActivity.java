package com.hoonhooney.softsquared_3.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hoonhooney.softsquared_3.DbBitmapUtils;
import com.hoonhooney.softsquared_3.ExifUtils;
import com.hoonhooney.softsquared_3.Note;
import com.hoonhooney.softsquared_3.R;
import com.hoonhooney.softsquared_3.database.DBOpenHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = "NoteActivity";
    private final int GET_GALLERY_IMAGE = 200;

    private ImageView button_back, button_save, button_add_photo, imageView_photo;
    private EditText editText_title, editText_details;
    private Note note;
    private long id;
    private boolean edit = false;
    private DBOpenHelper dbHelper;

    private Bitmap photoBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        button_back = findViewById(R.id.button_back);
        button_save = findViewById(R.id.button_save);
        button_add_photo = findViewById(R.id.button_add_photo);
        imageView_photo = findViewById(R.id.imageView_photo);
        editText_title = findViewById(R.id.editText_title);
        editText_details = findViewById(R.id.editText_details);

        dbHelper = new DBOpenHelper(NoteActivity.this);
        dbHelper.open();

        //새 글 생성인지 기존 글 수정인지 확인
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            edit = bundle.getBoolean("edit", false);
            if (edit){
                id = bundle.getLong("id");

                note = dbHelper.searchNoteFromDB(id);

                if (note != null){
                    editText_title.setText(note.getTitle());
                    editText_details.setText(note.getDetails());

                    if (note.getPhoto() != null){
                        photoBitmap = DbBitmapUtils.getImage(note.getPhoto());
                        imageView_photo.setVisibility(View.VISIBLE);
                        imageView_photo.setImageBitmap(photoBitmap);
                    }
                }
            }
        }

        //뒤로 가기
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //사진 첨부
        button_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(photoIntent, GET_GALLERY_IMAGE);
            }
        });

        //사진 삭제 기능
        imageView_photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setTitle("사진 삭제")
                        .setMessage("해당 사진을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                photoBitmap = null;
                                imageView_photo.setImageBitmap(null);
                                imageView_photo.setVisibility(View.GONE);

                                Toast.makeText(NoteActivity.this, "사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("아니오", null)
                .create().show();

                return false;
            }
        });

        //글 저장
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String title = editText_title.getText().toString();
                final String details = editText_details.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setTitle("저장");
                if (title.isEmpty() || details.isEmpty()){
                    builder.setMessage("제목과 내용을 모두 입력해주세요!")
                            .setPositiveButton("예", null)
                            .create().show();
                }else{
                    builder.setMessage("해당 노트를 저장하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    byte[] photoByteArray = photoBitmap == null ? null : DbBitmapUtils.getBytes(photoBitmap);

                                    if(edit){
                                        dbHelper.updateColumn(id, title, details, photoByteArray, new Date(System.currentTimeMillis()));
                                    }
                                    else{
                                        dbHelper.insertColumn(System.currentTimeMillis(), title, details, photoByteArray, new Date(System.currentTimeMillis()));
                                    }

                                    Toast.makeText(NoteActivity.this, "저장 완료!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).setNegativeButton("아니오", null)
                            .create()
                            .show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri photoUri = data.getData();

                Bitmap tempBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                int correctDegree = ExifUtils.getExifOrientation(getRealPathFromURI(photoUri));
                photoBitmap = ExifUtils.getRotatedBitmap(tempBitmap, correctDegree);

                imageView_photo.setVisibility(View.VISIBLE);
                imageView_photo.setImageBitmap(photoBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    이미지 절대경로 가져오기
    private String getRealPathFromURI(Uri contentURI) {
        String result;

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }
}