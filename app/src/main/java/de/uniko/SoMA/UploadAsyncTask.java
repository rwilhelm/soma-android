package de.uniko.SoMA;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * de.uniko.SoMA.soma
 * android
 * Created by asdf on 2/8/17.
 */

class UploadAsyncTask extends AsyncTask<List<LocationObject>, Integer, String> {
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
    @SafeVarargs
    protected final String doInBackground(List<LocationObject>... params) {

        // TODO Read this ...
        // http://stackoverflow.com/a/9963705/220472
        // http://stackoverflow.com/a/28120209/220472

        UploadHelperVolley uploader = new UploadHelperVolley();
        uploader.uploadLocations(context, params, new VolleyCallback(){
            @Override
            public void onSuccessResponse(int httpStatusCode) {
                Log.i(TAG, "UPLOAD SUCCESS " + httpStatusCode);
                context.sendBroadcast(new Intent(Constants.EVENT.UPLOAD_SUCCESS));
            }

            @Override
            public void onFailureResponse(int httpStatusCode) {
                Log.i(TAG, "UPLOAD FAILURE " + httpStatusCode);
            }
        });

        return null;
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
