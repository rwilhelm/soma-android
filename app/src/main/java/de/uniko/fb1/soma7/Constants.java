package de.uniko.fb1.soma7;

import android.app.AlarmManager;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/7/17.
 */

final class Constants {

    /* TODO https://developer.android.com/samples/BluetoothChat/src/com.example.android.bluetoothchat/Constants.html */
    public static final String APP_NAME = "SoMA";

    /* Application settings */
    public static final long UPDATE_INTERVAL = 5000;

    /* Upload scheduler */
    public static final long UPLOAD_INTERVAL = AlarmManager.INTERVAL_HALF_DAY;


    public interface ACTION {
        String ALL_UPLOADS_SUCCESSFUL            = "de.uniko.fb1.soma.action.ALL_UPLOADS_SUCCESSFUL";
        String ASSISTANT_PERMISSION_UPDATED      = "de.uniko.fb1.soma.action.ASSISTANT_PERMISSION_UPDATED";
        String DB_LOCATION_ADDED                 = "de.uniko.fb1.soma.action.DB_LOCATION_ADDED";
        String DB_TRIP_ADDED                     = "de.uniko.fb1.soma.action.DB_TRIP_ADDED";
        String ENABLE_SERVICE                    = "de.uniko.fb1.soma.action.ENABLE_SERVICE";
        String LOCATION_UPDATE                   = "de.uniko.fb1.soma.action.LOCATION_UPDATE";
        String ON_CONNECTION_FAILED              = "de.uniko.fb1.soma.action.ON_CONNECTION_FAILED";
        String PROPAGATE_CHANGES                 = "de.uniko.fb1.soma.action.PROPAGATE_CHANGES";
        String QUIT_SERVICE                      = "de.uniko.fb1.soma.action.QUIT_SERVICE";
        String RESUME_FOREGROUND_ACTION          = "de.uniko.fb1.soma.action.resumeforeground";
        String SCHEDULED_UPLOAD                  = "de.uniko.fb1.soma.action.SCHEDULED_UPLOAD";
        String START_SERVICE                     = "de.uniko.fb1.soma.action.START_SERVICE";
        String STOP_SERVICE                      = "de.uniko.fb1.soma.action.STOP_SERVICE";
        String UPLOAD_DATA                       = "de.uniko.fb1.soma.action.UPLOAD_DATA";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}
