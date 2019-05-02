package at.fhv.transportdetector.trackingtypes;

import org.omg.CORBA.UNKNOWN;

import java.util.LinkedList;
import java.util.List;

public class BackgroundGeolocationActivityMapper {
    public static List<BackgroundGeolocationActivity> getBackgroundGeolocationActivity(TransportType type) {
        List<BackgroundGeolocationActivity> activities = new LinkedList<>();
        switch (type) {
            case CAR:
            case BUS:
            case TRAIN:
                activities.add(BackgroundGeolocationActivity.IN_VEHICLE);
                break;
            case BIKE:
                activities.add(BackgroundGeolocationActivity.ON_BICYCLE);
                break;
            case WALK:
            case OTHER:
                activities.add(BackgroundGeolocationActivity.ON_FOOT);
                activities.add(BackgroundGeolocationActivity.WALKING);
                activities.add(BackgroundGeolocationActivity.RUNNING);
                break;
            case STATIONARY:
                activities.add(BackgroundGeolocationActivity.STILL);
                break;
        }

        return activities;
    }
}
