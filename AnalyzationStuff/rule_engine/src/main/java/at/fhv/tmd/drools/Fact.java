package at.fhv.tmd.drools;

import at.fhv.transportdetector.trackingtypes.TransportType;

/**
 * Created by Johannes on 08.06.2017.
 */
public class Fact {

  private String featureName;
  private double featureValue;
  private SegmentFact segmentFact;

  public String getFeatureName() {
    return featureName;
  }

  public void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  public double getFeatureValue() {
    return featureValue;
  }

  public void setFeatureValue(double featureValue) {
    this.featureValue = featureValue;
  }

  public void addTransportTypeProbability(TransportType transportType,double probability,double weight){

  }

  public void addTransportTypeProbability(TransportType transportType,double probability){

  }

  public SegmentFact getSegmentFact(){
    return segmentFact;
  }

  public void setSegmentFact(SegmentFact segmentFact) {
    this.segmentFact = segmentFact;
  }
}
