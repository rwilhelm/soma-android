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

        /* Update the location evey 5 seconds */
        long UPDATE_INTERVAL = 5000;

        /* Automatically upload the location data every ... */
        long UPLOAD_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    }

    interface ACTION {

        /* Alarms */
        String SET_FIRST_ALARM = "de.uniko.fb1.SoMA.action.SET_FIRST_ALARM";
        String SET_NEXT_ALARM = "de.uniko.fb1.SoMA.action.SET_NEXT_ALARM";
        String UPLOAD_SCHEDULER_NOTIFY = "de.uniko.fb1.SoMA.action.UPLOAD_SCHEDULER_NOTIFY";

        /* Alarm goes off and triggers data upload */
        String SCHEDULED_UPLOAD = "de.uniko.fb1.SoMA.action.SCHEDULED_UPLOAD";
        String MANUAL_UPLOAD = "de.uniko.fb1.SoMA.action.MANUAL_UPLOAD";
        String UPLOAD_DATA = "de.uniko.fb1.SoMA.action.UPLOAD_DATA";

        /* When you tap the notification */
        String RESUME_FOREGROUND_ACTION = "de.uniko.fb1.SoMA.action.RESUME_FOREGROUND_ACTION";

        /* -> NotificationService */
        String START_LOCATION_SERVICE = "de.uniko.fb1.SoMA.action.START_LOCATION_SERVICE";
        String STOP_LOCATION_SERVICE = "de.uniko.fb1.SoMA.action.STOP_LOCATION_SERVICE";

        String UPDATE_ALARM_INFO = "de.uniko.fb1.SoMA.action.UPDATE_ALARM_INFO";
        String UPDATE_LOCATION_COUNT = "de.uniko.fb1.SoMA.action.UPDATE_LOCATION_COUNT";

        String FUSED_LOCATION_API_DISCONNECTED = "de.uniko.fb1.SoMA.action.UPDATE_ALARM_INFO";
    }

    interface EVENT {

        /* New location received */
        String LOCATION_UPDATED = "de.uniko.fb1.SoMA.event.LOCATION_UPDATED";
        String ALARM_TRIGGERED = "de.uniko.fb1.SoMA.event.ALARM_TRIGGERED";
        String GOOGLE_API_CLIENT_CONNECTED = "de.uniko.fb1.SoMA.event.GOOGLE_API_CLIENT_CONNECTED";
    }

    interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}
