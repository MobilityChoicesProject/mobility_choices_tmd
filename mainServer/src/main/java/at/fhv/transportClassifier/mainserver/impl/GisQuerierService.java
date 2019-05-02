package at.fhv.transportClassifier.mainserver.impl;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.ICoordinate;
import at.fhv.transportClassifier.mainserver.api.AverageDistanceType;
import at.fhv.transportClassifier.mainserver.api.EndPointType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 19.06.2017.
 */
public class GisQuerierService {
  private static Logger logger = LoggerFactory.getLogger(GisQuerierService.class);



  public Distance calcEndPointAverageDistance(EntityManager entityManager,List<? extends ICoordinate> coordinates,
      EndPointType endPointType) {
    logger.debug("start EndpointAverageDistance calculation for '{}'",endPointType.name());
    int size = coordinates.size();
    if (size < 2) {
      logger.warn("coordinateList is too short:'{}' min=2", size);
      throw new IllegalArgumentException("coordinatelList is to short");
    }

    ICoordinate firstPoint = coordinates.get(0);
    ICoordinate lastPoint = coordinates.get(size - 1);


    Distance firstPointDistance=null;
    Distance lastPointDistance= null;
    try{
      firstPointDistance = calcDistance(entityManager,firstPoint, endPointType);
      lastPointDistance = calcDistance(entityManager,lastPoint, endPointType);
    }catch (Exception ex ){
      logger.warn("exception",ex);
      throw ex;
    }

    Distance averageDistance = new Distance((firstPointDistance.getKm() + lastPointDistance.getKm())/2);
    logger.debug("average distance:{}m for '{}'", averageDistance.getMeter(),endPointType.name());

    return averageDistance;
  }



  public Distance calcAverageDistanceToPath(EntityManager entityManager,List<? extends ICoordinate> coordinates,AverageDistanceType averageDistanceType){

    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
    otherSymbols.setDecimalSeparator('.');
    DecimalFormat df = new DecimalFormat("000.000000",otherSymbols);

    int size = coordinates.size();

    double distanceSum = 0;

    String querryString ="call calcAverageDistance(:averageDistanceType , :points)";
    StringBuilder stringBuilder = new StringBuilder(20* size);
    Iterator<? extends ICoordinate> iterator = coordinates.iterator();
    int counter = 0;
    while(iterator.hasNext()) {
      ICoordinate coordinate = iterator.next();
      double latitude = coordinate.getLatitude();
      double longitude = coordinate.getLongitude();

      if(Math.abs(latitude)>90){
        throw new IllegalArgumentException("latitude cant be bigger than 90");
      }
      if(Math.abs(longitude)>180){
        throw new IllegalArgumentException("longitude cant be bigger than 90");
      }

      stringBuilder.append(df.format(latitude));
      stringBuilder.append(df.format(longitude));
      counter++;

      if(counter>=800 || iterator.hasNext()== false){

        Query nativeQuery = entityManager.createNativeQuery(querryString);
        nativeQuery.setParameter("averageDistanceType",averageDistanceType.getValue());
        String s = stringBuilder.toString();
        nativeQuery.setParameter("points",s);

        List<Object[]> resultList = nativeQuery.getResultList();
        Object partdistanceSum = resultList.get(0)[1];
        double partdistanceInSumMeter = (Double) partdistanceSum;

        distanceSum+=partdistanceInSumMeter;

        counter=0;
        stringBuilder = new StringBuilder();


      }

    }

    double averageDistanceMeter = distanceSum/size;


    Distance distance = new Distance(averageDistanceMeter / 1000);
    logger.debug("average distance:{}m", averageDistanceMeter);
    return distance;
  }


  protected Distance calcDistance(EntityManager entityManager,ICoordinate coordinate,EndPointType endPointType){
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
