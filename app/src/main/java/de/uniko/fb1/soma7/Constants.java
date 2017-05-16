package de.uniko.fb1.soma7;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/7/17.
 */

public final class Constants {

    /* TODO https://developer.android.com/samples/BluetoothChat/src/com.example.android.bluetoothchat/Constants.html */
    public static final String APP_NAME = "SoMA";

    /* Application settings */
    public static final long UPDATE_INTERVAL_SECONDS = 10; // Every n seconds
    public static final long UPDATE_INTERVAL = 1000 * UPDATE_INTERVAL_SECONDS; // Every five seconds
    public static final long UPDATE_INTERVAL_FAST = UPDATE_INTERVAL / 2; // Faster update interval

    /* Upload scheduler */
    public static final long UPLOAD_INTERVAL = 60; // Every n seconds
    public static final long UPLOAD_SCHEDULE = 1000 * UPLOAD_INTERVAL * 60; // Every hour
//    public static final long UPLOAD_SCHEDULE = 1000 * 60 * 60; // Every hour

    /* Permissions */
    public static final int ASK_FOR_PERMISSION_ACCESS_FINE_LOCATION = 1;
    public static final int ASK_FOR_PERMISSION_ACCESS_COARSE_LOCATION = 2;
    public static final int ASK_FOR_PERMISSION_ACCESS_NETWORK_STATE = 3;
    public static final int ASK_FOR_PERMISSION_ACCESS_INTERNET = 4;
    public static final int ASK_FOR_PERMISSION_ACCESS_BOOT_COMPLETED = 5;

    /* Actions*/
    public static final String SAVE_LOCATION_TO_DB = "de.uniko.fb1.soma.SAVE_LOCATION_TO_DB";
    public static final String UPLOAD_TRIP = "de.uniko.fb1.soma.UPLOAD_TRIP";
    public static final String START_TRACKING = "de.uniko.fb1.soma.START_TRACKING";
    public static final String STOP_TRACKING = "de.uniko.fb1.soma.STOP_TRACKING";
    public static final String UPLOAD_DATA = "de.uniko.fb1.soma.UPLOAD_DATA";
    public static final String STATE_UPLOAD_INITIATED = "STATE_UPLOAD_INITIATED";
    public static final String STATE_UPLOAD_SUCCESSFUL = "STATE_UPLOAD_SUCCESSFUL";
    public static final String STATE_UPLOAD_FAILED = "STATE_UPLOAD_FAILED";

    // Database Info
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationDatabase";

    // Tables
    private static final String TABLE_TRIP = "trip";
    private static final String TABLE_LOCATION = "location";

    // Client Columns
    private static final String KEY_ANDROID_ID = "androidId"; // remoteClientId @ API

    // Trip Columns
    private static final String KEY_TRIP_ID = "id";
    private static final String KEY_TRIP_UUID = "uuid";
    private static final String KEY_TRIP_LOCATION_DATA = "locationData";

    // Location Columns
    private static final String KEY_LOCATION_ID = "id"; // remoteTripId @ API
    private static final String KEY_LOCATION_TRIP_ID_FK = "tripId";

    // Location Columns (DATA)
    public static final String KEY_LOCATION_ACCURACY = "accuracy";
    public static final String KEY_LOCATION_ALTITUDE = "altitude";
    public static final String KEY_LOCATION_BEARING = "bearing";
    public static final String KEY_LOCATION_LATITUDE = "latitude";
    public static final String KEY_LOCATION_LONGITUDE = "longitude";
    public static final String KEY_LOCATION_SPEED = "speed";
    public static final String KEY_LOCATION_TIME = "ts";

    private static final String ARG_LATITUDE = "latitude";

    public interface ACTION {

        String INIT_ACTION = "de.uniko.fb1.soma.action.init";
        String MAIN_ACTION = "de.uniko.fb1.soma.action.main";

        String START_FOREGROUND_ACTION = "de.uniko.fb1.soma.action.startforeground";
        String RESUME_FOREGROUND_ACTION = "de.uniko.fb1.soma.action.resumeforeground";

