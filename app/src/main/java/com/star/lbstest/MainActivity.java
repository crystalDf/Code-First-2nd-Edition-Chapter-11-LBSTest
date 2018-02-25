package com.star.lbstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.LocationClient;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient;

    private TextView mPositionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
