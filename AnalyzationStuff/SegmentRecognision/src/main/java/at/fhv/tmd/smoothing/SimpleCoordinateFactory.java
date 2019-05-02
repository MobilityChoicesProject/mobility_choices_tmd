package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportdetector.trackingtypes.builder.SimpleGpsPoint;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 02.05.2017.
 */
public class SimpleCoordinateFactory implements CoordinateFactory {

  @Override
  public IGpsPoint createCoordinate(double latitude, double longitude, double accuracy,
      LocalDateTime timestamp) {

    SimpleGpsPoint gpsPoint = new SimpleGpsPoint(timestamp,latitude,longitude,null,accuracy,null,timestamp);

    return gpsPoint;
  }
}
