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
 * Created by Johannes on 09.02.2017.
 */
@Entity
@Table(name="AccelerationValue")
public class AccelerationValueEntity {


    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "idAccelerationValue")
    private int id;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "xAcceleration")
    private double xAcceleration;
    @Column(name = "yAcceleration")
    private double yAcceleration;
    @Column(name = "zAcceleration")
    private double zAcceleration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="Tracking_idTracking")
    private TrackingEntiy trackingEntiy;

    public TrackingEntiy getTrackingEntiy() {
        return trackingEntiy;
    }

    public void setTrackingEntiy(TrackingEntiy trackingEntiy) {
        if(this.trackingEntiy != null && trackingEntiy ==null){
            this.trackingEntiy.getAcceleratorTrackEntities().remove(this.trackingEntiy);
        }
        this.trackingEntiy = trackingEntiy;
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

    public double getxAcceleration() {
        return xAcceleration;
    }

    public void setxAcceleration(double xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public double getyAcceleration() {
        return yAcceleration;
    }

    public void setyAcceleration(double yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public double getzAcceleration() {
        return zAcceleration;
    }

    public void setzAcceleration(double zAcceleration) {
        this.zAcceleration = zAcceleration;
    }
}
