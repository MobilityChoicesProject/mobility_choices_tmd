package at.fhv.transportClassifier.mainserver.impl;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.ICoordinate;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.RailwayClosestDistanceCalculator;
import at.fhv.transportClassifier.mainserver.api.AverageDistanceType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by Johannes on 23.07.2017.
 */
public class RailwayClosestDistanceCalculatorImp implements RailwayClosestDistanceCalculator {


  EntityManager entityManager;

  public RailwayClosestDistanceCalculatorImp() {
  }

  @Override
  public void updateEntityManager(EntityManager entityManager){
    this.entityManager = entityManager;
  }

  @Override
  public Distance calcDistance(IGpsPoint point) {

    Distance distance = calcDistance(entityManager, point, AverageDistanceType.RailRoute);

    return distance;
  }


  protected Distance calcDistance(EntityManager entityManager,ICoordinate coordinate,AverageDistanceType endPointType){
    Query nativeQuery = entityManager
        .createNativeQuery("call calcDistanceToOnePoint (:lat, :lng,:pointType)");
    nativeQuery.setParameter("lat",coordinate.getLatitude());
    nativeQuery.setParameter("lng",coordinate.getLongitude());
    nativeQuery.setParameter("pointType",endPointType.getValue());
    List<Object[]> resultList = nativeQuery.getResultList();
    Object distanceObj = resultList.get(0);
    double distance  = (Double) distanceObj;
    return new Distance(distance/1000);

  }

}
