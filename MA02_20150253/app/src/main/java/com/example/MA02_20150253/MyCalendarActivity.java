package com.example.MA02_20150253;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

public class MyCalendarActivity extends AppCompatActivity{
    MyStudyDBHelper studyDBHelper;
    CalendarView calendar;

    int y;
    int m;
    int d;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        studyDBHelper = new MyStudyDBHelper(this);

        calendar = (CalendarView)findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                y = year;
                m = month;
                d = dayOfMonth;
            }

        });
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSel:
                Intent intent = new Intent(MyCalendarActivity.this, MyDailyListActivity.class);
                intent.putExtra("Year", y);
                intent.putExtra("Month", m);
                intent.putExtra("Date", d);
                startActivity(intent);
                break;
        }
    }
}
