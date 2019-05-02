package at.fhv.transportdetector.trackingtypes;

import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface DisplayStateEventTracking extends Tracking {

    List<DisplayStateChangedEvent> getDisplayStateChangeEvents();
}
