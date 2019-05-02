package sample;

import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.builder.SimpleAllTracking;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.util.List;

/**
 * Created by Johannes on 19.04.2017.
 */
public class TrackingUtil {

  public static Tracking cloneTrackingWith(Tracking toCloneTracking,List<IExtendedGpsPoint> toUsedGpsPoints){
    Tracking tracking = toCloneTracking;
    SimpleTrackingBuilder trackingBuilder = new SimpleTrackingBuilder();
    trackingBuilder.setStartTimestamp(tracking.getStartTimestamp());
    trackingBuilder.setEndTimestamp(tracking.getEndTimestamp());
    for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {

      trackingBuilder.addTrackingInfo(trackingInfo.getInfoName(),trackingInfo.getInfoValue());
    }
    List<IExtendedGpsPoint> gpsPoints = toUsedGpsPoints;
    for (IExtendedGpsPoint gpsPoint : gpsPoints) {
      trackingBuilder.addGpsPoint(gpsPoint.getLatitude(),gpsPoint.getLongitude(),gpsPoint.getAltitude(),gpsPoint.getAccuracy(),gpsPoint.getSpeed(),gpsPoint.getSensorTime(),gpsPoint.getDeviceSavingSystemTime());
    }
    for (TrackingSegment trackingSegment : tracking.getTrackingSegmentBags().get(0).getSegments()) {
      trackingBuilder.addTrackingSegment(trackingSegment.getStartTime(),trackingSegment.getEndTime(),trackingSegment.getTransportType(),0);
    }
    if(tracking instanceof SimpleAllTracking){
      SimpleAllTracking tracking1 = (SimpleAllTracking)tracking;
      for (AcceleratorState acceleratorState : tracking1.getAcceleratorStates()) {
        trackingBuilder.addAcceleratorState(acceleratorState.getTime(),acceleratorState.getXAcceleration(),acceleratorState.getYAcceleration(),acceleratorState.getZAcceleration());
      }
    }


    return  trackingBuilder.build();
  }


}
