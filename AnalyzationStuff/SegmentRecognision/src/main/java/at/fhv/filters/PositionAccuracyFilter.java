package at.fhv.filters;

import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 17.04.2017.
 */
public class PositionAccuracyFilter  {


  public List<IExtendedGpsPoint> filter(List<IExtendedGpsPoint> gpsPointList){




    if (gpsPointList.size() < 10) {
      throw new IllegalArgumentException("list at least need 10 points");
    }

    double accuracySum = 0;
    for (IExtendedGpsPoint gpsPoint : gpsPointList) {
      Double accuracy = gpsPoint.getAccuracy();
      accuracySum += accuracy;
    }

    double expectedAccuracy = accuracySum / gpsPointList.size();

    if(expectedAccuracy < 30){
      expectedAccuracy = 30;
    }

    Iterator<IExtendedGpsPoint> iterator = gpsPointList.iterator();

    List<IExtendedGpsPoint> points = new ArrayList<>();
    while (iterator.hasNext()){
      IExtendedGpsPoint next = iterator.next();
      if(next.getAccuracy()<expectedAccuracy){
        points.add(next);
      }
    }

  return points;
    }




}
