package at.fhv.tmd.featureCalculation;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import javax.persistence.EntityManager;

/**
 * Created by Johannes on 23.07.2017.
 */
public interface RailwayClosestDistanceCalculator {

  void updateEntityManager(EntityManager entityManager);

  public Distance calcDistance(IGpsPoint point);

}
