package de.uniko.fb1.SoMA;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

public class UploadScheduler extends BroadcastReceiver {

    private static final String TAG = "UploadScheduler";

    /*
    *
    *
    *
    * */

    // TODO
    // Intent.ACTION_BUG_REPORT
    // Intent.ACTION_APP_ERROR
    // https://stackoverflow.com/q/10559267/220472

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED");
            startLocationService(context);
            setInitialAlarm(context);
        } else if (intent.getAction().equals(Constants.ACTION.SET_INITIAL_ALARM)) {
            Log.i(TAG, "SET_INITIAL_ALARM");
            setInitialAlarm(context);
        } else if (intent.getAction().equals(Constants.ACTION.SCHEDULED_UPLOAD)) {
            Log.i(TAG, "UPLOAD_DATA");
            uploadData(context);
            setNextAlarm(context);
        } else {
            Log.wtf(TAG, "UnsupportedOperationException",
                    new UnsupportedOperationException("Not yet implemented: " + intent.getAction()));
        }
    }

    /* Start the service on boot without opening the app */
    private void startLocationService(Context context) {
        Log.i(TAG, "Starting Location Service on " + Intent.ACTION_BOOT_COMPLETED);
        context.startService(new Intent(context, LocationService.class));
    }

    /* Send an upload intent to our location service */
    private void uploadData(Context context) {
        Log.i(TAG, "Sending upload intent to Location Service");
        Intent uploadIntent = new Intent(context, LocationService.class);
        uploadIntent.setAction(Constants.ACTION.SCHEDULED_UPLOAD);
        context.startService(uploadIntent);
    }

    /* Setup the first alarm to schedule uploading */
    // TODO return time when alarm will set off
    private void setInitialAlarm(Context context) {
        long triggerAtMillis = SystemClock.elapsedRealtime() + Constants.CONFIG.UPLOAD_INTERVAL;

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationService.class);

        Log.i(TAG, "[ALARM] First alarm at " + new Date(new Date().getTime() + (triggerAtMillis)));

        alarm.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis, // triggerAtMillis
                Constants.CONFIG.UPLOAD_INTERVAL, // intervalMillis
                PendingIntent.getBroadcast(context, 0, intent, 0)
        );


    }

    /* Setup the next alarm. The broadcast is first sent by our location service. We then do two things here:
       (1) Setup the next alarm (same same) and (2) send an intent to the location service
       to maybe upload the data. */
    private void setNextAlarm(Context context) {
        long triggerAtMillis = SystemClock.elapsedRealtime() + Constants.CONFIG.UPLOAD_INTERVAL;

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Constants.ACTION.SCHEDULED_UPLOAD);

        Log.i(TAG, "Next alarm at " + new Date(new Date().getTime() + (triggerAtMillis)));

        alarm.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis,
                PendingIntent.getBroadcast(context, 0, intent, 0)
        );

        broadcastNextAlarmTime(triggerAtMillis);
    }

    /**
     *
     */
    public void broadcastNextAlarmTime(Context context, long triggerAtMillis) {
        Intent updateAlarmInfo = new Intent(context, LocationService.class);
        updateAlarmInfo.setAction(Constants.ACTION.UPDATE_ALARM_INFO);
        updateAlarmInfo.putExtra("triggerAtMillis", triggerAtMillis);
        context.startService(updateAlarmInfo);
    }
}
