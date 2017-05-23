package de.uniko.fb1.soma7;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/7/17.
 */

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private BroadcastReceiver locationReceiver;

    /* Location provider */
    public Trip currentTrip; // Current Trip

    public void updateTrip(Context context, Location location) {
        if (currentTrip == null) {
            currentTrip = new Trip(context);
        }

        currentTrip.add(this, location);

        Log.v(TAG, "[currentTrip] count: " + currentTrip.getDataCount());

        if (currentTrip.getDataCount() > 100) {
            currentTrip.upload(this);
            currentTrip = new Trip(context);
        }
    }

//    public void updateTrip(Location location) {
//        if (currentTrip == null) {
//            currentTrip = new Trip(context);
//        }
//
//        currentTrip.add(this, location);
//
//        Log.v(TAG, "[currentTrip] count: " + currentTrip.getDataCount());
//
//        if (currentTrip.getDataCount() > 100) {
//            currentTrip.upload(this);
//            currentTrip = new Trip(context);
//        }
//    }

    /**
     * Show in notification bar
     **/
    private NotificationCompat.Builder updateNotification() {
        Log.d(TAG, "updateNotification: ");
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        long dataCount = db.countAllData();
        Intent notificationIntent = new Intent(this, MapsActivity.class);

        notificationIntent.setAction(Constants.ACTION.RESUME_FOREGROUND_ACTION);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_location_on);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle(Constants.APP_NAME)
                        .setContentText("") // TODO
                        .setContentInfo("Data: " + dataCount)
                        .setSmallIcon(R.drawable.ic_location_on)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);

        mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification.build());

        return notification;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.ACTION.ADD_LOCATION)){
                Log.i(TAG, "ADD LOCATION");
                currentTrip.add(intent.getParcelableExtra("location"));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, updateNotification().build());
        sendBroadcast(new Intent(Constants.ACTION.START_SERVICE));

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION.START_SERVICE);

        this.locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "[LOCRCV] RECEIVED INTENT");

                String action = intent.getAction();
                if(action.equals(Constants.ACTION.ADD_LOCATION)){
                    Log.i(TAG, "[LOCRCV] ADD LOCATION");
                    currentTrip.add(intent.getParcelableExtra("location"));
                }
            }
        };

        this.registerReceiver(this.locationReceiver, filter);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constants.ACTION.START_SERVICE);
//        registerReceiver(receiver, filter);

    }


    /* Actions, e.g. commands to this service, are passed as Intent carrying an action constant. */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand ");

        /* When the Intent is created in MainActitivy, we call setAction with
        Constants.ACTION.ENABLE_SERVICE */

        if (intent != null) {

            /* TODO How comes that intent can be null? */

            String action = intent.getAction();
            Log.i(TAG, "[RECEIVE] " + action);

            if (action.equals(Constants.ACTION.UPLOAD_DATA)) {
                uploadData();
            } else if (action.equals(Constants.ACTION.QUIT_SERVICE)) {
                Log.i(TAG, "[RECEIVE] QUIT_SERVICE");
                uploadData();
                stopForeground(true);
                stopSelf();
            } else if (action.equals(Constants.ACTION.ADD_LOCATION)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Location location = (Location) bundle.get("location");
                    currentTrip.add(location);
                }


//
//                    Parcel p = Parcel.obtain();
//                objLocation.writeToParcel(p, 0);
//                final byte[] b = p.marshall();      //now you've got bytes
//                p.recycle();


//                location = locationPar.readParcelable(Location.class.getClassLoader());
//                currentTrip.add(location);
            } else {
                Log.w(TAG, "UNKNOWN INTENT " + action);
            }
        } else {
            Log.e(TAG, "NULL INTENT");
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        sendBroadcast(new Intent(Constants.ACTION.STOP_SERVICE));
        this.unregisterReceiver(this.locationReceiver);
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

        /* Get all trips. */
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<Trip> trips = db.getTrips();

        String tripsText = trips.size() == 1 ? "trip" : "trips";
        Log.v(TAG, "Uploading " + trips.size() + " " + tripsText);
        Toast.makeText(this, "Uploading " + trips.size() + " " + tripsText, Toast.LENGTH_SHORT).show();

        /*
         * Pass each trip to UploadAsyncTask, where it will be uploaded asynchronously to the API.
         * Pre- and post-hooks happen there, e.g. deleting the trip.
         */
        for (Trip trip : trips) {
            new UploadAsyncTask(this).execute(trip);
        }
    }

}

