package at.fhv.features;

import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 21.06.2017.
 */
public class LabeledFeature {

  List<FeatureResult> featureResultList;

  private LocalDateTime trackingStartTime;
  private LocalDateTime segmentStartTime;
  private String trackingFileName;
  private TransportType transportType;



  public double getValue(String featureName){
    for (FeatureResult featureResult : featureResultList) {
      if (featureResult.getFeatureName().equals(featureName)) {
        return featureResult.getFeatureValue();
      }
    }
    String exceptionStr = "no such element with that featurename: '" + featureName + "'";
    throw new IllegalArgumentException(exceptionStr);

  }

  public List<FeatureResult> getFeatureResultList() {
    return featureResultList;
  }

  public void setFeatureResultList(
      List<FeatureResult> featureResultList) {
    this.featureResultList = featureResultList;
  }

  public LocalDateTime getTrackingStartTime() {
    return trackingStartTime;
  }

  public void setTrackingStartTime(LocalDateTime trackingStartTime) {
    this.trackingStartTime = trackingStartTime;
  }

  public LocalDateTime getSegmentStartTime() {
    return segmentStartTime;
  }

  public void setSegmentStartTime(LocalDateTime segmentStartTime) {
    this.segmentStartTime = segmentStartTime;
  }

  public String getTrackingFileName() {
    return trackingFileName;
  }

  public void setTrackingFileName(String trackingFileName) {
    this.trackingFileName = trackingFileName;
  }

  public TransportType getTransportType() {
    return transportType;
  }

  public void setTransportType(TransportType transportType) {
    this.transportType = transportType;
  }
}
