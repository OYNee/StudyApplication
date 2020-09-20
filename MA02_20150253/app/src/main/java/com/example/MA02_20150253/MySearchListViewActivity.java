
package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MySearchListViewActivity extends AppCompatActivity {

    private SearchAdapter myAdapter;
    private ArrayList<MySearchDto> resultList;
    private MySearchDBHelper mySearchDBHelper;
    private AutoCompleteTextView autoCompleteTextView;
    private ListView listView;

    private ArrayList<String> words = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_search);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoTvSearch);

        mySearchDBHelper = new MySearchDBHelper(this);
        listView = (ListView) findViewById(R.id.lvResult);
        resultList = new ArrayList<MySearchDto>();
        myAdapter = new SearchAdapter(this, R.layout.activity_search_result, resultList);
        listView.setAdapter(myAdapter);

        resultList.clear();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, words);
        autoCompleteTextView.setAdapter(adapter);

        SQLiteDatabase db = mySearchDBHelper.getReadableDatabase();

        String[] cols = null;
        String whereClause = null;
        String[] whereArgs = null;

        Cursor cursor = db.query(MySearchDBHelper.SEARCH_TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            words.add(title);
        }

        cursor.close();
        mySearchDBHelper.close();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resultList.get(position).getLink()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MySearchListViewActivity.this,"연결에 실패했습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MySearchListViewActivity.this)
                        .setTitle("삭제")
                        .setMessage("목록에서 삭제하시겠습니까?")
                        .setPositiveButton("삭제",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //정보를 DB에 저장
                                SQLiteDatabase db = mySearchDBHelper.getWritableDatabase();

                                String whereClause = "_id=?";
                                String[] whereArgs = {Integer.toString(resultList.get(position).get_id())};

                                long result = db.delete(MySearchDBHelper.SEARCH_TABLE_NAME, whereClause, whereArgs);
                                if (result > 0) Toast.makeText(MySearchListViewActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                                mySearchDBHelper.close();
                                viewList(null);
                            }
                        })
                        .setNegativeButton("취소", null).show();

                return true;
            }
        });
        viewList(null);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnSearch:
                viewList(autoCompleteTextView.getText().toString());
        }
    }

    private void viewList(String keyword) {
        resultList.clear();
        SQLiteDatabase db = mySearchDBHelper.getReadableDatabase();
        String[] cols = null;
        String selection = null;
        String[] selectArgs = null;

        if(keyword!=null) {
            selection = "title=?";
            selectArgs = new String[]{keyword};
        }

        Cursor cursor = db.query(MySearchDBHelper.SEARCH_TABLE_NAME, cols, selection, selectArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            String link = cursor.getString(3);
            MySearchDto mySearchDto = new MySearchDto(_id, title, desc, link);
            resultList.add(mySearchDto);
        }
        cursor.close();
        mySearchDBHelper.close();

        myAdapter.notifyDataSetChanged();
    }
}
