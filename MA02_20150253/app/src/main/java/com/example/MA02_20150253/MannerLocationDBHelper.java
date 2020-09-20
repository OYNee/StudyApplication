package com.example.MA02_20150253;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MannerLocationDBHelper extends SQLiteOpenHelper{
    private final static String DB_NAME = "mannerLocation_db";
    public static final String TABLE_NAME = "mannerLocation_table";

    public MannerLocationDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, address TEXT, latitude DOUBLE, longitude DOUBLE)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
