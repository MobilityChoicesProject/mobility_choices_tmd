package at.fhv.filters;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 02.05.2017.
 */
public class SamePositionWorseAccuracyFilter {

  public List<IExtendedGpsPoint> filterGpsPoints(List<IExtendedGpsPoint> gpsPoints){

    List<IGpsPoint> coordinates = (List<IGpsPoint>)(List<?>) gpsPoints;
    List<IGpsPoint> filter = filter(coordinates);
    return (List<IExtendedGpsPoint>) (List<?>)filter;

  }


  public List<IGpsPoint> filter(List<IGpsPoint> gpsPoints){

    List<IGpsPoint> points = new ArrayList<>();

    Iterator<IGpsPoint> iterator = gpsPoints.iterator();

    IGpsPoint current = iterator.next();

    while (iterator.hasNext()) {
      IGpsPoint next = iterator.next();

      boolean samePositionFlag = samePosition(current, next);
      if (samePositionFlag) {
        if (current.getAccuracy() < next.getAccuracy()) {
          continue;
        }
      }
      points.add(next);
      current = next;
    }
    return points;

  }

  private boolean samePosition(IGpsPoint gpsPointA, IGpsPoint gpsPointB){
    double latDiff = gpsPointA.getLatitude() - gpsPointB.getLatitude();
    double longDiff = gpsPointA.getLongitude() - gpsPointB.getLongitude();

    latDiff = Math.abs(latDiff);
    longDiff = Math.abs(longDiff);

    return latDiff < 0.000001 && longDiff <0.000001;

  }








}
