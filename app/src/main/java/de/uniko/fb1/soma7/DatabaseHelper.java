package de.uniko.fb1.soma7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// https://github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private final Context context;

    // Database Info
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "locationDatabase2";

    // Tables
    private static final String TABLE_TRIP = "trip";
    private static final String TABLE_LOCATION = "location";

    // Client Columns
    private static final String KEY_CLIENT_UUID = "clientUUID"; // clientUUID @ API

    // Trip Columns
    private static final String KEY_TRIP_ID = "id";
    private static final String KEY_TRIP_UUID = "uuid";
    private static final String KEY_TRIP_LOCATION_DATA = "locationData";

    // Location Columns
    private static final String KEY_LOCATION_ID = "id"; // remoteTripId @ API
    private static final String KEY_LOCATION_TRIP_ID_FK = "tripId";

    // Location Columns (DATA)
    private static final String KEY_LOCATION_ACCURACY = "accuracy";
    private static final String KEY_LOCATION_ALTITUDE = "altitude";
    private static final String KEY_LOCATION_BEARING = "bearing";
    private static final String KEY_LOCATION_LATITUDE = "latitude";
    private static final String KEY_LOCATION_LONGITUDE = "longitude";
    private static final String KEY_LOCATION_SPEED = "speed";
    private static final String KEY_LOCATION_TIME = "timestamp";

    // Get always the same instance
    private static DatabaseHelper sInstance;

    // Action Constants
    private static final String GET_TRIP_JSON_FROM_DB = "GET_TRIP_JSON_FROM_DB";

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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

//    JSONObject tripJSONObject(Context context, String tripId) {
//        Log.d(TAG, GET_TRIP_JSON_FROM_DB);
//        JSONObject json = new JSONObject();
////        this.getTrip(tripId);
////        Trip trip = LocationService.currentTrip;
//
//        try {
//            json.put(KEY_CLIENT_UUID, trip.getAndroidId());
//            json.put(KEY_TRIP_UUID, trip.getUUID());
//            json.put(KEY_TRIP_LOCATION_DATA, trip.getDataJSON(context));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return json;
//    }

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

        String CREATE_TRIP_TABLE = "CREATE TABLE " + TABLE_TRIP +
                "(" +
                KEY_TRIP_ID + " INTEGER PRIMARY KEY, " +
                KEY_TRIP_UUID + " TEXT, " +
                KEY_CLIENT_UUID + " TEXT" +
                ")";

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION +
                "(" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY, " +
                KEY_LOCATION_TRIP_ID_FK + " INTEGER REFERENCES " + TABLE_TRIP + "," + // foreign key
                KEY_LOCATION_ACCURACY + " TEXT, " +
                KEY_LOCATION_ALTITUDE + " TEXT, " +
                KEY_LOCATION_BEARING + " TEXT, " +
                KEY_LOCATION_LATITUDE + " TEXT, " +
                KEY_LOCATION_LONGITUDE + " TEXT, " +
                KEY_LOCATION_TIME + " TEXT, " +
                KEY_LOCATION_SPEED + " TEXT" +
                ")";

        db.execSQL(CREATE_TRIP_TABLE);
        Log.d(TAG, "onCreate " + db + " " + CREATE_TRIP_TABLE);

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
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
            onCreate(db);
        }
    }

    /**
     * Insert a trip into the database.
     */
    String addTrip(Trip trip) {
        Log.d(TAG, "addTrip " + trip);

        String id;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();

            values.put(KEY_TRIP_UUID, trip.getUUID());
            values.put(KEY_CLIENT_UUID, trip.getClientUUID());

            id = Long.toString(db.insertOrThrow(TABLE_TRIP, null, values));

            db.setTransactionSuccessful();
            Log.i(TAG, "NEW TRIP ADDED id=" + id + " " + values);

            return id;
        } catch (Exception e) {
            Log.w(TAG, e + "Error while trying to add trip to database");
            return null;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Transform a Location object for putting it into the SQL database
     * Location -> ContentValues
     */
    long addLocation(Location location, Trip trip) {

//        Log.d(TAG, "addLocation: " + location + " tripId=" + trip.getId());

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long id;

        try {
            ContentValues values = new ContentValues();

            Log.d(TAG, "addLocation to trip " + trip.getId());

            values.put(KEY_LOCATION_TRIP_ID_FK, trip.getId());
            values.put(KEY_LOCATION_ACCURACY, location.getAccuracy());
            values.put(KEY_LOCATION_ALTITUDE, location.getAltitude());
            values.put(KEY_LOCATION_BEARING, location.getBearing());
            values.put(KEY_LOCATION_LATITUDE, location.getLatitude());
            values.put(KEY_LOCATION_LONGITUDE, location.getLongitude());
            values.put(KEY_LOCATION_SPEED, location.getSpeed());
            values.put(KEY_LOCATION_TIME, location.getTime());

            /* Be careful not to delete the trip after uploading and making the next addLocation
            call result in FOREIGN KEY failure. */

            Log.i(TAG, "INSERT VALUES " + values);
            id = db.insertOrThrow(TABLE_LOCATION, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "SUCCESS: " + TABLE_LOCATION + " NEW ID: " + id);
            return id;
        } catch (Exception e) {
            Log.e(TAG, "addLocation to trip " + trip.getId() + " failed");
            Log.w(TAG, e);
            return -2;
        } finally {
            db.endTransaction();
        }
    }

    /*
     * Get all locations in the database
     */
    List<JSONObject> getLocations(String tripId) {
        Log.d(TAG, "getLocations tripId=" + tripId);

        String LOCATIONS_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE tripId = %s", TABLE_LOCATION, tripId);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(LOCATIONS_SELECT_QUERY, null);

        List<JSONObject> locations = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    JSONObject loc = new JSONObject();

                    loc.put(KEY_LOCATION_ACCURACY, cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_ACCURACY)));
                    loc.put(KEY_LOCATION_ALTITUDE, cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_ALTITUDE)));
                    loc.put(KEY_LOCATION_BEARING, cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_BEARING)));
                    loc.put(KEY_LOCATION_LATITUDE, cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_LATITUDE)));
                    loc.put(KEY_LOCATION_LONGITUDE, cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_LONGITUDE)));
                    loc.put(KEY_LOCATION_TIME, cursor.getLong(cursor.getColumnIndex(KEY_LOCATION_TIME)));
                    loc.put(KEY_LOCATION_SPEED, cursor.getFloat(cursor.getColumnIndex(KEY_LOCATION_SPEED)));

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

