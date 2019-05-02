package at.fhv.transportClassifier.mainserver.api;

/**
 * Created by Johannes on 13.06.2017.
 */
public enum EndPointType {
    BusStation(1),
    RailwayStation(2);

    private final int value;
    EndPointType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
