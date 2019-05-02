package at.fhv.transportClassifier.dal;

import at.fhv.transportClassifier.dal.databaseEntities.AccelerationValueEntity;
import at.fhv.transportClassifier.dal.databaseEntities.BoundingBoxEntity;
import at.fhv.transportClassifier.dal.databaseEntities.DisplayStateEventEntity;
import at.fhv.transportClassifier.dal.databaseEntities.GpsPointEntity;
import at.fhv.transportClassifier.dal.databaseEntities.GpsTrackingEntity;
import at.fhv.transportClassifier.dal.databaseEntities.StateChange;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingEntiy;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingInfoTypeEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentBagEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentEntity;
import at.fhv.transportClassifier.dal.databaseEntities.TrackingSegmentTypeEntity;
import at.fhv.transportClassifier.dal.leightweightdatabaseentities.LeightweightTrackingEntity;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Created by Johannes on 09.02.2017.
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            // loads configuration and mappings

            Configuration configuration = new Configuration();
            configuration.addPackage("at.fhv.transportClassifier.dal.databaseEntities") //the fully qualified package name
                    .addAnnotatedClass(BoundingBoxEntity.class)
                    .addAnnotatedClass(AccelerationValueEntity.class)
                    .addAnnotatedClass(DisplayStateEventEntity.class)
                    .addAnnotatedClass(GpsPointEntity.class)
                    .addAnnotatedClass(StateChange.class)
                    .addAnnotatedClass(TrackingEntiy.class)
                    .addAnnotatedClass(TrackingInfoTypeEntity.class)
                    .addAnnotatedClass(TrackingInfoEntity.class)
                    .addAnnotatedClass(TrackingSegmentTypeEntity.class)
                    .addAnnotatedClass(TrackingSegmentBagEntity.class)
                    .addAnnotatedClass(TrackingSegmentEntity.class)
                    .addAnnotatedClass(LeightweightTrackingEntity.class)
                    .addAnnotatedClass(GpsTrackingEntity.class)
                    .setProperty("hibernate.connection.driver_class","com.mysql.jdbc.Driver")
                    .setProperty("hibernate.connection.url","jdbc:mysql://127.0.0.1:3308/sys")
                    .setProperty("hibernate.connection.username","root")
                    .setProperty("hibernate.dialect","org.hibernate.dialect.MySQL5Dialect")
                    .buildSessionFactory();
            ServiceRegistry serviceRegistry
                    = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            // builds a session factory from the service registry
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);




        }

        return sessionFactory;
    }
}
