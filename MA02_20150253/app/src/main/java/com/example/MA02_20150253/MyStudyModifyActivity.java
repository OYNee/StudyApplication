package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;


public class MyStudyModifyActivity extends AppCompatActivity {

    private Calendar calendarS;
    private Calendar calendarE;

    private boolean start;
    private String resultS;
    private String resultE;

    private SQLiteDatabase db;
    private MyStudyDBHelper myDBHelper;

    private EditText etUpdateName;
    private EditText etUpdateTotal;
    private TextView tvUpdateStartDate;
    private TextView tvUpdateEndDate;

    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_add);

        calendarS = Calendar.getInstance();
        calendarE = Calendar.getInstance();

        myDBHelper = new MyStudyDBHelper(this);

        etUpdateName = (EditText) findViewById(R.id.etNewName);
        etUpdateTotal = (EditText) findViewById(R.id.etNewTotal);
        tvUpdateStartDate = (TextView)findViewById(R.id.tvNewStartDate);
        tvUpdateEndDate = (TextView)findViewById(R.id.tvNewEndDate);

        tvUpdateStartDate.setOnClickListener(mClickListener);
        tvUpdateEndDate.setOnClickListener(mClickListener);

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
            progress = Integer.parseInt(cursor.getString(3));
            String startDate = cursor.getString(4);
            String endDate = cursor.getString(5);

            etUpdateName.setText(name);
            etUpdateTotal.setText(total);
            tvUpdateStartDate.setText(startDate);
            tvUpdateEndDate.setText(endDate);

            StringTokenizer st = new StringTokenizer(startDate,"년, 월, 일" );
            int year = Integer.parseInt(st.nextToken());
            int month = Integer.parseInt(st.nextToken())-1;
            int date = Integer.parseInt(st.nextToken());
            calendarS.set(year,month,date);
            st = new StringTokenizer(endDate,"년, 월, 일" );
            year = Integer.parseInt(st.nextToken());
            month = Integer.parseInt(st.nextToken())-1;
            date = Integer.parseInt(st.nextToken());
            calendarE.set(year,month,date);

        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnAdd:
                int position = getIntent().getIntExtra("position", 0);
                ArrayList<MyStudyDto> myItemList = (ArrayList<MyStudyDto>) getIntent().getSerializableExtra("dataList");

                db = myDBHelper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put("name", etUpdateName.getText().toString());
                try {
                    if(progress>Integer.parseInt(etUpdateTotal.getText().toString())) {
                        row.put("total", progress);
                    } else {
                        row.put("total", etUpdateTotal.getText().toString());
                    }
                } catch (Exception e) {
                    row.put("total", "1");
                }
                row.put("start_date", tvUpdateStartDate.getText().toString());
                row.put("end_date", tvUpdateEndDate.getText().toString());

                String whereClause = "_id=?";
                String[] whereArgs = {Integer.toString(myItemList.get(position).get_id())};

                long result = db.update(MyStudyDBHelper.STUDY_TABLE_NAME, row, whereClause, whereArgs);

                if (result > 0) Toast.makeText(this, "수정했습니다.", Toast.LENGTH_SHORT).show();
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
                    new DatePickerDialog(MyStudyModifyActivity.this, mDateSetListener, calendarS.get(Calendar.YEAR), calendarS.get(Calendar.MONTH), calendarS.get(Calendar.DATE)).show();
                    break;

                case R.id.tvNewEndDate:
                    start = false;
                    new DatePickerDialog(MyStudyModifyActivity.this, mDateSetListener, calendarE.get(Calendar.YEAR), calendarE.get(Calendar.MONTH), calendarE.get(Calendar.DATE)).show();
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
                        tvUpdateStartDate.setText(resultS);
                        calendarS.set(year,month,dayOfMonth);
                    }
                    else {
                        if(calendarTemp.getTimeInMillis()< calendarS.getTimeInMillis())
                            return;
                        resultE = year + "년 " + (month+1) + "월 " + dayOfMonth + "일";
                        tvUpdateEndDate.setText(resultE);
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
