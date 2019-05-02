package at.fhv.filters;

import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTracking;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Created by Johannes on 17.05.2017.
 */
public class SameSequelTransportModeMergeFilter {


  public Tracking filter(Tracking tracking){


    SimpleTrackingBuilder simpleTrackingBuilder = new SimpleTrackingBuilder();

    for (IExtendedGpsPoint gpsPoint : tracking.getGpsPoints()) {
      simpleTrackingBuilder.addGpsPoint(gpsPoint.getLatitude(),gpsPoint.getLongitude(),null,gpsPoint.getAccuracy(),null,gpsPoint.getSensorTime(),gpsPoint.getDeviceSavingSystemTime());
    }


    for (TrackingSegmentBag trackingSegmentBag : tracking.getTrackingSegmentBags()) {
      int version = trackingSegmentBag.getVersion();
      LinkedList<SegmentTemp> segmentTemps = new LinkedList<>();

      TrackingSegment lastTrackingSegment = null;
      for (TrackingSegment segment : trackingSegmentBag.getSegments()) {
        if(lastTrackingSegment == null){
          lastTrackingSegment = segment;
          segmentTemps.add(new SegmentTemp(segment.getStartTime(),segment.getEndTime(),segment.getTransportType()));
        }else{


          if(lastTrackingSegment.getTransportType() == segment.getTransportType()){
            segmentTemps.getLast().setEndtime(segment.getEndTime());
          }else{
            segmentTemps.add(new SegmentTemp(segment.getStartTime(),segment.getEndTime(),segment.getTransportType()));
            lastTrackingSegment = segment;
          }
        }
      }

      for (SegmentTemp segmentTemp : segmentTemps) {
        simpleTrackingBuilder.addTrackingSegment(segmentTemp.getStartTime(),segmentTemp.getEndtime(),segmentTemp.getTransportType(),version);
      }


    }

    for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {
      simpleTrackingBuilder.addTrackingInfo(trackingInfo.getInfoName(),trackingInfo.getInfoValue());
    }

    simpleTrackingBuilder.setStartTimestamp(tracking.getStartTimestamp());
    simpleTrackingBuilder.setEndTimestamp(tracking.getEndTimestamp());
    Tracking build = simpleTrackingBuilder.build();
    SimpleTracking simpleTracking = (SimpleTracking) build;
    simpleTracking.setTrackingId(tracking.getId());
    return build;

  }

  protected static class SegmentTemp{
    LocalDateTime startTime;
    LocalDateTime endtime;
    TransportType transportType;

    public SegmentTemp(LocalDateTime startTime, LocalDateTime endtime,
        TransportType transportType) {
      this.startTime = startTime;
      this.endtime = endtime;
      this.transportType = transportType;
    }

    public LocalDateTime getStartTime() {
      return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
    }

    public LocalDateTime getEndtime() {
      return endtime;
    }

    public void setEndtime(LocalDateTime endtime) {
      this.endtime = endtime;
    }

    public TransportType getTransportType() {
      return transportType;
    }

    public void setTransportType(TransportType transportType) {
      this.transportType = transportType;
    }
  }


}
