package at.fhv.gis;

/**
 * Created by Johannes on 26.05.2017.
 */
public enum OverpassDataType {
  BusStops(1),
  RailwayStops(2),
  BusRoutes(3),
  RailwayRoutes(4);

  private final int value;
  OverpassDataType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
