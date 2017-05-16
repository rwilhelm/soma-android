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

    public void setAlarm (Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MyLocationService.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, i, 0);

        Log.d(TAG, "setAlarm: " + SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED");

            /* Start the service on boot */
            context.startService(new Intent(context, MyLocationService.class));

            /* Setup the alarm to schedule uploading */
            setAlarm(context);

        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            Log.i(TAG, "ACTION_SHUTDOWN");
        } else if (intent.getAction().equals(Intent.ACTION_BUG_REPORT)) {
            Log.i(TAG, "ACTION_BUG_REPORT");
        } else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.i(TAG, "ACTION_POWER_CONNECTED");
        } else if (intent.getAction().equals(Constants.ACTION.UPLOAD_DATA)) {
            Log.i(TAG, "ACTION.UPLOAD_DATA");

            /*
               The broadcast is first sent by our location service. We then do two things here:
               (1) Setup the next alarm (same same) and (2) send an intent to the location service
               to upload the data, please.
            */

            /* Setup the next alarm */
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + Constants.UPLOAD_SCHEDULE,
                    PendingIntent.getBroadcast(context, 0, new Intent(Constants.ACTION.SCHEDULED_UPLOAD), 0)
            );

            /* Send an upload intent to our location service */
            Intent service = new Intent(context, MyLocationService.class);
            //PendingIntent.getService(context, requestCode, service, flags);
            service.setAction(Constants.ACTION.UPLOAD_DATA);
            context.startService(service);



        } else if (intent.getAction().equals(Constants.ACTION.ON_CONNECTION_FAILED)) {
            Log.i(TAG, "ON_CONNECTION_FAILED");
            ObservableObject.getInstance().updateValue(intent);



        } else if (intent.getAction().equals(Constants.ACTION.ALL_UPLOADS_SUCCESSFUL)) {
            Log.i(TAG, "ALL_UPLOADS_SUCCESSFUL");
            Log.e(TAG , "onReceive ============================");


        } else {
            Log.wtf(TAG, "UnsupportedOperationException",
                    new UnsupportedOperationException("Not yet implemented: " + intent.getAction()));
        }
    }
}
