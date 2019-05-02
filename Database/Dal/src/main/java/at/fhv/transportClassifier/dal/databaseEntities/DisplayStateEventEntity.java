package at.fhv.transportClassifier.dal.databaseEntities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "DisplayStateEvent")
public class DisplayStateEventEntity {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "idDisplayState")
    private int idDisplayState;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "stateChange")
    private StateChange stateChange;

    @JoinColumn(name="Tracking_idTracking")
    @ManyToOne(fetch = FetchType.EAGER)
    private TrackingEntiy trackingEntiy;

    public TrackingEntiy getTrackingEntiy() {
        return trackingEntiy;
    }

    public void setTrackingEntiy(TrackingEntiy trackingEntiy) {
        if(trackingEntiy== null && this.trackingEntiy != null){
            this.trackingEntiy.getDisplayStateEventEntities().remove(this.trackingEntiy);
        }
        this.trackingEntiy = trackingEntiy;
    }


    public int getIdDisplayState() {
        return idDisplayState;
    }

    public void setIdDisplayState(int idDisplayState) {
        this.idDisplayState = idDisplayState;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public StateChange getStateChange() {
        return stateChange;
    }

    public void setStateChange(StateChange stateChanges) {
        this.stateChange = stateChanges;
    }
}
