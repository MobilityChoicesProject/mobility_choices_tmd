package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;

import java.time.LocalDateTime;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleGpsPoint implements IExtendedGpsPoint, IGpsPoint {

    private Double speed;
    private Double accuracy;
    private Double altitude;
    protected LocalDateTime sensorTime;
    protected Double latitude;
    protected Double longitude;
    protected LocalDateTime deviceSavingSystemTime;

    private Double confidence;
    private BackgroundGeolocationActivity activity;


    public SimpleGpsPoint(LocalDateTime sensorTime, double latitude, double longitude, Double altitude, Double accuracy, Double speed,
                          LocalDateTime systemTime) {
        this.sensorTime = sensorTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceSavingSystemTime = systemTime;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
    }

    public SimpleGpsPoint(LocalDateTime sensorTime, double latitude, double longitude, Double altitude, Double accuracy, Double speed,
                          LocalDateTime systemTime, Double confidence, BackgroundGeolocationActivity activity) {
        this(sensorTime, latitude, longitude, altitude, accuracy, speed, systemTime);
        this.confidence = confidence;
        this.activity = activity;
    }


    public SimpleGpsPoint(LocalDateTime time, double latitude, double longitude) {
        this(time, latitude, longitude, null, null, null, null, null, BackgroundGeolocationActivity.UNKNOWN);
    }

    public SimpleGpsPoint() {

    }

    @Override
    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    @Override
    public Double getAccuracy() {
        return accuracy;
    }

    @Override
    public LocalDateTime getTime() {
        return getMostAccurateTime();
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public Double getAltitude() {
        return altitude;
    }

    @Override
    public Double getConfidence() {
        return confidence;
    }

    @Override
    public BackgroundGeolocationActivity getActivity() {
        return activity;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    @Override
    public LocalDateTime getDeviceSavingSystemTime() {
        return deviceSavingSystemTime;
    }

    public void setDeviceSavingSystemTime(LocalDateTime deviceSavingSystemTime) {
        this.deviceSavingSystemTime = deviceSavingSystemTime;
    }

    public void setSensorTime(LocalDateTime time) {
        this.sensorTime = time;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public LocalDateTime getSensorTime() {
        return sensorTime;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public LocalDateTime getMostAccurateTime() {
        if (deviceSavingSystemTime != null) {
            return deviceSavingSystemTime;
        } else {
            return sensorTime;
        }
    }
}
