package experiments;

import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.GpsTrackingDaoIterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Johannes on 22.06.2017.
 */
public class DataSplitterExperiment {



  private LeightweightTrackingDao leightweightTrackingDao;

  public DataSplitterExperiment(
      LeightweightTrackingDao leightweightTrackingDao) {
    this.leightweightTrackingDao = leightweightTrackingDao;
  }


  public void doit(){

    int allTrackings = 0;
    int allTrackingsv1=0;

    int trackigsWithMoreThanOneTransportType =0;
    int trackingsWithOneTransportType=0;

    int trackigsWithMoreThanOneTransportTypev1 =0;
    int trackingsWithOneTransportTypev1=0;

    int allSegmentsCounter=0;
    int allSegmentsCounterv1=0;

    int manuallyEditedCounter= 0;
    int version1TrackingCounter= 0;

    GpsTrackingDaoIterator gpsTrackingDaoIterator = new GpsTrackingDaoIterator(leightweightTrackingDao);

    while (gpsTrackingDaoIterator.hasNext()) {

      allTrackings++;
      Tracking next = gpsTrackingDaoIterator.next();

      SameSequelTransportModeMergeFilter sameSequelTransportModeMergeFilter = new SameSequelTransportModeMergeFilter();
      next = sameSequelTransportModeMergeFilter.filter(next);

      TrackingSegmentBag latestTrackingSegmentBag = next.getLatestTrackingSegmentBag();
      boolean manuallyEdited = isManuallyEdited(next.getTrackingInfos());
      if(manuallyEdited){
        manuallyEditedCounter++;
      }
      boolean hasVersion1 = latestTrackingSegmentBag.getVersion() == 1;
      if(hasVersion1){
        version1TrackingCounter++;
      }
      if(hasVersion1 ||manuallyEdited ){



        allTrackingsv1++;
        TreeSet<TransportType> transportTypes = new TreeSet<>();
        boolean otherInlcuded=  false;
        for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
          transportTypes.add(trackingSegment.getTransportType());
          if(trackingSegment.getTransportType() == TransportType.OTHER){
            otherInlcuded = true;
          }
          allSegmentsCounterv1++;
        }
        int size = transportTypes.size();
        int numberOfTypesWithoutOther = size;
        if(otherInlcuded){
          numberOfTypesWithoutOther--;
        }

        if(numberOfTypesWithoutOther >1){
          trackigsWithMoreThanOneTransportTypev1++;
        }else{
          trackingsWithOneTransportTypev1++;
        }


      }


      TreeSet<TransportType> transportTypes = new TreeSet<>();
      boolean otherInlcuded=  false;
      for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
        transportTypes.add(trackingSegment.getTransportType());
        if(trackingSegment.getTransportType() == TransportType.OTHER){
          otherInlcuded = true;
        }
        allSegmentsCounter++;
        switch (trackingSegment.getTransportType()){
          case BUS:{

            break;
          }
          case CAR:{
            break;
          }
          case BIKE:{
            break;
          }case WALK:{
            break;
          }case OTHER:{
            break;
          }case TRAIN:{

          }
        }

      }
      int size = transportTypes.size();
      int numberOfTypesWithoutOther = size;
      if(otherInlcuded){
        numberOfTypesWithoutOther--;
      }

      if(numberOfTypesWithoutOther >1){
        trackigsWithMoreThanOneTransportType++;
      }else{
        trackingsWithOneTransportType++;
      }


    }




    System.out.println("trackings with more than one transportType except other: " + trackigsWithMoreThanOneTransportType);
    System.out.println("trackings with one or less transportType except other: " + trackingsWithOneTransportType);

    System.out.println("trackings v1 with more than one transportType except other: " + trackigsWithMoreThanOneTransportTypev1);
    System.out.println("trackings v1 with one or less transportType except other: " + trackingsWithOneTransportTypev1);

    System.out.println("number of newest Segments " + allSegmentsCounter);
    System.out.println("number of neweset segments v1: " + allSegmentsCounterv1);

  }



  private boolean isManuallyEdited(List<TrackingInfo> trackingInfos){
    for (TrackingInfo trackingInfo : trackingInfos) {
      if(trackingInfo.getInfoName().equals(Constants.ManualyEdited)){
        return true;
      }
    }
    return false;

  }


}
