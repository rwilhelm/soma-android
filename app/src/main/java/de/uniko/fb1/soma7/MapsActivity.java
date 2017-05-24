package de.uniko.fb1.soma7;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MapsActivity extends FragmentActivity implements Observer, OnMapReadyCallback, LocationAssistant.Listener {

    private static final String TAG = "MapsActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 200;
    private static GoogleMap mMap;
    private LocationAssistant assistant;
    private TextView tvLocation;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    private MapsActivityReceiver mapsActivityReceiver;
    private MyReceiver myReceiver;
    private List<Marker> myMarkers = new ArrayList<>();

    Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "" +
                "" +
                "STARTUP :)" +
                "" +
                "");

        if (savedInstanceState == null) {
            startRegistrationService();
        }

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        uploadButton = (Button) findViewById(R.id.upload);

        Switch toggle = (Switch) findViewById(R.id.toggle);

        uploadButton.setOnClickListener(v -> {
            View parentLayout = findViewById(android.R.id.content);
            Intent uploadData = new Intent(MapsActivity.this, LocationService.class);
            uploadData.setAction(Constants.ACTION.UPLOAD_DATA);
            startService(uploadData);
            showSnackBar(parentLayout, "Uploading data ...");
        });

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "start");
                assistant.start();
            } else {
                Log.d(TAG, "stop");
                assistant.stop();
            }
        });

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocation.setText(getString(R.string.empty));

        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, Constants.UPDATE_INTERVAL, false);
        assistant.setVerbose(false);

        Log.v(TAG, "Google Play Services available: " + checkPlayServices());
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "[1] onPause");
        unregisterBroadcastReceiver();
//        assistant.stop(); // TODO Really not neccessary?
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "[4] onResume");
        registerBroadcastReceiver();
//        assistant.start(); // TODO Can this be deleted?
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        if (location == null) return;
        sendBroadcast(new Intent(Constants.ACTION.LOCATION_UPDATE).putExtra("location", location));
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.addLocation(location);
        updateMap(location);
        updateUI(location);
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

    public class MapsActivityReceiver extends BroadcastReceiver {

        private static final String TAG = "MapsActivityReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            switch (action) {

                case Constants.ACTION.ASSISTANT_PERMISSION_UPDATED:
                    Log.v(TAG, "ASSISTANT_PERMISSION_UPDATED");
                    tvLocation.setOnClickListener(null); // TODO Why is that here?
                    break;

                default:
                    Log.w(TAG, "UNKNOWN INTENT" + action);

            }
        }
    }

    /* TODO */
    @Override
    public void update(Observable observable, Object data) {
        Log.wtf(TAG, "__update__!: " + data.toString());
        Toast.makeText(this, "__update__!", Toast.LENGTH_SHORT).show();
    }

    /* Register broadcast receivers */
    private void registerBroadcastReceiver() {
        Log.v(TAG, "Register broadcast receivers");

        if (myReceiver == null) myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_SHUTDOWN));
        registerReceiver(myReceiver, new IntentFilter(Intent.ACTION_BUG_REPORT));
        registerReceiver(myReceiver, new IntentFilter(Constants.ACTION.UPLOAD_DATA));
        registerReceiver(myReceiver, new IntentFilter(Constants.ACTION.LOCATION_UPDATE));
        registerReceiver(myReceiver, new IntentFilter(Constants.ACTION.ON_CONNECTION_FAILED));
        registerReceiver(myReceiver, new IntentFilter(Constants.ACTION.ALL_UPLOADS_SUCCESSFUL));

        if (mapsActivityReceiver == null) mapsActivityReceiver = new MapsActivityReceiver();
        registerReceiver(mapsActivityReceiver, new IntentFilter(Constants.ACTION.ASSISTANT_PERMISSION_UPDATED));
    }

    /* Unregister broadcast receivers */
    private void unregisterBroadcastReceiver() {
        Log.v(TAG, "Unregister broadcast receivers");
        if (mapsActivityReceiver != null) unregisterReceiver(mapsActivityReceiver);
        if (myReceiver != null) unregisterReceiver(myReceiver);
    }

    private void startRegistrationService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
        } else if (api.isUserResolvableError(code) &&
                api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES)) {
            Log.v(TAG, "startRegistrationService: WHAT HELLO WHAT WHAT HELP PLZ");
            // wait for onActivityResult call (see below)
        } else {
            Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_LONG).show();
        }
    }

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
        mMap = googleMap;
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

    public void updateMap(Location location) {
        LatLng latest = new LatLng(location.getLatitude(), location.getLongitude());
        DatabaseHelper db = DatabaseHelper.getInstance(this);

        mMap.clear();

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.RED));

        List<LatLng> latLngs = new ArrayList<>();
        db.getLocations().forEach(l -> latLngs.add(l.getLatLng()));
        line.setPoints(latLngs);

        for (Marker m: myMarkers) {
            m.remove();
        }

        Marker marker = mMap.addMarker(new MarkerOptions().position(latest).title("" + location.getTime()));

        myMarkers.add(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latest, 14));
    }

    public void updateUI(Location location) {
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

    private void showSnackBar(View view, String string) {
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show();
    }
//
//    private void showToast(Context context, String string) {
//        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
//    }
}
