package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 02.05.2017.
 */
public interface CoordinateFactory extends Serializable {


  IGpsPoint createCoordinate(double latitude, double longitude, double accuracy,
      LocalDateTime timestamp);

}
