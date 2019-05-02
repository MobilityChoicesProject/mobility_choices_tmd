package at.fhv.transportClassifier.dal.databaseEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/**
 * Created by Johannes on 13.02.2017.
 */
@Entity
@Table(name = "trackinginfo")
public class TrackingInfoEntity {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "idTrackingInfo")
    private int id;

    @Column(name="value")
    private String value;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="TrackingInfoType_idTrackingInfoType")
    private TrackingInfoTypeEntity trackingInfoTypeEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="Tracking_idTracking")
    private TrackingEntiy trackingEntiy;


    public TrackingEntiy getTrackingEntiy() {
        return trackingEntiy;
    }

    public void setTrackingEntiy(TrackingEntiy trackingEntiy) {
        if (trackingEntiy == null && this.trackingEntiy != null) {
        this.trackingEntiy.getTrackingInfos().remove(this.trackingEntiy);
        }
        this.trackingEntiy = trackingEntiy;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TrackingInfoTypeEntity getTrackingInfoTypeEntity() {
        return trackingInfoTypeEntity;
    }

    public void setTrackingInfoTypeEntity(TrackingInfoTypeEntity trackingInfoTypeEntity) {
        this.trackingInfoTypeEntity = trackingInfoTypeEntity;
    }
}
