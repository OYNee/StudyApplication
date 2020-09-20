package com.example.MA02_20150253;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;
import java.util.StringTokenizer;

public class MyDailyModifyActivity extends AppCompatActivity{

    public static AlarmManager mAlarmMgr = null;
    public static PendingIntent mAlarmIntent = null;

    private EditText etUpdateTitle;
    private EditText etUpdateMemo;

    private int d_day;
    private int alarm;
    private int tAlarm;
    private int success;

    private MyStudyDBHelper studyDBHelper;

    private String date;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_add);
        studyDBHelper = new MyStudyDBHelper(this);

        etUpdateTitle = (EditText) findViewById(R.id.etNewTitle);
        etUpdateMemo = (EditText) findViewById(R.id.etNewMemo);

        MyDailyStudyDto myItem = (MyDailyStudyDto) getIntent().getSerializableExtra("data");

        SQLiteDatabase db = studyDBHelper.getReadableDatabase();

        String[] cols = null;
        String whereClause = "_id=?";
        String[] whereArgs = { Integer.toString(myItem.get_id()) };

        Cursor cursor = db.query(MyStudyDBHelper.DIARY_TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);
        while (cursor.moveToNext()) {

            id = cursor.getInt(0);
            String title = cursor.getString(1);
            String memo = cursor.getString(2);
            date = cursor.getString(3);
            success = cursor.getInt(4);
            alarm = cursor.getInt(5);
            tAlarm = alarm;
            d_day = cursor.getInt(6);


            etUpdateTitle.setText(title);
            etUpdateMemo.setText(memo);
        }
        studyDBHelper.close();

        Switch swD = (Switch)findViewById(R.id.switch1);
        Switch swA = (Switch)findViewById(R.id.switch2);
        Switch swS = (Switch)findViewById(R.id.switch3);

        if(alarm==1)
            swA.setChecked(true);
        if(d_day==1)
            swD.setChecked(true);
        if(success==1)
            swS.setChecked(true);

        swA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    alarm = 1;
                else
                    alarm = 0;
            }
        });

        swD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    d_day = 1;
                else
                    d_day = 0;
            }
        });

        swS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    success = 1;
                else
                    success = 0;
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnS:

                SQLiteDatabase db = studyDBHelper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put("title", etUpdateTitle.getText().toString());
                row.put("memo", etUpdateMemo.getText().toString());
                row.put("success", success);
                row.put("is_alarm", alarm);
                row.put("is_d_day", d_day);

                MyDailyStudyDto myItem = (MyDailyStudyDto) getIntent().getSerializableExtra("data");

                String whereClause = "_id=?";
                String[] whereArgs = { Integer.toString(myItem.get_id()) };

                long result = db.update(MyStudyDBHelper.DIARY_TABLE_NAME, row, whereClause, whereArgs);

                if (result > 0) {
                    Toast.makeText(this, "수정했습니다.", Toast.LENGTH_SHORT).show();
                    if (alarm==1 && alarm !=tAlarm) {
                        setAlarmManager();
                    }
                    else if(alarm==0 && alarm !=tAlarm) {
                        Intent intent = new Intent(this, AlarmReceiver.class);
                        mAlarmIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        cancelAlarmManger();
                    }
                }
                else Toast.makeText(this, "Failure!", Toast.LENGTH_SHORT).show();

                studyDBHelper.close();

                finish();

                break;
            case R.id.btnC:
                finish();
                break;
        }
    }

    public void setAlarmManager(){

        StringTokenizer st = new StringTokenizer(date,"년, 월, 일" );
        int year = Integer.parseInt(st.nextToken());
        int month = Integer.parseInt(st.nextToken())-1;
        int date = Integer.parseInt(st.nextToken());
        mAlarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();

        cal.set(year, month, date-1);

        mAlarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmIntent);
    }

    public void cancelAlarmManger() {
        if (mAlarmIntent != null) {
            AlarmManager m = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent p = PendingIntent.getActivity(this,id,intent,PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mAlarmMgr.cancel(mAlarmIntent);
            m.cancel(mAlarmIntent);
            m.cancel(p);
            mAlarmMgr.cancel(p);
            mAlarmIntent.cancel();
            mAlarmMgr = null;
            mAlarmIntent = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("나가시겠습니까?")
                        .setMessage("지금 나가시면 수정된 내용이 반영되지 않습니다.")
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
