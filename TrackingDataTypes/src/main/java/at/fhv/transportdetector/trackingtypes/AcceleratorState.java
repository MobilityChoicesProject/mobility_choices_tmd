package at.fhv.transportdetector.trackingtypes;

import java.time.LocalDateTime;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface AcceleratorState {

    LocalDateTime getTime();
    double getXAcceleration();
    double getYAcceleration();
    double getZAcceleration();

}
