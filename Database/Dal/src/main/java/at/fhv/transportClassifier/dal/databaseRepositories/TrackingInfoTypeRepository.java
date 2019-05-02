package at.fhv.transportClassifier.dal.databaseRepositories;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoTypeEntity;
import at.fhv.transportClassifier.dal.interfaces.Repository;
import at.fhv.transportClassifier.dal.interfaces.SessionManager;
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
public class TrackingInfoTypeRepository implements Repository{

    private boolean uninitialized = true;
    private HibernateSessionMananger sessionMananger;

    public TrackingInfoTypeRepository(HibernateSessionMananger sessionMananger) {
        this.sessionMananger = sessionMananger;
    }

    private Map<String,TrackingInfoTypeEntity> trackingInfoTypeEntityMap = new TreeMap<>();

    public TrackingInfoTypeEntity createTrackingInfoTypeEntity(String name){
        if(uninitialized){
            Session session = sessionMananger.getSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<TrackingInfoTypeEntity> criteriaQuery = criteriaBuilder.createQuery(TrackingInfoTypeEntity.class);
            Root<TrackingInfoTypeEntity> root = criteriaQuery.from(TrackingInfoTypeEntity.class);
            criteriaQuery.select(root);
            Query query = session.createQuery(criteriaQuery);
            List<TrackingInfoTypeEntity> list = query.getResultList();

            for (TrackingInfoTypeEntity entity : list){
                trackingInfoTypeEntityMap.put(entity.getName(),entity);
            }

            uninitialized = false;
        }


        TrackingInfoTypeEntity trackingInfoTypeEntity;
        if(trackingInfoTypeEntityMap.containsKey(name)){
            trackingInfoTypeEntity = trackingInfoTypeEntityMap.get(name);
        }else{
            trackingInfoTypeEntity = new TrackingInfoTypeEntity();
            trackingInfoTypeEntity.setName(name);
            sessionMananger.getSession().persist(trackingInfoTypeEntity);
            trackingInfoTypeEntityMap.put(name,trackingInfoTypeEntity);
        }
        return trackingInfoTypeEntity;
    }



    @Override
    public SessionManager getSessionManager() {
        return null;
    }
}
