package com.hoonhooney.softsquared_3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.hoonhooney.softsquared_3.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBOpenHelper {
    private static final String DB_NAME = "Note.db";
    private static final int DB_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mContext;

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(Databases.CreateDB._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Databases.CreateDB._TABLE_NAME);

            onCreate(sqLiteDatabase);
        }
    }

    public DBOpenHelper(Context context){
        this.mContext = context;
    }

    public DBOpenHelper open() throws SQLException{
        mDBHelper = new DatabaseHelper(mContext, DB_NAME, null, DB_VERSION);

        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

//    Insert
    public void insertColumn(long id, String title, String details, byte[] photo, Date lastEdited){
        String sql = "INSERT INTO "+Databases.CreateDB._TABLE_NAME+
                " (_id, title, details, photo, last_edited) VALUES(?,?,?,?,?)";

        SQLiteStatement insertSmt = mDB.compileStatement(sql);
        insertSmt.clearBindings();
        insertSmt.bindLong(1, id);
        insertSmt.bindString(2, title);
        insertSmt.bindString(3, details);
        if (photo != null)
            insertSmt.bindBlob(4, photo);
        insertSmt.bindString(5, format.format(lastEdited));
        insertSmt.executeInsert();

    }

//    Sort
    public Cursor sortColumn(String base){
        Cursor c = mDB.rawQuery("SELECT * FROM "+Databases.CreateDB._TABLE_NAME
                +" ORDER BY "+base+";", null);
        return c;
    }

//    Search
    public Note searchNoteFromDB(long id){
        Note note = null;
        try {
            Cursor c = mDB.rawQuery("SELECT * FROM "+Databases.CreateDB._TABLE_NAME
                    +" WHERE _id= "+id+";", null);

            while(c.moveToNext()){
                String title = c.getString(1);
                String details = c.getString(2);
                byte[] photo = c.getBlob(3);
                Date lastEdited = format.parse(c.getString(4));

                note = new Note(title, details);
                note.setId(id);
                note.setPhoto(photo);
                note.setLastEdited(lastEdited);
            }

            return note;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

//    Update
    public void updateColumn(long id, String title, String details, byte[] photo, Date lastEdited){
        String sql = "UPDATE "+Databases.CreateDB._TABLE_NAME+
                " SET title=?, details=?, photo=?, last_edited=? WHERE _id=?";

        SQLiteStatement updateSmt = mDB.compileStatement(sql);
        updateSmt.clearBindings();
        updateSmt.bindString(1, title);
        updateSmt.bindString(2, details);
        if (photo != null)
            updateSmt.bindBlob(3, photo);
        updateSmt.bindString(4, format.format(lastEdited));
        updateSmt.bindLong(5, id);
        updateSmt.executeInsert();

    }

//    Delete
    public boolean deleteColumn(long id){
        return mDB.delete(Databases.CreateDB._TABLE_NAME,
                "_id="+id, null)>0;
    }
}
