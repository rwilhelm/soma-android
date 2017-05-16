package de.uniko.fb1.soma7;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Trip extends Object {
    private static final String TAG = "Trip";

    private String id;
    private String uuid;
    private String androidId;

    private Context context;

    @SuppressLint("HardwareIds")
    Trip(Context context) {
        super();
        this.context = context;

        this.uuid = UUID.randomUUID().toString();
        this.androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        this.id = saveToDatabase(context, this);
    }

    Trip(Context context, String androidId, String uuid, String id) {
        super();
        this.context = context;

        this.id = id;
        this.uuid = uuid;
        this.androidId = androidId;
    }

    String getId() {
        return id;
    }
    String getUUID() {
        return uuid;
    }
    String getAndroidId() {
        return androidId;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * INSERT Trip
     * @param context MainActivity's this
     */
    private String saveToDatabase(Context context, Trip trip) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        id = db.addTrip(trip); // returns new trip id from db
        return id;
    }

    /* Count collected location data */
    public long getDataCount() {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        return db.countAllData();
    }

    /* Delete trip from database */
    public void deleteFromDatabase() {
        DatabaseHelper db = DatabaseHelper.getInstance(context);

        if (MyLocationService.currentTrip != null) {
            if (this.getId().equals(MyLocationService.currentTrip.getId())) {
                MyLocationService.currentTrip = new Trip(context);
            }
        }

        db.deleteTripAndLocations(this);
    }

    /* Get JSON of trip and location data */
    JSONObject getTripJSON(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        JSONObject trip = db.getTrip(id);
        try {
            trip.put("locationData", getDataJSON(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trip;
    }

    /* Get JSON of the trip's location data */
    JSONArray getDataJSON(Context context) {
        Log.i(TAG, "getDataJSON => db.getLocations(id) " + id);
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        return new JSONArray(db.getLocations(id));
    }

    /**
     * Create request body
     */
    public String getRequestBody(Context context) {
        String requestBody = getTripJSON(context).toString();
        Log.i(TAG, "getRequestBody" + requestBody);
        return requestBody;
    }

}
