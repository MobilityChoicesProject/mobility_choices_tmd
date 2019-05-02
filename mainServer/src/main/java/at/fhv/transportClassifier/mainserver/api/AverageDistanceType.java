package at.fhv.transportClassifier.mainserver.api;

/**
 * Created by Johannes on 13.06.2017.
 */
public enum AverageDistanceType {
  BusRoute(3),
  RailRoute(4);

  private final int value;
  AverageDistanceType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
