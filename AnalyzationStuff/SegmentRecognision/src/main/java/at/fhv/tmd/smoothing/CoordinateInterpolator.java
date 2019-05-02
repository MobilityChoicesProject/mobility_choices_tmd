package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.SpeedAcceleration;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 22.05.2017.
 */
public interface CoordinateInterpolator extends Serializable{

  void setKernelBandwidth(double kernelBandwidth);


  /**
   * Calculates the interpolated coordinate for the passed Coordinates. There will be only interpolated coordinate values for already available coordinates. There will be no interpolated coordinates signal shortages,etc.
   * @return
   */
  List<IGpsPoint> getCoordinates();


  /**
   * Calculates interpolated coordinates from the starttime until the endtime. Each points time is an incrementation of his previous points time by the framesize; Therefore signal shortages, etc. are interpolated.
   * @param starTime
   * @param endtime
   * @param frameSize
   * @return
   */
  List<IGpsPoint> getInterpolatedCoordinatesExact(LocalDateTime starTime, LocalDateTime endtime,
      Duration frameSize);

  IGpsPoint getCoordinate(LocalDateTime time);

  Speed calcSpeedAt(LocalDateTime time);

  SpeedAcceleration calcSpeedAcceleration(LocalDateTime time);
}
