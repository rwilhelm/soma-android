package de.uniko.fb1.soma7;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED");

            /* Start the service on boot */
            context.startService(new Intent(context, LocationService.class));

            /* Setup the first alarm to schedule uploading */
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, LocationService.class);

            alarm.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + Constants.UPLOAD_INTERVAL, // triggerAtMillis
                    Constants.UPLOAD_INTERVAL, // intervalMillis
                    PendingIntent.getBroadcast(context, 0, i, 0)
            );

        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            Log.i(TAG, "ACTION_SHUTDOWN");

            /* TODO Make sure all data is saved and try upload when the device shuts down */

        } else if (intent.getAction().equals(Intent.ACTION_BUG_REPORT)) {
            Log.i(TAG, "ACTION_BUG_REPORT");

            /* TODO Implement crash reporting */

        } else if (intent.getAction().equals(Constants.ACTION.UPLOAD_DATA)) {
            Log.i(TAG, "ACTION_UPLOAD_DATA");

            /*
               The broadcast is first sent by our location service. We then do two things here:
               (1) Setup the next alarm (same same) and (2) send an intent to the location service
               to maybe upload the data.
            */

            /* Setup the next alarm */
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(Constants.ACTION.SCHEDULED_UPLOAD);

            alarm.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + Constants.UPLOAD_INTERVAL,
                    PendingIntent.getBroadcast(context, 0, i, 0)
            );

            /* Send an upload intent to our location service */
            Intent uploadIntent = new Intent(context, LocationService.class);

            // PendingIntent.getService(context, requestCode, service, flags);
            uploadIntent.setAction(Constants.ACTION.UPLOAD_DATA);
            context.startService(uploadIntent);

        } else if (intent.getAction().equals(Constants.ACTION.LOCATION_UPDATE)) {
            Log.i(TAG, "LOCATION_UPDATE");

            Intent service = new Intent(context, LocationService.class);
            service.setAction(Constants.ACTION.LOCATION_UPDATE);
            context.startService(service);

        } else if (intent.getAction().equals(Constants.ACTION.ON_CONNECTION_FAILED)) {
            Log.i(TAG, "ON_CONNECTION_FAILED");
        } else if (intent.getAction().equals(Constants.ACTION.ALL_UPLOADS_SUCCESSFUL)) {
            Log.i(TAG, "ALL_UPLOADS_SUCCESSFUL");
        } else {
            Log.wtf(TAG, "UnsupportedOperationException",
                    new UnsupportedOperationException("Not yet implemented: " + intent.getAction()));
        }
    }
}
