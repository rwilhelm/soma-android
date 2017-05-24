package de.uniko.fb1.soma7;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Observable;
import java.util.Observer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MapsActivity extends FragmentActivity implements Observer, OnMapReadyCallback, LocationAssistant.Listener {

    private static final String TAG = "MapsActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 200;
    private static GoogleMap mMap;
    private LocationAssistant assistant;
    private TextView tvLocation;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;

    private MapsActivityReceiver mapsActivityReceiver;
    private MyReceiver myReceiver;
//    private LocationService locationReceiver;

//
//    public static HttpClient getTestHttpClient() {
//        try {
//            SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy(){
//                @Override
//                public boolean isTrusted(X509Certificate[] chain,
//                                         String authType) throws CertificateException {
//                    return true;
//                }
//            }, new AllowAllHostnameVerifier());
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("https",8444, sf));
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(registry);
//            return new DefaultHttpClient(ccm);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new DefaultHttpClient();
//        }
//    }
//
//
//    final SchemeRegistry schemeRegistry = new SchemeRegistry();
//schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//schemeRegistry.register(new Scheme("https", createAdditionalCertsSSLSocketFactory(), 443));
//
//    // and then however you create your connection manager, I use ThreadSafeClientConnManager
//    final HttpParams params = new BasicHttpParams();
//...
//    final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params,schemeRegistry);
//




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "STARTUP :)");

        if (savedInstanceState == null) {
            startRegistrationService();
        } else {
            Log.w(TAG, "onCreate: NOT STARTING THE BACKGROUND SERVICE SORRY MAN");
        }

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        uploadButton = (Button) findViewById(R.id.upload);

        Switch toggle = (Switch) findViewById(R.id.toggle);

//        Intent service = new Intent(MapsActivity.this, LocationService.class);
//        service.setAction(Constants.ACTION.PROPAGATE_CHANGES);
//        startService(service);

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
        assistant.setVerbose(true);

        Log.v(TAG, "Google Play Services available: " + checkPlayServices());
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "[1] onPause");
        unregisterBroadcastReceiver();
//        assistant.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "[4] onResume");
        registerBroadcastReceiver();
//        assistant.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Intent service = new Intent(MapsActivity.this, LocationService.class);
        service.setAction(Constants.ACTION.ASSISTANT_PERMISSION_UPDATED);
        startService(service);
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
                    tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            assistant.requestLocationPermission();
                        }
                    });
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
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        assistant.changeLocationSettings();
                    }
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

//        Intent service = new Intent(MapsActivity.this, LocationService.class);
//        service.setAction(Constants.ACTION.LOCATION_UPDATE);
//        Parcel parcel = Parcel.obtain();
//        location.writeToParcel(parcel, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
//        service.putExtra("location", location);
//        startService(service);

        DatabaseHelper db = DatabaseHelper.getInstance(this);

        db.addLocation(location);

//        if ( > -1) {
//            Log.i(TAG, "ADDED LOCATION ");
//        } else {
//            Log.e(TAG, "FAILED ADDING LOCATION ");
//        }

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
                    tvLocation.setOnClickListener(null); // FIXME
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
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }







//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        assistant.onActivityResult(requestCode, resultCode);
//    }

    private void startLocationService() {
        /*
         * Start the service if it's not running.
         * TODO Check if Google Play Services are available, fallback to Android Location API
         * 10084000
         */
        if (isGooglePlayServicesAvailable(this)) {
            Log.v(TAG, "Google Play Services are available");
//            if (!isMyServiceRunning(LocationService.class)) {
//                Log.i(TAG, "startLocationService: [ACTION] " + Constants.ACTION.ENABLE_SERVICE);
//                Intent service = new Intent(MapsActivity.this, LocationService.class);
//                service.setAction(Constants.ACTION.ENABLE_SERVICE);
//                startService(service);
//            } else {
//                Log.i(TAG, "startLocationService: [ACTION] " + Constants.ACTION.PROPAGATE_CHANGES);
//                Intent service = new Intent(MapsActivity.this, LocationService.class);
//                service.setAction(Constants.ACTION.PROPAGATE_CHANGES);
//                startService(service);
//            }
        } else {
            Log.w(TAG, "Google Play Services not available");
        }
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

    private boolean isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        Log.v(TAG, "Checking if Google Play Services are available ... " + (resultCode == ConnectionResult.SUCCESS));
        return resultCode == ConnectionResult.SUCCESS;
    }



    public void updateMap(Location location) {
        LatLng latest = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latest).title("" + location.getTime()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latest, 14));
    }

    public void updateUI(Location location) {
        tvLocation.setOnClickListener(null);
        tvLocation.setText(location.getLongitude() + "\n" + location.getLatitude());
        tvLocation.setAlpha(1.0f);
        tvLocation.animate().alpha(0.5f).setDuration(400);
    }

//    void addLocationToDB(Location location) {
//        DatabaseHelper db = DatabaseHelper.getInstance(this);
//        if (db.addLocation(location, currentTrip) > -1) {
//            broadcastLastLocation();
//        }
//    }

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
//        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show();
    }

//    private void showToast(Context context, String string) {
//        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
//    }



    private Button uploadButton;

















}
