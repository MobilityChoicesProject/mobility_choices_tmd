package at.fhv.tmd.common;

import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;

import java.time.LocalDateTime;

/**
 * Created by Johannes on 02.05.2017.
 */
public interface IGpsPoint extends ICoordinate {


    Double getAccuracy();

    LocalDateTime getTime();

    Double getAltitude();

    Double getConfidence();

    BackgroundGeolocationActivity getActivity();
}
