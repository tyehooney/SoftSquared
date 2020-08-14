package com.example.softsquared_41.database;

import android.provider.BaseColumns;

public final class Databases {

    public static final class CreateDB implements BaseColumns{
        public static final String NAME = "name";
        public static final String LEVEL = "level";
        public static final String TIME = "time";
        public static final String _TABLE_NAME = "records";
        public static final String _CREATE = "create table if not exists "+_TABLE_NAME+"("
                +_ID+" integer PRIMARY KEY AUTOINCREMENT,"
                +NAME+" text not null ,"
                +LEVEL+" integer not null ,"
                +TIME+" text not null);";
    }
}

