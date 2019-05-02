package at.fhv.transportClassifier.dal.databaseRepositories;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.AccelerationValueEntity;
import at.fhv.transportClassifier.dal.databaseEntities.BoundingBoxEntity;
import at.fhv.transportClassifier.dal.databaseEntities.DisplayStateEventEntity;
import at.fhv.transportClassifier.dal.databaseEntities.GpsPointEntity;
import at.fhv.transportClassifier.dal.databaseEntities.StateChange;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingEntiy;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentBagEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentTypeEntity;
import at.fhv.transportClassifier.dal.interfaces.SessionManager;
import at.fhv.transportClassifier.dal.interfaces.Spezification;
import at.fhv.transportClassifier.dal.interfaces.TrackingRepository;
import at.fhv.transportClassifier.dal.interfaces.TrackingSpezification;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedType;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;


/**
 * Created by Johannes on 09.02.2017.
 */
public class HibernateTrackingRepository implements TrackingRepository{


    private HibernateSessionMananger sessionManager;

    protected HibernateTrackingInfoRepository trackingInfoRepository;
    protected HibernateSessionMananger getInternalSessionManager(){
        return sessionManager;
    }
    protected HibernateTrackingSegmentBagRepository hibernateTrackingSegmentBagRepository;
    protected HibernateBoundingBoxRepository hibernateBoundingBoxRepository;

    protected HibernateDatapointsRepository hibernateDatapointsRepository;
    protected Session getSession(){
        return sessionManager.getSession();
    }

    public HibernateTrackingRepository(HibernateSessionMananger sessionManager, HibernateTrackingInfoRepository trackingInfoRepository, HibernateTrackingSegmentBagRepository hibernateTrackingSegmentBagRepository, HibernateBoundingBoxRepository hibernateBoundingBoxRepository, HibernateDatapointsRepository hibernateDatapointsRepository) {
        this.sessionManager = sessionManager;
        this.trackingInfoRepository = trackingInfoRepository;
        this.hibernateTrackingSegmentBagRepository = hibernateTrackingSegmentBagRepository;
        this.hibernateBoundingBoxRepository = hibernateBoundingBoxRepository;
        this.hibernateDatapointsRepository = hibernateDatapointsRepository;
    }

    @Override
    public void add(Tracking tracking) {
        boolean useShortTransaction=!getInternalSessionManager().isTransactionOpen();
        try{

        if(useShortTransaction){
            getInternalSessionManager().startLongTransaction();
        }

            persist(tracking);

        if(useShortTransaction){
            getInternalSessionManager().commitLongTransaction();
        }

        }catch(Exception ex){
            try{
                getInternalSessionManager().rollbackLongTransaction();
                throw new RuntimeException(ex);
            }catch (Exception ex1){
                throw new RuntimeException(ex);
            }

        }

    }

    private void persist(Tracking tracking) {

        TrackingEntiy trackingEntiy = new TrackingEntiy();

        List<AccelerationValueEntity> accelerationValueEntities1 = hibernateDatapointsRepository.toAccelerationValueEntities(tracking, trackingEntiy);
        List<DisplayStateEventEntity> displayStateEventEntities1 = hibernateDatapointsRepository.tooDisplayStateEventEntities(tracking, trackingEntiy);
        List<GpsPointEntity> gpsPointEntities1 = hibernateDatapointsRepository.toGpsPointEntities(tracking, trackingEntiy);
        List<TrackingInfoEntity> trackingInfoEntities = trackingInfoRepository.toTrackingInfoEntities(tracking.getTrackingInfos(), trackingEntiy);
        List<TrackingSegmentBagEntity> trackingSegmentBagEntities = hibernateTrackingSegmentBagRepository.toTrackingSegmentBagEntities(tracking.getTrackingSegmentBags(), trackingEntiy);
        trackingEntiy.setStartTimestamp(tracking.getStartTimestamp());
        trackingEntiy.setEndTimestamp(tracking.getEndTimestamp());

        BoundingBoxEntity boundingBox = hibernateBoundingBoxRepository.createBoundingBox(tracking.getBoundingBox());
        trackingEntiy.setBoundingBox(boundingBox);

        sessionManager.getSession().persist(trackingEntiy);
        hibernateDatapointsRepository.persistAccelerationValueEntities(accelerationValueEntities1);
        hibernateDatapointsRepository.persistDisplayStateEventEntities(displayStateEventEntities1);
        hibernateDatapointsRepository.persistGpsPointEntities(gpsPointEntities1);
        trackingInfoRepository.persist(trackingInfoEntities);
        hibernateTrackingSegmentBagRepository.persist(trackingSegmentBagEntities);
    }





