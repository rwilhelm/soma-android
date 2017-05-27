package de.uniko.fb1.SoMA;

import android.app.AlarmManager;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/7/17.
 */

final class Constants {

    /* TODO https://developer.android.com/samples/BluetoothChat/src/com.example.android.bluetoothchat/Constants.html */
    static final String APP_NAME = "SoMA";

    /* Application settings */
    interface CONFIG {

        /* Start tracking when app (MapActivity) starts */
        boolean AUTO_START = true;

        /* Update the location evey 5 seconds */
        long UPDATE_INTERVAL = 5000;

        /* Automatically upload the location data every ... */
        long UPLOAD_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    }

    interface ACTION {

        /* Alarm goes off and triggers data upload */
        String SCHEDULED_UPLOAD = "de.uniko.fb1.SoMA.action.SCHEDULED_UPLOAD";
        String MANUAL_UPLOAD = "de.uniko.fb1.SoMA.action.MANUAL_UPLOAD";
        String SET_INITIAL_ALARM = "de.uniko.fb1.SoMA.action.SET_INITIAL_ALARM";

        /* LocationAssistant reconfigured */
        String ASSISTANT_PERMISSION_UPDATED = "de.uniko.fb1.SoMA.action.ASSISTANT_PERMISSION_UPDATED";

        /* When you tap the notification */
        String RESUME_FOREGROUND_ACTION = "de.uniko.fb1.SoMA.action.RESUME_FOREGROUND_ACTION";

        /* -> LocationService */
        String START_LOCATION_SERVICE = "de.uniko.fb1.SoMA.action.START_LOCATION_SERVICE";
        String STOP_LOCATION_SERVICE = "de.uniko.fb1.SoMA.action.STOP_LOCATION_SERVICE";

        /* -> LocationAssistant */
        String START_LOCATION_ASSISTANT = "de.uniko.fb1.SoMA.action.START_LOCATION_ASSISTANT";
        String STOP_LOCATION_ASSISTANT = "de.uniko.fb1.SoMA.action.STOP_LOCATION_ASSISTANT";

        /* New location received */
        String LOCATION_UPDATED = "de.uniko.fb1.SoMA.action.LOCATION_UPDATED";

        /* Upload successful */
        String UPLOAD_SUCCESS = "de.uniko.fb1.SoMA.action.UPLOAD_SUCCESS";

        /* Upload failed */
        String CONNECTION_FAILED = "de.uniko.fb1.SoMA.action.CONNECTION_FAILED";

        String UPDATE_ALARM_INFO = "de.uniko.fb1.SoMA.action.UPDATE_ALARM_INFO";
    }

    public interface EVENT {
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}
