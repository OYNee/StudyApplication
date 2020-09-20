package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyStudyDetailedActivity extends AppCompatActivity {
    private ArrayList<MyStudyDto> myItemList;

    private SQLiteDatabase db;
    private MyStudyDBHelper myDBHelper;

    private TextView tvDName;
    private EditText etProgress;
    private TextView tvDTotal;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvPercentage;

    private int totalInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_detailed);

        myDBHelper = new MyStudyDBHelper(this);

        myItemList = new ArrayList<MyStudyDto>();

        tvDName = (TextView) findViewById(R.id.tvDName);
        etProgress = (EditText) findViewById(R.id.etProgress);
        tvDTotal = (TextView) findViewById(R.id.tvDTotal);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        tvPercentage = (TextView) findViewById(R.id.tvPercentage);


    }

    @Override
    protected void onResume() {
        super.onResume();
        int position = getIntent().getIntExtra("position", 0);
        ArrayList<MyStudyDto> myItemList = (ArrayList<MyStudyDto>) getIntent().getSerializableExtra("dataList");


        myDBHelper = new MyStudyDBHelper(this);
        db = myDBHelper.getReadableDatabase();
        String[] cols = null;
        String whereClause = "_id=?";
        String[] whereArgs = { Integer.toString(myItemList.get(position).get_id()) };

        Cursor cursor = db.query(MyStudyDBHelper.STUDY_TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            String total = cursor.getString(2);
            try {
                totalInt = Integer.parseInt(total);
            } catch (Exception e) {
                totalInt = 1;
            }
            String progress = cursor.getString(3);
            String startDate = cursor.getString(4);
            String endDate = cursor.getString(5);

            tvDName.setText(name);
            etProgress.setText(progress);
            tvDTotal.setText(total);
            tvStartDate.setText(startDate);
            tvEndDate.setText("~ "+endDate);
            tvPercentage.setText(myItemList.get(position).getPercentage());
        }
    }

    public void onClick(View v) {
        int position = getIntent().getIntExtra("position", 0);
        ArrayList<MyStudyDto> myItemList = (ArrayList<MyStudyDto>) getIntent().getSerializableExtra("dataList");

        switch(v.getId()) {
            case R.id.btnOK:

                db = myDBHelper.getWritableDatabase();

                ContentValues row = new ContentValues();
                try {
                    if(totalInt<Integer.parseInt(etProgress.getText().toString())) {
                        row.put("progress", totalInt);
                    } else {
                        row.put("progress", etProgress.getText().toString());
                    }

                    String whereClause = "_id=?";
                    String[] whereArgs = {Integer.toString(myItemList.get(position).get_id())};

                    long result = db.update(MyStudyDBHelper.STUDY_TABLE_NAME, row, whereClause, whereArgs);

                    if (result > 0) {
                        Toast.makeText(this, "화이팅!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failure!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {
                    Toast.makeText(this, "진행 상황을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                myDBHelper.close();
                finish();
                break;

            case R.id.btnUpdate:

                Intent intent = new Intent(MyStudyDetailedActivity.this, MyStudyModifyActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("dataList", myItemList);
                startActivity(intent);

                myDBHelper.close();

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("나가시겠습니까?")
                        .setMessage("지금 나가시면 작성하던 내용이 반영되지 않습니다.")
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();

        }
        return true;
    }
}
