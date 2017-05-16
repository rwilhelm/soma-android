package de.uniko.fb1.soma7;

/**
 * Created by asdf on 2/11/17.
 */

public interface VolleyCallback<String> {
    void onSuccessResponse(int httpStatusCode);
    void onFailureResponse(int httpStatusCode);
}

//public interface VolleyCallback{
//    void onSuccess(String result);
//}


// http://stackoverflow.com/a/28120209/220472