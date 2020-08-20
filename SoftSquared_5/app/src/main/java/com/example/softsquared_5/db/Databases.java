package com.example.softsquared_5.db;

import android.provider.BaseColumns;

public final class Databases {

    public static final class CreateDB implements BaseColumns{
        public static final String USERID = "userId";
        public static final String NAME = "name";
        public static final String LATITUDE = "lat";
        public static final String LONGITUDE = "lon";
        public static final String _TABLE_NAME = "locations";
        public static final String _CREATE = "create table if not exists "+_TABLE_NAME+"("
                +_ID+" integer PRIMARY KEY AUTOINCREMENT, "
                +USERID+" integer not null, "
                +NAME+" text not null, "
                +LATITUDE+" real not null, "
                +LONGITUDE+" real not null);";
    }
}

