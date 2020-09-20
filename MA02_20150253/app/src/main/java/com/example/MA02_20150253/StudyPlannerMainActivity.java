
package com.example.MA02_20150253;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class StudyPlannerMainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_main);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgStudyListGo:
                Intent intent = new Intent(this, MyStudyListActivity.class);
                startActivity(intent);
                break;
            case R.id.imgCalendarGo:
                Intent intent2 = new Intent(this, MyCalendarActivity.class);
                startActivity(intent2);
                break;
        }
    }

}
