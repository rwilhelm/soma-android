package de.uniko.fb1.soma7;



import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by angelluis on 13/04/16.
 */

public class HurlSslStack extends HurlStack {
    private InputStream certificateStream;

    public HurlSslStack(InputStream input){
        certificateStream = input;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
        try {
            httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
            httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpsURLConnection;
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("localhost", session);
            }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {

        certificateStream.reset();
        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(certificateStream, "A9Q2BToZFksGoMwxaCORKAXFKjLAZ82WNPvcE6cVWZJHtDSTJMj3XgTSkaQRlXt".toCharArray());

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }
}