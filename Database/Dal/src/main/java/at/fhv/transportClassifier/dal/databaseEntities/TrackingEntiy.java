package at.fhv.transportClassifier.dal.databaseEntities;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
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
 * Created by Johannes on 09.02.2017.
 */
@Entity
@Table(name = "Tracking")
public class TrackingEntiy {

    @GeneratedValue(generator="increment")
    @Id
    @Column(name = "idTracking")
    private int trackingId;

    @OneToMany(mappedBy = "trackingEntiy", targetEntity=TrackingInfoEntity.class,orphanRemoval =true,cascade = CascadeType.REMOVE)
    private List<TrackingInfoEntity> trackingInfos;

    @OneToMany(mappedBy = "trackingEntiy", targetEntity = AccelerationValueEntity.class,cascade = CascadeType.REMOVE)
    private List<AccelerationValueEntity> acceleratorTrackEntities;

    @OneToMany(mappedBy = "trackingEntiy", targetEntity = GpsPointEntity.class,cascade = CascadeType.REMOVE)
    private List<GpsPointEntity> gpsTrackings;

    @OneToMany(mappedBy = "trackingEntiy", targetEntity = DisplayStateEventEntity.class,cascade = CascadeType.REMOVE)
    private List<DisplayStateEventEntity> displayStateEventEntities;

    @OneToMany(mappedBy = "trackingEntiy", targetEntity = TrackingSegmentBagEntity.class,orphanRemoval =true,cascade = CascadeType.REMOVE)
    private List<TrackingSegmentBagEntity> trackingSegmentBagEntities;

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @JoinColumn(name="BoundingBox_idBoundingBox")
    private BoundingBoxEntity boundingBox;

    @Column(name="startTimestamp")
    private LocalDateTime startTimestamp;


    @Column(name="endTimestamp")
    private LocalDateTime endTimestamp;

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



    public List<GpsPointEntity> getGpsTrackings() {
        return gpsTrackings;
    }

    public void setGpsTrackings(List<GpsPointEntity> gpsTrackings) {
        this.gpsTrackings = gpsTrackings;
    }

    public List<DisplayStateEventEntity> getDisplayStateEventEntities() {
        return displayStateEventEntities;
    }

    public void setDisplayStateEventEntities(List<DisplayStateEventEntity> displayStateEventEntities) {
        this.displayStateEventEntities = displayStateEventEntities;
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

    public List<AccelerationValueEntity> getAcceleratorTrackEntities() {
        return acceleratorTrackEntities;
    }

    public void setAcceleratorTrackEntities(List<AccelerationValueEntity> acceleratorTrackEntities) {
        this.acceleratorTrackEntities = acceleratorTrackEntities;
    }

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(LocalDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
}
