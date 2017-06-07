package de.uniko.SoMA;

import android.app.AlarmManager;

/**
 * de.uniko.SoMA.soma
 * android
 * Created by asdf on 2/7/17.
 */

final class Constants {

    /* TODO https://developer.android.com/samples/BluetoothChat/src/com.example.android.bluetoothchat/Constants.html */
    static final String APP_NAME = "SoMA";

    /* Application settings */
    interface CONFIG {

        /* Update the location evey 5 seconds */
        long UPDATE_INTERVAL = 5000;

        /* Automatically upload the location data every ... */
        long UPLOAD_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long MAX_LOCATIONS = 99;
    }

    interface ACTION {

        /* Alarms */
        String SET_FIRST_ALARM = "de.uniko.SoMA.action.SET_FIRST_ALARM";
        String SET_NEXT_ALARM = "de.uniko.SoMA.action.SET_NEXT_ALARM";
        String UPLOAD_SCHEDULER_NOTIFY = "de.uniko.SoMA.action.UPLOAD_SCHEDULER_NOTIFY";

        /* Alarm goes off and triggers data upload */
        String SCHEDULED_UPLOAD = "de.uniko.SoMA.action.SCHEDULED_UPLOAD";
        String MANUAL_UPLOAD = "de.uniko.SoMA.action.MANUAL_UPLOAD";
//        String UPLOAD_DATA = "de.uniko.SoMA.action.UPLOAD_DATA";

        /* When you tap the notification */
        String RESUME_FOREGROUND_ACTION = "de.uniko.SoMA.action.RESUME_FOREGROUND_ACTION";

        /* -> NotificationService */
        String START_LOCATION_SERVICE = "de.uniko.SoMA.action.START_LOCATION_SERVICE";
        String STOP_LOCATION_SERVICE = "de.uniko.SoMA.action.STOP_LOCATION_SERVICE";

        String UPDATE_ALARM_INFO = "de.uniko.SoMA.action.UPDATE_ALARM_INFO";
//        String UPDATE_LOCATION_COUNT = "de.uniko.SoMA.action.UPDATE_LOCATION_COUNT";
    }

    interface EVENT {

        /* New location received */
        String LOCATION_UPDATED = "de.uniko.SoMA.event.LOCATION_UPDATED";

        /* Connection results */
        String GOOGLE_API_CLIENT_CONNECTED = "de.uniko.SoMA.event.GOOGLE_API_CLIENT_CONNECTED";
        String FUSED_LOCATION_API_DISCONNECTED = "de.uniko.SoMA.action.FUSED_LOCATION_API_DISCONNECTED";
        String CONNECTION_SUSPENDED = "de.uniko.SoMA.action.CONNECTION_SUSPENDED";
        String CONNECTION_FAILED = "de.uniko.SoMA.action.CONNECTION_FAILED";

        /* Upload results */
        String ALARM_TRIGGERED = "de.uniko.SoMA.event.ALARM_TRIGGERED";
        String UPLOAD_SUCCESS = "de.uniko.SoMA.action.UPLOAD_SUCCESS";
    }

    interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}
