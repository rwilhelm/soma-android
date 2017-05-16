package de.uniko.fb1.soma7;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/7/17.
 */

public class MyLocationService extends Service implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MyLocationService";

    // TODO Implement Android Location API as fallback
    // http://stackoverflow.com/questions/33022662/android-locationmanager-vs-google-play-services

    public static boolean enabled;
    public static boolean isConnected;

    /* Location provider */
    public static Trip currentTrip; // Current Trip
    public static Location lastLocation; // Last received location

    private GoogleApiClient googleApiClient; // Google Play Services
    private LocationRequest locationRequest; // Request params of FusedLocationProviderApi

    /**
     * Show in notification bar
     **/
    public NotificationCompat.Builder updateNotification() {
        Log.d(TAG, "updateNotification: ");
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        long dataCount = db.countAllData();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setAction(Constants.ACTION.RESUME_FOREGROUND_ACTION);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_unilogo_android);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationText;

        notificationText = "Status: " + (isConnected ? "enabled" : "disabled");
        Log.d(TAG, "updateNotification: notificationText: " + notificationText);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle(Constants.APP_NAME)
                        .setContentText(notificationText)
                        .setContentInfo("Data: " + dataCount)
                        .setSmallIcon(R.drawable.ic_unilogo_android)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);

        mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification.build());

        return notification;
    }


    public void broadcastConnectionStatus() {
        Log.d(TAG, "broadcastConnectionStatus: ");
        if (googleApiClient.isConnected()) {
            sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CLIENT_IS_CONNECTED));
        } else if (googleApiClient.isConnecting()) {
            sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CLIENT_IS_CONNECTING));
        } else {
            sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CLIENT_IS_DISCONNECTED));
        }
    }


    public boolean isGooglePlayServicesAvailable(Context context){
        Log.d(TAG, "isGooglePlayServicesAvailable: ");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");

        /* TODO Check if Google Play Services are available, fallback to Android Location API */


        buildGoogleApiClient();

        /* TODO Check if the notification exists before calling startForeground */
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, updateNotification().build());

        sendBroadcast(new Intent(Constants.ACTION.START_SERVICE));

