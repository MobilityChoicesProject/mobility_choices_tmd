package at.fhv.transportdetector.trackingtypes;

import java.time.LocalDateTime;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface DisplayStateChangedEvent {

    DisplayStateChangedType getChangeEventType();
    LocalDateTime getTime();
}
