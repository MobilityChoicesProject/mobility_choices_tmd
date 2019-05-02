package at.fhv.tmddemoservice.jsonEntities;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by Johannes on 20.05.2017.
 */
public class GpsPointEntity implements IGpsPoint {


    private DateTimeFormatter formatterNoTimeZone = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private DateTimeFormatter formatterWithTimeZone = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

    private double lat;
    @SerializedName("lng")
    private double lng;
    private double accuracy;

    @SerializedName("time")
    private String timeStr;
    private double altitude;
    private double confidence;

    @SerializedName("type")
    private BackgroundGeolocationActivity activity;


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


    @Override
    public Double getLatitude() {
        return lat;
    }

    @Override
    public Double getLongitude() {
        return lng;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    @Override
    public LocalDateTime getTime() {
        try {
            return LocalDateTime.parse(timeStr, formatterNoTimeZone);
        }catch (DateTimeParseException e){
            return LocalDateTime.parse(timeStr, formatterWithTimeZone);
        }
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String time) {
        this.timeStr = time;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public BackgroundGeolocationActivity getActivity() {
        return activity;
    }

    public void setActivity(BackgroundGeolocationActivity activity) {
        this.activity = activity;
    }
}
