package at.fhv.tmd.common;

/**
 * Created by Johannes on 09.04.2017.
 */
public class Coordinate implements ICoordinate {

  private double Latitude;
  private double longitude;



  public Coordinate(double latitude, double longitude) {
    Latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public Double getLatitude() {
    return Latitude;
  }

  public void setLatitude(double latitude) {
    Latitude = latitude;
  }

  @Override
  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}
