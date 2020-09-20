package com.example.MA02_20150253;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class MyDailyDetailedActivity extends AppCompatActivity {
    private MyStudyDBHelper studyDBHelper;
    private TextView deTitle;
    private TextView deDate;
    private TextView deMemo;
    private TextView deAlarm;
    private TextView deDday;
    private TextView deSuccess;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detaild);

        studyDBHelper = new MyStudyDBHelper(this);

        deTitle = (TextView) findViewById(R.id.tvDeTitle);
        deDate = (TextView) findViewById(R.id.tvDeDate);
        deMemo = (TextView) findViewById(R.id.tvDeMemo);
        deDday = (TextView) findViewById(R.id.tvDday);
        deAlarm = (TextView) findViewById(R.id.tvAlarm);
        deSuccess = (TextView) findViewById(R.id.tvSuccess);
    }
    public void onClick(View v) {
        MyDailyStudyDto myItem = (MyDailyStudyDto) getIntent().getSerializableExtra("data");
        Intent intent;

        switch(v.getId()) {
            case R.id.btnUpdate:

                intent = new Intent(MyDailyDetailedActivity.this, MyDailyModifyActivity.class);
                intent.putExtra("data", myItem);
                startActivity(intent);

                studyDBHelper.close();

                break;

            case R.id.btnClose:

                finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        MyDailyStudyDto myItem = (MyDailyStudyDto) getIntent().getSerializableExtra("data");

        SQLiteDatabase db = studyDBHelper.getReadableDatabase();

        String[] cols = null;
        String whereClause = "_id=?";
        String[] whereArgs = { Integer.toString(myItem.get_id()) };

        Cursor cursor = db.query(MyStudyDBHelper.DIARY_TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            String memo = cursor.getString(2);
            String date = cursor.getString(3);
            int success = cursor.getInt(4);
            int alarm = cursor.getInt(5);
            int d_day = cursor.getInt(6);

            deTitle.setText(title);
            deDate.setText(date);
            deMemo.setText(memo);

            if (alarm == 0)
                deAlarm.setText("알람 Off");
            else
                deAlarm.setText("알람 On");
            if (d_day == 0)
                deDday.setText("D-Day 표시 Off");
            else
                deDday.setText("D-Day 표시 On");

            if (success == 0)
                deSuccess.setText("미완료");
            else
                deSuccess.setText("완료");

        }
        studyDBHelper.close();
    }
}
