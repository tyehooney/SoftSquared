package com.hoonhooney.softsquared_3.database;

import android.provider.BaseColumns;

public final class Databases {

    public static final class CreateDB implements BaseColumns{
        public static final String TITLE = "title";
        public static final String DETAILS = "details";
        public static final String LAST_EDITED = "last_edited";
        public static final String _TABLE_NAME = "notes";
        public static final String _CREATE = "create table if not exists "+_TABLE_NAME+"("
                +_ID+" integer primary key ,"
                +TITLE+" text not null ,"
                +DETAILS+" text not null ,"
                +LAST_EDITED+" TEXT not null DEFAULT (datetime('now', 'localtime')));";
    }
}

