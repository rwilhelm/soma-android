package de.uniko.fb1.SoMA;

import android.*;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationAssistant.Listener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 200;

    private final List<Marker> myMarkers = new ArrayList<>();

    private static GoogleMap map;

    private Boolean isLocationAssistantRunning = false;

    private AlarmReceiver alarmReceiver;
    private MapsReceiver mapsReceiver;
    private LocationAssistant assistant;
    private TextView tvLocation;
    private Location lastLocation;

//    private LocationAssistant.Listener listener = new LocationAssistant.Listener() {
//    };

    /**
     *
     *
     *
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");

        /* Location Assistant */
        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, Constants.CONFIG.UPDATE_INTERVAL, false);
        assistant.setVerbose(true);

        setContentView(R.layout.activity_maps);

        if (savedInstanceState == null) {
            Log.w(TAG, "[ACTIVITY] No savedInstanceState");
            // startRegistrationService();
        }

        /* Map fragment */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* Upload Button */
        Button uploadButton = (Button) findViewById(R.id.upload);
        uploadButton.setOnClickListener(v -> {
            showSnackBar("Uploading data");
            uploadData();
        });

        /* Status Display */
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocation.setText(getString(R.string.empty));

//        // FIXME This is maybe wrong
//        Intent firstAlarm = new Intent(this, AlarmReceiver.class);
//        firstAlarm.setAction(Constants.ACTION.SET_FIRST_ALARM);
//        startService(firstAlarm);

        Log.v(TAG, "[ACTIVITY] assistant.googleApiClient.isConnected() " + assistant.googleApiClient.isConnected());

        /* On-off Switch */
        Switch onOffSwitch = (Switch) findViewById(R.id.assistantSwitch);
        onOffSwitch.setChecked(assistant.googleApiClient.isConnected()); // FIXME
        onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startLocationAssistant();
            } else {
                stopLocationAssistant();
            }
        });

        /* Notification Area */
        if (!isMyServiceRunning(NotificationService.class)) {
            Log.v(TAG, "[MAIN CREATE] Starting NotificationService");
            Intent intent = new Intent(this, NotificationService.class);
            intent.setAction(Constants.ACTION.START_LOCATION_SERVICE);
            startService(intent);
        }


//        05-30 04:19:08.987 ? V/MapsActivity: [MAIN CREATE] Starting NotificationService
//        05-30 04:19:08.987 ? V/MapsActivity: [ALARM] Working ...
//        05-30 04:19:08.988 ? V/MapsActivity: [ALARM] Starting AlarmReceiver
//        05-30 04:19:08.988 ? W/ActivityManager: Unable to start service Intent { cmp=de.uniko.fb1.SoMA/.AlarmReceiver } U=0: not found
//        05-30 04:19:08.988 ? V/MapsActivity: Register broadcast receivers
//        05-30 04:19:08.989 ? D/MapsActivity: Google Play Services available: true

//        this.startService(new Intent(this, AlarmReceiver.class));

        /* Upload Scheduler */
//        Log.v(TAG, "[ALARM] Working ...");
//        if (!isMyServiceRunning(AlarmReceiver.class)) {
//            Log.v(TAG, "[ALARM] Starting new AlarmReceiver");
//
////            Intent intent = new Intent(this, NotificationService.class);
////            intent.setAction(Constants.ACTION.START_LOCATION_SERVICE);
////            startService(intent);
//
////            registerReceiver( alarmReceiver , new IntentFilter(Constants.ACTION.SET_FIRST_ALARM));
//            startService(new Intent(this, AlarmReceiver.class));
//
////            Intent myIntent = new Intent(getBaseContext(), **AlarmReceiver**.class);
////            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);
////            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
////            Calendar calendar = Calendar.getInstance();
////            calendar.setTimeInMillis(System.currentTimeMillis());
////            calendar.add(Calendar.MINUTE, shpref.getInt("timeoutint", 30));
////            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//
//
////            Intent intent = new Intent(this, AlarmReceiver.class);
//
////            PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
////            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
////            Log.v(TAG, "[ALARM] AlarmReceiver XXX " + am.getNextAlarmClock().getTriggerTime());
////            am.cancel(pi);
//
//        } else {
//            Log.v(TAG, "[ALARM] AlarmReceiver already running");
//        }

