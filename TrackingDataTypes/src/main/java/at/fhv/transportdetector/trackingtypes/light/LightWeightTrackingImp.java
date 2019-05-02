package at.fhv.transportdetector.trackingtypes.light;

import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Johannes on 01.03.2017.
 */
public class LightWeightTrackingImp implements LeightweightTracking {
   private LocalDateTime startDateTime;
    private BoundingBox boundingBox;
    private int trackingId;
    private List<TrackingInfo> trackingInfos;
    private Duration trackingDuration;
    private Set<TransportType> transportTypes;
    private boolean _isAcceleratorDataAvailable;

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }



    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
    }

    public void setTrackingInfos(List<TrackingInfo> trackingInfos) {
        this.trackingInfos = trackingInfos;
    }

    public void setTrackingDuration(Duration trackingDuration) {
        this.trackingDuration = trackingDuration;
    }

    public void setTransportTypes(Set<TransportType> transportTypes) {
        this.transportTypes = transportTypes;
    }

    public void set_isAcceleratorDataAvailable(boolean _isAcceleratorDataAvailable) {
        this._isAcceleratorDataAvailable = _isAcceleratorDataAvailable;
    }

    @Override
    public LocalDateTime getStartTimestamp() {
        return startDateTime;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }


    @Override
    public int getTrackingId() {
        return trackingId;
    }

    @Override
    public List<TrackingInfo> getTrackingInfos() {
        return trackingInfos;
    }

    @Override
    public Duration getTrackingDuration() {
        return trackingDuration;
    }

    @Override
    public Set<TransportType> getTransportTypes() {
        return transportTypes;
    }

    @Override
    public boolean isAcceleratorDataAvailable() {
        return _isAcceleratorDataAvailable;
    }
}
