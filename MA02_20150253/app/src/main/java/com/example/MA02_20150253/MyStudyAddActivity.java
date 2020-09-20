
package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MyStudyAddActivity extends AppCompatActivity {

    private Calendar calendarS;
    private Calendar calendarE;
    private boolean start;
    private String resultS;
    private String resultE;

    private MyStudyDBHelper myDBHelper;

    private EditText etNewName;
    private EditText etNewTotal;
    private TextView tvNewStartDate;
    private TextView tvNewEndDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_add);

        myDBHelper = new MyStudyDBHelper(this);

        etNewName = (EditText) findViewById(R.id.etNewName);
        etNewTotal = (EditText) findViewById(R.id.etNewTotal);
        tvNewStartDate = (TextView) findViewById(R.id.tvNewStartDate);
        tvNewEndDate = (TextView) findViewById(R.id.tvNewEndDate);

        tvNewStartDate.setText(Calendar.getInstance().get(Calendar.YEAR)+"년 " + (Calendar.getInstance().get(Calendar.MONTH)+1)+"월 " +Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "일");
        tvNewEndDate.setText(Calendar.getInstance().get(Calendar.YEAR)+"년 " + (Calendar.getInstance().get(Calendar.MONTH)+1)+"월 " +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"일");
        resultS = Calendar.getInstance().get(Calendar.YEAR)+"년 " + (Calendar.getInstance().get(Calendar.MONTH)+1)+"월 " +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"일";
        resultE = Calendar.getInstance().get(Calendar.YEAR)+"년 " + (Calendar.getInstance().get(Calendar.MONTH)+1)+"월 " +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"일";

        tvNewStartDate.setOnClickListener(mClickListener);
        tvNewEndDate.setOnClickListener(mClickListener);
        calendarS = Calendar.getInstance();
        calendarE = Calendar.getInstance();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:

                SQLiteDatabase db = myDBHelper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put("name", etNewName.getText().toString());
                row.put("total", etNewTotal.getText().toString());
                row.put("start_date", resultS);
                row.put("end_date", resultE);
                row.put("progress", "0");

                long result = db.insert(MyStudyDBHelper.STUDY_TABLE_NAME, null, row);

                if (result > 0) Toast.makeText(this, "추가했습니다.", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Failure!", Toast.LENGTH_SHORT).show();

                myDBHelper.close();

                finish();

                break;

            case R.id.btnAddCancel:
                finish();
                break;
        }
    }


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvNewStartDate:
                    start = true;
                    new DatePickerDialog(MyStudyAddActivity.this, mDateSetListener, calendarS.get(Calendar.YEAR), calendarS.get(Calendar.MONTH), calendarS.get(Calendar.DATE)).show();
                    break;

                case R.id.tvNewEndDate:
                    start = false;
                    new DatePickerDialog(MyStudyAddActivity.this, mDateSetListener, calendarE.get(Calendar.YEAR), calendarE.get(Calendar.MONTH), calendarE.get(Calendar.DATE)).show();
                    break;
            }
        }
    };

    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar calendarTemp = Calendar.getInstance();
                    calendarTemp.set(year,month,dayOfMonth);
                    if(start) {
                        if(calendarTemp.getTimeInMillis()> calendarE.getTimeInMillis())
                            return;
                        resultS = year + "년 " + (month+1) + "월 " + dayOfMonth + "일";
                        tvNewStartDate.setText(resultS);
                        calendarS.set(year,month,dayOfMonth);
                    }
                    else {
                        if(calendarTemp.getTimeInMillis()< calendarS.getTimeInMillis())
                            return;
                        resultE = year + "년 " + (month+1) + "월 " + dayOfMonth + "일";
                        tvNewEndDate.setText(resultE);
                        calendarE.set(year,month,dayOfMonth);
                    }
                }
            };



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