//        registerBroadcastReceiver();

        //            Intent intent = new Intent(this, AlarmReceiver.class);
//            intent.setAction(Constants.ACTION.SET_FIRST_ALARM);
//            startService(intent);

        scheduleAlarm();

        Log.d(TAG, "Google Play Services available: " + checkPlayServices());
        Log.d(TAG, "assistant.googleApiClient.isConnected(): " + assistant.googleApiClient.isConnected());
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "[1] onPause");
        Log.d(TAG, "assistant.googleApiClient.isConnected(): " + assistant.googleApiClient.isConnected());

        unregisterBroadcastReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "[9] onStop");
        Log.d(TAG, "assistant.googleApiClient.isConnected(): " + assistant.googleApiClient.isConnected());

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "[4] onResume");
        Log.d(TAG, "assistant.googleApiClient.isConnected(): " + assistant.googleApiClient.isConnected());

        registerBroadcastReceiver();
        assistant.start();
        queryAlarm();
//        isLocationAssistantRunning = true;
//        assistant.register(this, listener);
    }

    /**
     *
     *
     *
     *
     */


    @Override
    public void onNeedLocationPermission() {
        tvLocation.setText("Need\nPermission");
        tvLocation.setOnClickListener(view -> assistant.requestLocationPermission());
        assistant.requestAndPossiblyExplainLocationPermission();
    }

    @Override
    public void onExplainLocationPermission() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permissionExplanation)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    assistant.requestLocationPermission();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    tvLocation.setOnClickListener(v -> assistant.requestLocationPermission());
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (assistant.onPermissionsUpdated(requestCode, grantResults))
            tvLocation.setOnClickListener(null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        assistant.onActivityResult(requestCode, resultCode);
        Log.d(TAG, "assistant.googleApiClient.isConnected(): " + assistant.googleApiClient.isConnected());
    }


    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {
        Log.d(TAG, "assistant.googleApiClient.isConnected(): " + assistant.googleApiClient.isConnected());
        tvLocation.setText(getString(R.string.error));
    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView,
                                                        DialogInterface.OnClickListener fromDialog) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permissionPermanentlyDeclined)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    @Override
    public void onNeedLocationSettingsChange() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.switchOnLocationShort)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    assistant.changeLocationSettings();
                })
                .show();
    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.switchOnLocationLong)
                .setPositiveButton(R.string.ok, fromDialog)
                .show();
    }

    @Override
    public void onNewLocationAvailable(Location location) {
//        Log.d(TAG, "onNewLocationAvailable " + location);

//        if (!isLocationAssistantRunning) {
//            Log.d(TAG, "[NEWLOCATION] Not taking it " + location);
//            return;
//        }
//
//        if (location == null) {
//            Log.d(TAG, "[NEWLOCATION] It is null");
//            return;
//        }
//
//        if (lastLocation != null && location.getTime() == lastLocation.getTime()) {
//            Log.d(TAG, "[NEWLOCATION] Same timestamp " + location.getTime());
//            return;
//        }
//
//        if (lastLocation != null && location.getLatitude() == lastLocation.getLatitude()) {
//            Log.d(TAG, "[NEWLOCATION] Same timestamp " + location.getTime());
//            return;
//        }


        DatabaseHelper db = DatabaseHelper.getInstance(this);
        long locationCount = db.addLocation(location);
//        sendBroadcast(new Intent(Constants.EVENT.LOCATION_UPDATED).putExtra("locationCount", locationCount));
        Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(Constants.EVENT.LOCATION_UPDATED);
        intent.putExtra("locationCount", locationCount);
        startService(intent);

        updateMap(location); // FIXME
        updateUI(location);

//        Log.d(TAG, "[BROADCAST] " + Constants.EVENT.LOCATION_UPDATED);

//        if (isMyServiceRunning(NotificationService.class)) {
//            Log.d(TAG, "[BROADCAST] " + Constants.EVENT.LOCATION_UPDATED);
//            sendBroadcast(new Intent(Constants.EVENT.LOCATION_UPDATED).putExtra("location", location));
//        }

//        Log.d(TAG, "[NEWLOCATION] Adding location to DatabaseHelper");

//        lastLocation = location;

        // FIXME Who whants to know? (A) Notification, (B) UI TextView and (C) DatabaseHelper.add
        // TODO Rename NotificationService at it is not a location service but a notification service

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        tvLocation.setText(getString(R.string.mockLocationMessage));
        tvLocation.setOnClickListener(fromView);
    }

