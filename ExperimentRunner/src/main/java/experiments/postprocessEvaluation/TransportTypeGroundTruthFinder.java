package experiments.postprocessEvaluation;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.processFlow.TmdSegment;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.Timespan;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 07.08.2017.
 */
public class TransportTypeGroundTruthFinder {



  public TransportType getTransportType(TmdSegment tmdSegment, Tracking next,
      CoordinateInterpolator coordinateInterpolator){

    TransportTypeResult transportTypeWithLongestDuration = getTransportTypeWithLongestDuration(
        tmdSegment, next, coordinateInterpolator);

    if(transportTypeWithLongestDuration.isUnambigious()){
      return transportTypeWithLongestDuration.getTransportType();
    }else{
      return getTransportTypeInDistanceMiddle(tmdSegment,next,coordinateInterpolator);
    }




  }


  public TransportTypeResult getTransportTypeWithLongestDuration(TmdSegment tmdSegment, Tracking next,
      CoordinateInterpolator coordinateInterpolator) {

    HashMap<TransportType,Duration> durationHashMap = new HashMap<>();

    LocalDateTime startTime = tmdSegment.getStartTime();
    LocalDateTime endTime = tmdSegment.getEndTime();
    Timespan timespan = new Timespan(startTime,endTime);
    Duration timespanDuration = Duration.between(startTime,endTime);

    TrackingSegmentBag latestTrackingSegmentBag = next.getLatestTrackingSegmentBag();
    int size = latestTrackingSegmentBag.getSegments().size();
    LocalDateTime startTimeFirstPoint = latestTrackingSegmentBag.getSegments().get(0).getStartTime();
    if (startTimeFirstPoint.isAfter(endTime)) {
      return new TransportTypeResult(true,latestTrackingSegmentBag.getSegments().get(0).getTransportType());
    }
    LocalDateTime endTimeLastPoint = latestTrackingSegmentBag.getSegments().get(size - 1).getEndTime();
    if (endTimeLastPoint.isBefore(startTime)) {
      return new TransportTypeResult(true,latestTrackingSegmentBag.getSegments().get(size-1).getTransportType());
    }

    for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
      Timespan trackingSegmentTimeSpan = new Timespan(trackingSegment.getStartTime(),trackingSegment.getEndTime());

      TransportType transportType = trackingSegment.getTransportType();
      Duration overlappingDuration = trackingSegmentTimeSpan.getOverlappingDuration(timespan);


      Duration duration = durationHashMap.get(transportType);
      if(duration== null){
        duration = Duration.ofSeconds(0);
      }
      duration = duration.plus(overlappingDuration);
      durationHashMap.put(transportType,duration);
    }

    int equalBestResults  = 0;
    Duration maxDuration = Duration.ofSeconds(0);
    TransportType bestTransportType = null;
    for (TransportType transportType : durationHashMap.keySet()) {
      Duration duration = durationHashMap.get(transportType);
      if(duration.compareTo(maxDuration)>0){
        bestTransportType = transportType;
        maxDuration = duration;
        equalBestResults=1;
      }if(duration.compareTo(maxDuration) ==0){
        equalBestResults=2;
      }
    }

    if(bestTransportType == null){
      int debug=3;
    }
    if(equalBestResults ==1){
      return new TransportTypeResult(true,bestTransportType);
    }else{
      return new TransportTypeResult(false,bestTransportType);
    }
  }


  public static class TransportTypeResult{
    private boolean unambigious;
    private TransportType transportType;

    public TransportTypeResult(boolean unambigious,
        TransportType transportType) {
      this.unambigious = unambigious;
      this.transportType = transportType;
    }

    public boolean isUnambigious() {
      return unambigious;
    }

    public TransportType getTransportType() {
      return transportType;
    }
  }

  public TransportType getTransportTypeInDistanceMiddle(TmdSegment tmdSegment,Tracking tracking,CoordinateInterpolator coordinateInterpolator){
    LocalDateTime startTime = tmdSegment.getStartTime();
    LocalDateTime endTime = tmdSegment.getEndTime();

    List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
        .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

    Distance distanceSum = new Distance(0);
    IGpsPoint lastGpsPoint = null;
    for (IGpsPoint iGpsPoint : interpolatedCoordinatesExact) {
      if(lastGpsPoint!=null){
        Distance distance = CoordinateUtil.haversineDistance(lastGpsPoint, iGpsPoint);
        distanceSum = distanceSum.plus(distance);
      }
      lastGpsPoint = iGpsPoint;
    }

    LocalDateTime timeOfMiddlePoint = null;
    Distance halfDitance = new Distance(distanceSum.getKm()/2);
    distanceSum = new Distance(0);
    lastGpsPoint = null;
    for (IGpsPoint iGpsPoint : interpolatedCoordinatesExact) {
      if(lastGpsPoint!=null){
        Distance distance = CoordinateUtil.haversineDistance(lastGpsPoint, iGpsPoint);
        distanceSum = distanceSum.plus(distance);
        if(distanceSum.isLongerThan(halfDitance)){
          timeOfMiddlePoint = iGpsPoint.getTime();
          break;
        }
      }
      lastGpsPoint = iGpsPoint;
    }

    if(timeOfMiddlePoint== null){
      int debug= 3;
      throw new RuntimeException("Should not happen");
    }
    List<TrackingSegment> segments = tracking.getLatestTrackingSegmentBag().getSegments();

    TrackingSegment lastTrackingSegment = null;
    for (TrackingSegment trackingSegment : segments) {

      Timespan timespan = new Timespan(trackingSegment.getStartTime(),trackingSegment.getEndTime());
      boolean flag = timespan.checkIfArgumentIsBetween(timeOfMiddlePoint);
      if(flag){
        return trackingSegment.getTransportType();
      }
    }

    if(timeOfMiddlePoint.isBefore(segments.get(0).getStartTime())){
      return segments.get(0).getTransportType();
    }
    if(timeOfMiddlePoint.isAfter(segments.get(segments.size()-1).getEndTime())){
      return segments.get(segments.size()-1).getTransportType();
    }

    throw new RuntimeException("should never happen");
  }



}
