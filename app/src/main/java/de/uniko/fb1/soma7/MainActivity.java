package de.uniko.fb1.soma7;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import static com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER;
import static com.google.android.gms.location.LocationRequest.PRIORITY_NO_POWER;
//import static de.uniko.fb1.soma.R.id.action_settings;

/**
 * MainActivity
 */

public class MainActivity extends AppCompatActivity implements Observer {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;

    /* Observable to monitor updates from our service */
    private DataUpdateReceiver dataUpdateReceiver;

    /* Location data in UI */
//    private TextView accuracyTextView; // Accuracy
//    private TextView altitudeTextView; // Altitude
//    private TextView bearingTextView; // Bearing
//    private TextView elapsedTextView; // Elapsed nanoseconds
//    private TextView latitudeTextView; // Latitude
//    private TextView longitudeTextView; // Longitude
//    private TextView speedTextView; // Speed
    private TextView timeTextView; // Timestamp

//    private TextView googleApiClientTextView; // Google API client connection status
//
//    private TextView dbTripsTextView; // Counted trips in DB
//    private TextView dbLocationsTextView; // Counted locations in DB

    private TextView locationAvailability; // Location availability
//    private TextView locationRequestIntervalTextView; // Request interval
//    private TextView locationFastestIntervalTextView; // Fastest interval
    private TextView locationExpirationTimeTextView; // Expiration time
    private TextView locationMaxWaitTimeTextView; // Max wait time
    private TextView locationNumUpdatesTextView; // Num updates
    private TextView locationSmallestDisplacementTextView; // Smallest displacement
//    private TextView locationPriorityTextView; // Priority

    private GoogleMap mMap;

    /* Buttons */
    private Button serviceButton;
    private Button uploadButton;
    private Button quitButton;

    /* Assign all UI elements */
    public void setupUI () {

        /* TextViews */
//        accuracyTextView = (TextView)  findViewById(R.id.accuracy);
//        altitudeTextView = (TextView) findViewById(R.id.altitude);
//        bearingTextView = (TextView) findViewById(R.id.bearing);
//        elapsedTextView = (TextView) findViewById(R.id.elapsed);
//        latitudeTextView = (TextView) findViewById(R.id.latitude);
//        longitudeTextView = (TextView) findViewById(R.id.longitude);
//        speedTextView = (TextView) findViewById(R.id.speed);
        timeTextView = (TextView) findViewById(R.id.time);

//        googleApiClientTextView = (TextView) findViewById(R.id.google_api_client);

//        dbTripsTextView = (TextView) findViewById(R.id.db_trips);
//        dbLocationsTextView = (TextView) findViewById(R.id.db_locations);

        locationAvailability = (TextView) findViewById(R.id.location_availability);
//        locationRequestIntervalTextView = (TextView) findViewById(R.id.location_request_interval);
//        locationFastestIntervalTextView = (TextView) findViewById(R.id.location_fastest_interval);
        locationExpirationTimeTextView = (TextView) findViewById(R.id.location_expiration_time);
        locationMaxWaitTimeTextView = (TextView) findViewById(R.id.location_max_wait_time);
        locationNumUpdatesTextView = (TextView) findViewById(R.id.location_num_updates);
        locationSmallestDisplacementTextView = (TextView) findViewById(R.id.location_smallest_displacement);
//        locationPriorityTextView = (TextView) findViewById(R.id.location_priority);

        /* Buttons */
//        serviceButton = (Button) findViewById(R.id.button_service);
//        uploadButton = (Button) findViewById(R.id.button_upload);
//        quitButton = (Button) findViewById(R.id.button_quit);
    }

