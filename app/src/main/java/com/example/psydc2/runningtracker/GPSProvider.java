package com.example.psydc2.runningtracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by dovydas on 1/12/18.
 */

public class GPSProvider extends ContentProvider {

    private DBHelper dbHelper = null;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "gpsdata", 1);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "gpsdata/#", 2);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "tracks", 3);
        uriMatcher.addURI(ProviderContract.AUTHORITY, "tracks/#", 4);
    }


    @Override
    public boolean onCreate() {
        this.dbHelper = new DBHelper(this.getContext(), "GPS_DB", null, 2);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri)) {
            case 1: {
                return db.query(DBHelper.SQLITE_TABLE_GPSDATA, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case 2: {
                selection = "_ID = " + uri.getLastPathSegment();
            }
            case 3: {
                return db.query(DBHelper.SQLITE_TABLE_TRACKS, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case 4: {
                selection = "_ID = " + uri.getLastPathSegment();
            }
            default: {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String contentType;

        if(uri.getLastPathSegment() == null) {
            contentType = ProviderContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = ProviderContract.CONTENT_TYPE_SINGLE;
        }
        return contentType;

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri)) {
            case 1: {
                long id = db.insert(dbHelper.SQLITE_TABLE_GPSDATA, null, contentValues);
                db.close();

                Uri nu = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(nu, null);

                return nu;
            }
            case 3: {
                long id = db.insert(dbHelper.SQLITE_TABLE_TRACKS, null, contentValues);
                db.close();

                Uri nu = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(nu, null);

                return nu;
            }
            default: {
                return null;
            }
        }


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("not implemented");
    }
}
