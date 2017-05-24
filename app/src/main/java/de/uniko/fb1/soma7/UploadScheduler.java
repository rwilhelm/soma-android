package de.uniko.fb1.soma7;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class UploadScheduler extends BroadcastReceiver {

    private static final String TAG = "UploadScheduler";

    public static final String BOOT_COMPLETED = Intent.ACTION_BOOT_COMPLETED;
    public static final String UPLOAD_DATA = Constants.ACTION.UPLOAD_DATA;
    public static final String CONNECTION_FAILED = Constants.ACTION.CONNECTION_FAILED;
    public static final String UPLOAD_SUCCESS = Constants.ACTION.UPLOAD_SUCCESS;

    // TODO
    // Intent.ACTION_BUG_REPORT
    // Intent.ACTION_APP_ERROR
    // https://stackoverflow.com/q/10559267/220472

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(BOOT_COMPLETED)) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED");

            startLocationService(context);
            setInitialAlarm(context);

        } else if (intent.getAction().equals(UPLOAD_DATA)) {
            Log.i(TAG, "UPLOAD_DATA");

            uploadData(context);
            setNextAlarm(context);

        } else if (intent.getAction().equals(CONNECTION_FAILED)) {
            Log.i(TAG, "CONNECTION_FAILED");
        } else if (intent.getAction().equals(UPLOAD_SUCCESS)) {
            Log.i(TAG, "UPLOAD_SUCCESS");
        } else {
            Log.wtf(TAG, "UnsupportedOperationException",
                    new UnsupportedOperationException("Not yet implemented: " + intent.getAction()));
        }
    }

    /* Start the service on boot */
    private void startLocationService(Context context) {
        context.startService(new Intent(context, LocationService.class));
    }

    /* Send an upload intent to our location service */
    private void uploadData(Context context) {
        Intent uploadIntent = new Intent(context, LocationService.class);
        uploadIntent.setAction(Constants.ACTION.UPLOAD_DATA);
        context.startService(uploadIntent);
    }

    /* Setup the first alarm to schedule uploading */
    private void setInitialAlarm(Context context) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LocationService.class);
        alarm.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + Constants.UPLOAD_INTERVAL, // triggerAtMillis
                Constants.UPLOAD_INTERVAL, // intervalMillis
                PendingIntent.getBroadcast(context, 0, i, 0)
        );
    }

    /* Setup the next alarm. The broadcast is first sent by our location service. We then do two things here:
       (1) Setup the next alarm (same same) and (2) send an intent to the location service
       to maybe upload the data. */
    private void setNextAlarm(Context context) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(Constants.ACTION.SCHEDULED_UPLOAD);
        alarm.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + Constants.UPLOAD_INTERVAL,
                PendingIntent.getBroadcast(context, 0, i, 0)
        );
    }
}
