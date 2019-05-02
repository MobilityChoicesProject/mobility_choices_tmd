package at.fhv.transportdetector.trackingtypes.segmenttypes;

import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface TrackingSegment {

    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    TransportType getTransportType();
    List<IExtendedGpsPoint> getGpsPoints();
    BoundingBox getBoundingBox();

}
