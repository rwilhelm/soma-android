package de.uniko.fb1.soma7;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/8/17.
 */

class UploadAsyncTask extends AsyncTask<Trip, Integer, String> {
    private static final String TAG = "UploadAsyncTask";
    private final Context context;
//
//    public static final MediaType JSON
//            = MediaType.parse("application/json; charset=utf-8");

    /**
     * Creates a new asynchronous task.
     * This constructor must be invoked on the UI thread.
     */
    public UploadAsyncTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected String doInBackground(Trip... params) {

        // TODO Read this ...
        // http://stackoverflow.com/a/9963705/220472

        UploadHelperVolley uploader = new UploadHelperVolley();
//        OkHttpClient client = new OkHttpClient();
//        String url = "https://soma.uni-koblenz.de/api";
        
        for (final Trip trip : params) {
            Log.i(TAG, "PARAMS TRIP " + trip.toString());
//            String json = trip.getRequestBody();

//            String post(String url, String json) throws IOException {
//
//                RequestBody body = RequestBody.create(JSON, );
//
//                Request request = new Request.Builder()
//                        .url(url)
//                        .post(body)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
//                return response.body().string();
//            }

            uploader.uploadTrip(context, trip, new VolleyCallback(){
                @Override
                public void onSuccessResponse(int httpStatusCode) {
                    Log.i(TAG, "UPLOAD SUCCESS " + httpStatusCode);
                    trip.deleteFromDatabase();
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
