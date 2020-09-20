package com.example.MA02_20150253;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MannerLocationSettingActivity extends AppCompatActivity {

    public final static int ACT_ADD = 0;
    public final static int ACT_LIST = 100;
    public final static int ACT_SEARCH = 200;
    public final static int DEFAULT_ZOOM_LEVEL = 17;

    private Geocoder geoCoder;
    private GoogleMap mGoogleMap;

    private MarkerOptions centerMarkerOptions;
    private Marker centerMarker;

    private MarkerOptions poiMarkerOptions;
    private ArrayList<Marker> markerList;

    private MannerLocationDBHelper myDBHelper;
    MyLocation poi;

    LocationManager manager;
    LatLng lastLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myDBHelper = new MannerLocationDBHelper(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

        /*Geocodling을 위한 Geocoder 준비*/
        geoCoder = new Geocoder(this);

        /*중심 지점을 위한 마커 옵션 준비*/
        centerMarkerOptions = new MarkerOptions();
        centerMarkerOptions.title("현재 위치");
        centerMarkerOptions.snippet("최종 위치 입니다.");
        centerMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.enligne));

        /*POI 설정을 위한 마커 옵션 준비*/
        poiMarkerOptions = new MarkerOptions();
        poiMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_marker));

//        if(getIntent().getDoubleExtra("Lat", 0) != 0 && getIntent().getDoubleExtra("Lon", 0) != 0) {
//            moveCamera(getIntent().getDoubleExtra("Lat", 0), getIntent().getDoubleExtra("Lon", 0));
//        }

        markerList = new ArrayList<Marker>();

    }


    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
            mGoogleMap = map;

            String provider = manager.getBestProvider(new Criteria(), true);
            if (ActivityCompat.checkSelfPermission(MannerLocationSettingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MannerLocationSettingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = manager.getLastKnownLocation(provider);

            if( location == null ) {
                lastLatLng = new LatLng(37.606320, 127.041808);
            } else {
                Toast.makeText(MannerLocationSettingActivity.this, provider, Toast.LENGTH_SHORT).show();
                lastLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            }

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, DEFAULT_ZOOM_LEVEL));

//            중심 지점의 마커 표시
            centerMarkerOptions.position(lastLatLng);
            centerMarker = mGoogleMap.addMarker(centerMarkerOptions);
            centerMarker.showInfoWindow();

