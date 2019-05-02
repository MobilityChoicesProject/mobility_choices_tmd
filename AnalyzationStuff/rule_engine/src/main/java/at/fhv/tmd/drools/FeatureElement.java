package at.fhv.tmd.drools;

/**
 * Created by Johannes on 04.07.2017.
 */
public class FeatureElement {

  private double value;
  private String name;

  private TrackingElement trackingElement;


  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TrackingElement getTrackingElement() {
    return trackingElement;
  }

  public void setTrackingElement(TrackingElement trackingElement) {
    this.trackingElement = trackingElement;
  }
}
