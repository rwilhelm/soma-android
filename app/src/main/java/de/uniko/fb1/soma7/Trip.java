package de.uniko.fb1.soma7;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Trip {
    private static final String TAG = "Trip";

    private String id;
    private final String uuid;
    private final String clientUUID;
    private final String androidId;

    private final Context context;

    @SuppressLint("HardwareIds")
    Trip(Context context) {
        super();
        this.context = context;

        this.uuid = UUID.randomUUID().toString();
        this.androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.clientUUID = UUID.nameUUIDFromBytes(this.androidId.getBytes()).toString();
        this.id = saveToDatabase(context, this);
    }

    Trip(Context context, String androidId, String uuid, String id) {
        super();
        this.context = context;

        this.id = id;
        this.uuid = uuid;
        this.androidId = androidId;
        this.clientUUID = UUID.nameUUIDFromBytes(this.androidId.getBytes()).toString();
    }

    String getId() {
        return id;
    }
    String getUUID() {
        return uuid;
    }
    String getClientUUID() {
        return clientUUID;
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
        db.deleteTripAndLocations(this);
    }

    /* Get JSON of trip and location data */
    private JSONObject getTripJSON(Context context) {
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
    private JSONArray getDataJSON(Context context) {
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

    public Boolean add(Context context, Location location) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        if (db.addLocation(location, this) > -1) {
            Log.i(TAG, "ADDED LOCATION ");
//            broadcastLastLocation(); // TODO
            return true;
        } else {
            Log.e(TAG, "FAILED ADDING LOCATION ");
            return false;
        }
    }

    public Boolean add(Location location) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        if (db.addLocation(location, this) > -1) {
            Log.i(TAG, "ADDED LOCATION ");
//            broadcastLastLocation(); // TODO
            return true;
        } else {
            Log.e(TAG, "FAILED ADDING LOCATION ");
            return false;
        }
    }

//    private void broadcastLastLocation() {
//        Location location = MapsActivity.lastLocation;
//        if (location != null) {
//            sendBroadcast(new Intent(Constants.ACTION.LOCATION_UPDATE).putExtra("lastLocation", location));
//        } else {
//            Log.w(TAG, "broadcastLastLocation: SORRY NO LOCATION");
//        }
//    }

    /**
     * Upload all data, one by one (deletes after success)
     */
    public void upload(Context context) {
        Log.i(TAG, "uploadData");

        // TODO Only upload on WiFi
//        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//        if (!wifi.isWifiEnabled()){
//            return;
//        }

        /* Get all trips. */
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        List<Trip> trips = db.getTrips();

        /* BAUSTELLE! */

        Log.i(TAG, "Upload all trips except the current one");
//        Log.i(TAG, "Current trip: " + LocationService.getCurrentTrip());
        Log.d(TAG, "trips " + trips);

//        List<Trip> tripsToUpload = trips.stream()
//                .filter(p -> p.getId() == MapsActivity.currentTrip.getId()).collect(Collectors.toList());

//        trips.removeIf(p -> p.getAge() > 16);

        Collections.sort(trips, (obj1, obj2) -> {
            // ## Ascending order
            return obj1.getId().compareToIgnoreCase(obj2.getId()); // To compare string values
            // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

            // ## Descending order
            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
            // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
        });

        trips.remove(trips.size() - 1);
//        trips.removeIf(p -> p.getId().equals());
        Log.d(TAG, "Trips to upload: " + trips);

        String tripsText = trips.size() == 1 ? "trip" : "trips";
        Log.i(TAG, "Uploading " + trips.size() + " " + tripsText);
        Toast.makeText(context, "Uploading " + trips.size() + " " + tripsText, Toast.LENGTH_SHORT).show();

        /**
         * Pass each trip to UploadAsyncTask, where it will be uploaded asynchronously to the API.
         * Pre- and post-hooks happen there, e.g. deleting the trip.
         */
        for (Trip trip : trips) {
            new UploadAsyncTask(context).execute(trip);
        }
    }


}
