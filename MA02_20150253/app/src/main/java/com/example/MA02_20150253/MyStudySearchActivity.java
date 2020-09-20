package com.example.MA02_20150253;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyStudySearchActivity extends AppCompatActivity{
    private AutoCompleteTextView etSearch;
    private ArrayList<MyStudyDto> myItemList;
    private MyStudyListAdapter myAdapter;
    private ListView searchList;
    private MyStudyDBHelper myDBHelper;

    ArrayList<String> words = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_search);

        etSearch = (AutoCompleteTextView) findViewById(R.id.autoTvSearch);

        myDBHelper = new MyStudyDBHelper(this);
        myItemList = new ArrayList<MyStudyDto>();
        myAdapter = new MyStudyListAdapter(this, R.layout.study_item, myItemList);
        searchList = (ListView) findViewById(R.id.lvResult);
        searchList.setAdapter(myAdapter);

        myItemList.clear();

        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        String[] cols = null;
        String whereClause = null;
        String[] whereArgs = null;

        Cursor cursor = db.query(MyStudyDBHelper.STUDY_TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(1);

            words.add(name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, words);
        etSearch.setAdapter(adapter);

        searchList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MyStudySearchActivity.this)
                        .setTitle("알림")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = myDBHelper.getWritableDatabase();

                                String whereClause = "_id=?";
                                String[] whereArgs = {Integer.toString(myItemList.get(position).get_id())};

                                long result = db.delete(MyStudyDBHelper.STUDY_TABLE_NAME, whereClause, whereArgs);

                                if (result > 0) {
                                    Toast.makeText(MyStudySearchActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                                }

                                myDBHelper.close();

                                viewTable();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();

                return true;
            }
        });
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnSearch:
                viewTable();

                break;
        }
    }

    private void viewTable() {
        myItemList.clear();

        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        String[] cols = null;
        String whereClause = "_id=?";
        String[] whereArgs = {etSearch.getText().toString()};

        Cursor cursor = db.query(MyStudyDBHelper.STUDY_TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String name = cursor.getString(1);
            String total = cursor.getString(2);
            String progress = cursor.getString(3);

            String rr = "0%";
            double r = 0;
            try {
                int a = Integer.parseInt(progress);
                int b = Integer.parseInt(total);
                r = ((double)a / (double)b) * 100;

                rr = Integer.toString((int)r) + "%";
            }
            catch (Exception e) {
            }

            if(r >= 100) {
                MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step6, name, rr);
                myItemList.add(newItem);
            }
            else if(r >= 80) {
                MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step5, name, rr);
                myItemList.add(newItem);
            }
            else if(r >= 60) {
                MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step4, name, rr);
                myItemList.add(newItem);
            }
            else if(r >= 40) {
                MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step3, name, rr);
                myItemList.add(newItem);
            }
            else if(r >=20) {
                MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step2, name, rr);
                myItemList.add(newItem);
            }
            else  {
                MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step1, name, rr);
                myItemList.add(newItem);
            }


            searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MyStudySearchActivity.this, MyStudyDetailedActivity.class);
                    intent.putExtra("name", myItemList.get(position).getName());
                    intent.putExtra("position", position);
                    intent.putExtra("dataList", myItemList);
                    startActivity(intent);

                    finish();

                }
            });
        }
        cursor.close();
        myDBHelper.close();

        myAdapter.notifyDataSetChanged();
    }
}