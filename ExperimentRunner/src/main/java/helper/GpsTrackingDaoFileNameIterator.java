package helper;

import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Johannes on 21.06.2017.
 */
public class GpsTrackingDaoFileNameIterator implements Iterator<Tracking>{


  private final LeightweightTrackingDao leightweightTrackingDao;
  private int index = 0;
  private int size ;
  List<String> fileNames;

  private List<LeightweightTracking> goodOnes = new ArrayList<>();


  public GpsTrackingDaoFileNameIterator(LeightweightTrackingDao leightweightTrackingDao,List<String> filenames){
    this.leightweightTrackingDao = leightweightTrackingDao;
    List<LeightweightTracking> all = leightweightTrackingDao.getAll();
    size = all.size();
    this.fileNames = filenames;
    for (LeightweightTracking leightweightTracking : all) {
      String fileName = getFileName(leightweightTracking.getTrackingInfos());
      if(fileName!= null){
        boolean contains = filenames.contains(fileName);
        if(contains){
          goodOnes.add(leightweightTracking);
        }

      }
    }
    size = goodOnes.size();

  }

  private String getFileName(List<TrackingInfo> trackingInfos){
    for (TrackingInfo trackingInfo : trackingInfos) {
      if (trackingInfo.getInfoName().equals(Constants.FILENAME)) {
        return trackingInfo.getInfoValue();
      }
    }

    return null;

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
    LeightweightTracking leightweightTracking = goodOnes.get(index++);
    Tracking gpsTracking = leightweightTrackingDao.getGpsTracking(leightweightTracking);
    return gpsTracking;
  }
}