    /* Toolbar */
    public void setupToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    /* Permissions */
    public void handlePermissions () {

        /* Ask for permission: ACCESS_FINE_LOCATION */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.w(TAG, "PERMISSION FAILURE " + Manifest.permission.ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.ASK_FOR_PERMISSION_ACCESS_FINE_LOCATION);
        }

//        /* Ask for permission: ACCESS_COARSE_LOCATION */
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            Log.w(TAG, "PERMISSION FAILURE " + Manifest.permission.ACCESS_COARSE_LOCATION);
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    Constants.ASK_FOR_PERMISSION_ACCESS_COARSE_LOCATION);
//        }
//
//        /* Ask for permission: ACCESS_NETWORK_STATE */
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            Log.w(TAG, "PERMISSION FAILURE " + Manifest.permission.ACCESS_NETWORK_STATE);
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
//                    Constants.ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE);
//        }
//
//        /* Ask for permission: INTERNET */
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            Log.w(TAG, "PERMISSION FAILURE " + Manifest.permission.INTERNET);
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    Constants.ASK_FOR_PERMISSION_ACCESS_INTERNET);
//        }
//
//        /* Ask for permission: RECEIVE_BOOT_COMPLETED */
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            Log.w(TAG, "PERMISSION FAILURE " + Manifest.permission.RECEIVE_BOOT_COMPLETED);
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    Constants.ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED);
//        }

    }


    public boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        Log.i(TAG, "Checking if Google Play Services are available ... " + (resultCode == ConnectionResult.SUCCESS));
        return resultCode == ConnectionResult.SUCCESS;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//
//        LeakCanary.install(getApplication() );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate " + savedInstanceState);

        /* To observe our BroadcastReceiver */
        ObservableObject.getInstance().addObserver(this);

        /* Check all required permissions and handle result */
        handlePermissions();

        /* Startup the background service */
        if (savedInstanceState == null) {
            startRegistrationService();
//            mGameState = savedInstanceState.getString(GAME_STATE_KEY);
        } else {
            Log.w(TAG, "onCreate: NOT STARTING THE BACKGROUND SERVICE SORRY MAN");
        }

        /* Set view and parent layout */
//        setContentView(R.layout.activity_main);

        /* UI */
        setupUI();
        setupButtons();
        setupToolbar();

//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//    }

    private void startRegistrationService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
        } else if (api.isUserResolvableError(code) &&
                api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES)) {
            Log.i(TAG, "startRegistrationService: WHAT HELLO WHAT WHAT HELP PLZ");
                // wait for onActivityResult call (see below)
        } else {
            Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    startLocationService();
                } else {
                    Log.w(TAG, "onActivityResult: GOOGLE PLAY SERVICES ARE NOT OKAY :/");
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void startLocationService () {
        /*
         * Start the service if it's not running.
         * TODO Check if Google Play Services are available, fallback to Android Location API
         * 10084000
         */
        if (isGooglePlayServicesAvailable(this)) {
            Log.i(TAG, "Google Play Services are available");
            if (!isMyServiceRunning(MyLocationService.class)) {
                Log.w(TAG, "startLocationService: [ACTION] " + Constants.ACTION.ENABLE_SERVICE);
                Intent service = new Intent(MainActivity.this, MyLocationService.class);
                service.setAction(Constants.ACTION.ENABLE_SERVICE);
                startService(service);
            } else {
                Log.w(TAG, "startLocationService: [ACTION] " + Constants.ACTION.PROPAGATE_CHANGES);
                Intent service = new Intent(MainActivity.this, MyLocationService.class);
                service.setAction(Constants.ACTION.PROPAGATE_CHANGES);
                startService(service);
            }
        } else {
            Log.w(TAG, "Google Play Services not available");
        }
    }

    public void setupButtons () {
        /*
         * Service Button
         */
        serviceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyLocationService.class);
                Log.w(TAG, "onClick: MyLocationService.enabled: " + MyLocationService.enabled);
                if (MyLocationService.enabled) {
                    Log.i(TAG, "onClick: IS ENABLED -- WILL DISABLE");
                    startService(intent.setAction(Constants.ACTION.DISABLE_SERVICE));
                } else {
                    Log.i(TAG, "onClick: IS DISABLED -- WILL ENABLE");
                    startService(intent.setAction(Constants.ACTION.ENABLE_SERVICE));
                }
            }
        });

       /*
        * Upload Button
        */
