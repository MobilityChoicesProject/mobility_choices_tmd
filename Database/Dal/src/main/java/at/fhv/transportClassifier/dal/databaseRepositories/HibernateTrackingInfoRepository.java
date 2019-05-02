package at.fhv.transportClassifier.dal.databaseRepositories;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingEntiy;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoTypeEntity;
import at.fhv.transportClassifier.dal.interfaces.SessionManager;
import at.fhv.transportClassifier.dal.interfaces.Spezification;
import at.fhv.transportClassifier.dal.interfaces.TrackingInfoFilenameSpezification;
import at.fhv.transportClassifier.dal.interfaces.TrackingInfoRepository;
import at.fhv.transportClassifier.dal.spezifications.HibernateTrackingInfoFilenameSpezification;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingInfo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

/**
 * Created by Johannes on 14.02.2017.
 */
public class HibernateTrackingInfoRepository implements TrackingInfoRepository{


    private HibernateSessionMananger hibernateSessionMananger;
    private TrackingInfoTypeRepository trackingInfoTypeRepository;

    public HibernateTrackingInfoRepository(HibernateSessionMananger hibernateSessionMananger, TrackingInfoTypeRepository trackingInfoTypeRepository) {
        this.hibernateSessionMananger = hibernateSessionMananger;
        this.trackingInfoTypeRepository = trackingInfoTypeRepository;
    }


    @Override
    public SessionManager getSessionManager() {
        return hibernateSessionMananger;
    }


    public List<TrackingInfoEntity> toTrackingInfoEntities(List<TrackingInfo> trackingInfos, TrackingEntiy trackingEntiy){
       List<TrackingInfoEntity> trackingInfoEntities = new ArrayList<>();
        for (TrackingInfo trackingInfo : trackingInfos) {
            TrackingInfoEntity entity = new TrackingInfoEntity();

            entity.setValue(trackingInfo.getInfoValue());
            TrackingInfoTypeEntity trackingInfoTypeEntity= trackingInfoTypeRepository.createTrackingInfoTypeEntity(trackingInfo.getInfoName());
            entity.setTrackingInfoTypeEntity(trackingInfoTypeEntity);
            entity.setTrackingEntiy(trackingEntiy);
            trackingInfoEntities.add(entity);
        }
        return trackingInfoEntities;
    }


    public void persist(List<TrackingInfoEntity> entityList){
        for(TrackingInfoEntity entity : entityList){
            persist(entity);
        }
    }

    public void persist(TrackingInfoEntity entity) {
        hibernateSessionMananger.getSession().persist(entity);
    }


    protected List<TrackingInfo> toTrackingInfos(List<TrackingInfoEntity> entities){
        List<TrackingInfo> trackingInfos = new ArrayList<>();
        for (TrackingInfoEntity entity : entities) {
            TrackingInfo trackingInfo = new SimpleTrackingInfo(entity.getTrackingInfoTypeEntity().getName(),entity.getValue());

            trackingInfos.add(trackingInfo);
        }
        return trackingInfos;
    }



    @Override
    public List<TrackingInfo> query(Spezification<TrackingInfo> trackingInfoSpezification) {
        List<Spezification<TrackingInfo>> spezifications = new ArrayList<>();
        spezifications.add(trackingInfoSpezification);
        return query(spezifications);
    }


        @Override
    public List<TrackingInfo> query(List<Spezification<TrackingInfo>> trackingInfoSpezification) {


        Session session = hibernateSessionMananger.getSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<TrackingInfoEntity> criteriaQuery = criteriaBuilder.createQuery(TrackingInfoEntity.class);
        Root<TrackingInfoEntity> root = criteriaQuery.from(TrackingInfoEntity.class);
        criteriaQuery.select(root);
        Query query = session.createQuery(criteriaQuery);
        List<TrackingInfoEntity> list = query.getResultList();

        List<TrackingInfo> trackingInfos = toTrackingInfos(list);

        List<TrackingInfo> validItems = new ArrayList<>();
        boolean valid = false;
        for (TrackingInfo info : trackingInfos) {

            valid = true;
            for(Spezification<TrackingInfo> trackingInfoSpezification1 : trackingInfoSpezification){
                if (!trackingInfoSpezification1.isSatiesfiedBy(info)) {
                    valid = false;
                    break;
                }
            }

            if(valid){
                validItems.add(info);
            }
        }


        return validItems;
    }

    @Override
    public Spezification<TrackingInfo> getSpezification(String interfaceName) {
        if(TrackingInfoFilenameSpezification.class.getName().equals(interfaceName)  ){
            return new HibernateTrackingInfoFilenameSpezification();
        }else{
            throw new IllegalArgumentException("Unsupported spezification: "+ interfaceName);
        }

    }


}
