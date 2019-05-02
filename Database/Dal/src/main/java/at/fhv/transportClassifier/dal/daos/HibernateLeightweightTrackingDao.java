package at.fhv.transportClassifier.dal.daos;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.BoundingBoxEntity;
import at.fhv.transportClassifier.dal.databaseEntities.GpsPointEntity;
import at.fhv.transportClassifier.dal.databaseEntities.GpsTrackingEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingEntiy;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentBagEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentEntity;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingRepository;
import at.fhv.transportClassifier.dal.interfaces.LeightweightAccelerationValueDao;
import at.fhv.transportClassifier.dal.leightweightdatabaseentities.LeightweightTrackingEntity;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTracking;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingInfo;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import at.fhv.transportdetector.trackingtypes.light.LightWeightTrackingImp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

/**
 * Created by Johannes on 01.03.2017.
 */
public class  HibernateLeightweightTrackingDao implements at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao{

    private HibernateSessionMananger sessionManager;
    private LeightweightAccelerationValueDao leightweightAccelerationValueDao;
    private HibernateTrackingRepository hibernateTrackingRepository;


    public HibernateLeightweightTrackingDao(HibernateSessionMananger sessionManager, LeightweightAccelerationValueDao leightweightAccelerationValueDao, HibernateTrackingRepository hibernateTrackingRepository) {
        this.sessionManager = sessionManager;
        this.leightweightAccelerationValueDao = leightweightAccelerationValueDao;
        this.hibernateTrackingRepository = hibernateTrackingRepository;
    }

    @Override
    public List<LeightweightTracking> getAll() {

        List<LeightweightTracking> trackings = new ArrayList<>();
        try {

            Session session = getInternalSessionManager().getSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<LeightweightTrackingEntity> criteriaQuery = criteriaBuilder.createQuery(LeightweightTrackingEntity.class);
            Root<LeightweightTrackingEntity> root = criteriaQuery.from(LeightweightTrackingEntity.class);
            criteriaQuery.select(root);
            Query query = session.createQuery(criteriaQuery);
            List<LeightweightTrackingEntity> list = query.getResultList();




            trackingLoop:for (LeightweightTrackingEntity entity : list) {

                LightWeightTrackingImp leightweightTrackingEntity = new LightWeightTrackingImp();
                leightweightTrackingEntity.setStartDateTime(entity.getStartTimestamp());
                leightweightTrackingEntity.setTrackingId(entity.getTrackingId());


                BoundingBoxEntity bbox = entity.getBoundingBox();
                SimpleBoundingBox boundingBox = new SimpleBoundingBox(bbox.getSouthLatitude(),bbox.getWestLongitude(),bbox.getNorthLatitude(), bbox.getEastLongitude());

                leightweightTrackingEntity.setBoundingBox(boundingBox);

                List<TrackingInfoEntity> trackingInfos = entity.getTrackingInfos();
                List<TrackingInfo> trackingInfos1 = new ArrayList<>();
                    for (TrackingInfoEntity trackingInfo : trackingInfos) {
                        SimpleTrackingInfo simpleTrackingInfo = new SimpleTrackingInfo(trackingInfo.getTrackingInfoTypeEntity().getName(),trackingInfo.getValue());
                        trackingInfos1.add(simpleTrackingInfo);
                }
                leightweightTrackingEntity.setTrackingInfos(trackingInfos1);

                int version = Integer.MIN_VALUE;
                TrackingSegmentBagEntity newestTrackingSegmentBag = null;
                for (TrackingSegmentBagEntity trackingSegmentBagEntity : entity.getTrackingSegmentBagEntities()) {
                    if(trackingSegmentBagEntity.getVersion() > version){
                        version = trackingSegmentBagEntity.getVersion();
                        newestTrackingSegmentBag= trackingSegmentBagEntity;
                    }
                }
                if(newestTrackingSegmentBag == null){
                    continue;
                }

                Set<TransportType> transportTypeSet = new TreeSet<>();
                LocalDateTime lastTimeStamp = LocalDateTime.MIN;
                for (TrackingSegmentEntity trackingSegment : newestTrackingSegmentBag.getTrackingSegmentEntities()) {
                    String description = trackingSegment.getTrackingSegmentType().getName();
                    Class<TransportType> transportTypeClass = TransportType.class;
                    if(description == null){
                        continue trackingLoop;
                    }
                    TransportType transportType = Enum.valueOf(transportTypeClass, description);
                    LocalDateTime endTimeStamp = trackingSegment.getEndTimeStamp();
                    if(endTimeStamp.isAfter(lastTimeStamp)){
                        lastTimeStamp = endTimeStamp;
                    }
                    transportTypeSet.add(transportType);
                }


                Duration duration = Duration.between(entity.getStartTimestamp(),lastTimeStamp);
                leightweightTrackingEntity.setTrackingDuration(duration);

                leightweightTrackingEntity.setTransportTypes(transportTypeSet);
                trackings.add(leightweightTrackingEntity);

                int count = leightweightAccelerationValueDao.count(leightweightTrackingEntity);
                leightweightTrackingEntity.set_isAcceleratorDataAvailable(count > 0);
            }


         }catch(Exception ex) {

            throw new RuntimeException(ex);
        }
        return trackings;

    }



