package at.fhv.transportClassifier.dal.databaseRepositories;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentTypeEntity;
import at.fhv.transportClassifier.dal.interfaces.Repository;
import at.fhv.transportClassifier.dal.interfaces.SessionManager;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

/**
 * Created by Johannes on 14.02.2017.
 */
public class HibernateTrackingSegmentTypeRepository implements Repository {
    private boolean uninitialized = true;

    @Override
    public SessionManager getSessionManager() {
        return null;
    }

    protected HibernateSessionMananger hibernateSessionMananger;

    public HibernateTrackingSegmentTypeRepository(HibernateSessionMananger hibernateSessionMananger) {
        this.hibernateSessionMananger = hibernateSessionMananger;
    }


    protected Map<String,TrackingSegmentTypeEntity> transportTypeTrackingSegmentTypeEntityMap = new TreeMap<>();




    public TrackingSegmentTypeEntity createTrackingSegmentType(TransportType transportType){

        if(uninitialized){

            Session session = hibernateSessionMananger.getSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<TrackingSegmentTypeEntity> criteriaQuery = criteriaBuilder.createQuery(TrackingSegmentTypeEntity.class);
            Root<TrackingSegmentTypeEntity> root = criteriaQuery.from(TrackingSegmentTypeEntity.class);
            criteriaQuery.select(root);
            Query query = session.createQuery(criteriaQuery);
            List<TrackingSegmentTypeEntity> list = query.getResultList();

            for (TrackingSegmentTypeEntity entity : list){
                transportTypeTrackingSegmentTypeEntityMap.put(entity.getName(),entity);}

            uninitialized= true;
        }


        String name = transportType.name();
        if(transportTypeTrackingSegmentTypeEntityMap.containsKey(name)){
            return transportTypeTrackingSegmentTypeEntityMap.get(name);
        }else{
            TrackingSegmentTypeEntity trackingSegmentTypeEntity = new TrackingSegmentTypeEntity();
            trackingSegmentTypeEntity.setName(name);
            hibernateSessionMananger.getSession().persist(trackingSegmentTypeEntity);
            transportTypeTrackingSegmentTypeEntityMap.put(name,trackingSegmentTypeEntity);
            return  trackingSegmentTypeEntity;
        }


    }


}
