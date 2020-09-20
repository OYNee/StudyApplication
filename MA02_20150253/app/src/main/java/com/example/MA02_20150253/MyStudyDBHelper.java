package com.example.MA02_20150253;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyStudyDBHelper extends SQLiteOpenHelper{
    private final static String DB_NAME = "my_study_db";
    public static final String STUDY_TABLE_NAME = "my_study_table";
    public static final String DIARY_TABLE_NAME = "my_diary_table";
    public static final String DATA_TABLE_NAME = "my_study_data_table";

    public MyStudyDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStudyTable = "CREATE TABLE " + STUDY_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, total TEXT, progress TEXT, start_date TEXT, end_date TEXT)";
        db.execSQL(createStudyTable);

        String createDailyTable = "CREATE TABLE " + DIARY_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, memo TEXT, date TEXT, success INTEGER, is_alarm INTEGER, is_d_day INTEGER)";
        db.execSQL(createDailyTable);

        String createDataTable = "CREATE TABLE " + DATA_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "year INTEGER, month INTEGER, date INTEGER, time LONG)";
        db.execSQL(createDataTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
