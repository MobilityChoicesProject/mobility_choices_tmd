package at.fhv.transportClassifier.dal.databaseEntities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Johannes on 13.02.2017.
 */
@Entity
@Table(name = "GpsPoint")
public class GpsPointEntity {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "idGpsPoint")
    private int id;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "longitude")
    private double longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="Tracking_idTracking")
    private TrackingEntiy trackingEntiy;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "deviceSystemSavingTime")
    private LocalDateTime deviceSystemSavingTime;

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public LocalDateTime getDeviceSystemSavingTime() {
        return deviceSystemSavingTime;
    }

    public void setDeviceSystemSavingTime(LocalDateTime deviceSystemSavingTime) {
        this.deviceSystemSavingTime = deviceSystemSavingTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public TrackingEntiy getTrackingEntiy() {
        return trackingEntiy;
    }

    public void setTrackingEntiy(TrackingEntiy trackingEntiy) {
        if(trackingEntiy == null && this.trackingEntiy != null){
            this.trackingEntiy.getGpsTrackings().remove(this.trackingEntiy);
        }
        this.trackingEntiy = trackingEntiy;
    }
}
