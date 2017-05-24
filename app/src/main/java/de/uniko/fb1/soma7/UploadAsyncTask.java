package de.uniko.fb1.soma7;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/8/17.
 */

class UploadAsyncTask extends AsyncTask<List<DatabaseHelper.DataObject>, Integer, String> {
    private static final String TAG = "UploadAsyncTask";
    private final Context context;

    /**
     * Creates a new asynchronous task.
     * This constructor must be invoked on the UI thread.
     */
    UploadAsyncTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected final String doInBackground(List<DatabaseHelper.DataObject>... params) {

        // TODO Read this ...
        // http://stackoverflow.com/a/9963705/220472

        Log.d(TAG, "Class of params" + params.getClass());
        Log.d(TAG, "Length of params" + params.length);
        Log.d(TAG, "Params " + params);

        UploadHelperVolley uploader = new UploadHelperVolley();
        uploader.uploadAllLocations(context, params, new VolleyCallback(){
            @Override
            public void onSuccessResponse(int httpStatusCode) {
                Log.i(TAG, "UPLOAD SUCCESS" + httpStatusCode);
            }

            @Override
            public void onFailureResponse(int httpStatusCode) {
                Log.i(TAG, "UPLOAD FAILURE " + httpStatusCode);
            }
        });

//        for (final Location location : params) {
//            Log.i(TAG, "PARAMS LOCATION " + locations.toString());
//        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "onProgressUpdate: " + Arrays.toString(values));
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        Log.i(TAG, "onPostExecute: " + string);
    }
}
