package de.uniko.fb1.SoMA;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

// https://github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "locationDatabase3"; // FIXME

    // Tables
    private static final String TABLE_LOCATION = "location";

    // Location Columns
    private static final String KEY_LOCATION_ID = "id";
    private static final String KEY_LOCATION_ACCURACY = "accuracy";
    private static final String KEY_LOCATION_ALTITUDE = "altitude";
    private static final String KEY_LOCATION_BEARING = "bearing";
    private static final String KEY_LOCATION_LATITUDE = "latitude";
    private static final String KEY_LOCATION_LONGITUDE = "longitude";
    private static final String KEY_LOCATION_SPEED = "speed";
    private static final String KEY_LOCATION_TIME = "timestamp";

    // Get always the same instance
    private static DatabaseHelper sInstance;

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static synchronized DatabaseHelper getInstance(Context context) {

        /*
         * Use the application context, which will ensure that you
         * don't accidentally leak an Activity's context.
         * See this article for more information: http://bit.ly/6LRzfx
         */
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    /*
     * Called when the database connection is being configured..
     * Configure database settings for things like foreign key support, write-ahead logging, etc.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        Log.d(TAG, "onConfigure " + db);
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /*
     * Called when the database is created for the FIRST time.
     * If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION +
                "(" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY, " +
                KEY_LOCATION_ACCURACY + " TEXT, " +
                KEY_LOCATION_ALTITUDE + " TEXT, " +
                KEY_LOCATION_BEARING + " TEXT, " +
                KEY_LOCATION_LATITUDE + " TEXT, " +
                KEY_LOCATION_LONGITUDE + " TEXT, " +
                KEY_LOCATION_TIME + " TEXT, " +
                KEY_LOCATION_SPEED + " TEXT" +
                ")";

        db.execSQL(CREATE_LOCATION_TABLE);
        Log.d(TAG, "onCreate " + db + " " + CREATE_LOCATION_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. This method will only be called
     * if a database already exists on disk with the same DATABASE_NAME, but the
     * DATABASE_VERSION is different than the version of the database that exists on
     * disk.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade " + db + " " + oldVersion + " => " + newVersion);

        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
            onCreate(db);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    long addLocation(Location location) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long id;

        try {
            ContentValues values = new ContentValues();

            values.put(KEY_LOCATION_ACCURACY, location.getAccuracy());
            values.put(KEY_LOCATION_ALTITUDE, location.getAltitude());
            values.put(KEY_LOCATION_BEARING, location.getBearing());
            values.put(KEY_LOCATION_LATITUDE, location.getLatitude());
            values.put(KEY_LOCATION_LONGITUDE, location.getLongitude());
            values.put(KEY_LOCATION_SPEED, location.getSpeed());
            values.put(KEY_LOCATION_TIME, location.getTime());

            Log.i(TAG, "INSERT VALUES " + values);
            id = db.insertOrThrow(TABLE_LOCATION, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "SUCCESS: " + TABLE_LOCATION + " NEW ID: " + id);
            return id;
        } catch (Exception e) {
            Log.w(TAG, e);
            return -2;
        } finally {
            db.endTransaction();
        }
    }

    static class DataObject {
        private final int id;
        private final float altitude;
        private final float accuracy;
        private final float latitude;
        private final float longitude;
        private final float bearing;
        private final long timestamp;
        private final float speed;

        public DataObject(int id, float accuracy, float altitude, float bearing, float latitude, float longitude, long timestamp, float speed) {
            this.id = id;
            this.accuracy = accuracy;
            this.altitude = altitude;
            this.latitude = latitude;
            this.longitude = longitude;
            this.bearing = bearing;
            this.timestamp = timestamp;
            this.speed = speed;
        }

        public int getId() {
            return this.id;
        }

        public LatLng getLatLng() {
            return new LatLng(this.latitude, this.longitude);
        }
    }

    /*
     * Get all locations in the database
     */
    List<DataObject> getLocations() {
        Log.d(TAG, "getLocations");

        String LOCATIONS_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_LOCATION);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(LOCATIONS_SELECT_QUERY, null);

        List<DataObject> locations = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    DataObject loc = new DataObject(
                            cursor.getInt(cursor.getColumnIndex(KEY_LOCATION_ID)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_ACCURACY)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_ALTITUDE)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_BEARING)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_LATITUDE)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_LONGITUDE)),
                            cursor.getLong(cursor.getColumnIndex(KEY_LOCATION_TIME)),
                            cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_SPEED))
                    );
                    locations.add(loc);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "Error while trying to get locations from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return locations;
    }

    /**
     * Count all location data
     */
    long countLocations() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT COUNT(*) FROM %s", TABLE_LOCATION), null);
        cursor.moveToFirst();
        long c = cursor.getInt(0);
        cursor.close();
        return c;
    }

    /**
     * Delete location
     */
    void deleteLocation(int locationId) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_LOCATION, "id=?", new String[]{String.valueOf(locationId)});
            db.setTransactionSuccessful();
            Log.d(TAG, "deleteLocation: " + locationId + " OK");
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to location");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

}
