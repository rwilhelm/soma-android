package de.uniko.fb1.SoMA;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
            Log.i(TAG, "[BOOT] COMPLETED");
            setInitialAlarm(context);
            startNotificationService(context);

        } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0) {
                Log.v(TAG, "[TICK] " + new Date().getTime());
                listAlarms(context);
            }

        } else if (intent.getAction().equals(Constants.ACTION.SET_FIRST_ALARM)) {
            Log.i(TAG, "[ALARM] INIT");
            setInitialAlarm(context);

        } else if (intent.getAction().equals(Constants.ACTION.SET_NEXT_ALARM)) {
            Log.i(TAG, "[ALARM] NEXT");
            setNextAlarm(context);

        } else if (intent.getAction().equals(Constants.EVENT.ALARM_TRIGGERED)) {
            Log.i(TAG, "[ALARM] TRIGGERED");
            setNextAlarm(context);
            uploadData(context);


        } else {
            Log.wtf(TAG, "UnsupportedOperationException",
                    new UnsupportedOperationException("Not yet implemented: " + intent.getAction()));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void listAlarms(Context context) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (AlarmManager.AlarmClockInfo aci = mAlarmManager.getNextAlarmClock();
             aci != null;
             aci = mAlarmManager.getNextAlarmClock()) {
            Log.d(TAG, aci.getShowIntent().toString());
            Log.d(TAG, String.format("[ALARM]: Next Alarm at %d", aci.getTriggerTime()));
        }
    }

    /* Start the service on boot without opening the app */
    private void startNotificationService(Context context) {
        Log.i(TAG, "Starting Location Service on " + Intent.ACTION_BOOT_COMPLETED);
        context.startService(new Intent(context, NotificationService.class));
    }

    /* Send an upload intent to our location service */
    private void uploadData(Context context) {
        Log.i(TAG, "Sending upload intent to Location Service");
        Intent uploadIntent = new Intent(context, NotificationService.class);
        uploadIntent.setAction(Constants.ACTION.SCHEDULED_UPLOAD);
        context.startService(uploadIntent);
    }

    /* Setup the first alarm to schedule uploading */
    // TODO return time when alarm will set off
    private void setInitialAlarm(Context context) {
        long triggerAtMillis = SystemClock.elapsedRealtime() + Constants.CONFIG.UPLOAD_INTERVAL;

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);

        Log.i(TAG, "[ALARM] First alarm at " + new Date(new Date().getTime() + (triggerAtMillis)));

        alarm.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis, // triggerAtMillis
                Constants.CONFIG.UPLOAD_INTERVAL, // intervalMillis
                PendingIntent.getBroadcast(context, 0, intent, 0)
        );

        broadcastNextAlarmTime(context, triggerAtMillis);
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

        broadcastNextAlarmTime(context, triggerAtMillis);
    }

    /**
     *
     */
    public void broadcastNextAlarmTime(Context context, long triggerAtMillis) {
        Intent updateAlarmInfo = new Intent(context, NotificationService.class);
        updateAlarmInfo.setAction(Constants.ACTION.UPDATE_ALARM_INFO);
        updateAlarmInfo.putExtra("triggerAtMillis", triggerAtMillis);
        context.startService(updateAlarmInfo);
    }
}
