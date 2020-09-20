

package com.example.MA02_20150253;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class MannerLocationService extends Service {
    final static int NOTI2 = 2;
    private MannerLocationDBHelper myDBHelper;
    private LocationManager locMan;
    private MyLocationListener locationListener;
    private String provider;
    private boolean isMannerMode = false;
    private AudioManager audioManager;
    private static final String TAG = "Service!!";
    String gpsEnabled;

    @Override
    public void onCreate() {
        super.onCreate();
        myDBHelper = new MannerLocationDBHelper(this);
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "서비스 종료", Toast.LENGTH_SHORT).show();

        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        audioManager.setStreamMute(AudioManager.STREAM_RING, false);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);

        SharedPreferences preferences = getSharedPreferences("MannerSetting", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsMannerMod", false);
        editor.commit();

        locMan.removeUpdates(locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        provider = locMan.getBestProvider(new Criteria(), true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            stopSelf();
        }
        gpsEnabled = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!(gpsEnabled.matches(".*gps.*") && gpsEnabled.matches(".*network.*"))) {
            stopSelf();
        }
        locMan.requestLocationUpdates(provider, 100000, 20, locationListener);

        SharedPreferences preferences = getSharedPreferences("MannerSetting", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsMannerMod", true);
        editor.commit();

        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

            float[] results = new float[3];
            float minDistanceMeter = 200;
            SQLiteDatabase db = myDBHelper.getReadableDatabase();
            String[] cols = null;
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursor = db.query(MannerLocationDBHelper.TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);
            while (cursor.moveToNext()) {
                Double latitude = cursor.getDouble(3);
                Double longitude = cursor.getDouble(4);
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), latitude, longitude, results);
                float distanceMeter = results[0];
                if(cursor.isFirst()) {
                    minDistanceMeter = distanceMeter;
                }
                if(minDistanceMeter > distanceMeter) {
                    minDistanceMeter = distanceMeter;
                }
                Log.e(TAG, "distance" + minDistanceMeter);
            }

            if(minDistanceMeter <= 100 && !isMannerMode) {
                audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                //매너모드
                Log.e(TAG, "service...Start" + location.getLatitude() + location.getLongitude());
                isMannerMode = true;
            }
            if(minDistanceMeter > 100 && isMannerMode) {
                audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                //원래 음량으로 돌려놓기
                isMannerMode = false;
                Log.e(TAG, "service...end" + location.getLatitude() + location.getLongitude());
            }
            cursor.close();
            myDBHelper.close();
        }

        public void onProviderDisabled(String provider) {
            NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(MannerLocationService.this, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MannerLocationService.this, 0, intent1, 0);
            Notification notification =  new Notification.Builder(MannerLocationService.this).setTicker("매너모드 서비스가 종료됩니다.")
                    .setContentTitle("매너모드 서비스 종료")
                    .setContentText("현재 GPS가 꺼져있어 자동으로 서비스를 종료합니다.")
                    .setSmallIcon(R.drawable.chick_round)
                    .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.chick_round))
                    .setContentIntent(pendingIntent)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notifier.notify(NOTI2,notification);


            stopSelf();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(MannerLocationService.this, "서비스 사용 가능", Toast.LENGTH_SHORT).show();
        }

        public  void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(MannerLocationService.this, "서비스 가능 범위를 벗어났습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(MannerLocationService.this, "일시적으로 서비스 사용이 불가능합니다.", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Toast.makeText(MannerLocationService.this, "서비스 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