    public List<TrackingInfoEntity> addTrackingInfo(Tracking tracking, TrackingEntiy trackingEntiy) {
        List<TrackingInfoEntity>  trackingInfoEntities= new ArrayList<>();


        return null;
    }


    @Override
    public void remove(Tracking tracking) {

        Session session = sessionManager.getSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<TrackingEntiy> criteriaQuery = criteriaBuilder.createQuery(TrackingEntiy.class);
        Root<TrackingEntiy> root = criteriaQuery.from(TrackingEntiy.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("startTimestamp"),tracking.getStartTimestamp()));
        List<TrackingEntiy> resultList = session.createQuery(criteriaQuery).getResultList();
        TrackingEntiy trackingEntiy1 = resultList.get(0);
        for (TrackingEntiy trackingEntiy : resultList) {
            if (trackingEntiy.getTrackingSegmentBagEntities().size()>1) {
                int b = 5;
            }else{
                session.remove(trackingEntiy);
            }
        }

        int b4=4;



    }

    @Override
    public void update(Tracking tracking) {

        sessionManager.startLongTransaction();
        Session session = sessionManager.getSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<TrackingEntiy> criteriaQuery = criteriaBuilder.createQuery(TrackingEntiy.class);
        Root<TrackingEntiy> root = criteriaQuery.from(TrackingEntiy.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("startTimestamp"),tracking.getStartTimestamp()));

        List<TrackingEntiy> resultList = session.createQuery(criteriaQuery).getResultList();
        TrackingEntiy trackingEntiy1 = resultList.get(0);

        for (TrackingInfoEntity trackingInfoEntity : trackingEntiy1.getTrackingInfos()) {
            session.remove(trackingInfoEntity);
        }
        List<TrackingInfoEntity> trackingInfos = trackingEntiy1.getTrackingInfos();
        trackingInfos.clear();
        List<TrackingInfoEntity> trackingInfoEntities = trackingInfoRepository.toTrackingInfoEntities(tracking.getTrackingInfos(), trackingEntiy1);
        trackingInfos.addAll(trackingInfoEntities);



        List<TrackingSegment> trackingSegments = new ArrayList<>();
        for (TrackingSegment trackingSegment : tracking.getTrackingSegmentBags().get(0)
            .getSegments()) {
            boolean alreadyIn = false;
            for (TrackingSegment segment : trackingSegments) {
                if(segment.getStartTime().equals(trackingSegment.getStartTime())){
                    alreadyIn  = true;
                }

            }
            if(!alreadyIn){
                trackingSegments.add(trackingSegment);
            }

        }
        tracking.getTrackingSegmentBags().get(0).getSegments().clear();
        tracking.getTrackingSegmentBags().get(0).getSegments().addAll(trackingSegments);



