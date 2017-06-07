package de.uniko.SoMA;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * de.uniko.SoMA.soma
 * android
 * Created by asdf on 2/7/17.
 */

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";

    /*
    * What this service does ...
    * - Start the notification service and keep it running in the background.
    * - Update the notification via received Intent.
    * - Receive intents to start/stop the notification service.
    * */

    /**
     * Show in notification bar
     **/
    private NotificationCompat.Builder updateNotification() {
        return updateNotification(0);
    }

    /**
     * Show in notification bar
     **/
    private NotificationCompat.Builder updateNotification(long locationCount) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);

        long dataCount = db.countLocations();
        String contentText = "Ihre persönlichen Daten werden gesammelt"; // TODO
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_location_on);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.setAction(Constants.ACTION.RESUME_FOREGROUND_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

//        // FIXME
//        long now = new Date().getTime();
//        long endTime = new Date(now + triggerAtMillis).getTime();
//        long secondsUntilUpload = (endTime - now)/1000;
//        Log.i(TAG, "First alarm at " + new Date(new Date().getTime() + secondsUntilUpload) + " seconds");

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle(Constants.APP_NAME)
                        .setContentText(contentText)
                        .setSubText("Data: " + locationCount)
                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification.build());

        Log.d(TAG, "updateNotification: dataCount: " + dataCount + ", notificationIntent: " + contentText);
        return notification;
    }

    private void startLocationService() {
        Log.i(TAG, "startLocationService");
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, updateNotification().build());
    }

    private void stopLocationService() {
        Log.i(TAG, "stopLocationService");
        stopForeground(true); // Stop running in the background
        stopSelf();
    }

    /* Upload all data, one by one (deletes after success) <3 */
    private void uploadData() {
        Log.v(TAG, "uploadData");
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<LocationObject> locations = db.getLocations();

        // noinspection unchecked
        new UploadAsyncTask(this).execute(locations);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");

        Log.i(TAG, "Starting sticky foreground notification service");
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, updateNotification().build());
//
//        Log.i(TAG, "Asking the main activity to start the Location Assistant");
//        sendBroadcast(new Intent(Constants.ACTION.START_LOCATION_SERVICE)); // FIXME
    }

    @Override
    /* Actions, e.g. commands to this service, are passed as Intent carrying an action constant. */
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Log.i(TAG, "onStartCommand: intent:" + intent + ", action: " + action);
            if (action != null) {
                Log.i(TAG, "onStartCommand: " + action);
                switch (action) {
                    case Constants.ACTION.START_LOCATION_SERVICE:
                        startLocationService();
                        break;
                    case Constants.ACTION.STOP_LOCATION_SERVICE:
                        stopLocationService();
                        break;
                    case Constants.ACTION.MANUAL_UPLOAD:
                        uploadData();
                        break;
                    case Constants.ACTION.UPDATE_ALARM_INFO:
                        long triggerAtMillis = intent.getLongExtra("triggerAtMillis", 555);
                        Log.d(TAG, "Constants.ACTION.UPDATE_ALARM_INFO: triggerAtMillis " + triggerAtMillis);
                        updateNotification(triggerAtMillis);
                        break;
                    case Constants.EVENT.LOCATION_UPDATED:
                        long locationCount = intent.getLongExtra("locationCount", 0);
                        Log.d(TAG, "Constants.EVENT.LOCATION_UPDATED: locationCount " + locationCount);
                        updateNotification(locationCount);
                        break;
                }
            } else {
                Log.e(TAG, "NULL ACTION");
            }
        } else {
            Log.e(TAG, "NULL INTENT");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        sendBroadcast(new Intent(Constants.ACTION.STOP_LOCATION_SERVICE));
        super.onDestroy(); // should be called last
    }

    // Used only in case if services are bound (Bound Services).
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind ");
        return null;
    }

}

