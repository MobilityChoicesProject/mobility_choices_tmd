package at.fhv.transportClassifier.dal.leightweightdatabaseentities;

import at.fhv.transportClassifier.dal.databaseEntities.BoundingBoxEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentBagEntity;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by Johannes on 01.03.2017.
 */
@Entity
@Table(name = "Tracking")
public class LeightweightTrackingEntity   {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idTracking")
    private int trackingId;

    @OneToMany(mappedBy = "trackingEntiy", targetEntity=TrackingInfoEntity.class)
    private List<TrackingInfoEntity> trackingInfos;

//    @OneToMany(mappedBy = "trackingEntiy", targetEntity = AccelerationValueEntity.class)
//    private List<AccelerationValueEntity> acceleratorTrackEntities;
//
//    @OneToMany(mappedBy = "trackingEntiy", targetEntity = GpsPointEntity.class)
//    private List<GpsPointEntity> gpsTrackings;
//
//    @OneToMany(mappedBy = "trackingEntiy", targetEntity = DisplayStateEventEntity.class)
//    private List<DisplayStateEventEntity> displayStateEventEntities;

    @OneToMany(mappedBy = "trackingEntiy", targetEntity = TrackingSegmentBagEntity.class)
    private List<TrackingSegmentBagEntity> trackingSegmentBagEntities;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="BoundingBox_idBoundingBox")
    private BoundingBoxEntity boundingBox;

    @Column(name="startTimestamp")
    private LocalDateTime startTimestamp;

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(LocalDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public BoundingBoxEntity getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBoxEntity boundingBox) {
        this.boundingBox = boundingBox;
    }

    public List<TrackingSegmentBagEntity> getTrackingSegmentBagEntities() {
        return trackingSegmentBagEntities;
    }

    public void setTrackingSegmentBagEntities(List<TrackingSegmentBagEntity> trackingSegmentBagEntities) {
        this.trackingSegmentBagEntities = trackingSegmentBagEntities;
    }

    public int getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
    }

    public List<TrackingInfoEntity> getTrackingInfos() {
        return trackingInfos;
    }

    public void setTrackingInfos(List<TrackingInfoEntity> trackingInfos) {
        this.trackingInfos = trackingInfos;
    }





}
