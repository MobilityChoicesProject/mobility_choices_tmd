package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.AccelerationTracking;
import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedEvent;
import at.fhv.transportdetector.trackingtypes.DisplayStateEventTracking;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleAllTracking extends SimpleTracking implements AccelerationTracking, DisplayStateEventTracking {


    protected List<DisplayStateChangedEvent> displayStateChangeEvents;
    protected List<AcceleratorState> acceleratorSates;

    public void addDisplayStateChangeEvents(List<DisplayStateChangedEvent> displayStateChangeEvents) {
        getDisplayStateChangeEvents().addAll(displayStateChangeEvents);
    }

    public void addAcceleratorSates(List<AcceleratorState> acceleratorSates) {
        getAcceleratorStates().addAll(acceleratorSates) ;
    }

    @Override
    public List<AcceleratorState> getAcceleratorStates() {
        if(acceleratorSates == null){
            acceleratorSates = new ArrayList<>();
        }
        return acceleratorSates;
    }

    @Override
    public List<DisplayStateChangedEvent> getDisplayStateChangeEvents() {
        if(displayStateChangeEvents == null){
            displayStateChangeEvents = new ArrayList<>();
        }

        return displayStateChangeEvents;
    }
}
