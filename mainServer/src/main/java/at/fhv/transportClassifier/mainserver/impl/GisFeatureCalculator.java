package at.fhv.transportClassifier.mainserver.impl;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.FeatureCalculationException;
import at.fhv.tmd.featureCalculation.FeatureCalculator;
import at.fhv.tmd.featureCalculation.FeatureContext;
import at.fhv.tmd.featureCalculation.FeatureInputConstants;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import at.fhv.transportClassifier.mainserver.api.AverageDistanceType;
import at.fhv.transportClassifier.mainserver.api.EndPointType;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * Created by Johannes on 20.06.2017.
 */
public class GisFeatureCalculator implements FeatureCalculator {

  GisQuerierService gisQuerierService = new GisQuerierService();



  private EntityManager entityManager;
  private ITransaction transaction;

  public void init(EntityManager entityManager, ITransaction transaction){
    this.entityManager = entityManager;
    this.transaction = transaction;
  }


  @Override
  public void calcFeature(FeatureContext featureContext) throws FeatureCalculationException {

    try {
      transaction.beginn();
      List<IGpsPoint> gpsPoints = featureContext.getInput(FeatureInputConstants.continousInterpolatedGpsList);

      Distance averageBusRouteDistance = gisQuerierService
          .calcAverageDistanceToPath(entityManager, gpsPoints, AverageDistanceType.BusRoute);

      Distance averageRailRouteDistance = gisQuerierService
          .calcAverageDistanceToPath(entityManager, gpsPoints, AverageDistanceType.RailRoute);

      Distance endBusStationDistance = gisQuerierService
          .calcEndPointAverageDistance(entityManager, gpsPoints, EndPointType.BusStation);

      Distance endRailwayStationDistance = gisQuerierService
          .calcEndPointAverageDistance(entityManager, gpsPoints, EndPointType.RailwayStation);


      featureContext.addFeature(FeatureConstants.averageBusRouteDistance,averageBusRouteDistance.getKm());
      featureContext.addFeature(FeatureConstants.averageRailRouteDistance,averageRailRouteDistance.getKm());
      featureContext.addFeature(FeatureConstants.averageBusEndPointDistance,endBusStationDistance.getKm());
      featureContext.addFeature(FeatureConstants.averageTrainEndPointDistance,endRailwayStationDistance.getKm());

      transaction.commit();

    } catch (TransactionException e) {
      throw new FeatureCalculationException(e);
    }
  }
}
