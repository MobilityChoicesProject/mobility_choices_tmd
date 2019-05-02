package at.fhv.transportClassifier.dal.databaseRepositories;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.AccelerationValueEntity;
import at.fhv.transportClassifier.dal.databaseEntities.DisplayStateEventEntity;
import at.fhv.transportClassifier.dal.databaseEntities.GpsPointEntity;
import at.fhv.transportClassifier.dal.databaseEntities.StateChange;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingEntiy;
import at.fhv.transportClassifier.dal.interfaces.Repository;
import at.fhv.transportClassifier.dal.interfaces.SessionManager;
import at.fhv.transportdetector.trackingtypes.AccelerationTracking;
import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedEvent;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedType;
import at.fhv.transportdetector.trackingtypes.DisplayStateEventTracking;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 14.02.2017.
 */
public class HibernateDatapointsRepository implements Repository {

    HibernateSessionMananger sessionMananger;

    @Override
    public SessionManager getSessionManager() {
        return sessionMananger;
    }

    public HibernateDatapointsRepository(HibernateSessionMananger sessionMananger) {
        this.sessionMananger = sessionMananger;
    }

    public List<GpsPointEntity> toGpsPointEntities(Tracking tracking, TrackingEntiy trackingEntiy) {

        List<IExtendedGpsPoint> gpsPoints = tracking.getGpsPoints();
        List<GpsPointEntity> gpsPointEntities = new ArrayList<>();
        for (IExtendedGpsPoint gpsPoint : gpsPoints) {
            GpsPointEntity entity = new GpsPointEntity();
            entity.setLatitude(gpsPoint.getLatitude());
            entity.setLongitude(gpsPoint.getLongitude());
            entity.setTrackingEntiy(trackingEntiy);
            entity.setTimestamp(gpsPoint.getSensorTime());

            entity.setAccuracy(gpsPoint.getAccuracy());
            entity.setSpeed(gpsPoint.getSpeed());
            entity.setDeviceSystemSavingTime(gpsPoint.getDeviceSavingSystemTime());
            entity.setAltitude(gpsPoint.getAltitude());

            gpsPointEntities.add(entity);
        }
        return gpsPointEntities;
    }


    public List<AccelerationValueEntity> toAccelerationValueEntities(Tracking tracking, TrackingEntiy trackingEntiy) {

        List<AccelerationValueEntity>  accelerationValueEntities= new ArrayList<>();

        if(tracking instanceof AccelerationTracking){
            AccelerationTracking accelerationTracking = (AccelerationTracking) tracking;
            for (AcceleratorState acceleratorState : accelerationTracking.getAcceleratorStates()) {

                AccelerationValueEntity entity = new AccelerationValueEntity();
                entity.setTimestamp(acceleratorState.getTime());
                entity.setxAcceleration(acceleratorState.getXAcceleration());
                entity.setyAcceleration(acceleratorState.getYAcceleration());
                entity.setzAcceleration(acceleratorState.getZAcceleration());
                entity.setTrackingEntiy(trackingEntiy);
                accelerationValueEntities.add(entity);
            }
        }
        return accelerationValueEntities;
    }



    public List<DisplayStateEventEntity> tooDisplayStateEventEntities(Tracking tracking, TrackingEntiy trackingEntiy) {

        List<DisplayStateEventEntity>  displayStateEventEntities= new ArrayList<>();

        if(tracking instanceof DisplayStateEventTracking){
            DisplayStateEventTracking displayStateEventTracking = (DisplayStateEventTracking) tracking;
            for (DisplayStateChangedEvent displayStateEvent : displayStateEventTracking.getDisplayStateChangeEvents()) {

                DisplayStateEventEntity entity = new DisplayStateEventEntity();
                entity.setTimestamp(displayStateEvent.getTime());

                DisplayStateChangedType changeEventType = displayStateEvent.getChangeEventType();
                if( changeEventType == DisplayStateChangedType.TURNED_OFF){
                    entity.setStateChange(StateChange.turnedOff);
                }else{
                    entity.setStateChange(StateChange.turnedOn);
                }

                entity.setTrackingEntiy(trackingEntiy);
                displayStateEventEntities.add(entity);
            }
        }
        return displayStateEventEntities;
    }


    public void persistDisplayStateEventEntities(List<DisplayStateEventEntity> entities){
        for(DisplayStateEventEntity entity : entities){
            sessionMananger.getSession().persist(entity);
        }
    }

    public void persistGpsPointEntities(List<GpsPointEntity> gpsPointEntities){
        for(GpsPointEntity gpsPointEntity : gpsPointEntities){
            sessionMananger.getSession().persist(gpsPointEntity);
        }
    }

    public void persistAccelerationValueEntities(List<AccelerationValueEntity> accelerationValueEntities){
        for(Object o : accelerationValueEntities){
            sessionMananger.getSession().persist(o);
        }
    }




}