//        final Button uploadButton = (Button) findViewById(R.id.button_upload);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentLayout = findViewById(android.R.id.content);
                Intent uploadData = new Intent(MainActivity.this, MyLocationService.class);
                uploadData.setAction(Constants.ACTION.UPLOAD_DATA);
                startService(uploadData);
                showSnackBar(parentLayout, "Uploading data ...");
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentLayout = findViewById(android.R.id.content);
                Intent quitService = new Intent(MainActivity.this, MyLocationService.class);
                quitService.setAction(Constants.ACTION.QUIT_SERVICE);
                startService(quitService);
                showSnackBar(parentLayout, "Service will stop ...");
            }
        });
    }

    /* Overrides android.support.v7.app.AppCompatActivity */
    @Override
    protected void onStart() {
        Log.i(TAG, "[3] onStart");
        Log.i(TAG, "startUIUpdates()");
        super.onStart();
    }

    /* Overrides android.support.v7.app.AppCompatActivity */
    @Override
    protected void onStop() {
        Log.i(TAG, "[2] onStop");
        super.onStop();
    }

    /* Overrides android.support.v7.app.AppCompatActivity */
    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        // unsubscribe!
        super.onDestroy();
    }

    /* Overrides android.support.v4.app.FragmentActivity */
    @Override
    public void onResume() {
        Log.i(TAG, "[4] onResume");
        registerBroadcastReceiver();
//        if (MyLocationService.isConnected) {
//            serviceButton.setText(R.string.button_text_disconnect);
//        } else {
//            serviceButton.setText(R.string.button_text_connect);
//        }
        super.onResume();
    }

    /* Overrides android.support.v4.app.FragmentActivity */
    @Override
    protected void onPause() {
        Log.i(TAG, "[1] onPause");
        unregisterBroadcastReceiver();
        super.onPause();
    }

    /* TODO */
    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "__update__!: " + data.toString());
        Toast.makeText(this, "__update__!", Toast.LENGTH_SHORT).show();
    }

    /* Register broadcast receivers */
    public void registerBroadcastReceiver() {
        Log.i(TAG, "Register broadcast receivers");
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();

        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.GOOGLE_API_CONNECTION_SUCCESS));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.GOOGLE_API_CONNECTION_FAILURE));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.GOOGLE_API_CONNECTION_SUSPEND));

        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.GOOGLE_API_CLIENT_IS_CONNECTED));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.GOOGLE_API_CLIENT_IS_DISCONNECTED));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.GOOGLE_API_CLIENT_IS_CONNECTING));

        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.FUSED_LOCATION_API_CONNECTED));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.FUSED_LOCATION_API_DISCONNECTED));

        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.LOCATION_AVAILABILITY));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.LOCATION_PREFERENCES));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.LOCATION_UPDATE));

        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.DB_LOCATION_ADDED));
        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.DB_TRIP_ADDED));

        registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.ACTION.STOP_SERVICE));
    }

    /* Unregister broadcast receivers */
    public void unregisterBroadcastReceiver() {
        Log.i(TAG, "Unregister broadcast receivers");
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }

    /* Overrides android.support.v4.app.FragmentActivity */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.w(TAG, "onRequestPermissionsResult");

        /* Select a target layout for the Snackbar */
        View parentLayout = findViewById(android.R.id.content);

        switch (requestCode) {
            case Constants.ASK_FOR_PERMISSION_ACCESS_FINE_LOCATION: {
                Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_FINE_LOCATION");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_FINE_LOCATION GRANTED");
//                    Snackbar.make(parentLayout, "Access fine location: granted", Snackbar.LENGTH_LONG).show();
                }
                else {
                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_FINE_LOCATION DENIED");
//                    Snackbar.make(parentLayout, "Access fine location: denied", Snackbar.LENGTH_LONG).show();
                }
            }
