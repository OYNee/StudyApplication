package com.example.MA02_20150253;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySearchDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "my_search_db";
    public static final String SEARCH_TABLE_NAME = "my_search_table";

    public MySearchDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + SEARCH_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, description TEXT, link TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
