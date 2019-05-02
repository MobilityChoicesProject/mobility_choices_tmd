package at.fhv.transportdetector.trackingtypes;

import at.fhv.tmd.common.IGpsPoint;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 07.02.2017.
 */
public interface IExtendedGpsPoint extends IGpsPoint {

    Double getSpeed();

    Double getAccuracy();

    Double getAltitude();

    LocalDateTime getDeviceSavingSystemTime();

    LocalDateTime getSensorTime();

    LocalDateTime getMostAccurateTime();
}
