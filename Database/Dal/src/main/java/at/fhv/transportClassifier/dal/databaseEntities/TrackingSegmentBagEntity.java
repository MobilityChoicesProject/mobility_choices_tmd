package at.fhv.transportClassifier.dal.databaseEntities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
 * Created by Johannes on 13.02.2017.
 */
@Entity
@Table(name = "TrackingSegmentBag")
public class TrackingSegmentBagEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idTrackingSegmentBag")
    private int id;
    @Column(name = "version")
    private int version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tracking_idtracking")
    private TrackingEntiy trackingEntiy;

    @OneToMany(mappedBy = "trackingSegmentBag", targetEntity = TrackingSegmentEntity.class,orphanRemoval =true)
    private List<TrackingSegmentEntity> trackingSegmentEntities;

    public List<TrackingSegmentEntity> getTrackingSegmentEntities() {
        return trackingSegmentEntities;
    }

    public void setTrackingSegmentEntities(List<TrackingSegmentEntity> trackingSegmentEntities) {
        this.trackingSegmentEntities = trackingSegmentEntities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TrackingEntiy getTrackingEntiy() {
        return trackingEntiy;
    }

    public void setTrackingEntiy(TrackingEntiy trackingEntiy) {
        if(trackingEntiy == null && this.trackingEntiy != null){
            this.trackingEntiy.getTrackingSegmentBagEntities().remove(this.trackingEntiy);
        }
        this.trackingEntiy = trackingEntiy;
    }
}
