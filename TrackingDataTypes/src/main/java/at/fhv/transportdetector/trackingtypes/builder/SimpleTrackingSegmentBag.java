package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleTrackingSegmentBag implements TrackingSegmentBag{

    private List<TrackingSegment> segments = new ArrayList<>();
    private int version;

    public void addSegments(List<TrackingSegment> segments) {
        this.segments.addAll(segments);
    }
    public void addSegment(TrackingSegment segment) {
        this.segments.add(segment);
    }


    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public List<TrackingSegment> getSegments() {
        return segments;
    }

    @Override
    public int getVersion() {
        return version;
    }
}
