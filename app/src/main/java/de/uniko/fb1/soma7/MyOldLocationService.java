package de.uniko.fb1.soma7;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by asdf on 5/17/17.
 */

public class MyOldLocationService {
    private static final String TAG = "LocationService";

    public boolean isGooglePlayServicesAvailable(Context context){
        Log.d(TAG, "isGooglePlayServicesAvailable: ");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }


//    private void broadcastPreferences() {
//        sendBroadcast(new Intent(Constants.ACTION.LOCATION_PREFERENCES)
//                .putExtra("interval", locationRequest.getInterval())
//                .putExtra("fastestInterval", locationRequest.getFastestInterval())
//                .putExtra("priority", locationRequest.getPriority())
//                .putExtra("expirationTime", locationRequest.getExpirationTime())
//                .putExtra("maxWaitTime", locationRequest.getMaxWaitTime())
//                .putExtra("numUpdates", locationRequest.getNumUpdates())
//                .putExtra("smallestDisplacement", locationRequest.getSmallestDisplacement())
//        );
//    }

//    private void broadcastLastLocation() {
//        if (lastLocation != null) {
//            sendBroadcast(new Intent(Constants.ACTION.LOCATION_UPDATE).putExtra("lastLocation", lastLocation));
//        } else {
//            Log.w(TAG, "broadcastLastLocation: SORRY NO LOCATION");
//        }
//    }


    /* Called when the googleApiClient has connected. */
    // Implements com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Log.i(TAG, "[SEND] GOOGLE_API_CONNECTION_SUCCESS");
//        sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CONNECTION_SUCCESS)
//                        .putExtra("connection_status", "connected")
//        );
//
//        /* Start tracking as soon as the Google API Client is connected.
//        Requires a connected Google API Client. */
//        startLocationService();
//
////        isConnected = true;
////        broadcastConnectionStatus();
//        updateNotification();
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.i(TAG, "GOOGLE_API_CONNECTION_SUSPEND");
//        sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CONNECTION_SUSPEND)
//                .putExtra("connection_status", "suspended")
//        );
//
////        isConnected = false;
////        broadcastConnectionStatus();
//        updateNotification();
//    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.w(TAG, "GOOGLE_API_CONNECTION_FAILURE" + " " + connectionResult.getErrorCode() + " " + connectionResult.getErrorMessage());
//        sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CONNECTION_FAILURE)
//                .putExtra("connection_status", "failed")
//                .putExtra("errorCode", connectionResult.getErrorCode())
//                .putExtra("errorMessage", connectionResult.getErrorMessage())
//        );
//
////        isConnected = false;
////        broadcastConnectionStatus();
//        updateNotification();
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Log.i(TAG, "onLocationChanged " + location);
//
//        if (currentTrip == null) {
//            Log.d(TAG, "onLocationChanged: NO CURRENT TRIP -- RETURNING");
//            return;
//        }
//
//        lastLocation = location;
//
//        DatabaseHelper db = DatabaseHelper.getInstance(this);
//
//        if (db.addLocation(location, currentTrip) > -1) {
//            broadcastLastLocation();
//        }
//
//        updateNotification();
//    }

    /**
     * Connect the Google API Client
     */
//    private void connectGoogleAPIClient() {
//        Log.i(TAG, "connectGoogleAPIClient");
//
////        if (googleApiClient != null && !googleApiClient.isConnected()) {
////            googleApiClient.connect();
////        }
////
////        broadcastPreferences();
//    }


//    public LocationService() {
//        super();
//        Log.i(TAG, "LocationService");
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        Log.i(TAG, "onTaskRemoved: " + rootIntent);
//    }
//
//    @Override
//    public void onRebind(Intent intent) {
//        Log.i(TAG, "onRebind: " + intent);
//        super.onRebind(intent);
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        Log.i(TAG, "onUnbind: " + intent);
//        return super.onUnbind(intent);
//    }
//
//    @Override
//    public void onTrimMemory(int level) {
//        Log.i(TAG, "onTrimMemory: " + level);
//        super.onTrimMemory(level);
//    }
//
//    @Override
//    public void onLowMemory() {
//        Log.i(TAG, "onLowMemory");
//        super.onLowMemory();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        Log.i(TAG, "onConfigurationChanged: " + newConfig);
//        super.onConfigurationChanged(newConfig);
//    }

