package helper;

import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Johannes on 21.06.2017.
 */
public class GpsTrackingDaoIterator implements Iterator<Tracking>{


  private final LeightweightTrackingDao leightweightTrackingDao;
  private List<LeightweightTracking> all;
  private int index = 0;
  private int size ;

  public GpsTrackingDaoIterator(LeightweightTrackingDao leightweightTrackingDao){
    this.leightweightTrackingDao = leightweightTrackingDao;
    all = leightweightTrackingDao.getAll();
    size = all.size();
  }

  @Override
  public boolean hasNext() {

    return index< size;
  }

  @Override
  public Tracking next() {
    if(!hasNext()){
      throw new NoSuchElementException();
    }
    LeightweightTracking leightweightTracking = all.get(index++);
    Tracking gpsTracking = leightweightTrackingDao.getGpsTracking(leightweightTracking);
    return gpsTracking;
  }
}
