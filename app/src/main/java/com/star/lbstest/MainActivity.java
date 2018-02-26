package com.star.lbstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSION_LIST = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_CODE = 0;

    public LocationClient mLocationClient;

    private TextView mPositionTextView;
    private MapView mMapView;

    private BaiduMap mBaiduMap;
    private boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        mPositionTextView = findViewById(R.id.position_text_view);
        mMapView = findViewById(R.id.mapView);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        List<String> permissionList = new ArrayList<>();

        for (String permission: PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(
                    new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,
                    permissions, REQUEST_CODE);
        } else {
            requestLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLocationClient.stop();
        mMapView.onDestroy();

        mBaiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE:

                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();

                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        String permission = permissions[i];

                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()) {
                        requestLocation();
                    } else {
                        for (String permission : deniedPermissions) {
                            Toast.makeText(this,
                                    "permission denied: " + permission,
                            Toast.LENGTH_LONG).show();
                        }

                        finish();
                    }
                } else {
                    Toast.makeText(this,
                            "Unknown Error", Toast.LENGTH_LONG).show();
                }

                break;
            default:
                break;
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setScanSpan(5000);
//        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        locationClientOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(locationClientOption);
    }

    private void navigateTo(BDLocation bdLocation) {

        if (isFirstLocate) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(mapStatusUpdate);

            mapStatusUpdate = MapStatusUpdateFactory.zoomTo(16);
            mBaiduMap.animateMapStatus(mapStatusUpdate);

            isFirstLocate = false;
        }

        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude());

        mBaiduMap.setMyLocationData(builder.build());
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            StringBuilder currentPosition = new StringBuilder();

            String locType = "";

            switch (bdLocation.getLocType()) {
                case BDLocation.TypeGpsLocation:
                    locType = "GPS";
                    navigateTo(bdLocation);
                    break;
                case BDLocation.TypeNetWorkLocation:
                    locType = "Network";
                    navigateTo(bdLocation);
                    break;
                default:
            }

            currentPosition
                    .append("Latitude: ").append(bdLocation.getLatitude()).append("\n")
                    .append("Longitude: ").append(bdLocation.getLongitude()).append("\n")
                    .append("Country: ").append(bdLocation.getCountry()).append("\n")
                    .append("Province: ").append(bdLocation.getProvince()).append("\n")
                    .append("City: ").append(bdLocation.getCity()).append("\n")
                    .append("District: ").append(bdLocation.getDistrict()).append("\n")
                    .append("Street: ").append(bdLocation.getStreet()).append("\n")
                    .append("LocType: ").append(locType).append("\n");

            mPositionTextView.setText(currentPosition);
        }
    }
}
