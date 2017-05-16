package de.uniko.fb1.soma7;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/8/17.
 */

public class UploadDataTask extends AsyncTask<Trip, Integer, String> {
    private static final String TAG = "UploadDataTask";
    private final Context context;

    /**
     * Creates a new asynchronous task.
     * This constructor must be invoked on the UI thread.
     */
    public UploadDataTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected String doInBackground(Trip... params) {

        // TODO Read this ...
        // http://stackoverflow.com/a/9963705/220472

        UploadHelper uploader = new UploadHelper();
        for (final Trip trip : params) {
            Log.i(TAG, "PARAMS TRIP " + trip.toString());

            uploader.uploadTrip(context, trip, new VolleyCallback(){
                @Override
                public void onSuccessResponse(int httpStatusCode) {
                    Log.i(TAG, "UPLOAD SUCCESS " + httpStatusCode);
                }

                @Override
                public void onFailureResponse(int httpStatusCode) {
                    Log.i(TAG, "UPLOAD FAILURE " + httpStatusCode);
                }
            });
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "onProgressUpdate: " + values.toString());
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        Log.i(TAG, "onPostExecute: " + string);
    }
}