//            추후 관리를 위해 중심지점의 마커 보관
            markerList.add(centerMarker);
            viewMarker();
            /*마커의 InfoWindow 클릭 시의 이벤트 처리*/
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
//                    마커의 tag에 저장한 poi 객체를 get
                    poi = (MyLocation) marker.getTag();
                    if (poi != null) {
                        Toast.makeText(MannerLocationSettingActivity.this, poi.getAddress(), Toast.LENGTH_SHORT).show();
                        poi = null;
                    }
                }
            });
            mGoogleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                @Override
                public void onInfoWindowLongClick(Marker marker) {
                    poi = (MyLocation) marker.getTag();
                    if(poi != null) {
                        SQLiteDatabase db = myDBHelper.getWritableDatabase();
                        String whereClause = "_id=?";
                        String[] whereArgs = {Integer.toString(poi.get_id())};

                        long result = db.delete(MannerLocationDBHelper.TABLE_NAME, whereClause, whereArgs);
                        if (result > 0) {
                            Toast.makeText(MannerLocationSettingActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                            marker.remove();
                        }
                        else
                            Toast.makeText(MannerLocationSettingActivity.this, "삭제 실패!", Toast.LENGTH_SHORT).show();

                        myDBHelper.close();
                        poi = null;
                    }

                }
            });

            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                    List<Address> addressList = null;
                    try {
//                        위도와 경도에 해당하는 주소 목록을 지오코딩으로 확인
                        addressList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    지오코딩의 결과 list 에서 값 추출
                    if (addressList != null && addressList.size() > 0) {
//                        지오코딩을 통해 해당 주소의 명칭, 주소 확인
                        poi = new MyLocation(addressList.get(0).getPremises(), addressList.get(0).getAddressLine(0));

                        poi.setLatitude(latLng.latitude);
                        poi.setLongitude(latLng.longitude);

                        poiMarkerOptions.position(new LatLng(latLng.latitude, latLng.longitude));

                        Intent intent = new Intent(MannerLocationSettingActivity.this, MannerLocationAddActivity.class);

                        intent.putExtra("Title", poi.getTitle());
                        intent.putExtra("Address", poi.getAddress());
                        startActivityForResult(intent, ACT_ADD);

                    }
                }
            });
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACT_ADD:
                if (resultCode == RESULT_OK) {
                    SQLiteDatabase db = myDBHelper.getWritableDatabase();

                    poi.setTitle(data.getStringExtra("Title"));
                    poi.setAddress(data.getStringExtra("Address"));

                    ContentValues row = new ContentValues();
                    row.put("title", poi.getTitle());
                    row.put("address", poi.getAddress());
                    row.put("latitude", poi.getLatitude());
                    row.put("longitude", poi.getLongitude());
                    long result = db.insert(MannerLocationDBHelper.TABLE_NAME, null, row);

                    if (result > 0) Toast.makeText(this, "추가했습니다.", Toast.LENGTH_SHORT).show();

                    Cursor cursor = db.rawQuery("select * from mannerLocation_table where title = '" + poi.getTitle() + "' and address = '"+ poi.getAddress() +
                    "' and latitude = " + poi.getLatitude() + " and longitude = " + poi.getLongitude(), null);

                    while (cursor.moveToNext()) {
                        poi.set_id(cursor.getInt(0));
                    }

                    myDBHelper.close();

                    poiMarkerOptions.title(poi.getTitle());

                    Marker newMarker = mGoogleMap.addMarker(poiMarkerOptions);

//                        지도에 추가할 마커에 해당 위치 정보를 갖고 있는 poi 객체 저장
                    newMarker.setTag(poi);

//                        추후 마커 관리(전체 마커 삭제, 마커 이동 등)를 위한 마커 리스트에 추가한 마커 저장
                    markerList.add(newMarker);
                    poi = null;
                } else
                    poi = null;
                break;
            case ACT_LIST:
                if(resultCode == RESULT_OK) {
                    List<Address> list = null;
                    try {
                        String addr = data.getStringExtra("Address");
                        list = geoCoder.getFromLocationName(addr, 5);
                        moveCamera(list.get(0).getLatitude(), list.get(0).getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }
                }
                break;

            case ACT_SEARCH:
                if(resultCode == RESULT_OK) {
                    List<Address> list = null;
                    try {
                        String addr = data.getStringExtra("Addr");
                        list = geoCoder.getFromLocationName(addr, 5);
                        moveCamera(list.get(0).getLatitude(), list.get(0).getLongitude());
                        CircleOptions c = new CircleOptions().center(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()))
                                .radius(10).strokeColor(Color.BLUE);
                        mGoogleMap.addCircle(c);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }
                }

        }
    }

    private void viewMarker() {

        markerList.clear();

        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        String[] cols = null;
        String whereClause = null;
        String[] whereArgs = null;

        Cursor cursor = db.query(MannerLocationDBHelper.TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            poi = new MyLocation(null, null);
            poi.set_id(cursor.getInt(0));
            poi.setTitle(cursor.getString(1));
            poi.setAddress(cursor.getString(2));

            poiMarkerOptions.title(poi.getTitle());
            poiMarkerOptions.position(new LatLng(cursor.getDouble(3), cursor.getDouble(4)));

            Marker newMarker = mGoogleMap.addMarker(poiMarkerOptions);
            newMarker.setTag(poi);
            markerList.add(newMarker);
        }

        poi = null;
        cursor.close();
        myDBHelper.close();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnPoi:
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, DEFAULT_ZOOM_LEVEL));
        }
    }
    public void moveCamera(double lat, double lon) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), DEFAULT_ZOOM_LEVEL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.list:
                intent = new Intent(this, MannerLocationListActivity.class);
                startActivityForResult(intent,ACT_LIST);


                break;

            case R.id.search:
                intent = new Intent(this, MannerLocationSearchActivity.class);
                startActivityForResult(intent,ACT_SEARCH);

                break;
        }
        return false;
    }
}