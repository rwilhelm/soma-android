package de.uniko.fb1.soma7;

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
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/7/17.
 */

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    /**
     * Show in notification bar
     **/
    private NotificationCompat.Builder updateNotification() {
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

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle(Constants.APP_NAME)
                        .setContentText(contentText)
                        .setSubText("Data: " + dataCount)
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, updateNotification().build());
        sendBroadcast(new Intent(Constants.ACTION.START_ASSISTANT));
    }

    @Override
    /* Actions, e.g. commands to this service, are passed as Intent carrying an action constant. */
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Log.i(TAG, "onStartCommand: " + action);
            switch (action) {
                case Constants.ACTION.START_ASSISTANT:
                    quitService();
                    break;
                case Constants.ACTION.STOP_ASSISTANT:
                    quitService();
                    break;
                case Constants.ACTION.UPLOAD_DATA:
                    uploadData();
                    break;
                case Constants.ACTION.UPDATE_NOTIFICATION:
                    updateNotification();
                    break;
            }
        } else {
            Log.e(TAG, "NULL INTENT");
        }
        return START_STICKY;
    }

    private void quitService() {
        Log.i(TAG, "quitService");
        stopForeground(true); // TODO What is this?
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        sendBroadcast(new Intent(Constants.ACTION.STOP_ASSISTANT));
        super.onDestroy(); // should be called last
    }

    // Used only in case if services are bound (Bound Services).
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind ");
        return null;
    }

    /**
     * Upload all data, one by one (deletes after success)
     */
    private void uploadData() {
        Log.v(TAG, "uploadData");
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<DatabaseHelper.DataObject> locations = db.getLocations();

        // noinspection unchecked
        new UploadAsyncTask(this).execute(locations);
    }
}

