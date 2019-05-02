package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedEvent;
import at.fhv.transportdetector.trackingtypes.segmenttypes.AcceleratorStateTrackingSegment;
import at.fhv.transportdetector.trackingtypes.segmenttypes.DisplayStateChangedEventSegment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleAllTrackingSegment extends SimpleTrackingSegment implements AcceleratorStateTrackingSegment,DisplayStateChangedEventSegment{


    protected List<AcceleratorState> allAcceleratorStates ;
    protected List<DisplayStateChangedEvent> allDisplayStateChangedEvents;

    protected List<AcceleratorState> acceleratorStates ;
    protected List<DisplayStateChangedEvent> displayStateChangedEvents ;


    public void setAllAcceleratorStates(List<AcceleratorState> allAcceleratorStates) {
        this.allAcceleratorStates = allAcceleratorStates;
    }

    public void setAllDisplayStateChangedEvents(List<DisplayStateChangedEvent> allDisplayStateChangedEvents) {
        this.allDisplayStateChangedEvents = allDisplayStateChangedEvents;
    }

    @Override
    public List<AcceleratorState> getAcceleratorStates() {
        if(acceleratorStates == null){
            initAcceleratorStates();
        }
        return acceleratorStates;
    }

    private void initAcceleratorStates() {
        acceleratorStates = new ArrayList<>();
        for (AcceleratorState acceleratorState : allAcceleratorStates) {
            boolean isAfter =acceleratorState.getTime().isAfter(startTime);
            boolean isAt = acceleratorState.getTime().isEqual(startTime);
            boolean minLimitValid = isAfter||isAt;
            boolean maxLimitValid =acceleratorState.getTime().isBefore(endTime);

            if(minLimitValid && maxLimitValid){
                acceleratorStates.add(acceleratorState);
            }
        }
        allAcceleratorStates = null;
    }

    @Override
    public List<DisplayStateChangedEvent> getDisplayStateChangedEvent() {

        if(displayStateChangedEvents == null){
            initDisplayStateChangedEvents();
        }
        return displayStateChangedEvents;
    }



    private void initDisplayStateChangedEvents() {
        displayStateChangedEvents = new ArrayList<>();
        for (DisplayStateChangedEvent displayStateChangedEvent : allDisplayStateChangedEvents) {
            boolean isAfter =displayStateChangedEvent.getTime().isAfter(startTime);
            boolean isAt = displayStateChangedEvent.getTime().isEqual(startTime);
            boolean minLimitValid = isAfter||isAt;
            boolean maxLimitValid =displayStateChangedEvent.getTime().isBefore(endTime);

            if(minLimitValid && maxLimitValid){
                displayStateChangedEvents.add(displayStateChangedEvent);
            }
        }
        allDisplayStateChangedEvents = null;
    }
}
