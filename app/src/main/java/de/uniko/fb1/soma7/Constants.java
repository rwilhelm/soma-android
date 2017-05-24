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
        String ASSISTANT_PERMISSION_UPDATED      = "de.uniko.fb1.soma.action.ASSISTANT_PERMISSION_UPDATED";
        String RESUME_FOREGROUND_ACTION          = "de.uniko.fb1.soma.action.RESUME_FOREGROUND_ACTION";
        String SCHEDULED_UPLOAD                  = "de.uniko.fb1.soma.action.SCHEDULED_UPLOAD";

        /* -> LocationService */
        String START_ASSISTANT                   = "de.uniko.fb1.soma.action.START_ASSISTANT";
        String STOP_ASSISTANT                    = "de.uniko.fb1.soma.action.STOP_ASSISTANT";
        String UPDATE_NOTIFICATION               = "de.uniko.fb1.soma.action.UPDATE_NOTIFICATION";
        String UPLOAD_DATA                       = "de.uniko.fb1.soma.action.UPLOAD_DATA";

        String LOCATION_UPDATE                   = "de.uniko.fb1.soma.action.LOCATION_UPDATE";

        String LOCATION_UPDATED = "de.uniko.fb1.soma.action.LOCATION_UPDATE";
        String UPLOAD_SUCCESS = "de.uniko.fb1.soma.action.UPLOAD_SUCCESS";
        String CONNECTION_FAILED              = "de.uniko.fb1.soma.action.CONNECTION_FAILED";

    }

    public interface EVENT {
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}
