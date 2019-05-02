package at.fhv.transportdetector.trackingtypes.segmenttypes;

import at.fhv.transportdetector.trackingtypes.DisplayStateChangedEvent;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface DisplayStateChangedEventSegment  extends TrackingSegment{

    List<DisplayStateChangedEvent> getDisplayStateChangedEvent();

}
