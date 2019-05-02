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
public interface LeightweightTracking {
    LocalDateTime getStartTimestamp();


    BoundingBox getBoundingBox();

    int getTrackingId();

    List<TrackingInfo> getTrackingInfos();

    Duration getTrackingDuration();

    Set<TransportType> getTransportTypes();

    boolean isAcceleratorDataAvailable();

}