    /**
     * Connect the Google API Client FIXME cleanup
     */
//    private void disconnectGoogleAPIClient() {
//        if (googleApiClient != null) {
//            Log.d(TAG, "disconnectGoogleAPIClient: googleApiClient IS NOT NULL");
//            if (googleApiClient.isConnected()) {
//                Log.d(TAG, "disconnectGoogleAPIClient: googleApiClient IS CONNECTED");
//            } else {
//                Log.d(TAG, "disconnectGoogleAPIClient: googleApiClient IS NOT CONNECTED");
//            }
//        } else {
//
//        }
//
//        if (googleApiClient != null && googleApiClient.isConnected()) {
//            Log.d(TAG, "disconnectGoogleAPIClient: OK ALL END PLEASE!");
//            googleApiClient.disconnect();
//            isConnected = false;
//            broadcastConnectionStatus();
//            updateNotification();
//        } else {
//            Log.d(TAG, "disconnectGoogleAPIClient: NOPE!");
//        }
//
//    }

    /**
     * Request location updates. Needs a connected googleApiClient
     * which will be passed to the FusedLocationApi.
     */
//    private void startLocationService() {
//        Log.i(TAG, "startLocationService");
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "XXX LOCATION PERMISSION FAILURE XXX ");
//
//            // TODO: Consider calling
//            // FIXME! FusedLocationApi for old androids
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        } else {
//            Log.d(TAG, "XXX LOCATION PERMISSION SUCCESS XXX ");
//        }
//
//        Log.d(TAG, "XXX LOCATION FIX XXX " + LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient).isLocationAvailable());
//
//        /* Check and broadcast if location is available */
//        Boolean isLocationAvailable = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient).isLocationAvailable();
//
//        Log.i(TAG, "isLocationAvailable " + isLocationAvailable);
//
//        sendBroadcast(new Intent(Constants.ACTION.LOCATION_AVAILABILITY)
//                .putExtra("isLocationAvailable", isLocationAvailable)
//        );
//
//        if (isLocationAvailable) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//            sendBroadcast(new Intent(Constants.ACTION.FUSED_LOCATION_API_CONNECTED));
//        }
//    }

    /**
     * Stop location updates
     */
//    private void stopLocationService() {
//        Log.i(TAG, "stopLocationService");
//        if (googleApiClient !=null && googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            sendBroadcast(new Intent(Constants.ACTION.FUSED_LOCATION_API_DISCONNECTED));
//        }
//    }


    /**
     * Build a GoogleApiClient and set up the location request
     */
//    private synchronized void buildGoogleApiClient() {
//        Log.i(TAG, "buildGoogleApiClient");
//
//        /* Build a new GoogleApiClient and register the listeners residing in this class. */
//        googleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        /* Configure the locationRequest object which is passed to the FusedLocationApi */
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(Constants.UPDATE_INTERVAL);
//        locationRequest.setFastestInterval(Constants.UPDATE_INTERVAL_FAST);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//    }

//    /* Create a new trip and put it in the database. */
//    public void createNewTrip() {
//        if (MapsActivity.currentTrip == null) MapsActivity.currentTrip = new Trip(this);
//    }

//    private void enableService() {
//        enabled = true;
////        if (currentTrip == null) currentTrip = new Trip(this);
//        connectGoogleAPIClient(); // will call startLocationService in onConnected
//    }

//    private void disableService() {
////        currentTrip = null;
//        enabled = false;
//        disconnectGoogleAPIClient();
//        stopLocationService();
//    }


}
