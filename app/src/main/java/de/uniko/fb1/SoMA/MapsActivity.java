package de.uniko.fb1.SoMA;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MapsActivity extends FragmentActivity implements Observer, OnMapReadyCallback, LocationAssistant.Listener {

    private static final String TAG = "MapsActivity";

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 200;

    private final List<Marker> myMarkers = new ArrayList<>();

    private static GoogleMap map;

    private Boolean isLocationAssistantRunning = false;

    private UploadScheduler uploadScheduler;
    private LocationAssistant assistant;
    private UIReceiver uiReceiver;
    private TextView uploadTimer;
    private TextView tvLocation;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        if (savedInstanceState == null) {
//            startRegistrationService();
        }

        /* Map */
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* Upload Button */
        Button uploadButton = (Button) findViewById(R.id.upload);
        uploadButton.setOnClickListener(v -> {
            showSnackBar("Uploading data ...");
            uploadData();
        });

        /* Status Display */
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocation.setText(getString(R.string.empty));

        /* Location Assistant */
        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, Constants.CONFIG.UPDATE_INTERVAL, false);
        assistant.setVerbose(false);

        /* On-off Switch */
        Switch onOffSwitch = (Switch) findViewById(R.id.switch_on_off);
        onOffSwitch.setChecked(assistant.isConnected()); // FIXME
        onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (assistant.isConnected()) {
                startLocationAssistant();
            } else {
                stopLocationAssistant();
            }
        });

        /* Notification Area */
        if (!isMyServiceRunning(LocationService.class)) {
            Log.v(TAG, "Starting LocationService");
            sendBroadcast(new Intent(Constants.ACTION.START_LOCATION_SERVICE));
        }

        /* Upload Scheduler */
        if (!isMyServiceRunning(UploadScheduler.class)) {
            Log.v(TAG, "Starting Upload Scheduler");
            sendBroadcast(new Intent(Constants.ACTION.SET_INITIAL_ALARM));
        }

        uploadTimer = (TextView)findViewById(R.id.uploadTimer);
        uploadTimer.setText(String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));

        registerBroadcastReceiver();

        Log.v(TAG, "Google Play Services available: " + checkPlayServices());
        Log.v(TAG, "Is location status ok: " + assistant.isLocationStatusOk());
    }

    public void startLocationAssistant() {
        Log.d(TAG, "Starting Location Assistant");
        assistant.start();
        isLocationAssistantRunning = true;
    }

    public void stopLocationAssistant() {
        Log.d(TAG, "Stopping Location Assistant");
        assistant.stop();
        isLocationAssistantRunning = false;
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "[1] onPause");
        unregisterBroadcastReceiver();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "[9] onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "[4] onResume");
        registerBroadcastReceiver();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        sendBroadcast(new Intent(Constants.ACTION.SET_INITIAL_ALARM));
        // FIXME This is wrong
        Intent service = new Intent(MapsActivity.this, LocationService.class);
        service.setAction(Constants.ACTION.ASSISTANT_PERMISSION_UPDATED);
        startService(service);
    }

    @Override
    public void onNeedLocationPermission() {
        updateMap(assistant.bestLocation);
        updateUI(assistant.bestLocation);
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
        Log.d(TAG, "onNewLocationAvailable " + location);
        if (location == null || lastLocation != null && location.getTime() == lastLocation.getTime()) return;
        lastLocation = location;
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.addLocation(location);

        // TODO Compare sending intents vs sending broadcasts
        // sendBroadcast(new Intent(Constants.ACTION.LOCATION_UPDATED).putExtra("location", location));
        if (isMyServiceRunning(LocationService.class)) {
            Intent service = new Intent(MapsActivity.this, LocationService.class);
            service.setAction(Constants.ACTION.LOCATION_UPDATED);
            startService(service);
        }

        updateMap(location); // FIXME
        updateUI(location);
        listAlarms();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void listAlarms() {
        AlarmManager mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        for (AlarmManager.AlarmClockInfo aci = mAlarmManager.getNextAlarmClock();
             aci != null;
             aci = mAlarmManager.getNextAlarmClock()) {
            Log.d(TAG, aci.getShowIntent().toString());
            Log.d(TAG, String.format("Trigger time: %d", aci.getTriggerTime()));
        }
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        tvLocation.setText(getString(R.string.mockLocationMessage));
        tvLocation.setOnClickListener(fromView);
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {
        tvLocation.setText(getString(R.string.error));
    }

    /* TODO What is this */
    @Override
    public void update(Observable observable, Object data) {
        Log.wtf(TAG, "__update__!: " + data.toString());
        Toast.makeText(this, "__update__!", Toast.LENGTH_SHORT).show();
    }

    public class UIReceiver extends BroadcastReceiver {
        private static final String TAG = "UIReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION.ASSISTANT_PERMISSION_UPDATED:
                    Log.v(TAG, "ASSISTANT_PERMISSION_UPDATED");
                    /* Update the location status text view and make it unclickable. */
                    tvLocation.setOnClickListener(null);
                    break;
                case Intent.ACTION_TIME_TICK:
                    if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0) {
                        Log.v(TAG, "ACTION_TIME_TICK");
                        uploadTimer.setText(String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
                    }
                    break;
            }
        }
    }

    /* Register broadcast receivers */
    private void registerBroadcastReceiver() {
        Log.v(TAG, "Register broadcast receivers");

        if (uploadScheduler == null) uploadScheduler = new UploadScheduler();
        registerReceiver(uploadScheduler, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        registerReceiver(uploadScheduler, new IntentFilter(Constants.ACTION.SCHEDULED_UPLOAD));
        registerReceiver(uploadScheduler, new IntentFilter(Constants.ACTION.SET_INITIAL_ALARM));

        if (uiReceiver == null) uiReceiver = new UIReceiver();
        registerReceiver(uiReceiver, new IntentFilter(Constants.ACTION.ASSISTANT_PERMISSION_UPDATED));
        registerReceiver(uiReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

    }

    /* Unregister broadcast receivers */
    private void unregisterBroadcastReceiver() {
        Log.v(TAG, "Unregister broadcast receivers");
        if (uiReceiver != null) unregisterReceiver(uiReceiver);
        if (uploadScheduler != null) unregisterReceiver(uploadScheduler);
    }

    private void uploadData() {
        Intent uploadData = new Intent(MapsActivity.this, LocationService.class);
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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "onMapReady");
        map = googleMap;
    }

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

    private void updateMap(Location location) {
        if (location == null) return;
        LatLng latest = new LatLng(location.getLatitude(), location.getLongitude());
        drawPolyline();
        for (Marker m: myMarkers) {
            m.remove();
        }
        myMarkers.add(map.addMarker(new MarkerOptions().position(latest)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latest, 14));
    }

    private void drawPolyline() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<LatLng> latLngs = new ArrayList<>();
        for (DatabaseHelper.DataObject l: db.getLocations()) {
            latLngs.add(l.getLatLng());
        }

        map.clear();
        Polyline line = map.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.RED));

        line.setPoints(latLngs);
    }

    private void updateUI(Location location) {
        if (location == null) return;
        tvLocation.setOnClickListener(null);
        tvLocation.setText(location.getLongitude() + "\n" + location.getLatitude());
        tvLocation.setAlpha(1.0f);
        tvLocation.animate().alpha(0.5f).setDuration(400);
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
}
