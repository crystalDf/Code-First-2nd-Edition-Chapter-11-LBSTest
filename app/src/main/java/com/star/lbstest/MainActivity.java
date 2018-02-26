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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        mPositionTextView = findViewById(R.id.position_text_view);

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
        mLocationClient.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            StringBuilder currentPosition = new StringBuilder();

            String locType = "";

            switch (bdLocation.getLocType()) {
                case BDLocation.TypeGpsLocation:
                    locType = "GPS";
                    break;
                case BDLocation.TypeNetWorkLocation:
                    locType = "Network";
                    break;
                default:
            }

            currentPosition
                    .append("Latitude: ").append(bdLocation.getLatitude()).append("\n")
                    .append("Longitude: ").append(bdLocation.getLongitude()).append("\n")
                    .append("LocType: ").append(locType).append("\n");

            mPositionTextView.setText(currentPosition);
        }
    }
}
