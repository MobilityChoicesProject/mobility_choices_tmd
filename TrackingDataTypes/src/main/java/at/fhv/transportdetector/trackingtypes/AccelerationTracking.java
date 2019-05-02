package at.fhv.transportdetector.trackingtypes;

import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface AccelerationTracking extends Tracking {

    List<AcceleratorState> getAcceleratorStates();

}
