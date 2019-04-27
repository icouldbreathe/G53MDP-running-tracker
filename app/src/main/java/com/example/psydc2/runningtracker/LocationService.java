package com.example.psydc2.runningtracker;

import android.app.Notification;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

public class LocationService extends Service {

    private LocationManager mLocationManager = null;
    private LocationListener mLocationListener = null;
    private static final int LOCATION_MIN_TIME = 5;
    private static final float LOCATION_MIN_DISTANCE = 5;
    private Location mLastLocation;
    private float speed = 0;
    private float overallDistance = 0;
    private float avgSpeed = 0;
    private double altitude = 0;
    private float timestamp;

    public class MyBinder extends Binder implements IInterface {

        @Override
        public IBinder asBinder() {
            return this;
        }

        float getDistance() {
            return overallDistance;
        }

        float getSpeed() {
            return avgSpeed;
        }

        double getAltitude() {
            return altitude;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener(LocationManager.GPS_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_MIN_TIME,
                    LOCATION_MIN_DISTANCE,
                    mLocationListener);

        } catch(SecurityException e) {
            e.printStackTrace();
        }

        startForeground(1, new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        ContentValues newValues = new ContentValues();
        newValues.put(ProviderContract.TRACKID, 0);
        newValues.put(ProviderContract.TIMESTAMP, timestamp);
        newValues.put(ProviderContract.DISTANCE, overallDistance);
        newValues.put(ProviderContract.AVGSPEED, avgSpeed);

        getContentResolver().insert(ProviderContract.TRACKS_URI, newValues);
        mLocationManager.removeUpdates(mLocationListener);
        super.onDestroy();

    }

    public class MyLocationListener implements LocationListener {

        float distance = 0;
        int iTrackPoint = 0;
        float sumSpeed = 0;

        public MyLocationListener(String provider) {
            mLastLocation = new Location(provider);
            try {
                mLastLocation = mLocationManager.getLastKnownLocation(provider);
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d("location", location.getLatitude() + " " + location.getLongitude());

            iTrackPoint++;
            try {
                distance = mLastLocation.distanceTo(location) / 1000;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            speed = (distance / ((location.getTime() - mLastLocation.getTime()))*3600000);

            if(speed < 46) { // 45 km/h is the fastest speed humans can run in theory.
                if(iTrackPoint == 1) {
                    timestamp = location.getTime();
                }
                overallDistance += distance;

                sumSpeed += speed;
                avgSpeed = sumSpeed / iTrackPoint;

                altitude = location.getAltitude();

                ContentValues newValues = new ContentValues();

                newValues.put(ProviderContract.TRACKID, 0);
                newValues.put(ProviderContract.LATITUDE, location.getLatitude());
                newValues.put(ProviderContract.LONGITUDE, location.getLongitude());
                newValues.put(ProviderContract.TIMESTAMP, location.getTime());

                getContentResolver().insert(ProviderContract.GPSDATA_URI, newValues);


                mLastLocation = location;


            } else {
                // Might be a faulty gps point
                iTrackPoint--;
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
            // information about the signal, i.e. number of satellites
            Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            // the user enabled (for example) the GPS
            Log.d("g53mdp", "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // the user disabled (for example) the GPS
            Log.d("g53mdp", "onProviderDisabled: " + provider);
        }
    }

}