//    @Override
//    public void onError(LocationAssistant.ErrorType type, String message) {
//        tvLocationError();
//    }

    private void updateUI(Location location) {
        if (location == null) return;
        tvLocation.setOnClickListener(null);
        tvLocation.setText(location.getLongitude() + "\n" + location.getLatitude());
        tvLocation.setAlpha(1.0f);
        tvLocation.animate().alpha(0.5f).setDuration(400);
    }

    /**
     *
     *
     *
     *
     */

    public void startLocationAssistant() {
        Log.d(TAG, "Starting Location Assistant");
        assistant.start();
//        isLocationAssistantRunning = true;
    }

    public void stopLocationAssistant() {
        Log.d(TAG, "Stopping Location Assistant");
        assistant.stop();
//        isLocationAssistantRunning = false;
    }

//    private void tvLocationError() {
//        /* Update the location status text view and make it unclickable. */
//        tvLocation.setText(getString(R.string.error));
//    }

//    /* TODO Observable Pattern */
//    @Override
//    public void update(Observable observable, Object data) {
//        Log.wtf(TAG, "__update__!: " + data.toString());
//        Toast.makeText(this, "__update__!", Toast.LENGTH_SHORT).show();
//    }

//    /* TODO BroadcastReceiver Pattern */
//    public class MapsReceiver extends BroadcastReceiver {
//        private static final String TAG = "MapsReceiver";
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (intent.getAction()) {
//                case Intent.ACTION_TIME_TICK:
//                    if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0) {
//                        Log.v(TAG, "[MAIN RECEIVER] ACTION_TIME_TICK " + new Date().getTime());
//                        uploadTimer.setText(String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
//                    }
//                    break;
//            }
//        }
//    }

    /* TODO BroadcastReceiver Pattern */
    public class MapsReceiver extends BroadcastReceiver {
        private static final String TAG = "MapsReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            Switch onOffSwitch = (Switch) findViewById(R.id.assistantSwitch);
            switch (intent.getAction()) {
                case Constants.EVENT.GOOGLE_API_CLIENT_CONNECTED:
                    onOffSwitch.setChecked(true); // FIXME
                    break;
                case Constants.ACTION.FUSED_LOCATION_API_DISCONNECTED:
                    onOffSwitch.setChecked(false); // FIXME
                    break;
            }
        }
    }

    private void queryAlarm() {
        /* Request the AlarmManager object */
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /* Create the PendingIntent that will launch the BroadcastReceiver */
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);

        /* Schedule Alarm with and authorize to WakeUp the device during sleep */
        AlarmManager.AlarmClockInfo nextAlarmClock = manager.getNextAlarmClock();
        if (nextAlarmClock != null) {
            long triggerTime = nextAlarmClock.getTriggerTime();
            Log.i(TAG, "Next Alarm Clock @ " + triggerTime);
        }
    }

    private void scheduleAlarm() {
        /* Request the AlarmManager object */
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /* Create the PendingIntent that will launch the BroadcastReceiver */
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);

        /* Schedule Alarm with and authorize to WakeUp the device during sleep */
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 1000, pending);
    }

    private void cancelAlarm() {
        /* Request the AlarmManager object */
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /* Create the PendingIntent that would have launched the BroadcastReceiver */
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);

        /* Cancel the alarm associated with that PendingIntent */
        manager.cancel(pending);
    }

    /* Register broadcast receivers */
    private void registerBroadcastReceiver() {
        Log.v(TAG, "Register broadcast receivers");

        if (alarmReceiver == null) {
            Log.d(TAG, "Creating new AlarmReceiver");
            alarmReceiver = new AlarmReceiver();
        } else {
            Log.d(TAG, "Using old AlarmReceiver");
        }

        registerReceiver(alarmReceiver, new IntentFilter(Constants.ACTION.UPLOAD_SCHEDULER_NOTIFY));

        if (mapsReceiver == null) {
            Log.d(TAG, "Creating new MapsReceiver");
            mapsReceiver = new MapsReceiver();
        } else {
            Log.d(TAG, "Using old MapsReceiver");
        }

        registerReceiver(mapsReceiver, new IntentFilter(Constants.EVENT.GOOGLE_API_CLIENT_CONNECTED));
        registerReceiver(mapsReceiver, new IntentFilter(Constants.ACTION.FUSED_LOCATION_API_DISCONNECTED));

        // FIXME maybe register broadcastreceiver for notificationService not here but over there
//        registerReceiver(alarmReceiver, new IntentFilter(Constants.EVENT.LOCATION_UPDATED));
    }

    /* Unregister broadcast receivers */
    private void unregisterBroadcastReceiver() {
        Log.v(TAG, "Unregister broadcast receivers");
        if (alarmReceiver != null) unregisterReceiver(alarmReceiver);
        if (mapsReceiver != null) unregisterReceiver(mapsReceiver);
    }

    private void uploadData() {
        Intent uploadData = new Intent(MapsActivity.this, NotificationService.class);
        uploadData.setAction(Constants.ACTION.MANUAL_UPLOAD);
        startService(uploadData);
    }

