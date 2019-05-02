package helper.segmentation;

import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.segmentClassification.util.Helper;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.segmentsplitting.Segment;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportClassifier.segmentsplitting.SegmentationService;
import at.fhv.transportClassifier.segmentsplitting.SegmentationServiceImp;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import experiments.ClassificationEvaluationExperiment.SegmentationResultInfo;
import helper.Timespan;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 20.07.2017.
 */
public class SegmentationHelper {
  SegmentationService segmentationService = new SegmentationServiceImp();
  private ConfigService configService;


  public List<SegmentationResultInfo> calcSegmentsAndFeatureResults1(Tracking tracking,CoordinateInterpolator coordinateInterpolator){


    List<Segment> segmentedSegments = segmentationService
        .splitIntoSegments(coordinateInterpolator, tracking.getStartTimestamp(),
            tracking.getEndTimestamp());

    Iterator<Segment> iterator = segmentedSegments.iterator();

    List<SegmentationResultInfo> resultsList = new ArrayList<>();
    while (iterator.hasNext()) {
      Segment segment =  iterator.next();
      if(segment.getPreType() == SegmentPreType.NonClassifiable){
        continue;
      }
      SegmentationResultInfo segmentationResultInfo = new SegmentationResultInfo();
      segmentationResultInfo.setStartTime(segment.getStartTime());
      segmentationResultInfo.setEndTime(segment.getEndTime());
      segmentationResultInfo.setPreType(segment.getPreType());

      if (segment.getPreType() != SegmentPreType.WalkingSegment
          && segment.getPreType() != SegmentPreType.NonWalkingSegment   && segment.getPreType() != SegmentPreType.stationaryCluster) {

      } else {
        List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
            .getInterpolatedCoordinatesExact(segment.getStartTime(), segment.getEndTime(),
                Duration
                    .ofSeconds(1L));

        try{


          segmentationResultInfo.setFeatureResults(null);
          segmentationResultInfo.setGpsPoints(interpolatedCoordinatesExact);

        }catch ( Exception ex){
          System.out.println("Exception with tracking id: " +tracking.getId());
          ex.printStackTrace();
        }


      }
      resultsList.add(segmentationResultInfo);

    }

    // classify

    return resultsList;

  }


    public List<SegmentationResultInfo> calcSegmentsAndFeatureResults(Tracking tracking){

    SameSequelTransportModeMergeFilter sameSequelTransportModeMergeFilter = new SameSequelTransportModeMergeFilter();
    tracking = sameSequelTransportModeMergeFilter.filter(tracking);
    CoordinateInterpolator coordinateInterpolator = Helper.filterAndCreateCoordinateInterpolator(tracking,configService
        );

    return calcSegmentsAndFeatureResults1(tracking,coordinateInterpolator);



  }


  public TransportType getDominantTransportType(SegmentationResultInfo segmentationResultInfo,
      Tracking tracking,int version) {


    TrackingSegmentBag latestTrackingSegmentBag = null;

    if(version==-1){
      latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();
    }else{
      tracking.getTrackingSegmentBagWithVersion(version);
    }


    // if gps points before first segment, use first segment
    if(segmentationResultInfo.getStartTime().isBefore(latestTrackingSegmentBag.getSegments().get(0).getStartTime())){
      return latestTrackingSegmentBag.getSegments().get(0).getTransportType();
    }


    LocalDateTime startTime = segmentationResultInfo.getStartTime();
    LocalDateTime endTime = segmentationResultInfo.getEndTime();
    Timespan timespan1 = new Timespan(startTime,endTime);

    HashMap<TransportType,Duration> durationHashMap= new HashMap<>();

    for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
      Timespan timespan = new Timespan(trackingSegment.getStartTime(),trackingSegment.getEndTime());

      Duration overlappingDuration1 = timespan.getOverlappingDuration(timespan1);

      if(overlappingDuration1.isNegative()){
        int debug2= 3;
      }
      TransportType transportType = trackingSegment.getTransportType();
      Duration duration = durationHashMap.get(transportType);
      if(duration==null){
        duration = Duration.ofSeconds(0);
      }

      duration = duration.plus(overlappingDuration1);
      durationHashMap.put(transportType,duration);
    }

    Duration maxDuration = Duration.ofSeconds(0);
    TransportType maxOverlappingTransportType = null;
    for (TransportType transportType : durationHashMap.keySet()) {
      Duration duration = durationHashMap.get(transportType);
      if(maxDuration.compareTo(duration)<0){
        maxDuration = duration;
        maxOverlappingTransportType= transportType;
      }
    }

    if(maxOverlappingTransportType == null){
      int debug = 3;
    }
    return maxOverlappingTransportType;

  }



}
