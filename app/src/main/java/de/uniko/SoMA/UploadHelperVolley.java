package de.uniko.SoMA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * de.uniko.SoMA.soma
 * android
 * Created by asdf on 2/6/17.
 */

class UploadHelperVolley {

    private static final String TAG = "UploadHelperVolley";

    private static class UploadObject {
        private final String uuid;
        private final String device_id;
        private final List<LocationObject> locationData;

        UploadObject(String device_id, List<LocationObject> locationData) {
            this.uuid = UUID.randomUUID().toString();
            this.device_id = device_id;
            this.locationData = locationData;
        }

        String getRequestBody() {
            return new Gson().toJson(this);
        }

        ArrayList<Integer> getLocationIds() {
            ArrayList<Integer> ids = new ArrayList<>();
            for (LocationObject location: locationData) {
                location.getId();
                ids.add(location.getId());
            }
            return ids;
        }
    }

    void uploadLocations(final Context context, final List<LocationObject>[] locations, final VolleyCallback callback) {
        Log.w(TAG, "uploadLocations: Uploading " + locations.length + " locations" );

        // http://blog.applegrew.com/2015/04/using-pinned-self-signed-ssl-certificate-with-android-volley/
        // http://stackoverflow.com/a/28120209/220472

        // Get device id as UUID
        @SuppressLint("HardwareIds")
        String clientId = UUID.nameUUIDFromBytes(Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID).getBytes()).toString();


        DatabaseHelper db = DatabaseHelper.getInstance(context);
        String url = context.getString(R.string.upload_url);

        // TODO iterate over locations instead (which are lists of locations actually)
        UploadObject u = new UploadObject(clientId, locations[0]);

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory(context));
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        final RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext(), hurlStack);

        final StringRequest uploadRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.w(TAG, "[UPLOAD REQUEST] Response: " + response);
                },
                error -> {
                    Log.w(TAG, "[UPLOAD REQUEST] Failure: " + error.getMessage());
                    queue.stop(); // TODO
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int httpStatusCode = response.statusCode;
                if (httpStatusCode == 200) {
                    callback.onSuccessResponse(httpStatusCode); // TODO See VolleyCallback.java
                } else {
                    callback.onFailureResponse(httpStatusCode);
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String requestBody = u.getRequestBody();
                Log.i(TAG, "Request Body: " + requestBody);
                return requestBody.getBytes();
            }

            /* Get headers */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization: Basic", "iemaino4ser1tighon"); // TODO Implement JWT auth
                return params;
            }
        };

        /* ... */

        RequestQueue.RequestFinishedListener listener = request -> {
            if (request.equals(uploadRequest)) {
                Log.i(TAG, "[UPLOAD REQUEST] RequestFinishedListener: " + u.getLocationIds());
                //  u.getLocationIds().forEach(db::deleteLocation);
                for (int id: u.getLocationIds()) {
                    db.deleteLocation(id);
                }
            }
        };

        queue.addRequestFinishedListener(listener);

        uploadRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(uploadRequest);
    }

    // Let's assume your server app is hosting inside a server machine which has a server
    // certificate in which "Issued to" is "localhost", for example. Then, inside verify
    // method you can verify "localhost". If not, you can temporarily return true.
    private HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> {
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            // return true;
            // return hv.verify("localhost", session);
            return hv.verify("soma.uni-koblenz.de", session); // TODO Extract string resource
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("[UNTRUST CLIENT CERT]", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("[UNTRUST SERVER CERT]", e.toString());
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory(Context context)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, java.security.cert.CertificateException {

        // Cert file stored in app/src/main/res/raw
        InputStream caInput = context.getApplicationContext().getResources().openRawResource(R.raw.mycert);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null); // Initializes keystore
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }
}
