package at.fhv.jn.googleMaps;

/**
 * Created by Johannes on 01.02.2017.
 */
public class SimpleDataPoint implements DataPoint{

    private double latitude;
    private double longitude;

    public SimpleDataPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
