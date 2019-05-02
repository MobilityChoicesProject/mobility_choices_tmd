package at.fhv.transportdetector.trackingtypes;

import com.google.gson.annotations.SerializedName;

public enum BackgroundGeolocationActivity {
    @SerializedName("in_vehicle")
    IN_VEHICLE,
    @SerializedName("on_bicycle")
    ON_BICYCLE,
    @SerializedName("on_foot")
    ON_FOOT,
    @SerializedName("running")
    RUNNING,
    @SerializedName("walking")
    WALKING,
    @SerializedName("still")
    STILL,
    @SerializedName("unknown")
    UNKNOWN
}
