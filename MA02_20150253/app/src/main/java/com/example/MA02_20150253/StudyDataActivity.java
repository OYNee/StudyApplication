package com.example.MA02_20150253;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class StudyDataActivity extends AppCompatActivity{
    private MyStudyDBHelper myStudyDBHelper;
    private Calendar calendar;
    private TextView tvFirst;
    private TextView tvSecond;
    private TextView tvThird;
    private TextView tvFourth;
    private TextView tvFifth;
    private TextView tvDate;

    private TextView[] textViews;

    private long[] history;
    private DrawingView v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_data);

        tvFirst = (TextView)findViewById(R.id.tvFirst);
        tvSecond = (TextView)findViewById(R.id.tvSecond);
        tvThird = (TextView)findViewById(R.id.tvThird);
        tvFourth = (TextView)findViewById(R.id.tvFourth);
        tvFifth = (TextView)findViewById(R.id.tvFifth);
        tvDate = (TextView)findViewById(R.id.tvDateSel);

        textViews = new TextView[5];
        textViews[0]= tvFirst;
        textViews[1]= tvSecond;
        textViews[2]= tvThird;
        textViews[3]= tvFourth;
        textViews[4]= tvFifth;

        history = new long[textViews.length];

        myStudyDBHelper = new MyStudyDBHelper(this);

        calendar = Calendar.getInstance();

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear);

        v = new DrawingView(this);

        linearLayout.addView(v);

        viewDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        tvDate.setText(Html.fromHtml("<u>~ " + calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH)+1) + "월 " + calendar.get(Calendar.DATE) + "일</u>"));
        tvDate.setOnClickListener(clickListener);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvDateSel:
                    new DatePickerDialog(StudyDataActivity.this, mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
                    break;
            }
        }
    };
    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, dayOfMonth);
                    tvDate.setText(Html.fromHtml("<u>~ "+year + "년 " + (month+1) + "월 " + dayOfMonth + "일</u>"));
                    viewDate(year, month, dayOfMonth);
                }
            };

    private void viewDate(int y, int m, int d) {
        SQLiteDatabase db = myStudyDBHelper.getReadableDatabase();
        Calendar cal = Calendar.getInstance();
        cal.set(y, m, d);
        int year = y;
        int month = m;
        int date = d;

        for(int i = 0; i < textViews.length; i++) {
            Cursor cursor = db.rawQuery("SELECT year, month, date, time FROM my_study_data_table WHERE year = '" + year + "' and month = '" + month +
                    "' and date = '" + date + "';", null);
            if(cursor.moveToNext()) {
                history[i] = cursor.getLong(3);
                textViews[i].setText("  " + String.valueOf(year)+"년\n" + String.valueOf(month + 1) +"월 " + String.valueOf(date)+"일");
            }
            else {
                history[i] = 0;
                textViews[i].setText("  " + String.valueOf(year) + "년\n" + String.valueOf(month + 1) +"월 " + String.valueOf(date)+"일");
            }
            if(date == 1) {
                if(month == 0) {
                    year--;
                    month = 11;
                    cal.set(year, month, date);
                }
                else {
                    month--;
                    cal.set(year, month, date);
                }
                date = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            else {
                date--;
            }
        }
        v.invalidate();

    }


    public class DrawingView extends View {
        Paint paint;


        public DrawingView(Context context) {
            super(context);

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            paint.setTextSize(30);
        }

        public DrawingView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public DrawingView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int[] hour = new int[history.length];
            int[] minute = new int[history.length];
            int[] graph = new int[history.length];

            for(int i =0; i<history.length; i++) {
                hour[i] = (int)history[i]/(1000*60*60);
                minute[i] = (int)((history[i] / (1000*60))-(60*hour[i]));
                graph[i] = (int)((history[i]*0.00001));
            }

            canvas.drawRect(60, 850-graph[4] , 90, 850, paint);
            canvas.drawText(hour[4]+" 시간", 50, 770-graph[4], paint);
            canvas.drawText(minute[4]+" 분", 50, 810-graph[4], paint);

            canvas.drawRect(205, 850-graph[3], 235, 850, paint);
            canvas.drawText(hour[3]+" 시간", 195, 770-graph[3], paint);
            canvas.drawText(minute[3]+" 분", 195, 810-graph[3], paint);

            canvas.drawRect(350, 850-graph[2], 380, 850, paint);
            canvas.drawText(hour[2]+" 시간", 340, 770-graph[2], paint);
            canvas.drawText(minute[2]+" 분", 340, 810-graph[2], paint);

            canvas.drawRect(495, 850-graph[1], 525, 850, paint);
            canvas.drawText(hour[1]+" 시간", 485, 770-graph[1], paint);
            canvas.drawText(minute[1]+" 분", 485, 810-graph[1], paint);

            canvas.drawRect(640, 850-graph[0], 670, 850, paint);
            canvas.drawText(hour[0]+" 시간", 630, 770-graph[0], paint);
            canvas.drawText(minute[0]+" 분", 630, 810-graph[0], paint);
        }
    }

}
