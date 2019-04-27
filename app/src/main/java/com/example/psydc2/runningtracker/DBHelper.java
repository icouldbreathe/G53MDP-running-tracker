package com.example.psydc2.runningtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dovydas on 1/12/18.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TRACKID = "trackid";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_TIMESTAMP = "timestamp";

    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_AVG_SPEED = "avgspeed";

    public static final String SQLITE_TABLE_GPSDATA = "gpsdata";
    public static final String SQLITE_TABLE_TRACKS = "tracks";

    private static final String SQLITE_CREATE_TABLE_GPSDATA =
            "CREATE TABLE if not exists " + SQLITE_TABLE_GPSDATA + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement, " +
                    KEY_TRACKID + " integer not null, " +
                    KEY_LATITUDE + " real not null, " +
                    KEY_LONGITUDE + " real not null, " +
                    KEY_TIMESTAMP + " text not null, " +
                    "FOREIGN KEY(" + KEY_TRACKID + ") REFERENCES " + SQLITE_TABLE_TRACKS + "(" + KEY_TRACKID + ")" +
                    ");";

    private static final String SQLITE_CREATE_TABLE_TRACKS =
            "CREATE TABLE if not exists " + SQLITE_TABLE_TRACKS + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement, " +
                    KEY_TRACKID + " integer not null, " +
                    KEY_TIMESTAMP + " text not null, " +
                    KEY_DISTANCE + " real not null, " +
                    KEY_AVG_SPEED + " real not null" +
                    ");";




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLITE_CREATE_TABLE_TRACKS);
        db.execSQL(SQLITE_CREATE_TABLE_GPSDATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE_GPSDATA);
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE_TRACKS);
        onCreate(db);

    }
}
