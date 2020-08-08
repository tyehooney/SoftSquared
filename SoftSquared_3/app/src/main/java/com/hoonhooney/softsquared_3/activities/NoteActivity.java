package com.hoonhooney.softsquared_3.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hoonhooney.softsquared_3.utils.DbBitmapUtils;
import com.hoonhooney.softsquared_3.utils.ExifUtils;
import com.hoonhooney.softsquared_3.Note;
import com.hoonhooney.softsquared_3.R;
import com.hoonhooney.softsquared_3.database.DBOpenHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = "NoteActivity";
    private final int GET_GALLERY_IMAGE = 200;
    private final int TAKE_PHOTO = 201;

    private ImageView button_back, button_save, button_add_photo, imageView_photo;
    private EditText editText_title, editText_details;
    private Note note;
    private long id;
    private boolean edit = false;
    private DBOpenHelper dbHelper;

    private InputMethodManager imm;

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

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        //새 글 생성인지 기존 글 수정인지 확인
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            edit = bundle.getBoolean("edit", false);
        }
        if (edit){
            id = bundle.getLong("id");

            note = dbHelper.getNoteFromDB(id);

            if (note != null){
                editText_title.setText(note.getTitle());
                editText_details.setText(note.getDetails());

                if (note.getPhoto() != null){
                    photoBitmap = DbBitmapUtils.getImage(note.getPhoto());
                    imageView_photo.setVisibility(View.VISIBLE);
                    imageView_photo.setImageBitmap(photoBitmap);
                }
            }
        }else{
            editText_title.requestFocus();
            imm.showSoftInput(editText_title, 0);
        }

        setListeners();
    }

    private void setListeners(){
        //뒤로 가기
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //제목에서 엔터 키 누를 때 내용으로 전환
        editText_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER){
                    editText_details.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //사진 첨부
        button_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText_title.hasFocus())
                    imm.hideSoftInputFromWindow(editText_title.getWindowToken(),0);
                else if (editText_details.hasFocus())
                    imm.hideSoftInputFromWindow(editText_details.getWindowToken(), 0);

                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setTitle("사진 첨부")
                        .setMessage("작업을 선택하세요.")
                        .setNegativeButton("사진 촬영", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (intent.resolveActivity(getPackageManager()) != null){
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    if (photoFile != null){
                                        Uri photoUri = FileProvider.getUriForFile(NoteActivity.this,
                                                getPackageName(), photoFile);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                        startActivityForResult(intent, TAKE_PHOTO);
                                    }
                                }
                            }
                        })
                        .setNeutralButton("앨범에서 가져오기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                                photoIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(photoIntent, GET_GALLERY_IMAGE);
                            }
                        }).setPositiveButton("취소", null)
                        .create().show();
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
                if (editText_title.hasFocus())
                    imm.hideSoftInputFromWindow(editText_title.getWindowToken(),0);
                else if (editText_details.hasFocus())
                    imm.hideSoftInputFromWindow(editText_details.getWindowToken(), 0);

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

        if (resultCode == RESULT_OK && data != null && data.getData() != null){
            if (requestCode == GET_GALLERY_IMAGE || requestCode == TAKE_PHOTO){
                try {
                    Uri photoUri = data.getData();

                    Bitmap tempBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    int correctDegree = requestCode == GET_GALLERY_IMAGE ?
                            ExifUtils.getExifOrientation(getRealPathFromURI(photoUri))
                            : ExifUtils.getExifOrientation(photoUri.toString());
                    photoBitmap = ExifUtils.getRotatedBitmap(tempBitmap, correctDegree);

                    imageView_photo.setVisibility(View.VISIBLE);
                    imageView_photo.setImageBitmap(photoBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    //새 사진 파일 생성
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        return image;
    }
}