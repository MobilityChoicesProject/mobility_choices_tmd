package at.fhv.transportClassifier.common;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import java.time.Duration;

/**
 * Created by Johannes on 12.03.2017.
 */
public class CoordinateUtil {


    public static Speed calcSpeedBetween1(IExtendedGpsPoint firstGpsPoint, IExtendedGpsPoint secondGpsPoint){

        double km = haversine(firstGpsPoint.getLatitude(), firstGpsPoint.getLongitude(), secondGpsPoint.getLatitude(), secondGpsPoint.getLongitude());

        Duration between = Duration.between(firstGpsPoint.getSensorTime(), secondGpsPoint.getSensorTime());
        double hours = Math.abs(between.toMillis()) / 1000.0 / 60 / 60;
        double kmsPerHour = km / hours;
        return new Speed(kmsPerHour);
    }

    public static Speed calcSpeedBetween(IGpsPoint firstGpsPoint, IGpsPoint secondGpsPoint){

        double km = haversine(firstGpsPoint.getLatitude(), firstGpsPoint.getLongitude(), secondGpsPoint.getLatitude(), secondGpsPoint.getLongitude());

        Duration between = Duration.between(firstGpsPoint.getTime(), secondGpsPoint.getTime());
        double hours = Math.abs(between.toMillis()) / 1000.0 / 60 / 60;
        double kmsPerHour = km / hours;
        return new Speed(kmsPerHour);
    }



    public static Distance haversineDistanceGpsPoint(IExtendedGpsPoint gpsPoint1, IExtendedGpsPoint gpsPoint2){
        return new Distance(haversine(gpsPoint1.getLatitude(),gpsPoint1.getLongitude(),gpsPoint2.getLatitude(),gpsPoint2.getLongitude()));
    }


    public static Distance haversineDistance(IGpsPoint gpsPoint1, IGpsPoint gpsPoint2){
        return new Distance(haversine(gpsPoint1.getLatitude(),gpsPoint1.getLongitude(),gpsPoint2.getLatitude(),gpsPoint2.getLongitude()));
    }



    public static Distance haversineDistance(
        double lat1, double lng1, double lat2, double lng2) {
        return new Distance(haversine(lat1,lng1,lat2,lng2));



    }

    /**
     * Calculates the distance in km between two lat/long points
     * using the haversine formula
     */
    private static double haversine(
            double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }



}
