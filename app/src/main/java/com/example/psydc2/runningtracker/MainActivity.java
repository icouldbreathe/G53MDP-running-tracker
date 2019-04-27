package com.example.psydc2.runningtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Provider;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private int isGPSAccessGranted;
    private boolean isTracking = false;
    private FloatingActionButton fab;
    private float overallDistanceRun = 0;
    private int overallAvgSpeed = 0;
    private TextView distanceText;
    private TextView speedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.floatingActionButton);
        distanceText = findViewById(R.id.distanceText);
        speedText = findViewById(R.id.speedText);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isTracking) {
                    fab.setImageResource(R.drawable.ic_stop_black_24dp);
                    isTracking = true;

                    Intent intent = new Intent(view.getContext(), TrackingActivity.class);
                    intent.putExtra("tracking", "start");
                    startActivity(intent);

                } else {
                    fab.setImageResource(R.drawable.ic_directions_run_black_24dp);
                    isTracking = false;

                    Intent intent = new Intent(view.getContext(), TrackingActivity.class);
                    intent.putExtra("tracking", "stop");
                    startActivity(intent);
                }
            }
        });

        isGPSAccessGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(isGPSAccessGranted == PackageManager.PERMISSION_DENIED) {
            //Ask for permission if denied

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show an explanation to the user
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

        queryContentProvider();
    }

    private void queryContentProvider() {
        Cursor cursor = getContentResolver().query(ProviderContract.TRACKS_URI, new String[] {
                ProviderContract._ID,
                ProviderContract.DISTANCE,
                ProviderContract.AVGSPEED
        }, null, null, null, null);

        int sumSpeed = 0;

        if(cursor.getCount() > 0) {
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                overallDistanceRun += Float.valueOf(cursor.getString(1));
                sumSpeed += Float.valueOf(cursor.getString(2));
            }
            overallAvgSpeed = sumSpeed / cursor.getCount();
        }

        distanceText.setText("Distance run of all time: " + String.format("%.2f", overallDistanceRun) + " km");
        speedText.setText("Speed of all time: " + overallAvgSpeed + " km/h");


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("isTracking", isTracking);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        isTracking = savedInstanceState.getBoolean("isTracking");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted

                    Toast.makeText(this, "GPS Access Granted", Toast.LENGTH_SHORT).show();


                } else {
                    //permission denied

                    Toast.makeText(this, "The tracker needs GPS permission to work", Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        }
    }







}