//        if (locationCount(tripId) != locations.size()) {
//            Log.wtf(TAG, "WARNING MISCOUNTED LOCATIONS" + locationCount(tripId) + " " + locations.size());
//        }

//        Log.i(TAG, "" + locations); FIXME
        return locations;
    }

    /*
     * Get all trips from the database
     */
    List<Trip> getTrips() {
        Log.d(TAG, "getTrips");

        String LOCATIONS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_TRIP);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(LOCATIONS_SELECT_QUERY, null);

        List<Trip> trips = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    String androidId = cursor.getString(cursor.getColumnIndex(KEY_CLIENT_UUID));
                    String uuid = cursor.getString(cursor.getColumnIndex(KEY_TRIP_UUID));
                    String id = cursor.getString(cursor.getColumnIndex(KEY_TRIP_ID));
                    trips.add(new Trip(context, androidId, uuid, id));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "Error while trying to get trips from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        Log.w(TAG, "TRIPS: " + trips);
        return trips;
    }

    /*
     * Get one trip from the database
     */
    JSONObject getTrip(String tripId) {
        Log.d(TAG, "getTrip " + tripId);

        String LOCATIONS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE id = %s", TABLE_TRIP, tripId);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(LOCATIONS_SELECT_QUERY, null);

        JSONObject trip = new JSONObject();

        try {
            if (cursor.moveToFirst()) {
                do {
                    String clientUUID = cursor.getString(cursor.getColumnIndex(KEY_CLIENT_UUID));
                    String tripUUID = cursor.getString(cursor.getColumnIndex(KEY_TRIP_UUID));
                    String id = cursor.getString(cursor.getColumnIndex(KEY_TRIP_ID));

                    trip.put(KEY_CLIENT_UUID, clientUUID);
                    trip.put(KEY_TRIP_UUID, tripUUID);

                    List<JSONObject> locations = this.getLocations(id);
                    trip.put(KEY_TRIP_LOCATION_DATA, new JSONArray(locations));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "Error while trying to get trips from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return trip;
    }

    /**
     * Count all location data
     */
    public long countAllData() {
        String b = "SELECT COUNT(*) FROM %s";
        String LOCATION_SELECT_QUERY = String.format(b, TABLE_LOCATION);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(LOCATION_SELECT_QUERY, null);
        cursor.moveToFirst();
        long c = cursor.getInt(0);
        cursor.close();
        return c;
    }

    /**
     * Count a trip's location data
     */
    private int countAllData(Trip trip) {
        String tripId = trip.getId();
        String b = "SELECT COUNT(*) FROM %s WHERE tripId = %s";
        String TRIPS_SELECT_QUERY = String.format(b, TABLE_LOCATION, tripId);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TRIPS_SELECT_QUERY, null);
        cursor.moveToFirst();
        int c = cursor.getInt(0);
        cursor.close();
        return c;
    }

    /**
     * Count all trips
     */
    public long countAllTrips() {
        String b = "SELECT COUNT(*) FROM %s";
        String TRIP_SELECT_QUERY = String.format(b, TABLE_TRIP);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TRIP_SELECT_QUERY, null);
        cursor.moveToFirst();
        long c = cursor.getInt(0);
        cursor.close();
        return c;
    }

    /**
     * Delete trip and location data
     */
    void deleteTripAndLocations(Trip trip) {

        String tripId = trip.getId();
        Log.i(TAG, "deleteTripAndLocations: tripId: " + tripId);

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        String whereTripClause = "id=?";
        String whereLocationClause = "tripId=?";

        try {
            db.delete(TABLE_LOCATION, whereLocationClause, new String[]{String.valueOf(tripId)});
            db.delete(TABLE_TRIP, whereTripClause, new String[]{String.valueOf(tripId)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to deleteFromDatabase trip and locations");
            e.printStackTrace();
        } finally {
            db.endTransaction();
            Log.i(TAG, "DELETED TRIP: " + tripId);
        }
    }

}
