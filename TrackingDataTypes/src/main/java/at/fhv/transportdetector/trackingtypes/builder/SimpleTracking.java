package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleTracking implements Tracking{

    protected List<TrackingInfo> trackingInfos = new ArrayList<>();
    protected List<TrackingSegmentBag> trackingSegmentBags = new ArrayList<>();
    protected List<IExtendedGpsPoint> gpsPoints = new ArrayList<>();
    protected BoundingBox boundingBox;
    protected LocalDateTime startTimestamp;
    protected LocalDateTime endTimestamp;
    private Long trackingId;

  @Override
    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

  @Override
  public TrackingSegmentBag getLatestTrackingSegmentBag() {

    Iterator<TrackingSegmentBag> iterator = trackingSegmentBags.iterator();
    TrackingSegmentBag latestTrackingSegmentBag=iterator.next();
    while (iterator.hasNext()){
      TrackingSegmentBag next = iterator.next();
      if(next.getVersion()>latestTrackingSegmentBag.getVersion()){
        latestTrackingSegmentBag = next;
      }
    }

    return latestTrackingSegmentBag;
  }

  @Override
  public TrackingSegmentBag getTrackingSegmentBagWithVersion(int version) {
    for (TrackingSegmentBag trackingSegmentBag : trackingSegmentBags) {
      if (trackingSegmentBag.getVersion()== version) {
        return trackingSegmentBag;
      }
    }

    throw new NotAvailableExcepion("No TrackingSegmentBag with version("+version+") available");
  }

  @Override
  public String getTrackingInfo(String key) {
    for (TrackingInfo trackingInfo : trackingInfos) {
      if (trackingInfo.getInfoName().equals(key)) {
        return trackingInfo.getInfoValue();
      }
    }
    return null;
  }

  @Override
  public boolean hasTrackingInfo(String key) {

    for (TrackingInfo trackingInfo : trackingInfos) {
      if (trackingInfo.getInfoName().equals(key)) {
        return true;
      }
    }
      return false;
  }

  @Override
  public Long getId() {

    return trackingId;
  }

  public void setEndTimestamp(LocalDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void addTrackingInfo(TrackingInfo trackingInfo){
        trackingInfos.add(trackingInfo);
    }

    public void addTrackingInfos(List<TrackingInfo> trackingInfos){
        this.trackingInfos.addAll(trackingInfos);
    }

    public void addTrackingSegmentBags(List<TrackingSegmentBag> trackingSegmentBags){
        this.trackingSegmentBags.addAll(trackingSegmentBags);
    }
    public void addTrackingSegmentBag(TrackingSegmentBag trackingSegmentBag){
        this.trackingSegmentBags.add(trackingSegmentBag);
    }

    public void addGpsPoint(IExtendedGpsPoint gpsPoint){
        gpsPoints.add(gpsPoint);
        if(boundingBox == null){
            boundingBox = new SimpleBoundingBox(gpsPoint.getLatitude(),gpsPoint.getLongitude(),gpsPoint.getLatitude(),gpsPoint.getLongitude());
        }else{
            boundingBox = boundingBox.extendBoundingBox(gpsPoint);
        }
    }

    public void addGpsPoints(List<IExtendedGpsPoint> gpsPoints){
        this.gpsPoints.addAll(gpsPoints);
        if(boundingBox == null){
            boundingBox = new SimpleBoundingBox(gpsPoints);
        }else{
            boundingBox = boundingBox.extendBoundingBox(gpsPoints);
        }
    }

    public void setStartTimestamp(LocalDateTime startTimestamp){
        this.startTimestamp = startTimestamp;
    }

    @Override
    public List<TrackingInfo> getTrackingInfos() {
        return trackingInfos;
    }

    @Override
    public List<TrackingSegmentBag> getTrackingSegmentBags() {
        return trackingSegmentBags;
    }

    @Override
    public List<IExtendedGpsPoint> getGpsPoints() {
        return gpsPoints;
    }


    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

  public void setTrackingId(Long trackingId) {
    this.trackingId = trackingId;
  }
}