//        scheduleUploads(this);
        /* Set up the alarm */
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        alarm.set(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + Constants.UPLOAD_SCHEDULE,
//                PendingIntent.getBroadcast(this, 0, new Intent(Constants.ACTION.SCHEDULED_UPLOAD), 0)
//        );

    }


    /* Actions, e.g. commands to this service, are passed as Intent carrying an action constant. */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand ");

        /* When the Intent is created in MainActitivy, we call setAction with
        Constants.ACTION.ENABLE_SERVICE */

        if (intent != null) {

            /* TODO How comes that intent can be null? */

            String action = intent.getAction();
            Log.wtf(TAG, "[RECEIVE] " + action);

            if (action.equals(Constants.ACTION.ENABLE_SERVICE)) {
                Log.i(TAG, "[RECEIVE] ENABLE_SERVICE");
                Log.i(TAG, "[RECEIVE] ENABLE_SERVICE");
                enableService();
            } else if (action.equals(Constants.ACTION.DISABLE_SERVICE)) {
                Log.i(TAG, "[RECEIVE] DISABLE_SERVICE");
                disableService();
            } else if (action.equals(Constants.ACTION.RESUME_SERVICE)) {
                Log.i(TAG, "[RECEIVE] RESUME_SERVICE");
                broadcastPreferences();
                broadcastLastLocation();
                broadcastConnectionStatus();
            } else if (action.equals(Constants.ACTION.UPLOAD_DATA)) {
                Log.i(TAG, "[RECEIVE] UPLOAD_DATA");
                uploadData();
            } else if (action.equals(Constants.ACTION.PROPAGATE_CHANGES)) {
                Log.i(TAG, "[RECEIVE] PROPAGATE_CHANGES");
                broadcastPreferences();
                broadcastLastLocation();
                broadcastConnectionStatus();
            } else if (action.equals(Constants.ACTION.QUIT_SERVICE)) {
                Log.i(TAG, "[RECEIVE] QUIT_SERVICE");
                disableService();
                uploadData();
                stopForeground(true);
                stopSelf();
//                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
            } else {
                Log.w(TAG, "UNKNOWN INTENT" + action);
            }
        } else {
            Log.e(TAG, "NULL INTENT");
        }

        return START_STICKY;
    }

    private void broadcastPreferences() {
        sendBroadcast(new Intent(Constants.ACTION.LOCATION_PREFERENCES)
                .putExtra("interval", locationRequest.getInterval())
                .putExtra("fastestInterval", locationRequest.getFastestInterval())
                .putExtra("priority", locationRequest.getPriority())
                .putExtra("expirationTime", locationRequest.getExpirationTime())
                .putExtra("maxWaitTime", locationRequest.getMaxWaitTime())
                .putExtra("numUpdates", locationRequest.getNumUpdates())
                .putExtra("smallestDisplacement", locationRequest.getSmallestDisplacement())
        );
    }

    private void broadcastLastLocation() {
        if (lastLocation != null) {
            sendBroadcast(new Intent(Constants.ACTION.LOCATION_UPDATE).putExtra("lastLocation", lastLocation));
        } else {
            Log.w(TAG, "broadcastLastLocation: SORRY NO LOCATION");
        }
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        sendBroadcast(new Intent(Constants.ACTION.STOP_SERVICE));
        super.onDestroy(); // should be called last
    }

    // Used only in case if services are bound (Bound Services).
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind ");
        return null;
    }


    /* Called when the googleApiClient has connected. */
    // Implements com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "[SEND] GOOGLE_API_CONNECTION_SUCCESS");
        sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CONNECTION_SUCCESS)
                        .putExtra("connection_status", "connected")
        );

        /* Start tracking as soon as the Google API Client is connected.
        Requires a connected Google API Client. */
        startLocationService();

        isConnected = true;
        broadcastConnectionStatus();
        updateNotification();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GOOGLE_API_CONNECTION_SUSPEND");
        sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CONNECTION_SUSPEND)
                .putExtra("connection_status", "suspended")
        );

        isConnected = false;
        broadcastConnectionStatus();
        updateNotification();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "GOOGLE_API_CONNECTION_FAILURE" + " " + connectionResult.getErrorCode() + " " + connectionResult.getErrorMessage());
        sendBroadcast(new Intent(Constants.ACTION.GOOGLE_API_CONNECTION_FAILURE)
                .putExtra("connection_status", "failed")
                .putExtra("errorCode", connectionResult.getErrorCode())
                .putExtra("errorMessage", connectionResult.getErrorMessage())
        );

        isConnected = false;
        broadcastConnectionStatus();
        updateNotification();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged " + location);

        if (currentTrip == null) {
            Log.d(TAG, "onLocationChanged: NO CURRENT TRIP -- RETURNING");
            return;
        }

        lastLocation = location;

        DatabaseHelper db = DatabaseHelper.getInstance(this);

        if (db.addLocation(location, currentTrip) > -1) {
            broadcastLastLocation();
        }

        updateNotification();
    }

    /**
     * Connect the Google API Client
     */
    private void connectGoogleAPIClient() {
        Log.i(TAG, "connectGoogleAPIClient");

        if (googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        broadcastPreferences();
    }


//    public MyLocationService() {
//        super();
//        Log.i(TAG, "MyLocationService");
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
    private void disconnectGoogleAPIClient() {
        if (googleApiClient != null) {
            Log.d(TAG, "disconnectGoogleAPIClient: googleApiClient IS NOT NULL");
            if (googleApiClient.isConnected()) {
                Log.d(TAG, "disconnectGoogleAPIClient: googleApiClient IS CONNECTED");
            } else {
                Log.d(TAG, "disconnectGoogleAPIClient: googleApiClient IS NOT CONNECTED");
            }
        } else {

        }

        if (googleApiClient != null && googleApiClient.isConnected()) {
            Log.d(TAG, "disconnectGoogleAPIClient: OK ALL END PLEASE!");
            googleApiClient.disconnect();
            isConnected = false;
            broadcastConnectionStatus();
            updateNotification();
        } else {
            Log.d(TAG, "disconnectGoogleAPIClient: NOPE!");
        }

    }

    /**
     * Request location updates. Needs a connected googleApiClient
     * which will be passed to the FusedLocationApi.
     */
    private void startLocationService() {
        Log.i(TAG, "startLocationService");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "XXX LOCATION PERMISSION FAILURE XXX ");

            // TODO: Consider calling
            // FIXME! FusedLocationApi for old androids
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            Log.d(TAG, "XXX LOCATION PERMISSION SUCCESS XXX ");
        }

        Log.d(TAG, "XXX LOCATION FIX XXX " + LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient).isLocationAvailable());

        /* Check and broadcast if location is available */
        Boolean isLocationAvailable = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient).isLocationAvailable();

        Log.i(TAG, "isLocationAvailable " + isLocationAvailable);

        sendBroadcast(new Intent(Constants.ACTION.LOCATION_AVAILABILITY)
                .putExtra("isLocationAvailable", isLocationAvailable)
        );

        if (isLocationAvailable) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            sendBroadcast(new Intent(Constants.ACTION.FUSED_LOCATION_API_CONNECTED));
        }
    }

    /**
     * Stop location updates
     */
    public void stopLocationService() {
        Log.i(TAG, "stopLocationService");
        if (googleApiClient !=null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            sendBroadcast(new Intent(Constants.ACTION.FUSED_LOCATION_API_DISCONNECTED));
        }
    }

    /**
     * Upload all data, one by one (deletes after success)
     */
    public void uploadData() {
        Log.i(TAG, "uploadData");

        // TODO Only upload on WiFi
//        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//        if (!wifi.isWifiEnabled()){
//            return;
//        }

        /* Get all trips. */
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<Trip> trips = db.getTrips();

        String tripsText = trips.size() == 1 ? "trip" : "trips";
        Log.i(TAG, "Uploading " + trips.size() + " " + tripsText);
        Toast.makeText(this, "Uploading " + trips.size() + " " + tripsText, Toast.LENGTH_SHORT).show();

        /**
         * Pass each trip to UploadDataTask, where it will be uploaded asynchronously to the API.
         * Pre- and post-hooks happen there, e.g. deleting the trip.
         */
        for (Trip trip : trips) {
            new UploadDataTask(this).execute(trip);
        }
    }

    /**
     * Build a GoogleApiClient and set up the location request
     */
    private synchronized void buildGoogleApiClient() {
        Log.i(TAG, "buildGoogleApiClient");

        /* Build a new GoogleApiClient and register the listeners residing in this class. */
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /* Configure the locationRequest object which is passed to the FusedLocationApi */
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.UPDATE_INTERVAL);
        locationRequest.setFastestInterval(Constants.UPDATE_INTERVAL_FAST);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    /* Create a new trip and put it in the database. */
    public void createNewTrip() {
        if (currentTrip == null) currentTrip = new Trip(this);
    }

    public void enableService() {
        enabled = true;
        if (currentTrip == null) currentTrip = new Trip(this);
        connectGoogleAPIClient(); // will call startLocationService in onConnected
    }

    public void disableService() {
        currentTrip = null;
        enabled = false;
        disconnectGoogleAPIClient();
        stopLocationService();
    }

}

