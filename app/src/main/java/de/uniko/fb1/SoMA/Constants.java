package de.uniko.fb1.SoMA;

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

        /* Alarm goes off and triggers data upload */
        String SCHEDULED_UPLOAD                  = "de.uniko.fb1.SoMA.action.SCHEDULED_UPLOAD";

        /* LocationAssistant reconfigured */
        String ASSISTANT_PERMISSION_UPDATED      = "de.uniko.fb1.SoMA.action.ASSISTANT_PERMISSION_UPDATED";

        /* Alarm goes off and triggers data upload */
        String RESUME_FOREGROUND_ACTION          = "de.uniko.fb1.SoMA.action.RESUME_FOREGROUND_ACTION";

        /* -> LocationService */
        String START_ASSISTANT                   = "de.uniko.fb1.SoMA.action.START_ASSISTANT";
        String STOP_ASSISTANT                    = "de.uniko.fb1.SoMA.action.STOP_ASSISTANT";

        /* New location received */
        String LOCATION_UPDATED = "de.uniko.fb1.SoMA.action.LOCATION_UPDATE";

        /* Upload successful */
        String UPLOAD_SUCCESS = "de.uniko.fb1.SoMA.action.UPLOAD_SUCCESS";

        /* Upload failed */
        String CONNECTION_FAILED = "de.uniko.fb1.SoMA.action.CONNECTION_FAILED";

    }

    public interface EVENT {
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}
