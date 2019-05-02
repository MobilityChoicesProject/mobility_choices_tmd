package at.fhv.transportClassifier.mainserver.impl;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.SpeedAcceleration;
import at.fhv.tmd.featureCalculation.FeatureCalculationException;
import at.fhv.tmd.featureCalculation.FeatureCalculator;
import at.fhv.tmd.featureCalculation.FeatureContext;
import at.fhv.tmd.featureCalculation.FeatureInputConstants;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.CoordinateUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 08.05.2017.
 */
public class SpeedCalculatorService implements FeatureCalculator {


  @Override
  public void calcFeature(FeatureContext featureContext) throws FeatureCalculationException {
    List<IGpsPoint> gpsPoints = featureContext
        .getInput(FeatureInputConstants.continousInterpolatedGpsList);
    CoordinateInterpolator coordinateInterpolator = featureContext
        .getInput(FeatureInputConstants.coordinateInterpolator);

    List<Speed> speeds = new ArrayList<>(gpsPoints.size());
    List<SpeedAcceleration> speedAccelerations = new ArrayList<>(gpsPoints.size());

    IGpsPoint lastPoint = null;
    for (IGpsPoint iGpsPoint : gpsPoints) {
      LocalDateTime time = iGpsPoint.getTime();

      Speed speed = coordinateInterpolator.calcSpeedAt(time);
      SpeedAcceleration speedAcceleration = coordinateInterpolator.calcSpeedAcceleration(time);

      speeds.add(speed);
      if(speedAcceleration.getMs_per_s()>0){
        speedAccelerations.add(speedAcceleration);
      }

      if(lastPoint != null){
        Speed distance = CoordinateUtil.calcSpeedBetween(lastPoint, iGpsPoint);
        double kmPerHour = speed.getKmPerHour();
        double kmPerHour1 = distance.getKmPerHour();
        if (Math.abs(kmPerHour-kmPerHour1)>30) {
          int b =4;
        }
      }


      lastPoint = iGpsPoint;
    }

    Speed averageSpeed = calcAverageSpeed(speeds,featureContext);
    calcSpeedVariance(speeds,averageSpeed,featureContext);
    calcMedianSpeedAnd96PercentilSpeed(speeds,featureContext);

    calcAverageSpeedAcceleration(speedAccelerations,featureContext);
    calcMedianSpeedAcceleration(speedAccelerations,featureContext);
    calcDurationAndLength(gpsPoints,featureContext);
  }

  private void calcDurationAndLength(List<IGpsPoint> gpsPoints,FeatureContext context) {

    int size = gpsPoints.size();

    IGpsPoint firstPoint = gpsPoints.get(0);
    IGpsPoint lastPoint = gpsPoints.get(size - 1);

    Duration duration = Duration.between(firstPoint.getTime(), lastPoint.getTime());

    double distanceSumKm = 0;
    Iterator<IGpsPoint> iterator = gpsPoints.iterator();
    IGpsPoint previousPoint = iterator.next();
    while (iterator.hasNext()) {
      IGpsPoint currentPoint = iterator.next();
      Distance distance = CoordinateUtil.haversineDistance(previousPoint, currentPoint);
      distanceSumKm += distance.getKm();
      previousPoint = currentPoint;
    }

    long totalSeconds = duration.toMillis() / 1000;

    context.addFeature(FeatureConstants.durationInSeconds,totalSeconds);
    context.addFeature(FeatureConstants.distanceInKm,distanceSumKm);

  }


  private void calcMedianSpeedAcceleration(List<SpeedAcceleration> speedAccelerations,
      FeatureContext featureContext) {
    speedAccelerations = new ArrayList<>(speedAccelerations);
    speedAccelerations.sort((o1, o2) -> Double.compare(o1.getMs_per_s(),o2.getMs_per_s()));
    int size = speedAccelerations.size();
    if(size == 0){
      SpeedAcceleration acceleration = new SpeedAcceleration(0);
      featureContext.addFeature(FeatureConstants.medianSpeedAcceleration,acceleration.getMs_per_s());

    }else{
      int medianIndex = size/2;
      SpeedAcceleration median = speedAccelerations.get(medianIndex);
      featureContext.addFeature(FeatureConstants.medianSpeedAcceleration,median.getMs_per_s());
    }

  }

  private SpeedAcceleration calcAverageSpeedAcceleration(List<SpeedAcceleration> speedAccelerations,
      FeatureContext featureContext) {

    if(speedAccelerations.size() == 0){
      SpeedAcceleration acceleration = new SpeedAcceleration(0);
      featureContext.addFeature(FeatureConstants.averageSpeedAcceleration,acceleration.getMs_per_s());
      return acceleration;
    }

    double speedAccelerationSum = 0;
    for (SpeedAcceleration speedAcceleration : speedAccelerations) {

      speedAccelerationSum+= speedAcceleration.getMs_per_s();
    }
    double averageSpeedAcceleration = speedAccelerationSum / (speedAccelerations.size()*1.0);
    SpeedAcceleration acceleration = new SpeedAcceleration(averageSpeedAcceleration);

    featureContext.addFeature(FeatureConstants.averageSpeedAcceleration,averageSpeedAcceleration);

    return acceleration;
  }


  private Speed calcAverageSpeed(List<Speed> speeds,FeatureContext context){

    double speedSum = 0;
    for (Speed speed : speeds) {
      speedSum += speed.getKmPerHour();
    }
    Speed averageSpeed = new Speed(speedSum / speeds.size());
    context.addFeature(FeatureConstants.averageSpeed,averageSpeed.getKmPerHour());
    return averageSpeed;
  }


  private void calcSpeedVariance(List<Speed> speeds, Speed averageSpeed,FeatureContext context){
    double squaredDistanceSum = 0;
    double _80PercentSquaredDistanceSum = 0;

    speeds = new ArrayList<>(speeds);
    speeds.sort((o1, o2) -> Double.compare(o1.getKmPerHour(),o2.getKmPerHour()));


    int _80PercentIndex = (int) (speeds.size()*0.8);
    int index=0;
    for (Speed speed : speeds) {

      double squaredDistance = Math.pow(speed.getKmPerHour() - averageSpeed.getKmPerHour(), 2);
      squaredDistanceSum +=squaredDistance;

      if(index<_80PercentIndex){
        _80PercentSquaredDistanceSum+=squaredDistance;
      }

      index++;

    }
    double speedVarianceInKmH = squaredDistanceSum / speeds.size();
    double _80PercentSpeedVarianceInKmH = _80PercentSquaredDistanceSum / _80PercentIndex;
    context.addFeature(FeatureConstants.speed_variance,speedVarianceInKmH);
    context.addFeature(FeatureConstants._80_percent_speed_variance,_80PercentSpeedVarianceInKmH);
  }


  private void calcMedianSpeedAnd96PercentilSpeed(List<Speed> speeds,FeatureContext context){
    ArrayList<Speed> speedCopy = new ArrayList(speeds);
    speedCopy.sort((o1, o2) -> Double.compare(o1.getKmPerHour(),o2.getKmPerHour()) );
    int size = speedCopy.size();
    int middleIndex = size/2;
    Speed medianSpeed = speedCopy.get(middleIndex);
    context.addFeature(FeatureConstants.medianSpeed,medianSpeed.getKmPerHour());

    int _95percentilIndex = (int) (size*0.95);
    Speed _95PercentilSpeed = speedCopy.get(_95percentilIndex);
    context.addFeature(FeatureConstants._95Percentil,_95PercentilSpeed.getKmPerHour());

  }

}
