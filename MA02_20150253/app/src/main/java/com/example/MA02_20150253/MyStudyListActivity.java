package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyStudyListActivity extends AppCompatActivity {
    private TextView tvText;

    private char select = 0;

    private ArrayList<MyStudyDto> myItemList;
    private MyStudyListAdapter myAdapter;
    private ListView listView;
    private MyStudyDBHelper myDBHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_list);

        myDBHelper = new MyStudyDBHelper(this);
        myItemList = new ArrayList<MyStudyDto>();

        myAdapter = new MyStudyListAdapter(this, R.layout.study_item, myItemList);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(myAdapter);

        tvText =  (TextView) findViewById(R.id.tvText);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyStudyListActivity.this, MyStudyDetailedActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("dataList", myItemList);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MyStudyListActivity.this)
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
                                    Toast.makeText(MyStudyListActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        viewTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.all:
                select = 0;
                viewTable();
                break;

            case R.id.complete:
                select = 1;
                viewTable();
                break;

            case R.id.inProgress:
                select = 2;
                viewTable();
                break;

            case R.id.addStudy:
                intent = new Intent(MyStudyListActivity.this, MyStudyAddActivity.class);
                startActivity(intent);
                break;

            case R.id.search:
                intent = new Intent(MyStudyListActivity.this, MyStudySearchActivity.class);
                startActivity(intent);
                break;

        }
        return false;
    }


    private void viewTable() {
        myItemList.clear();

        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        String[] cols = null;
        String whereClause = null;
        String[] whereArgs = null;

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

            switch (select) {
                case 0:
                    tvText.setText("전체 목록");
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
                    else {
                        MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step1, name, rr);
                        myItemList.add(newItem);
                    }
                    break;

                case 1:
                    tvText.setText("완료된 공부");
                    if(r >= 100) {
                        MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step6, name, rr);
                        myItemList.add(newItem);
                    }
                    break;

                case 2:
                    tvText.setText("진행 중인 공부");
                    if(r >= 100) {
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
                    else {
                        MyStudyDto newItem = new MyStudyDto(_id, R.drawable.step1, name, rr);
                        myItemList.add(newItem);
                    }
                    break;
            }
        }

        cursor.close();
        myDBHelper.close();

        Comparator<MyStudyDto> nameAsc = new Comparator<MyStudyDto>() {
            @Override
            public int compare(MyStudyDto o1, MyStudyDto o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        Collections.sort(myItemList, nameAsc);

        myAdapter.notifyDataSetChanged();
    }

}
