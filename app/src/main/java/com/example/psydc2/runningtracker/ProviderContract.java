package com.example.psydc2.runningtracker;

import android.net.Uri;

/**
 * Created by dovydas on 1/12/18.
 */

public class ProviderContract {
    public static final String AUTHORITY = "com.example.psydc2.runningtracker.GPSProvider";

    public static final Uri GPSDATA_URI = Uri.parse("content://"+AUTHORITY+"/gpsdata");
    public static final Uri TRACKS_URI = Uri.parse("content://"+AUTHORITY+"/tracks");

    public static final String _ID = "_id";
    public static final String TRACKID = "trackid";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIMESTAMP = "timestamp";
    public static final String DISTANCE = "distance";
    public static final String AVGSPEED = "avgspeed";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/GPSProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/GPSProvider.data.text";
}
