package de.uniko.fb1.soma7;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Created by asdf on 5/16/17.
 */

class UploadHelperOkHttp {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

//    String post(String url, String json) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
//    }

    public static void main(String[] args) throws IOException {
//        PostExample example = new PostExample();
//        String json = example.bowlingJson("Jesse", "Jake");
//        String response = example.post("http://www.roundsapp.com/post", json);
//        System.out.println(response);
    }
}
