package at.fhv.transportClassifier.dal.databaseRepositories;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.BoundingBoxEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentBagEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentTypeEntity;
import at.fhv.transportClassifier.dal.interfaces.Repository;
import at.fhv.transportClassifier.dal.interfaces.SessionManager;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 14.02.2017.
 */
public class HibernateTrackingSegmentRepository implements Repository {

    private HibernateSessionMananger hibernateSessionMananger;
    private HibernateBoundingBoxRepository hibernateBoundingBoxRepository;

    protected HibernateTrackingSegmentTypeRepository hibernateTrackingSegmentTypeRepository;

    public HibernateTrackingSegmentRepository(HibernateSessionMananger hibernateSessionMananger, HibernateBoundingBoxRepository hibernateBoundingBoxRepository, HibernateTrackingSegmentTypeRepository hibernateTrackingSegmentTypeRepository) {
        this.hibernateSessionMananger = hibernateSessionMananger;
        this.hibernateBoundingBoxRepository = hibernateBoundingBoxRepository;
        this.hibernateTrackingSegmentTypeRepository = hibernateTrackingSegmentTypeRepository;
    }

    @Override
    public SessionManager getSessionManager() {
        return null;
    }


    public void toTrackingSegment(List<TrackingSegment> trackingSegments, TrackingSegmentBagEntity trackingSegmentBag){

        for (TrackingSegment trackingSegment : trackingSegments) {
            TrackingSegmentEntity entity = new TrackingSegmentEntity();

            BoundingBoxEntity boundingBox = hibernateBoundingBoxRepository.createBoundingBox(trackingSegment.getBoundingBox());
            entity.setBoundingBox(boundingBox);

            entity.setEndTimeStamp(trackingSegment.getEndTime());
            entity.setStartTimeStamp(trackingSegment.getStartTime());
            entity.setSavingTimeStamp(LocalDateTime.now());
            TransportType transportType = trackingSegment.getTransportType();

            TrackingSegmentTypeEntity trackingSegmentType = hibernateTrackingSegmentTypeRepository.createTrackingSegmentType(transportType);
            entity.setTrackingSegmentType(trackingSegmentType);
            entity.setTrackingSegmentBag(trackingSegmentBag);

        }

    }

    public void persist(List<TrackingSegmentEntity> trackingSegments){
        for (TrackingSegmentEntity trackingSegment : trackingSegments) {
            hibernateSessionMananger.getSession().persist(trackingSegment);

        }

    }

}
