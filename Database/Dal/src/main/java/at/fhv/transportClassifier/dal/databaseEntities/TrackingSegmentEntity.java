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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by Johannes on 13.02.2017.
 */

@Entity
@Table(name = "TrackingSegment")
public class TrackingSegmentEntity {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "idTrackingSegment")
    private int id;
    @Column(name="startTimestamp")
    private LocalDateTime startTimeStamp;
    @Column(name="endTimestamp")
    private LocalDateTime endTimeStamp;
    @Column(name="savingTimestamp")
    private LocalDateTime savingTimeStamp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="TrackingSegmentBag_idTrackingSegmentBag")
    private TrackingSegmentBagEntity trackingSegmentBag;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="TrackingSegmentType_idTrackingSegmentType")
    private TrackingSegmentTypeEntity trackingSegmentType;


    @OneToOne(fetch = FetchType.EAGER,orphanRemoval =true)
    @JoinColumn(name="BoundingBox_idBoundingBox")
    private BoundingBoxEntity boundingBox;

    public BoundingBoxEntity getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBoxEntity boundingBox) {
        this.boundingBox = boundingBox;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(LocalDateTime startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public LocalDateTime getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(LocalDateTime endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public LocalDateTime getSavingTimeStamp() {
        return savingTimeStamp;
    }

    public void setSavingTimeStamp(LocalDateTime savingTimeStamp) {
        this.savingTimeStamp = savingTimeStamp;
    }

    public TrackingSegmentBagEntity getTrackingSegmentBag() {
        return trackingSegmentBag;
    }

    public void setTrackingSegmentBag(TrackingSegmentBagEntity trackingSegmentBag) {
        if(trackingSegmentBag == null&& this.trackingSegmentBag != null){
            this.trackingSegmentBag.getTrackingSegmentEntities().remove(this);
        }
        this.trackingSegmentBag = trackingSegmentBag;
        trackingSegmentBag.getTrackingSegmentEntities().add(this);
    }

    public TrackingSegmentTypeEntity getTrackingSegmentType() {
        return trackingSegmentType;
    }

    public void setTrackingSegmentType(TrackingSegmentTypeEntity trackingSegmentType) {
        this.trackingSegmentType = trackingSegmentType;
    }
}
