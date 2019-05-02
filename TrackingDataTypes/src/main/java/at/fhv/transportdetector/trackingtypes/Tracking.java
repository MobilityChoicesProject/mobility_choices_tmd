package at.fhv.transportdetector.trackingtypes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface Tracking {


    List<TrackingInfo> getTrackingInfos();
    List<TrackingSegmentBag> getTrackingSegmentBags();
    List<IExtendedGpsPoint> getGpsPoints();
    BoundingBox getBoundingBox();
    LocalDateTime getStartTimestamp();
    LocalDateTime getEndTimestamp();


    TrackingSegmentBag getLatestTrackingSegmentBag();
    TrackingSegmentBag getTrackingSegmentBagWithVersion(int version);
    String getTrackingInfo(String key);
    boolean hasTrackingInfo(String key);
    Long getId();

}