        String ON_CONNECTION_FAILED = "de.uniko.fb1.soma.action.ON_CONNECTION_FAILED";

        String RUN_SCHEDULED_UPLOAD = "de.uniko.fb1.soma.action.RUN_SCHEDULED_UPLOAD";
        String SCHEDULED_UPLOAD = "de.uniko.fb1.soma.action.SCHEDULED_UPLOAD";
        String START_SERVICE = "de.uniko.fb1.soma.action.START_SERVICE";
        String START_TRACKING = "de.uniko.fb1.soma.action.START_TRACKING";
        String STOP_FOREGROUND_ACTION = "de.uniko.fb1.soma.action.stopforeground";
        String
                STOP_SERVICE = "de.uniko.fb1.soma.action.STOP_SERVICE";
        String STOP_TRACKING = "de.uniko.fb1.soma.action.STOP_TRACKING";
        String UPDATE_CURRENT_TRIP = "de.uniko.fb1.soma.action.UPDATE_CURRENT_TRIP";
        String LOCATION_UPDATE = "de.uniko.fb1.soma.action.LOCATION_UPDATE";
        String UPLOAD_DATA = "de.uniko.fb1.soma.action.UPLOAD_DATA";
        String ALL_UPLOADS_SUCCESSFUL = "de.uniko.fb1.soma.action.ALL_UPLOADS_SUCCESSFUL";
        String UPDATES_SERVICE_STATUS = "de.uniko.fb1.soma.action.UPDATES_SERVICE_STATUS";

        String CONNECT_GOOGLE_API_CLIENT = "de.uniko.fb1.soma.action.CONNECT_GOOGLE_API_CLIENT";
        String DISCONNECT_GOOGLE_API_CLIENT = "de.uniko.fb1.soma.action.DISCONNECT_GOOGLE_API_CLIENT";

        String FUSED_LOCATION_API_CONNECTED = "de.uniko.fb1.soma.action.FUSED_LOCATION_API_CONNECTED";
        String FUSED_LOCATION_API_DISCONNECTED = "de.uniko.fb1.soma.action.FUSED_LOCATION_API_DISCONNECTED";

        String GOOGLE_API_CONNECTION_FAILURE = "de.uniko.fb1.soma.action.GOOGLE_API_CONNECTION_FAILURE";
        String GOOGLE_API_CONNECTION_SUCCESS = "de.uniko.fb1.soma.action.GOOGLE_API_CONNECTION_SUCCESS";
        String GOOGLE_API_CONNECTION_SUSPEND = "de.uniko.fb1.soma.action.GOOGLE_API_CONNECTION_SUSPEND";

        String GOOGLE_API_CLIENT_IS_CONNECTED = "de.uniko.fb1.soma.action.GOOGLE_API_CLIENT_IS_CONNECTED";
        String GOOGLE_API_CLIENT_IS_CONNECTING = "de.uniko.fb1.soma.action.GOOGLE_API_CLIENT_IS_CONNECTING";
        String GOOGLE_API_CLIENT_IS_DISCONNECTED = "de.uniko.fb1.soma.action.GOOGLE_API_CLIENT_IS_DISCONNECTED";

        String ENABLE_SERVICE = "de.uniko.fb1.soma.action.ENABLE_SERVICE";
        String DISABLE_SERVICE = "de.uniko.fb1.soma.action.DISABLE_SERVICE";
        String RESUME_SERVICE = "de.uniko.fb1.soma.action.RESUME_SERVICE";

        String DB_LOCATION_ADDED = "de.uniko.fb1.soma.action.DB_LOCATION_ADDED";
        String DB_TRIP_ADDED = "de.uniko.fb1.soma.action.DB_TRIP_ADDED";

        String LOCATION_PREFERENCES = "de.uniko.fb1.soma.action.LOCATION_PREFERENCES";
        String LOCATION_AVAILABILITY = "de.uniko.fb1.soma.action.LOCATION_AVAILABILITY";

        String PROPAGATE_CHANGES = "de.uniko.fb1.soma.action.PROPAGATE_CHANGES";
        String QUIT_SERVICE = "de.uniko.fb1.soma.action.QUIT_SERVICE";

    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}
