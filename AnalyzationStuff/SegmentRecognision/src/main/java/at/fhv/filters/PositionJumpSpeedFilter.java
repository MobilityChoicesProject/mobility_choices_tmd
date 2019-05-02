package at.fhv.filters;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceImp;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 17.04.2017.
 */
public class PositionJumpSpeedFilter  {

  private Speed speedThreshold;

  public PositionJumpSpeedFilter(
      ConfigService configService) {
    this.configService = configService;
  }

  ConfigService configService;

  protected Speed getSpeedThreshold() {
    return speedThreshold;
  }

  public List<IExtendedGpsPoint> filterGpsPoints(List<IExtendedGpsPoint> gpsPointList) {
    List<IGpsPoint> coordinates = (List<IGpsPoint>)(List<?>) gpsPointList;
    List<IGpsPoint> filter = filter(coordinates);
    return (List<IExtendedGpsPoint>) (List<?>)filter;
  }

  private void updateConfigSettings() {
    double kmH = configService.getValue(ConfigServiceDefaultCache.PositionJumpSpeedThesholdInKmH);
    speedThreshold= new Speed(kmH);
  }


  public List<IGpsPoint> filter(List<IGpsPoint> coordinates){

    if (coordinates.size() < 10) {
      throw new IllegalArgumentException("list at least need 10 points");
    }
    updateConfigSettings();


    Iterator<IGpsPoint> iterator = coordinates.iterator();
      IGpsPoint current = iterator.next();

    // contains all the gps segments which are seperated by a jump point
    List<List<IGpsPoint>> gpsSegments = new LinkedList<>();
    List<IGpsPoint> lastList = new LinkedList<>();
    gpsSegments.add(lastList);
    lastList.add(current);

    while (iterator.hasNext()){
      IGpsPoint next = iterator.next();

      if (isPositionJump(current,next)) {
        lastList = new LinkedList<>();
        gpsSegments.add(lastList);
      }

      lastList.add(next);
      current = next;
    }

    int indexCounter= 0;
    int indexOfBiggestList  = 0;
    int biggestList =0;
    for (List<IGpsPoint> gpsSegment : gpsSegments) {
      int size = gpsSegment.size();
      if(biggestList < size){
        biggestList = size;
        indexOfBiggestList = indexCounter;
      }

      indexCounter++;
    }


      IGpsPoint mostLeftPoint = gpsSegments.get(indexOfBiggestList).get(0);
    LinkedList<IGpsPoint> filteredList = new LinkedList<>(gpsSegments.get(indexOfBiggestList));


    for(int i = indexOfBiggestList -1; i >=0 ; i-- ){

      List<IGpsPoint> gpsSegment = gpsSegments.get(i);
      for(int j = gpsSegment.size()-1; j>=0;j--){
        IGpsPoint gpsPoint = gpsSegment.get(j);
        if(isPositionJump(mostLeftPoint,gpsPoint)){
          // drop gpsPoint
        }else{
           // take all of the gpspoints
            for(;j>=0;j--){
                gpsPoint = gpsSegment.get(j);
                filteredList.add(0,gpsPoint);
                mostLeftPoint = gpsPoint;
            }
        }

      }

    }

      IGpsPoint mostRightPoint = gpsSegments.get(indexOfBiggestList).get(gpsSegments.get(indexOfBiggestList).size()-1);
    for(int i = indexOfBiggestList +1; i < gpsSegments.size() ; i++ ) {

      List<IGpsPoint> gpsSegment = gpsSegments.get(i);
      for (int j = 0; j < gpsSegment.size(); j++) {
        IGpsPoint gpsPoint = gpsSegment.get(j);
        if (isPositionJump(mostRightPoint, gpsPoint)) {
          // drop gpsPoint
        } else {
          // take all of the gpspoints
          for (; j < gpsSegment.size(); j++) {
            gpsPoint = gpsSegment.get(j);
            filteredList.add(gpsPoint);
            mostRightPoint = gpsPoint;
          }
        }
      }
    }

    return filteredList;
  }


  private boolean isPositionJump(IGpsPoint gpsPoint1, IGpsPoint gpsPoint2) {
    Speed speed = CoordinateUtil.calcSpeedBetween(gpsPoint1, gpsPoint2);
    return speed.isFaster(getSpeedThreshold());
  }



}
