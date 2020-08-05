package com.hoonhooney.softsquared_3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public long insertColumn(long id, String title, String details, Date lastEdited){
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put(Databases.CreateDB.TITLE, title);
        values.put(Databases.CreateDB.DETAILS, details);
        values.put(Databases.CreateDB.LAST_EDITED, format.format(lastEdited));

        return mDB.insert(Databases.CreateDB._TABLE_NAME, null, values);
    }

//    Sort
    public Cursor sortColumn(String base){
        Cursor c = mDB.rawQuery("SELECT * FROM "+Databases.CreateDB._TABLE_NAME
                +" ORDER BY "+base+";", null);
        return c;
    }

//    Select
    public Cursor selectColumns(){
        return mDB.query(Databases.CreateDB._TABLE_NAME, null, null,
                null, null, null, null);
    }

//    Update
    public boolean updateColumn(long id, String title, String details, Date lastEdited){
        ContentValues values = new ContentValues();
        values.put(Databases.CreateDB.TITLE, title);
        values.put(Databases.CreateDB.DETAILS, details);
        values.put(Databases.CreateDB.LAST_EDITED, format.format(lastEdited));

        return mDB.update(Databases.CreateDB._TABLE_NAME, values,
                "_id="+id, null) > 0;
    }

//    Delete
    public boolean deleteColumn(long id){
        return mDB.delete(Databases.CreateDB._TABLE_NAME,
                "_id="+id, null)>0;
    }
}
