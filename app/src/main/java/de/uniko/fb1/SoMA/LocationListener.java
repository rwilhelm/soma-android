package de.uniko.fb1.SoMA;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.View;

/**
 * Created by asdf on 5/28/17.
 */

public class LocationListener implements LocationAssistant.Listener {

    private static final String TAG = "LocationListener";

    private LocationAssistant assistant;

    @Override
    public void onNeedLocationPermission() {
        Log.d(TAG, "onNeedLocationPermission");
    }

    @Override
    public void onExplainLocationPermission() {
        Log.d(TAG, "onExplainLocationPermission");
        Intent intent = new Intent();
    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        Log.d(TAG, "onLocationPermissionPermanentlyDeclined");
    }

    @Override
    public void onNeedLocationSettingsChange() {
        Log.d(TAG, "onNeedLocationSettingsChange");
    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        Log.d(TAG, "onFallBackToSystemSettings");
    }

    @Override
    public void onNewLocationAvailable(Location location) {
        Log.d(TAG, "onNewLocationAvailable " + location);
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        Log.d(TAG, "onMockLocationsDetected");
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {
        Log.d(TAG, "onError " + message);
    }
}