//            case Constants.ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE: {
//                Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE");
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE GRANTED");
//                    Snackbar snackbar = Snackbar
//                            .make(parentLayout, "ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE GRANTED", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//                else {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE DENIED");
//                    Snackbar snackbar = Snackbar
//                            .make(parentLayout, "ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE DENIED", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//            }
//            case Constants.ASK_FOR_PERMISSION_ACCESS_COARSE_LOCATION: {
//                Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_COARSE_LOCATION");
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_COARSE_LOCATION GRANTED");
//                    showSnackBar(parentLayout, "Access coarse location: granted");
//                }
//                else {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_COARSE_LOCATION DENIED");
//                    showSnackBar(parentLayout, "Access coarse location: denied");
//                }
//            }
//            case Constants.ASK_FOR_PERMISSION_ACCESS_INTERNET: {
//                Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_INTERNET");
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_INTERNET GRANTED");
//                    Snackbar snackbar = Snackbar
//                            .make(parentLayout, "ASK_FOR_PERMISSION_ACCESS_INTERNET GRANTED", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//                else {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_INTERNET DENIED");
//                    Snackbar snackbar = Snackbar
//                            .make(parentLayout, "ASK_FOR_PERMISSION_ACCESS_INTERNET DENIED", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//            }
//            case Constants.ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED: {
//                Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED");
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED GRANTED");
//                    Snackbar snackbar = Snackbar
//                            .make(parentLayout, "ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED GRANTED", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//                else {
//                    Log.i(TAG, "ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED DENIED");
//                    Snackbar snackbar = Snackbar
//                            .make(parentLayout, "ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED DENIED", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//            }
        }
    }

    /* Overrides android.app.Activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* Overrides android.app.Activity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == action_settings) {
//             startActivity(new Intent(this, SettingsActivity.class)); // FIXME
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /* Overrides android.app.Activity */
    @Override
    @SuppressWarnings("EmptyMethod")
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "RESTORE_BUNDLE " + savedInstanceState);
    }

    /* Check if service is running*/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showSnackBar(View view, String string) {
//        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show();
    }

    private void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    private class DataUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            DatabaseHelper db = DatabaseHelper.getInstance(context);
            View parentLayout = findViewById(android.R.id.content);

            switch (action) {

                case Constants.ACTION.LOCATION_UPDATE:
                    Log.i(TAG, "LOCATION_UPDATE");
                    Location lastLocation = intent.getParcelableExtra("lastLocation");
                    updateLastLocationView(lastLocation);
                    break;

                case Constants.ACTION.LOCATION_AVAILABILITY:
                    Boolean isLocationAvailable = intent.getBooleanExtra("isLocationAvailable", false);
                    Log.i(TAG, "LOCATION_AVAILABILITY " + isLocationAvailable);
//                    locationAvailability.setText(String.format("%s: %s", getString(R.string.is_location_available_label), isLocationAvailable));
                    locationAvailability.setTextColor(isLocationAvailable ? 0xFF4E5340 : 0xFF985277);
                    locationAvailability.setTypeface(null, Typeface.BOLD);
                    break;

                case Constants.ACTION.LOCATION_PREFERENCES:
                    Log.i(TAG, "LOCATION_PREFERENCES");

                    long interval = intent.getLongExtra("interval", 0);
                    long fastestInterval = intent.getLongExtra("fastestInterval", 0);
                    int priority = intent.getIntExtra("priority", 0);
                    long expirationTime = intent.getLongExtra("expirationTime", 0);
                    long maxWaitTime = intent.getLongExtra("maxWaitTime", 0);
                    int numUpdates = intent.getIntExtra("numUpdates", 0);
                    float smallestDisplacement = intent.getFloatExtra("smallestDisplacement", 0);

//                    String interval_text = "" + (interval / 1000) + " " + getString(R.string.seconds);
//                    String smallestDisplacement_text = "" + smallestDisplacement + " " + getString(R.string.meters);

                    String priority_text = "...";
                    switch (priority) {
                        case PRIORITY_BALANCED_POWER_ACCURACY: // 101
//                            priority_text = getString(R.string.medium);
                            break;
                        case PRIORITY_HIGH_ACCURACY:
//                            priority_text = getString(R.string.high);
                            break;
                        case PRIORITY_LOW_POWER:
//                            priority_text = getString(R.string.low);
                            break;
                        case PRIORITY_NO_POWER:
//                            priority_text = getString(R.string.very_low);
                            break;
                    }

//                    locationRequestIntervalTextView.setText(String.format("%s: %s", getString(R.string.location_request_interval_label), interval_text));
//                    locationPriorityTextView.setText(String.format("%s: %s", getString(R.string.location_priority_label), priority_text));
//                    locationFastestIntervalTextView.setText(String.format("%s: %s", getString(R.string.location_fastest_interval_label), fastestInterval));
//                    locationExpirationTimeTextView.setText(String.format("%s: %s", getString(R.string.location_expiration_time_label), expirationTime));
//                    locationMaxWaitTimeTextView.setText(String.format("%s: %s", getString(R.string.location_max_wait_time_label), maxWaitTime));
//                    locationNumUpdatesTextView.setText(String.format("%s: %s", getString(R.string.location_num_updates_label), numUpdates));
//                    locationSmallestDisplacementTextView.setText(String.format("%s: %s", getString(R.string.location_smallest_displacement_label), smallestDisplacement_text));
                    break;

                case Constants.ACTION.GOOGLE_API_CONNECTION_SUCCESS:
                    Log.i(TAG, "GOOGLE_API_CONNECTION_SUCCESS");
//                    showSnackBar(parentLayout, getString(R.string.msg_api_client_connected));
                    serviceButton.setEnabled(true);
                    break;

                case Constants.ACTION.GOOGLE_API_CONNECTION_FAILURE:
                    Log.i(TAG, "GOOGLE_API_CONNECTION_FAILURE");
//                    showSnackBar(parentLayout, getString(R.string.msg_api_client_failure));
                    serviceButton.setEnabled(true);
                    break;

                case Constants.ACTION.GOOGLE_API_CONNECTION_SUSPEND:
                    Log.i(TAG, "GOOGLE_API_CONNECTION_SUSPEND");
//                    showSnackBar(parentLayout, getString(R.string.msg_api_client_suspended));
                    serviceButton.setEnabled(false);
                    break;

                case Constants.ACTION.GOOGLE_API_CLIENT_IS_CONNECTED:
                    Log.i(TAG, "GOOGLE_API_CLIENT_IS_CONNECTED ");
//                    googleApiClientTextView.setText(String.format("%s: %s", getString(R.string.google_api_client_label), getString(R.string.connected)));
//                    googleApiClientTextView.setTextColor(0xFF4E5340);
//                    googleApiClientTextView.setTypeface(null, Typeface.BOLD);
//                    serviceButton.setText(R.string.button_text_disconnect);
                    break;

                case Constants.ACTION.GOOGLE_API_CLIENT_IS_DISCONNECTED:
                    Log.i(TAG, "GOOGLE_API_CLIENT_IS_DISCONNECTED");
//                    googleApiClientTextView.setText(String.format("%s: %s", getString(R.string.google_api_client_label), getString(R.string.disconnected)));
//                    googleApiClientTextView.setTextColor(0xFF985277);
//                    googleApiClientTextView.setTypeface(null, Typeface.BOLD);
//                    serviceButton.setText(R.string.button_text_connect);
                    break;

                case Constants.ACTION.GOOGLE_API_CLIENT_IS_CONNECTING:
                    Log.i(TAG, "GOOGLE_API_CLIENT_IS_CONNECTING");
//                    googleApiClientTextView.setText(String.format("%s: %s", getString(R.string.google_api_client_label), getString(R.string.connecting)));
//                    googleApiClientTextView.setTextColor(0xFFFF8C61);
//                    googleApiClientTextView.setTypeface(null, Typeface.BOLD);
                    serviceButton.setEnabled(false);
                    break;

                case Constants.ACTION.DB_LOCATION_ADDED:
                    Log.i(TAG, "DB_LOCATION_ADDED");
                    break;

                case Constants.ACTION.DB_TRIP_ADDED:
                    Log.i(TAG, "DB_TRIP_ADDED");
                    break;

                case Constants.ACTION.STOP_SERVICE:
                    Log.i(TAG, "STOP_SERVICE");
                    showToast(context, "Service stopped");
                    break;

                case Constants.ACTION.START_SERVICE:
                    Log.i(TAG, "START_SERVICE");
                    showToast(context, "Service started");
                    break;
            }

            /* Always TODO Maybe not... */
