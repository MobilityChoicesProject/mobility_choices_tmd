package at.fhv.transportClassifier.mainserver.bean.gis;

public class LatitudeLongitudeSteps {

  private int latitudeSteps;
  private int longitudeSteps;

  public LatitudeLongitudeSteps(int latitudeSteps, int longitudeSteps) {
    this.latitudeSteps = latitudeSteps;
    this.longitudeSteps = longitudeSteps;
  }

  public void setLatitudeSteps(int latitudeSteps) {
    this.latitudeSteps = latitudeSteps;
  }

  public void setLongitudeSteps(int longitudeSteps) {
    this.longitudeSteps = longitudeSteps;
  }

  public int getLatitudeSteps() {
    return latitudeSteps;
  }

  public int getLongitudeSteps() {
    return longitudeSteps;
  }
}
