package at.fhv.transportClassifier.dal.databaseRepositories;
import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingEntiy;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentBagEntity;
import at.fhv.transportClassifier.dal.interfaces.Repository;
import at.fhv.transportClassifier.dal.interfaces.SessionManager;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 14.02.2017.
 */
public class HibernateTrackingSegmentBagRepository implements Repository{

    protected HibernateSessionMananger hibernateSessionMananger;
    protected HibernateTrackingSegmentRepository hibernateTrackingSegmentRepository;

    public HibernateTrackingSegmentBagRepository(HibernateSessionMananger hibernateSessionMananger, HibernateTrackingSegmentRepository hibernateTrackingSegmentRepository) {
        this.hibernateSessionMananger = hibernateSessionMananger;
        this.hibernateTrackingSegmentRepository = hibernateTrackingSegmentRepository;
    }

    @Override
    public SessionManager getSessionManager() {
        return null;
    }

    public List<TrackingSegmentBagEntity> toTrackingSegmentBagEntities(List<TrackingSegmentBag> trackingSegmentBags, TrackingEntiy trackingEntiy){

        List<TrackingSegmentBagEntity> trackingSegmentBagEntities = new ArrayList<>();

        for (TrackingSegmentBag trackingSegmentBag : trackingSegmentBags) {

            TrackingSegmentBagEntity trackingSegmentBagEntity = new TrackingSegmentBagEntity();
            trackingSegmentBagEntity.setTrackingEntiy(trackingEntiy);
            trackingSegmentBagEntity.setTrackingSegmentEntities(new ArrayList<>());
            trackingSegmentBagEntity.setVersion(trackingSegmentBag.getVersion());
            hibernateTrackingSegmentRepository.toTrackingSegment(trackingSegmentBag.getSegments(),trackingSegmentBagEntity);
            trackingSegmentBagEntities.add(trackingSegmentBagEntity);

        }
         return trackingSegmentBagEntities;
    }

    public void persist(List<TrackingSegmentBagEntity> trackingSegmentBagEntities){
        for (TrackingSegmentBagEntity trackingSegmentBagEntity : trackingSegmentBagEntities) {
            hibernateSessionMananger.getSession().persist(trackingSegmentBagEntity);
            hibernateTrackingSegmentRepository.persist(trackingSegmentBagEntity.getTrackingSegmentEntities());
        }




    }

}
