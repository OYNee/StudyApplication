package com.example.MA02_20150253;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MyDailyAddActivity extends AppCompatActivity{

    private Calendar cal;
    private EditText etNewTitle;
    private EditText etNewMemo;

    private int id;
    private int alarm;
    private int d_day;

    private MyStudyDBHelper studyDBHelper;

    public static AlarmManager mAlarmMgr = null;
    public static PendingIntent mAlarmIntent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_add);

        cal = Calendar.getInstance();

        studyDBHelper = new MyStudyDBHelper(this);

        etNewTitle = (EditText) findViewById(R.id.etNewTitle);
        etNewMemo = (EditText) findViewById(R.id.etNewMemo);

        alarm = 0;
        d_day = 0;

        Switch swD = (Switch)findViewById(R.id.switch1);
        Switch swA = (Switch)findViewById(R.id.switch2);
        Switch swS = (Switch)findViewById(R.id.switch3);
        swS.setVisibility(View.INVISIBLE);

        swD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    d_day = 1;
                else
                    d_day = 0;
            }
        });

        swA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    alarm = 1;
                else
                    alarm = 0;
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnS:
                SQLiteDatabase db = studyDBHelper.getWritableDatabase();

                String date = getIntent().getIntExtra("Year", cal.get(Calendar.YEAR)) + "년 " +
                        (getIntent().getIntExtra("Month", cal.get(Calendar.MONTH))+1) + "월 " +
                        getIntent().getIntExtra("Date", cal.get(Calendar.DAY_OF_MONTH)) + "일";


                ContentValues row = new ContentValues();
                row.put("title", etNewTitle.getText().toString());
                row.put("memo", etNewMemo.getText().toString());
                row.put("date", date);
                row.put("is_alarm", alarm);
                row.put("is_d_day", d_day);
                row.put("success", 0);

                if(alarm == 1) {
                    setAlarmManager();
                }

                long result = db.insert(MyStudyDBHelper.DIARY_TABLE_NAME, null, row);

                if (result > 0) Toast.makeText(this, "추가했습니다.", Toast.LENGTH_SHORT).show();
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

        mAlarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        int year = getIntent().getIntExtra("Year", cal.get(Calendar.YEAR));
        int month = getIntent().getIntExtra("Month", cal.get(Calendar.MONTH))+1;
        int date = getIntent().getIntExtra("Date", cal.get(Calendar.DAY_OF_MONTH));
        cal.set(year, month-1, date-1);

        mAlarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmIntent);
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
