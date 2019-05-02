package at.fhv.transportdetector.trackingtypes;

import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface TrackingSegmentBag {

    List<TrackingSegment> getSegments();
    int getVersion();

}
