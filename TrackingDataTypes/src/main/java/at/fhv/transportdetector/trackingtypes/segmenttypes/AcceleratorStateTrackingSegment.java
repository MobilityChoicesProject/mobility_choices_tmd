package at.fhv.transportdetector.trackingtypes.segmenttypes;

import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface AcceleratorStateTrackingSegment  extends TrackingSegment{

    List<AcceleratorState> getAcceleratorStates();
}
