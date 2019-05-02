package at.fhv.transportdetector.trackingtypes;

import at.fhv.tmd.common.IGpsPoint;
import java.util.List;

/**
 * Created by Johannes on 15.02.2017.
 */
public interface BoundingBox {
    BoundingBox extendBoundingBox(IExtendedGpsPoint gpsPoint);

    BoundingBox extendBoundingBox(List<IExtendedGpsPoint> includedPoints);

    double getSouthLatitude();

    double getWestLongitude();

    double getNorthLatitude();

    double getEastLongitude();

    boolean contains(BoundingBox possibleContainedBoundingBox);

    boolean contains(IGpsPoint coordinate);

    boolean contains(double latitude, double longitude);

    boolean containsWithLowerBorders(double latitude,double longitude);
}
