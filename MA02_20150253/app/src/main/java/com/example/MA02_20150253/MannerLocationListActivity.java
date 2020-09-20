package com.example.MA02_20150253;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import java.util.ArrayList;

public class MannerLocationListActivity extends AppCompatActivity{

    private MannerLocationCursorAdapter myCursorAdapter;
    private MannerLocationDBHelper myLocDBHelper;
    private AutoCompleteTextView autoCompleteTextView;
    private ListView listView;
    private Cursor cursor;


    private String[] addr;

    private ArrayList<String> words = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_search);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoTvSearch);
        myCursorAdapter = new MannerLocationCursorAdapter(this, cursor);

        myLocDBHelper = new MannerLocationDBHelper(this);
        listView = (ListView) findViewById(R.id.lvResult);
        listView.setAdapter(myCursorAdapter);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, words);
        autoCompleteTextView.setAdapter(adapter);

        SQLiteDatabase db = myLocDBHelper.getReadableDatabase();

        String[] cols = null;
        String whereClause = null;
        String[] whereArgs = null;

        final Cursor cursor = db.query(MannerLocationDBHelper.TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);
        addr = new String[cursor.getCount()];
        int i=0;
        while (cursor.moveToNext()) {

            addr[i] = cursor.getString(2);
            String title = cursor.getString(1);
            Log.e("TAG", addr[i]);
            words.add(title);
            i++;
        }
        myLocDBHelper.close();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("Address", addr[position]);
                Log.e("TAG", addr[position]);
                setResult(RESULT_OK, intent);
                finish();


            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnSearch:
                SQLiteDatabase db = myLocDBHelper.getReadableDatabase();
                cursor = db.rawQuery("select * from " + MannerLocationDBHelper.TABLE_NAME + " where title = '" + autoCompleteTextView.getText().toString() + "'", null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DB에서 데이터를 읽어와 Adapter에 설정
        SQLiteDatabase db = myLocDBHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + MannerLocationDBHelper.TABLE_NAME, null);
        myCursorAdapter.changeCursor(cursor);
        myLocDBHelper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}
