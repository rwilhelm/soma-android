package de.uniko.fb1.soma7;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * de.uniko.fb1.soma
 * android
 * Created by asdf on 2/6/17.
 */

class UploadHelperVolley {

    private static final String TAG = "UploadHelperVolley";

    public void uploadTrip(final Context context, final Trip trip, final VolleyCallback callback) {
        final RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
//        final String url = context.getString(R.string.REMOTE_API_URL);
        final String requestBody = trip.getRequestBody(context);


//        queue = MySingleton.getInstance(context).getRequestQueue();



        Log.v(TAG, "REQUEST BODY " + requestBody);


        String url = "https://soma.uni-koblenz.de:5000/upload";
        final StringRequest uploadRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                callback.onSuccessResponse(response);
//                callback.onSuccessResponse(response);

//                if (Objects.equals(response, "OK")) {
//                    deleteFromDatabase(context);
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "UPLOAD FAILURE " + error.getMessage());
                queue.stop();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int httpStatusCode = response.statusCode;
                if (httpStatusCode == 200) {
                    callback.onSuccessResponse(httpStatusCode);
                } else {
                    callback.onFailureResponse(httpStatusCode);
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("a", String.valueOf(1L));
                return params;
            }

            /* Get headers */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization: Basic", "TOKEN"); // TODO
                return params;
            }
        };

        RequestQueue.RequestFinishedListener listener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if (request.equals(uploadRequest)) {
                    Log.i(TAG, "onRequestFinished: " + request); // TODO Return something useful
                }
            }
        };

        queue.addRequestFinishedListener(listener);

        uploadRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(uploadRequest);

//        MySingleton.getInstance(context).addToRequestQueue(uploadRequest);

        // return uploadRequest.hasHadResponseDelivered();
    }



}
