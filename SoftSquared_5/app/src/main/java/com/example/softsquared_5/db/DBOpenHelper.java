package com.example.softsquared_5.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.UnsupportedEncodingException;

public class DBOpenHelper {
    private static final String DB_NAME = "Locations.db";
    private static final int DB_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mContext;

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
    public void insertColumn(long id, String name, double latitude, double longitude){
        String sql = "INSERT INTO "+Databases.CreateDB._TABLE_NAME+
                " (userId,name,lat,lon) VALUES(?,?,?,?)";

        SQLiteStatement insertSmt = mDB.compileStatement(sql);
        insertSmt.clearBindings();
        insertSmt.bindLong(1, id);
        insertSmt.bindString(2, name);
        insertSmt.bindDouble(3, latitude);
        insertSmt.bindDouble(4, longitude);

        insertSmt.executeInsert();
    }

//    Sort
    public Cursor sortColumn(long id, String base){
        return mDB.rawQuery("SELECT * FROM "+Databases.CreateDB._TABLE_NAME
                +" WHERE userId="+id+" ORDER BY "+base+";", null);
    }

//        Delete
    public boolean deleteColumn(long id, String name) {
        return mDB.delete(Databases.CreateDB._TABLE_NAME,
                "userId = "+id+" AND name = '"+name+"'", null)>0;
    }

//    Search
//    public Note getNoteFromDB(long id){
//        Note note = null;
//        try {
//            Cursor c = mDB.rawQuery("SELECT * FROM "+Databases.CreateDB._TABLE_NAME
//                    +" WHERE _id= "+id+";", null);
//
//            while(c.moveToNext()){
//                String title = c.getString(1);
//                String details = c.getString(2);
//                byte[] photo = c.getBlob(3);
//                Date lastEdited = c.getString(4).length() == 16 ? format2.parse(c.getString(4)) : format.parse(c.getString(4));
//
//                note = new Note(title, details);
//                note.setId(id);
//                note.setPhoto(photo);
//                note.setLastEdited(lastEdited);
//            }
//
//            return note;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
