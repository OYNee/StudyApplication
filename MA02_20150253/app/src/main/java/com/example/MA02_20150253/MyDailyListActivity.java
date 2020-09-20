package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class MyDailyListActivity extends AppCompatActivity {

    private ArrayList<MyDailyStudyDto> myDailyStudyDtoArrayList;
    private MyDailyStudyAdapter myDailyStudyAdapter;
    private ListView listView;
    private MyStudyDBHelper myDBHelper;
    private TextView textView;
    private String query;
    private char select = 0;
    int year;
    int month;
    int date;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_list);

        year = getIntent().getIntExtra("Year", Calendar.getInstance().get(Calendar.YEAR));
        month = getIntent().getIntExtra("Month", Calendar.getInstance().get(Calendar.MONTH));
        date = getIntent().getIntExtra("Date", Calendar.getInstance().get(Calendar.DATE));

        if(year < 1900) {
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH);
            date = Calendar.getInstance().get(Calendar.DATE);
        }

        query = year + "년 " + (month+1) + "월 " + date + "일";

        textView = (TextView)findViewById(R.id.tvListTitle);
        textView.setText(query);

        myDBHelper = new MyStudyDBHelper(this);
        myDailyStudyDtoArrayList = new ArrayList<MyDailyStudyDto>();
        myDailyStudyAdapter = new MyDailyStudyAdapter(this, R.layout.study_item_daily, myDailyStudyDtoArrayList);

        listView = (ListView) findViewById(R.id.dailyList);
        listView.setAdapter(myDailyStudyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyDailyListActivity.this, MyDailyDetailedActivity.class);
                intent.putExtra("data", myDailyStudyDtoArrayList.get(position));
                startActivity(intent);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MyDailyListActivity.this)
                        .setTitle("알림")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                                String whereClause = "_id=?";
                                String[] whereArgs = {Integer.toString(myDailyStudyDtoArrayList.get(position).get_id())};

                                long result = db.delete(MyStudyDBHelper.DIARY_TABLE_NAME, whereClause, whereArgs);

                                if (result > 0) {
                                    Toast.makeText(MyDailyListActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                                }

                                myDBHelper.close();
                                viewDailyTable();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewDailyTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.all:
                select = 0;
                viewDailyTable();

                break;

            case R.id.complete:
                select = 1;
                viewDailyTable();

                break;

            case R.id.inProgress:
                select = 2;
                viewDailyTable();

                break;

            case R.id.addStudy:
                intent = new Intent(MyDailyListActivity.this, MyDailyAddActivity.class);
                intent.putExtra("Year", year);
                intent.putExtra("Month", month);
                intent.putExtra("Date", date);
                startActivity(intent);

                break;
        }
        return false;
    }
    private void viewDailyTable() {

        myDailyStudyDtoArrayList.clear();

        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ MyStudyDBHelper.DIARY_TABLE_NAME + " WHERE date = '" + query + "';", null);

        while (cursor.moveToNext()) {
            Log.e("TAG", query + " ");
            int _id = cursor.getInt(0);
            String title = cursor.getString(1);
            MyDailyStudyDto newItem = null;

            switch (select) {
                case 0:
                    if(cursor.getInt(4)==0) {
                        newItem = new MyDailyStudyDto(_id, R.drawable.red, title);
                        myDailyStudyDtoArrayList.add(newItem);
                    } else {
                        newItem = new MyDailyStudyDto(_id, R.drawable.green, title);
                        myDailyStudyDtoArrayList.add(newItem);
                    }
                    break;

                case 1:
                    if(cursor.getInt(4)==0) {

                    } else {
                        newItem = new MyDailyStudyDto(_id, R.drawable.green, title);
                        myDailyStudyDtoArrayList.add(newItem);
                    }
                    break;

                case 2:
                    if(cursor.getInt(4)==0) {
                        newItem = new MyDailyStudyDto(_id, R.drawable.red, title);
                        myDailyStudyDtoArrayList.add(newItem);
                    } else {

                    }
                    break;
            }
        }

        cursor.close();
        myDBHelper.close();

        Comparator<MyDailyStudyDto> nameAsc = new Comparator<MyDailyStudyDto>() {
            @Override
            public int compare(MyDailyStudyDto o1, MyDailyStudyDto o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        };
        Collections.sort(myDailyStudyDtoArrayList, nameAsc);

        myDailyStudyAdapter.notifyDataSetChanged();
    }


}