//        Iterator<TrackingSegment> iterator1 = tracking.getTrackingSegmentBags().get(0).getSegments()
//            .iterator();
//        TrackingSegment next1 = iterator1.next();
//        while (iterator1.hasNext()) {
//            iterator1.next();
//            iterator1.remove();
//        }
//        iterator1 = tracking.getTrackingSegmentBags().get(1).getSegments()
//            .iterator();
//         next1 = iterator1.next();
//        while (iterator1.hasNext()) {
//            iterator1.next();
//            iterator1.remove();
//        }

        for (TrackingSegmentBagEntity trackingSegmentBagEntity : trackingEntiy1
            .getTrackingSegmentBagEntities()) {
            session.remove(trackingSegmentBagEntity);
        }
        trackingEntiy1.getTrackingSegmentBagEntities().clear();
        List<TrackingSegmentBagEntity> trackingSegmentBagEntities = hibernateTrackingSegmentBagRepository.toTrackingSegmentBagEntities(tracking.getTrackingSegmentBags(), trackingEntiy1);
        trackingEntiy1.getTrackingSegmentBagEntities().addAll(trackingSegmentBagEntities);


        session.merge(trackingEntiy1);
        hibernateTrackingSegmentBagRepository.persist(trackingSegmentBagEntities);
        trackingInfoRepository.persist(trackingInfoEntities);


        sessionManager.commitLongTransaction();
    }


    public void update1(Tracking tracking) {

        Session session = sessionManager.getSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<TrackingEntiy> criteriaQuery = criteriaBuilder.createQuery(TrackingEntiy.class);
        Root<TrackingEntiy> root = criteriaQuery.from(TrackingEntiy.class);

        criteriaQuery.where(criteriaBuilder.equal(root.get("startTimestamp"),tracking.getStartTimestamp()));

        List<TrackingEntiy> resultList = session.createQuery(criteriaQuery).getResultList();
        TrackingEntiy oldTrackingEntity = resultList.get(0);

        session.remove(oldTrackingEntity);

//
//        trackingEntiy1.getTrackingSegmentBagEntities().clear();
//        List<TrackingSegmentBagEntity> trackingSegmentBagEntities = hibernateTrackingSegmentBagRepository.toTrackingSegmentBagEntities(tracking.getTrackingSegmentBags(), trackingEntiy1);
//        trackingEntiy1.getTrackingSegmentBagEntities().addAll(trackingSegmentBagEntities);
//
//
//        session.merge(trackingEntiy1);
//        hibernateTrackingSegmentBagRepository.persist(trackingSegmentBagEntities);
//        trackingInfoRepository.persist(trackingInfoEntities);


    }


    @Override
    public List<Tracking> query(TrackingSpezification trackingSpezification) {
        return null;
    }

    @Override
    public List<Tracking> query(List<TrackingSpezification> trackingSpezifications) {

        Session session = sessionManager.getSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<TrackingEntiy> criteriaQuery = criteriaBuilder.createQuery(TrackingEntiy.class);
        Root<TrackingEntiy> root = criteriaQuery.from(TrackingEntiy.class);
        criteriaQuery.select(root);
        Query query = session.createQuery(criteriaQuery);
        List<TrackingEntiy> list = query.getResultList();


        List<Tracking> trackings = new ArrayList<>();

        for (TrackingEntiy entity : list) {
            Tracking build = createTracking(entity);
            trackings.add(build);
        }
        return trackings;
    }

    public Tracking createTracking(TrackingEntiy entity) {
        SimpleTrackingBuilder builder = new SimpleTrackingBuilder();

        for (TrackingInfoEntity trackingInfo : entity.getTrackingInfos()) {
            builder.addTrackingInfo(trackingInfo.getTrackingInfoTypeEntity().getName(), trackingInfo.getValue());
        }

        for (AccelerationValueEntity accelerationValueEntity : entity.getAcceleratorTrackEntities()) {
            builder.addAcceleratorState(accelerationValueEntity.getTimestamp(), accelerationValueEntity.getxAcceleration(), accelerationValueEntity.getyAcceleration(), accelerationValueEntity.getzAcceleration());
        }
        for (DisplayStateEventEntity displayStateEventEntity : entity.getDisplayStateEventEntities()) {
            DisplayStateChangedType displayStateChangedType = displayStateEventEntity.getStateChange() == StateChange.turnedOff ? DisplayStateChangedType.TURNED_OFF : DisplayStateChangedType.TURNED_ON;
            builder.addDisplayStateChangedEvent(displayStateEventEntity.getTimestamp(), displayStateChangedType);
        }

        for (GpsPointEntity gpsPointEntity : entity.getGpsTrackings()) {
            Double altitude = gpsPointEntity.getAltitude();
            Double accuracy = gpsPointEntity.getAccuracy();
            Double speed = gpsPointEntity.getSpeed();
            LocalDateTime systemSavingTime = gpsPointEntity.getDeviceSystemSavingTime();
            builder.addGpsPoint(gpsPointEntity.getLatitude(), gpsPointEntity.getLongitude(), altitude, accuracy, speed, gpsPointEntity.getTimestamp(),systemSavingTime);
        }

        builder.setStartTimestamp(entity.getStartTimestamp());
        builder.setEndTimestamp(entity.getEndTimestamp());

        for (TrackingSegmentBagEntity trackingSegmentBagEntity : entity.getTrackingSegmentBagEntities()) {
            for (TrackingSegmentEntity segmentEntity : trackingSegmentBagEntity.getTrackingSegmentEntities()) {
                TrackingSegmentTypeEntity trackingSegmentType = segmentEntity.getTrackingSegmentType();
                TransportType transportType = getTransportType(trackingSegmentType.getName());
                int version = trackingSegmentBagEntity.getVersion();
                builder.addTrackingSegment(segmentEntity.getStartTimeStamp(), segmentEntity.getEndTimeStamp(),transportType,version );
            }
        }
        return builder.build();
    }


    private TransportType getTransportType(String transportTypeStr) {
        Class<TransportType> transportTypeClass = TransportType.class;
        TransportType transportType = Enum.valueOf(transportTypeClass, transportTypeStr);
        return transportType;


    }







    @Override
    public Spezification<Tracking> getSpezification(Class<Spezification<Tracking>> spezification) {
        return null;
    }


    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