//            dbLocationsTextView.setText(String.format("%s: %s", getString(R.string.db_locations_label), db.countAllData()));
//            dbTripsTextView.setText(String.format("%s: %s", getString(R.string.db_trips_label), db.countAllTrips()));


            if (!isMyServiceRunning(MyLocationService.class)) {
                quitButton.setEnabled(false);
            } else {
                quitButton.setEnabled(true);
            }


        }
    }

    /**
     * Refresh UI elements
     */
    private void updateLastLocationView(Location lastLocation) {
//        Log.i(TAG, "updateLastLocationView");

        // TODO Read this ...
        // http://www.programcreek.com/java-api-examples/index.php?class=android.os.SystemClock&method=elapsedRealtimeNanos

        if (lastLocation == null)
            throw new AssertionError();

        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();
        long elapsedRealtimeNanos = (SystemClock.elapsedRealtimeNanos() - lastLocation.getElapsedRealtimeNanos()) / 1000000;
        String time = new SimpleDateFormat("HH:mm:ss.SSS yyyy-MM-dd").format(lastLocation.getTime());

//        String latitude_label = getString(R.string.latitude_label);
//        String latitude_text = String.format("%s: %s", latitude_label, latitude);
//        latitudeTextView.setText(latitude_text);

//        String longitude_label = getString(R.string.longitude_label);
//        String longitude_text = String.format("%s: %s", longitude_label, longitude);
//        longitudeTextView.setText(longitude_text);

//        String elapsed_label = getString(R.string.elapsed_label);
//        String elapsed_text = String.format("%s: %s", elapsed_label, elapsedRealtimeNanos / (60 * 1000));
//        elapsedTextView.setText(elapsed_text);

//        String time_label = getString(R.string.time_label);
//        String time_text = String.format("%s: %s", time_label, time);
//        timeTextView.setText(time_text);

//        String accuracy_label = getString(R.string.accuracy_label);
//        String accuracy_text = lastLocation.hasAccuracy()
//                ? String.format("%s: %s", accuracy_label, lastLocation.getAccuracy())
//                : String.format("%s: N/A", accuracy_label);
//        accuracyTextView.setText(accuracy_text);

//        String altitude_label = getString(R.string.altitude_label);
//        String altitude_text = lastLocation.hasAltitude()
//                ? String.format("%s: %s", altitude_label, lastLocation.getAltitude())
//                : String.format("%s: N/A", altitude_label);
//        altitudeTextView.setText(altitude_text);

//        String bearing_label = getString(R.string.bearing_label);
//        String bearing_text = lastLocation.hasBearing()
//                ? String.format("%s: %s", bearing_label, lastLocation.getBearing())
//                : String.format("%s: N/A", bearing_label);
//        bearingTextView.setText(bearing_text);

//        String speed_label = getString(R.string.speed_label);
//        String speed_text = lastLocation.hasSpeed()
//                ? String.format("%s: %s", speed_label, lastLocation.getSpeed())
//                : String.format("%s: N/A", speed_label);
//        speedTextView.setText(speed_text);
    }
}