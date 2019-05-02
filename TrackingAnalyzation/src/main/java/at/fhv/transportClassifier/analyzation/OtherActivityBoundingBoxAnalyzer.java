package at.fhv.transportClassifier.analyzation;

import at.fhv.tmd.common.Distance;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 23.03.2017.
 */
public class OtherActivityBoundingBoxAnalyzer {


  public List<OtherActivityResult> analyze(Tracking tracking){

    List<OtherActivityResult> results = new ArrayList<>();
    TrackingSegmentBag trackingSegmentBag = tracking.getTrackingSegmentBags().get(0);
    for (TrackingSegment trackingSegment : trackingSegmentBag.getSegments()) {
      if(trackingSegment.getTransportType() == TransportType.OTHER){

        List<IExtendedGpsPoint> gpsPoints = trackingSegment.getGpsPoints();
        BoundingBox boundingBox = new SimpleBoundingBox(gpsPoints);

        Distance latitudeLength =CoordinateUtil.haversineDistance(boundingBox.getSouthLatitude(),boundingBox.getWestLongitude(),boundingBox.getNorthLatitude(),boundingBox.getWestLongitude());
        Distance longitudeLength =CoordinateUtil.haversineDistance(boundingBox.getSouthLatitude(),boundingBox.getWestLongitude(),boundingBox.getSouthLatitude(),boundingBox.getEastLongitude());
        LocalDateTime startTime = trackingSegment.getStartTime();
        LocalDateTime endTime = trackingSegment.getEndTime();
        int numberOfPoints = gpsPoints.size();

        double areaInSquareMeters = latitudeLength.getMeter() * longitudeLength.getMeter();

        long seconds = Duration.between(startTime, endTime).toMillis() / 1000;
        double sizePerDurationSecond = areaInSquareMeters / seconds;
        Distance distanceBetweenFirstAndLastPoint = CoordinateUtil
            .haversineDistanceGpsPoint(gpsPoints.get(0), gpsPoints.get(gpsPoints.size() - 1));


        OtherActivityResultImp otherActivityResult = new OtherActivityResultImp();
        otherActivityResult.setEndTime(endTime);
        otherActivityResult.setStartTime(startTime);
        otherActivityResult.setLatitudeLenghtMeters(latitudeLength.getMeter());
        otherActivityResult.setLongitudeLengthMeters(longitudeLength.getMeter());
        otherActivityResult.setSizePerDurationSecond(sizePerDurationSecond);
        otherActivityResult.setNumberOfPoints( numberOfPoints);
        otherActivityResult.setDistanceBetweenFirstAndLastPoint(distanceBetweenFirstAndLastPoint.getMeter());
        results.add(otherActivityResult);


      }
    }
    return results;





  }





  private static class OtherActivityResultImp implements
      OtherActivityResult {
    private double longitudeLengthMeters;
    private double latitudeLenghtMeters;
    private double sizePerDurationSecond;
    private int numberOfPoints;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double distanceBetweenFirstAndLastPoint;

    @Override
    public double getLongitudeLengthMeters() {
      return longitudeLengthMeters;
    }

    public void setLongitudeLengthMeters(double longitudeLengthMeters) {
      this.longitudeLengthMeters = longitudeLengthMeters;
    }

    @Override
    public double getLatitudeLenghtMeters() {
      return latitudeLenghtMeters;
    }

    public void setLatitudeLenghtMeters(double langitudeLenghtMeters) {
      this.latitudeLenghtMeters = langitudeLenghtMeters;
    }

    @Override
    public double getSizePerDurationSecond() {
      return sizePerDurationSecond;
    }

    public void setSizePerDurationSecond(double sizePerDurationSecond) {
      this.sizePerDurationSecond = sizePerDurationSecond;
    }

    @Override
    public int getNumberOfPoints() {
      return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
      this.numberOfPoints = numberOfPoints;
    }

    @Override
    public LocalDateTime getStartTime() {
      return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
      return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
    }

    @Override
    public double getDistanceBetweenFirstAndLastPoint() {
      return distanceBetweenFirstAndLastPoint;
    }

    public void setDistanceBetweenFirstAndLastPoint(double distanceBetweenFirstAndLastPoint) {
      this.distanceBetweenFirstAndLastPoint = distanceBetweenFirstAndLastPoint;
    }
  }


  /**
   * Created by Johannes on 23.03.2017.
   */
  public interface OtherActivityResult {

    double getLongitudeLengthMeters();

    double getLatitudeLenghtMeters();

    double getSizePerDurationSecond();

    int getNumberOfPoints();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();

    double getDistanceBetweenFirstAndLastPoint();
  }
}
