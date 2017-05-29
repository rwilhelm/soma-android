package de.uniko.fb1.SoMA;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by asdf on 5/27/17.
 */

class LocationObject {
    private final int id;
    private final float latitude;
    private final float longitude;

    LocationObject(int id, float accuracy, float altitude, float bearing, float latitude, float longitude, long timestamp, float speed) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return this.id;
    }

    LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }
}
