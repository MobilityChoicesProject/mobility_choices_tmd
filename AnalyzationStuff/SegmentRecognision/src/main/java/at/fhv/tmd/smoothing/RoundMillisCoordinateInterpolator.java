package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.SpeedAcceleration;
import at.fhv.transportClassifier.segmentsplitting.TimeUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 22.05.2017.
 */
public class RoundMillisCoordinateInterpolator implements CoordinateInterpolator {

  private List<IGpsPoint> coordinates;
  private CoordinateInterpolator coordinateInterpolator;

  public RoundMillisCoordinateInterpolator(List<IGpsPoint> coordinates,CoordinateInterpolator coordinateInterpolator){
   this.coordinateInterpolator = coordinateInterpolator;
   this.coordinates = coordinates;
  }

  @Override
  public void setKernelBandwidth(double kernelBandwidth) {
    coordinateInterpolator.setKernelBandwidth(kernelBandwidth);
  }

  @Override
  public List<IGpsPoint> getCoordinates() {
    List<IGpsPoint> coordinates2 = new ArrayList<>();
    for (IGpsPoint coordinate : coordinates) {
      LocalDateTime time = coordinate.getTime();
      time = TimeUtil.removeMs(time);
      coordinates2.add(getCoordinate(time));
    }
    return coordinates2;
  }

  @Override
  public List<IGpsPoint> getInterpolatedCoordinatesExact(LocalDateTime starTime,
      LocalDateTime endtime, Duration frameSize) {
    starTime = TimeUtil.removeMs(starTime);
    endtime = TimeUtil.removeMs(endtime);
    return coordinateInterpolator.getInterpolatedCoordinatesExact(starTime,endtime,frameSize);
  }

  @Override
  public IGpsPoint getCoordinate(LocalDateTime time) {
    time = TimeUtil.removeMs(time);
    return coordinateInterpolator.getCoordinate(time);
  }

  @Override
  public Speed calcSpeedAt(LocalDateTime time) {
    time = TimeUtil.removeMs(time);

    return coordinateInterpolator.calcSpeedAt(time);
  }

  @Override
  public SpeedAcceleration calcSpeedAcceleration(LocalDateTime time) {
    time = TimeUtil.removeMs(time);
    return coordinateInterpolator.calcSpeedAcceleration(time);
  }
}