    public Tracking getGpsTracking(LeightweightTracking leightweightTracking){

     return getGpsTracking(leightweightTracking.getTrackingId());
    }

    @Override
    public Tracking getGpsTracking(long id) {
        SimpleTrackingBuilder simpleTrackingBuilder = new SimpleTrackingBuilder();
        Session session = getInternalSessionManager().getSession();
        sessionManager.startLongTransaction();
        GpsTrackingEntity trackingEntiy = session.get(GpsTrackingEntity.class,(int) id);
        List<GpsPointEntity> gpsTrackings = trackingEntiy.getGpsTrackings();
        for (GpsPointEntity gpsTracking : gpsTrackings) {
            simpleTrackingBuilder.addGpsPoint(gpsTracking.getLatitude(),gpsTracking.getLongitude(),gpsTracking.getAltitude(),gpsTracking.getAccuracy(),gpsTracking.getSpeed(),gpsTracking.getTimestamp(),gpsTracking.getDeviceSystemSavingTime());
        }

        simpleTrackingBuilder.setStartTimestamp(trackingEntiy.getStartTimestamp());
        simpleTrackingBuilder.setEndTimestamp(trackingEntiy.getEndTimestamp());

        for (TrackingInfoEntity entity : trackingEntiy.getTrackingInfos()) {
            simpleTrackingBuilder.addTrackingInfo(entity.getTrackingInfoTypeEntity().getName(),entity.getValue());
        }

        for (TrackingSegmentBagEntity trackingSegmentBagEntity : trackingEntiy
            .getTrackingSegmentBagEntities()) {
            for (TrackingSegmentEntity segmentEntity : trackingSegmentBagEntity
                .getTrackingSegmentEntities()) {

                TransportType transportType = getTransportType(
                    segmentEntity.getTrackingSegmentType().getName());
                simpleTrackingBuilder.addTrackingSegment(segmentEntity.getStartTimeStamp(),segmentEntity.getEndTimeStamp(),transportType,trackingSegmentBagEntity.getVersion());

            }
        }
        long trackingId = id;
        sessionManager.commitLongTransaction();
        Tracking build = simpleTrackingBuilder.build();
        SimpleTracking simpleTracking = (SimpleTracking) build;
        simpleTracking.setTrackingId(trackingId);
        return simpleTracking;
    }

    private TransportType getTransportType(String transportTypeStr) {
        Class<TransportType> transportTypeClass = TransportType.class;
        TransportType transportType = Enum.valueOf(transportTypeClass, transportTypeStr);
        return transportType;


    }

    @Override
    public Tracking getFullTracking(LeightweightTracking leightweightTracking){

        Session session = getInternalSessionManager().getSession();
        sessionManager.startLongTransaction();
        TrackingEntiy trackingEntiy = session.get(TrackingEntiy.class, leightweightTracking.getTrackingId());
        Tracking tracking = hibernateTrackingRepository.createTracking(trackingEntiy);
        sessionManager.commitLongTransaction();
        return tracking;
    }


    @Override
    public List<LeightweightTracking> getAll(int startIndex, int countLimit) {
        return null;
    }

    public HibernateSessionMananger getInternalSessionManager() {
        return sessionManager;
    }




}

