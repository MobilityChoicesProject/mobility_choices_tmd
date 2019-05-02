package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.SpeedAcceleration;
import at.fhv.transportClassifier.common.CoordinateUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 02.05.2017.
 */
public class SimpleCoordinateInterpolator implements CoordinateInterpolator {

  private List<IGpsPoint> coordinates;
  private GaussKernelSmootherCache smootherCache;

  private double kernelBandwidth = 5;

  @Override
  public void setKernelBandwidth(double kernelBandwidth){
    this.kernelBandwidth = kernelBandwidth;
  }



  public SimpleCoordinateInterpolator(GaussKernelSmootherCache kernelSmootherCache,List<IGpsPoint> coordinateList){
    this.coordinates = coordinateList;
    smootherCache= kernelSmootherCache;
  }


  @Override
  public List<IGpsPoint> getCoordinates() {
    List<IGpsPoint> coordinates1 = new ArrayList<>();
    for (IGpsPoint coordinate : coordinates) {
      coordinates1.add(getCoordinate(coordinate.getTime()));
    }
    return coordinates1;
  }



  @Override
  public List<IGpsPoint> getInterpolatedCoordinatesExact(LocalDateTime starTime,
      LocalDateTime endtime, Duration frameSize){

   List<IGpsPoint> coordinates = new ArrayList<>();
    Duration between = Duration.between(starTime, endtime);
    long millisBetween = between.toMillis();
    long frameSizeMillis = frameSize.toMillis();

    long numberOfPoints = millisBetween / frameSizeMillis;

    for(int i = 0; i <= numberOfPoints;i++){

      long nanosToAdd = i * frameSizeMillis * 1000000;
      LocalDateTime localDateTime = starTime.plusNanos(nanosToAdd);
      IGpsPoint coordinate = smootherCache.calcAndPutInCache(localDateTime,this.coordinates, kernelBandwidth);
      coordinates.add(coordinate);
    }

    long modulo = millisBetween % frameSizeMillis;
    if (modulo !=0) {
      long nanosToAdd = ((numberOfPoints-1) * frameSizeMillis + modulo)* 1000000;
      LocalDateTime localDateTime = starTime.plusNanos(nanosToAdd);
      IGpsPoint coordinate = smootherCache.calcAndPutInCache(localDateTime,this.coordinates, kernelBandwidth);
      coordinates.add(coordinate);
    }

    return coordinates;
  }



  @Override
  public IGpsPoint getCoordinate(LocalDateTime time){
    IGpsPoint coordinate1 = smootherCache.calcAndPutInCache(time,this.coordinates, kernelBandwidth);
    return coordinate1;
  }



  @Override
  public Speed calcSpeedAt(LocalDateTime time){
    int _100microsecondsInNano = 1000000 * 100;
    LocalDateTime afterDate = time.plusNanos(_100microsecondsInNano);
    LocalDateTime beforeDate = time.minusNanos(_100microsecondsInNano);

    IGpsPoint beforeCoordinate = smootherCache.calcWithoutCaching(beforeDate, this.coordinates, kernelBandwidth);
    IGpsPoint afterCoordinate = smootherCache.calcWithoutCaching(afterDate, this.coordinates, kernelBandwidth);
    Speed speed = CoordinateUtil.calcSpeedBetween(beforeCoordinate, afterCoordinate);
    return speed;
  }


  @Override
  public SpeedAcceleration calcSpeedAcceleration(LocalDateTime time){
    int _100microsecondsInNano = 1000000 * 100;
    LocalDateTime afterDate = time.plusNanos(_100microsecondsInNano);
    LocalDateTime beforeDate = time.minusNanos(_100microsecondsInNano);

    Speed firstSpeed = calcSpeedAt(beforeDate);
    Speed secondSpeed = calcSpeedAt(afterDate);

    double seconds = Duration.between(beforeDate, afterDate).toMillis()/1000.0;

    Double speedDiff = secondSpeed.getMeterPerSecond()-firstSpeed.getMeterPerSecond();
    double acceleration = speedDiff / seconds;
    return new SpeedAcceleration(acceleration);

  }











}
