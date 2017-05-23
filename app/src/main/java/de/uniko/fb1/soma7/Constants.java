package de.uniko.fb1.soma7;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/7/17.
 */

final class Constants {

    /* TODO https://developer.android.com/samples/BluetoothChat/src/com.example.android.bluetoothchat/Constants.html */
    public static final String APP_NAME = "SoMA";

    /* Application settings */
    private static final long UPDATE_INTERVAL_SECONDS = 10; // Every n seconds
    public static final long UPDATE_INTERVAL = 1000 * UPDATE_INTERVAL_SECONDS; // Every uhm seconds
    public static final long UPDATE_INTERVAL_FAST = UPDATE_INTERVAL / 2; // Faster update interval

    /* Upload scheduler */
    private static final long UPLOAD_INTERVAL = 60; // Every n seconds
    public static final long UPLOAD_SCHEDULE = 1000 * UPLOAD_INTERVAL * 60; // Every hour


    public interface ACTION {
        String ADD_LOCATION                      = "de.uniko.fb1.soma.action.ADD_LOCATION";
        String ALL_UPLOADS_SUCCESSFUL            = "de.uniko.fb1.soma.action.ALL_UPLOADS_SUCCESSFUL";
        String ASSISTANT_PERMISSION_UPDATED      = "de.uniko.fb1.soma.action.ASSISTANT_PERMISSION_UPDATED";
        String DB_LOCATION_ADDED                 = "de.uniko.fb1.soma.action.DB_LOCATION_ADDED";
        String DB_TRIP_ADDED                     = "de.uniko.fb1.soma.action.DB_TRIP_ADDED";
        String ENABLE_SERVICE                    = "de.uniko.fb1.soma.action.ENABLE_SERVICE";
        String FUSED_LOCATION_API_CONNECTED      = "de.uniko.fb1.soma.action.FUSED_LOCATION_API_CONNECTED";
        String FUSED_LOCATION_API_DISCONNECTED   = "de.uniko.fb1.soma.action.FUSED_LOCATION_API_DISCONNECTED";
        String GOOGLE_API_CLIENT_IS_CONNECTED    = "de.uniko.fb1.soma.action.GOOGLE_API_CLIENT_IS_CONNECTED";
        String GOOGLE_API_CLIENT_IS_CONNECTING   = "de.uniko.fb1.soma.action.GOOGLE_API_CLIENT_IS_CONNECTING";
        String GOOGLE_API_CLIENT_IS_DISCONNECTED = "de.uniko.fb1.soma.action.GOOGLE_API_CLIENT_IS_DISCONNECTED";
        String GOOGLE_API_CONNECTION_FAILURE     = "de.uniko.fb1.soma.action.GOOGLE_API_CONNECTION_FAILURE";
        String GOOGLE_API_CONNECTION_SUCCESS     = "de.uniko.fb1.soma.action.GOOGLE_API_CONNECTION_SUCCESS";
        String GOOGLE_API_CONNECTION_SUSPEND     = "de.uniko.fb1.soma.action.GOOGLE_API_CONNECTION_SUSPEND";
        String LOCATION_AVAILABILITY             = "de.uniko.fb1.soma.action.LOCATION_AVAILABILITY";
        String LOCATION_PREFERENCES              = "de.uniko.fb1.soma.action.LOCATION_PREFERENCES";
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
