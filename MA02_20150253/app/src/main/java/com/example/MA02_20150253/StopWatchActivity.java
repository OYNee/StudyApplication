
package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import java.util.Calendar;

public class StopWatchActivity extends AppCompatActivity{

    private MyStudyDBHelper myStudyDBHelper;

    private Chronometer chronometer;
    private SensorManager sensorManager;

    private boolean start = false;
    private long timeWhenStopped = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stopwatch_activity);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        myStudyDBHelper = new MyStudyDBHelper(this);
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                new AlertDialog.Builder(StopWatchActivity.this)
                        .setTitle("공부한 시간 저장")
                        .setMessage("지금까지 공부한 시간을 저장하시겠습니까?")
                        .setPositiveButton("저장",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //정보를 DB에 저장
                                SQLiteDatabase db = myStudyDBHelper.getWritableDatabase();
                                ContentValues row = new ContentValues();

                                Calendar calendar = Calendar.getInstance();
                                Cursor cursor = db.rawQuery("SELECT year, month, date, time FROM my_study_data_table WHERE year = '" + calendar.get(Calendar.YEAR) + "' and month = '" + calendar.get(Calendar.MONTH) +
                                        "' and date = '" + calendar.get(Calendar.DATE) + "';", null);
                                if (!cursor.moveToFirst()) {
                                    row.put("year", calendar.get(Calendar.YEAR));
                                    row.put("month", calendar.get(Calendar.MONTH));
                                    row.put("date", calendar.get(Calendar.DATE));
                                    row.put("time", timeWhenStopped*-1);
                                    long result = db.insert(MyStudyDBHelper.DATA_TABLE_NAME, null, row);
                                    if(result>0) Toast.makeText(StopWatchActivity.this, "기록OK!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.execSQL("UPDATE my_study_data_table SET time = '"+ (cursor.getLong(3)+timeWhenStopped*-1) +"' WHERE year = '"
                                            + calendar.get(Calendar.YEAR) + "' and month = '" + calendar.get(Calendar.MONTH) +
                                            "' and date = '" + calendar.get(Calendar.DATE) + "';");
                                    start = false;
                                    timeWhenStopped = 0;
                                    chronometer.stop();
                                    chronometer.setBase(SystemClock.elapsedRealtime());
                                    Toast.makeText(StopWatchActivity.this, "기록OK!", Toast.LENGTH_SHORT).show();
                                }
                                myStudyDBHelper.close();
                            }
                        })
                        .setNegativeButton("취소", null).show();
                break;
            case R.id.btnReset:
                new AlertDialog.Builder(StopWatchActivity.this)
                        .setTitle("리셋을 누르셨습니다.")
                        .setMessage("지금까지 측정된 시간을 버리시겠어요?")
                        .setPositiveButton("예",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                start = false;
                                timeWhenStopped = 0;
                                chronometer.stop();
                                chronometer.setBase(SystemClock.elapsedRealtime());
                            }
                        })
                        .setNegativeButton("취소", null).show();

                break;

            case R.id.imgBtnGoHistory:
                Intent intent = new Intent(this, StudyDataActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int delay = SensorManager.SENSOR_DELAY_UI;
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                //return;
            }
            float[] values = event.values;
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                //if (event.accuracy != sensorManager.SENSOR_STATUS_UNRELIABLE) {
                    if (values[0] == 0) {
                        if (!start) {
                            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                            chronometer.start();
                            start = true;
                        }
                    } else {
                        if (start) {
                            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                            chronometer.stop();

                            start = false;
                        }
                    }
                //}
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //처리할 필요 x
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (timeWhenStopped < -1000) {
                    new AlertDialog.Builder(this)
                            .setTitle("나가시겠습니까?")
                            .setMessage("지금 측정된 기록이 모두 없어집니다.")
                            .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();

                } else
                    finish();
        }

        return true;


    }
}
