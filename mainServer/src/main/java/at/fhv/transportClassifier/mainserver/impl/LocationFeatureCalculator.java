package at.fhv.transportClassifier.mainserver.impl;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.FeatureCalculationException;
import at.fhv.tmd.featureCalculation.FeatureCalculator;
import at.fhv.tmd.featureCalculation.FeatureContext;
import at.fhv.tmd.featureCalculation.FeatureInputConstants;
import at.fhv.transportClassifier.common.CoordinateUtil;
import java.util.List;

/**
 * Created by Johannes on 28.06.2017.
 */
public class LocationFeatureCalculator implements FeatureCalculator {


  private int numberOfPoints=30;

  @Override
  public void calcFeature(FeatureContext featureContext) throws FeatureCalculationException {
    List<IGpsPoint> gpsPoints = featureContext
        .getInput(FeatureInputConstants.continousInterpolatedGpsList);

    int size = gpsPoints.size();


    Distance averDistanceSum = new Distance(0);

    for(int i = 0; i < size;i++){

      IGpsPoint gpsPoint = gpsPoints.get(i);

      Distance distanceSum = new Distance(0);
      int numberOfUsedPoints = 0;
      for(int j = i-1; j>= 0 && j >=(i-numberOfPoints);j--){
        numberOfUsedPoints++;
        IGpsPoint previousGpsPoint = gpsPoints.get(j);
        Distance distance = CoordinateUtil.haversineDistance(previousGpsPoint, gpsPoint);
        distanceSum = distanceSum.plus(distance);
      }

      for(int j = i+1; j<size && j <=(i+numberOfPoints);j++){
        numberOfUsedPoints++;
        IGpsPoint nextGpsPoint = gpsPoints.get(j);
        Distance distance = CoordinateUtil.haversineDistance(nextGpsPoint, gpsPoint);
        distanceSum = distanceSum.plus(distance);
      }

      Distance averageDistance = new Distance(distanceSum.getKm()/numberOfUsedPoints);
      averDistanceSum = averDistanceSum.plus(averageDistance);

    }

    double averageDistanceToPreviousAndNextPoints = averDistanceSum.getKm() / size;

    featureContext.addFeature(FeatureConstants.averageDistanceToClosePoints,averageDistanceToPreviousAndNextPoints);
  }
}
