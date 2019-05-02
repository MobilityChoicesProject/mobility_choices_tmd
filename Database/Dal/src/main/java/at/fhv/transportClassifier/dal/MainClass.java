package at.fhv.transportClassifier.dal;

import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoTypeEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by Johannes on 13.02.2017.
 */
public class MainClass {


    public static void main(String args[]){

        try{
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();


            TrackingInfoTypeEntity entiy = new TrackingInfoTypeEntity();
            entiy.setName("origin");
//            session.save(entiy);


            TrackingInfoTypeEntity enti = session.load(TrackingInfoTypeEntity.class,1);

            TrackingInfoEntity trackingInfo = new TrackingInfoEntity();
            trackingInfo.setTrackingInfoTypeEntity(enti);
            trackingInfo.setValue("test");

            session.persist(trackingInfo);
            session.persist(trackingInfo);
            session.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

}
