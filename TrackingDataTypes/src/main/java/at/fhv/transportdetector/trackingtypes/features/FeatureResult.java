package at.fhv.transportdetector.trackingtypes.features;

import java.io.Serializable;

/**
 * Created by Johannes on 20.06.2017.
 */
public class FeatureResult implements Serializable{

  private String featureName;
  private double featureValue;

  public FeatureResult(String featureName, double featureValue) {
    this.featureName = featureName;
    this.featureValue = featureValue;
  }

  public String getFeatureName() {
    return featureName;
  }

  public double getFeatureValue() {
    return featureValue;
  }
}