//    private void startRegistrationService() {
//        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
//        int code = api.isGooglePlayServicesAvailable(this);
//        if (code == ConnectionResult.SUCCESS) {
//            onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
//        } else if (api.isUserResolvableError(code) &&
//                api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES)) {
//            Log.v(TAG, "startRegistrationService: WHAT HELLO WHAT WHAT HELP PLZ");
//            // wait for onActivityResult call (see below)
//        } else {
//            Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_LONG).show();
//        }
//    }

    /**
     *
     * Map
     *
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "onMapReady");
        map = googleMap;
    }

    private void updateMap(Location location) {
        if (location == null) return;
        LatLng latest = new LatLng(location.getLatitude(), location.getLongitude());
        drawPolyline();
        for (Marker m: myMarkers) {
            m.remove();
        }
        myMarkers.add(map.addMarker(new MarkerOptions().position(latest)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latest, 16));
    }

    private void updateMarker() {

    }

    private void drawPolyline() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<LatLng> latLngs = new ArrayList<>();
        for (LocationObject l: db.getLocations()) {
            latLngs.add(l.getLatLng());
        }

        map.clear();
        Polyline line = map.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.RED));


        line.setPoints(latLngs);
    }

    private void focusMap(List<LatLng> latLngs) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (!latLngs.isEmpty()) {
            for (LatLng point: latLngs) {
                builder.include(point);
            }

            LatLngBounds bounds = builder.build();

            int padding = 128; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.animateCamera(cu);
        }
    }
    /**
     * Helpers
     */

    /* Check if a service is running*/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // http://stackoverflow.com/a/31016761/220472
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private void showSnackBar(String string) {
        View view = findViewById(android.R.id.content);
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show();
    }

    private void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
















//
//
//    /**
//     * Build a GoogleApiClient and set up the location request
//     */
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
//
//    public void enableService() {
//        enabled = true;
//        if (currentTrip == null) currentTrip = new Trip(this);
//        connectGoogleAPIClient(); // will call startLocationService in onConnected
//    }
//
//    public void disableService() {
//        currentTrip = null;
//        enabled = false;
//        disconnectGoogleAPIClient();
//        stopLocationService();
//    }
//
//
//    /**
//     * Stop location updates
//     */
//    public void stopLocationService() {
//        Log.i(TAG, "stopLocationService");
//        if (googleApiClient !=null && googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            sendBroadcast(new Intent(Constants.ACTION.FUSED_LOCATION_API_DISCONNECTED));
//        }
//    }
//
//
//
//    /**
//     * Request location updates. Needs a connected googleApiClient
//     * which will be passed to the FusedLocationApi.
//     */
//    private void startLocationService() {
//        Log.i(TAG, "startLocationService");
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
//
//    /**
//     * Connect the Google API Client FIXME cleanup
//     */
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
//
//
//    /**
//     * Connect the Google API Client
//     */
//    private void connectGoogleAPIClient() {
//        Log.i(TAG, "connectGoogleAPIClient");
//
//        if (googleApiClient != null && !googleApiClient.isConnected()) {
//            googleApiClient.connect();
//        }
//
//        broadcastPreferences();
//    }
//





































}
