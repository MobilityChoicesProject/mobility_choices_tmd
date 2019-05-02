package at.fhv.filters;

import at.fhv.tmd.common.Speed;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 17.04.2017.
 */
public class PositionJumpAccelerationFilter  {


  public List<IExtendedGpsPoint> filter(List<IExtendedGpsPoint> gpsPointList){

    checkSize(gpsPointList);

    Iterator<IExtendedGpsPoint> iterator = gpsPointList.iterator();
    IExtendedGpsPoint last = iterator.next();
    IExtendedGpsPoint current = iterator.next();

    // contains all the gps segments which are seperated by a jump point
    List<List<IExtendedGpsPoint>> gpsSegments = new LinkedList<>();
    List<IExtendedGpsPoint> lastList = new LinkedList<>();
    gpsSegments.add(lastList);
    lastList.add(current);

    while (iterator.hasNext()){
      IExtendedGpsPoint next = iterator.next();

      if (isPositionJump(last,current,next)) {
        lastList = new LinkedList<>();
        gpsSegments.add(lastList);
      }

      lastList.add(next);
      last =current;
      current = next;
    }

    int indexCounter= 0;
    int indexOfBiggestList  = 0;
    int biggestList =0;
    for (List<IExtendedGpsPoint> gpsSegment : gpsSegments) {
      int size = gpsSegment.size();
      if(biggestList < size){
        biggestList = size;
        indexOfBiggestList = indexCounter;
      }

      indexCounter++;
    }


    IExtendedGpsPoint secondMostLeftPoint = gpsSegments.get(indexOfBiggestList).get(1);
    IExtendedGpsPoint mostLeftPoint = gpsSegments.get(indexOfBiggestList).get(0);
    LinkedList<IExtendedGpsPoint> filteredList = new LinkedList<>(gpsSegments.get(indexOfBiggestList));

    for(int i = indexOfBiggestList -1; i >=0 ; i-- ){

      List<IExtendedGpsPoint> gpsSegment = gpsSegments.get(i);
      for(int j = gpsSegment.size()-1; j>=0;j--){
        IExtendedGpsPoint gpsPoint = gpsSegment.get(j);

        if(isPositionJump(secondMostLeftPoint,mostLeftPoint,gpsPoint)){
          // drop gpsPoint
        }else{
           // take all of the gpspoints
            for(;j>=0;j--){
                gpsPoint = gpsSegment.get(j);
                filteredList.add(0,gpsPoint);


            }

          if(gpsSegment.size()>2){
            secondMostLeftPoint  = gpsSegment.get(1);
          }
          mostLeftPoint = gpsPoint;

        }

      }

    }


    IExtendedGpsPoint secondMostRightPoint = gpsSegments.get(indexOfBiggestList).get(gpsSegments.get(indexOfBiggestList).size()-2);
    IExtendedGpsPoint mostRightPoint = gpsSegments.get(indexOfBiggestList).get(gpsSegments.get(indexOfBiggestList).size()-1);
    for(int i = indexOfBiggestList +1; i < gpsSegments.size() ; i++ ) {

      List<IExtendedGpsPoint> gpsSegment = gpsSegments.get(i);
      for (int j = 0; j < gpsSegment.size(); j++) {
        IExtendedGpsPoint gpsPoint = gpsSegment.get(j);
        if (isPositionJump(secondMostRightPoint,mostRightPoint, gpsPoint)) {
          // drop gpsPoint
        } else {
          // take all of the gpspoints
          for (; j < gpsSegment.size(); j++) {
            gpsPoint = gpsSegment.get(j);
            filteredList.add(gpsPoint);
            mostRightPoint = gpsPoint;
          }
          if(gpsSegment.size()>2){
            secondMostRightPoint=gpsSegment.get(gpsSegment.size()-2);
          }
        }
      }
    }

    return filteredList;

  }

  protected void checkSize(List<IExtendedGpsPoint> gpsPointList) {
    if (gpsPointList.size() < 10) {
      throw new IllegalArgumentException("list at least need 10 points");
    }
  }


  private boolean isPositionJump(IExtendedGpsPoint past,IExtendedGpsPoint current, IExtendedGpsPoint next){

    Speed speed1 = CoordinateUtil.calcSpeedBetween1(past, current);
    Speed speed2 = CoordinateUtil.calcSpeedBetween1(current, next);

    Speed speedDiff = new Speed(Math.abs(speed1.getKmPerHour() - speed2.getKmPerHour()));
    LocalDateTime mostAccurateTime = current.getMostAccurateTime();
    LocalDateTime mostAccurateTime1 = next.getMostAccurateTime();
    Duration between = Duration.between(mostAccurateTime, mostAccurateTime1);
    double durationInSeconds = between.toMillis() / 1000.0;
    double kmh_s = speedDiff.getKmPerHour() / durationInSeconds;

    return kmh_s > 30;


  }



}
