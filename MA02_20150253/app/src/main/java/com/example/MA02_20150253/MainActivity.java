package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    String gpsEnabled;
    ImageButton mannerSetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mannerSetting = (ImageButton) findViewById(R.id.imgMannerOff);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("MannerSetting", 0);
        if(preferences.getBoolean("IsMannerMod", false)) {
            mannerSetting.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.stopWatch:
                intent = new Intent(this, StopWatchActivity.class);
                startActivity(intent);
                break;
            case R.id.studyDiary:
                intent = new Intent(this, StudyPlannerMainActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.autoManner:
                gpsEnabled = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                if (!(gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {
                    new AlertDialog.Builder(this)
                            .setTitle("매너위치 서비스 설정")
                            .setMessage("GPS가 꺼져있으면 이 기능을 사용할 수 없습니다.\n활성화 하시겠습니까?")
                            .setPositiveButton("켜기",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    //GPS 설정 화면을 띄움
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("닫기", null).show();
                }

                if ((gpsEnabled.matches(".*gps.*") || gpsEnabled.matches(".*network.*"))) {
                    intent = new Intent(this, MannerLocationService.class);
                    startService(intent);
                    intent = new Intent(this, MannerLocationSettingActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.imgMannerOff:
                Intent intent2 = new Intent(this, MannerLocationService.class);
                stopService(intent2);
                mannerSetting.setVisibility(View.INVISIBLE);

        }
    }
}
