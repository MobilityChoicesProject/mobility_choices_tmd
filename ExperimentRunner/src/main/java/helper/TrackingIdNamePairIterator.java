package helper;

import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.Tracking;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Johannes on 26.06.2017.
 */
public class TrackingIdNamePairIterator implements Iterator<Tracking> {

  List<TrackingIdNamePair> trackingIdNamePairList;
  LeightweightTrackingDao leightweightTrackingDao;

  Iterator<TrackingIdNamePair> iterator =null;

  public TrackingIdNamePairIterator(List<TrackingIdNamePair> trackingIdNamePairList,
      LeightweightTrackingDao leightweightTrackingDao) {
    this.trackingIdNamePairList = trackingIdNamePairList;
    this.leightweightTrackingDao = leightweightTrackingDao;
    iterator = trackingIdNamePairList.iterator();
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public Tracking next() {
    if(!iterator.hasNext()){
      throw new NoSuchElementException("End of iterator reached");
    }

    TrackingIdNamePair next = iterator.next();
    Tracking gpsTracking = leightweightTrackingDao.getGpsTracking(next.getId());
    return gpsTracking;
  }


}
