package com.example.psydc2.runningtracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class TrackingActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationService.MyBinder mLocationService = null;
    private int isGPSAccessGranted;
    private Intent locationIntent;
    private boolean mBound = false;
    private Handler handler;
    private boolean isTracking;
    private FloatingActionButton fab;
    private TextView distanceText;
    private TextView speedText;
    private TextView altitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);


        distanceText = findViewById(R.id.distanceText);
        speedText = findViewById(R.id.speedText);
        altitudeText = findViewById(R.id.altitudeText);

        if(getIntent().hasExtra("tracking")) {
            try {
                if(getIntent().getExtras().getString("tracking").equals("start")) {
                    //start tracking
                    if(!isTracking) {
                        isTracking = true;
                        startLocationTracking();
                        Log.d("tracking", "sent start");
                    }
                } else if (getIntent().getExtras().getString("tracking").equals("stop")) {
                    //stop tracking
                    isTracking = false;
                    stopLocationTracking();

                    Log.d("tracking", "sent stop");

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isTracking) {
                    fab.setImageResource(R.drawable.ic_stop_black_24dp);
                    isTracking = true;

                    startLocationTracking();

                } else {
                    fab.setImageResource(R.drawable.ic_directions_run_black_24dp);
                    isTracking = false;

                    stopLocationTracking();

                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            if(mBound) {
                String distance = String.format("%.2f", mLocationService.getDistance());
                String speed = String.format("%1.0f", mLocationService.getSpeed());
                String altitude = String.format("%1.0f", mLocationService.getAltitude());

                distanceText.setText(distance + " km");
                speedText.setText(speed + " km/h");
                altitudeText.setText(altitude + " m");
            }
            handler.postDelayed(this, 1000);
        }
    };

    public void startLocationTracking() {
        isGPSAccessGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(isGPSAccessGranted == PackageManager.PERMISSION_DENIED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show an explanation to the user
                Toast.makeText(this, "Enable the GPS access in the settings.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else if(isGPSAccessGranted == PackageManager.PERMISSION_GRANTED) {
            //Start tracking from here.

            locationIntent = new Intent(this, LocationService.class);
            startService(locationIntent);

            mBound = getApplicationContext().bindService(locationIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            if(mBound) {
                Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void stopLocationTracking() {
        if(mBound) {
            getApplicationContext().unbindService(serviceConnection);
            mBound = false;
        }
        stopService(new Intent(TrackingActivity.this, LocationService.class));

        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mLocationService = (LocationService.MyBinder) iBinder;
            mBound = true;

            handler = new Handler();
            handler.post(updateUI);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mLocationService = null;
            mBound = false;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mBound) {
            if(serviceConnection != null) {
                getApplicationContext().unbindService(serviceConnection);
                serviceConnection = null;
                mBound = false;
            }
        }
    }



}
