package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.DisplayStateChangedEvent;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedType;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleDisplayStateChangedEvent implements DisplayStateChangedEvent {
    protected DisplayStateChangedType displayStateChangedType;
    protected LocalDateTime time;

    public void setDisplayStateChangedType(DisplayStateChangedType displayStateChangedType) {
        this.displayStateChangedType = displayStateChangedType;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public SimpleDisplayStateChangedEvent(DisplayStateChangedType displayStateChangedType, LocalDateTime time) {
        this.displayStateChangedType = displayStateChangedType;
        this.time = time;
    }

    public SimpleDisplayStateChangedEvent() {
    }

    @Override
    public DisplayStateChangedType getChangeEventType() {
        return displayStateChangedType;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }


}
