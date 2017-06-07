package de.uniko.SoMA;

/**
 * Created by asdf on 2/11/17.
 */

interface VolleyCallback {
    void onSuccessResponse(int httpStatusCode);
    void onFailureResponse(int httpStatusCode);
}
