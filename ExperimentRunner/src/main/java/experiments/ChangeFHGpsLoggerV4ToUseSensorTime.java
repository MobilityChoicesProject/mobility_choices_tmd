package experiments;

import at.fhv.transportClassifier.common.BinaryCollectionSearcher;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportClassifier.dal.interfaces.TrackingRepository;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingSegment;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.GpsTrackingDaoIterator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 25.06.2017.
 */
public class ChangeFHGpsLoggerV4ToUseSensorTime {

  private TrackingRepository trackingRepository;

  public ChangeFHGpsLoggerV4ToUseSensorTime(
      TrackingRepository trackingRepository) {
    this.trackingRepository = trackingRepository;
  }

  public void doIt(LeightweightTrackingDao leightweightTrackingDao){

    BinaryCollectionSearcher<IExtendedGpsPoint,LocalDateTime> binaryCollectionSearcher = new BinaryCollectionSearcher<>();

    GpsTrackingDaoIterator gpsTrackingDaoIterator = new GpsTrackingDaoIterator(leightweightTrackingDao);

    int counterFileToChange =0;
    int counterFileToChange1 =0;
    int counter = 0;

    int fail = 0;
    int fail1 = 0;

    while (gpsTrackingDaoIterator.hasNext()) {
      Tracking tracking = gpsTrackingDaoIterator.next();


      if(checkIfNotSegmentedTimes(tracking)){
        counterFileToChange++;
      }

      // set endtime of segments to the starttime of the next segment
      TrackingSegmentBag latestTrackingSegmentBag1 = tracking.getLatestTrackingSegmentBag();
      if(latestTrackingSegmentBag1.getVersion()== 1){

        if(checkIfNotSegmentedTimes(tracking)){
          counterFileToChange1++;
        }


        TrackingSegment latestTrackingSegment=null;
        List<TrackingSegment> trackingSegments = new ArrayList<>();
        boolean firstRound= true;

        for (TrackingSegment trackingSegment : latestTrackingSegmentBag1.getSegments()) {
          if(latestTrackingSegment== null){
            latestTrackingSegment = trackingSegment;
            continue;
          }
          SimpleTrackingSegment simpleTrackingSegment = new SimpleTrackingSegment();
          simpleTrackingSegment.setStartTime(latestTrackingSegment.getStartTime());
          simpleTrackingSegment.setEndTime(trackingSegment.getStartTime());
          simpleTrackingSegment.setTransportType(latestTrackingSegment.getTransportType());
          simpleTrackingSegment.setAllGpsPoints(tracking.getGpsPoints());
          trackingSegments.add(simpleTrackingSegment);

          latestTrackingSegment = trackingSegment;
        }

        SimpleTrackingSegment simpleTrackingSegment = new SimpleTrackingSegment();
        simpleTrackingSegment.setStartTime(latestTrackingSegment.getStartTime());
        simpleTrackingSegment.setEndTime(latestTrackingSegment.getEndTime());
        simpleTrackingSegment.setTransportType(latestTrackingSegment.getTransportType());
        simpleTrackingSegment.setAllGpsPoints(tracking.getGpsPoints());
        trackingSegments.add(simpleTrackingSegment);

        tracking.getTrackingSegmentBags().remove(latestTrackingSegmentBag1);
        SimpleTrackingSegmentBag trackingSegmentBag  = new SimpleTrackingSegmentBag();
        tracking.getTrackingSegmentBags().add(trackingSegmentBag);
        trackingSegmentBag.setVersion(1);
        trackingSegmentBag.addSegments(trackingSegments);

        if(checkIfNotSegmentedTimes(tracking)){
          int debug= 4;
        }
//        trackingRepository.update(tracking);
      }

      boolean isV4Version = tracking.hasTrackingInfo(Constants.FH_GPS_LOGGER_VERSION_4);

      if(isV4Version){

        TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();
        if(latestTrackingSegmentBag.getVersion() == 1){


          List<TrackingSegment> trackingSegments = new ArrayList<>();

          for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
            LocalDateTime startTime = trackingSegment.getStartTime();
            LocalDateTime endTimestamp = tracking.getEndTimestamp();

            int index1 = binaryCollectionSearcher.find(tracking.getGpsPoints(), startTime,
                (item, localDateTime) -> item.getDeviceSavingSystemTime().compareTo(localDateTime));

            int index2 = binaryCollectionSearcher.find(tracking.getGpsPoints(), startTime,
                (item, localDateTime) -> item.getDeviceSavingSystemTime().compareTo(endTimestamp));


            if(index1<0 || index2 <0){
              fail++;
            }

            IExtendedGpsPoint iExtendedGpsPoint1 = tracking.getGpsPoints().get(index1);
            IExtendedGpsPoint iExtendedGpsPoint2 = tracking.getGpsPoints().get(index2);

            SimpleTrackingSegment simpleTrackingSegment = new SimpleTrackingSegment();
            simpleTrackingSegment.setStartTime(iExtendedGpsPoint1.getSensorTime());
            simpleTrackingSegment.setStartTime(iExtendedGpsPoint2.getSensorTime());
            simpleTrackingSegment.setTransportType(trackingSegment.getTransportType());
            simpleTrackingSegment.setAllGpsPoints(tracking.getGpsPoints());
            trackingSegments.add(simpleTrackingSegment);



          }

          tracking.getTrackingSegmentBags().remove(latestTrackingSegmentBag1);
          SimpleTrackingSegmentBag trackingSegmentBag  = new SimpleTrackingSegmentBag();
          tracking.getTrackingSegmentBags().add(trackingSegmentBag);
          trackingSegmentBag.setVersion(1);
          trackingSegmentBag.addSegments(trackingSegments);

//          trackingRepository.update(tracking);
          boolean b = checkInSequentielOrder(tracking);
          boolean b1 = checkIfNotSegmentedTimes(tracking);
          if(!b || b1){
            fail1++;
          }

        }

      }
      System.out.println(counter++);
    }



  System.out.println("files to change: "+counterFileToChange);
  System.out.println("files to change1: "+counterFileToChange1);
  System.out.println("fail: "+fail);
  System.out.println("fail: "+fail1);

  }

  private boolean checkIfNotSegmentedTimes(Tracking tracking) {
    TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();

    TrackingSegment latestTrackingSegment= null;
    for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
      if(latestTrackingSegment!= null){
        if(!trackingSegment.getStartTime().isEqual(latestTrackingSegment.getEndTime())){
          return true;
        }
      }

      if(tracking.getStartTimestamp()== null || tracking.getEndTimestamp()== null){
        int debug = 3;
      }

      latestTrackingSegment = trackingSegment;
    }
    return false;

  }


  private boolean checkInSequentielOrder(Tracking tracking){
    TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();

    LocalDateTime lastStartTime= null;
    LocalDateTime lastEndTime = null;
    for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {

      LocalDateTime startTime = trackingSegment.getStartTime();
      LocalDateTime endTime = trackingSegment.getEndTime();

      if(startTime.isAfter(endTime) || startTime.isAfter(endTime)){
        return false;
      }

      if(startTime.isBefore(lastEndTime)){
        return false;
      }
      lastEndTime= endTime;

    }

      return true;
  }

}
