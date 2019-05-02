package at.fhv.filters;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 18.04.2017.
 * Removes gps points which timestamp is not in the chronological order: If there are gps point X and Y. First X and then Y. If now Y has a timestamp which is before the timestamp of X, Y is removed from the list.
 */
public class WrongTimeGpsFilter  {

  public List<IExtendedGpsPoint> filterGpsPoints(List<IExtendedGpsPoint> gpsPointList) {
    List<IGpsPoint> coordinates = (List<IGpsPoint>)(List<?>) gpsPointList;
    List<IGpsPoint> filter = filter(coordinates);
    return (List<IExtendedGpsPoint>) (List<?>)filter;
  }

    public List<IGpsPoint> filter(List<IGpsPoint> coordinates){

    Iterator<IGpsPoint> iterator = coordinates.iterator();

      IGpsPoint last = iterator.next();
    while (iterator.hasNext()){

      IGpsPoint next = iterator.next();
      boolean nextIsAfter = next.getTime().isAfter(last.getTime())  ||  next.getTime().isEqual(last.getTime());
      if (nextIsAfter) {
        last = next;
      } else {
        iterator.remove();
      }

    }

    return coordinates;
  }




}
