package at.fhv.gis;

import at.fhv.gis.entities.db.ConfigSettingEntity;
import at.fhv.gis.entities.db.GisArea;
import at.fhv.gis.entities.db.GisPoint;
import at.fhv.gis.entities.db.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Created by Johannes on 09.02.2017.
 */
public class GisHibernateUtil {
    private static SessionFactory sessionFactory;

    public static Session getSession(){
        if(sessionFactory == null){
            sessionFactory =getSessionFactory();
        }
        return sessionFactory.openSession();
    }


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            // loads configuration and mappings

            Configuration configuration = new Configuration();
            configuration.addPackage("at.fhv.gis.entities") //the fully qualified package name
                    .addAnnotatedClass(GisPoint.class)
                    .addAnnotatedClass(GisArea.class)
                    .addAnnotatedClass(ConfigSettingEntity.class)
                    .addAnnotatedClass(UserEntity.class)
                    .setProperty("hibernate.connection.driver_class","com.mysql.jdbc.Driver")
                    .setProperty("hibernate.connection.url","jdbc:mysql://127.0.0.1:3306/gis_data")
                    .setProperty("hibernate.connection.username","root")
                    .setProperty("hibernate.dialect","org.hibernate.spatial.dialect.mysql.MySQLSpatialDialect")
                    .setProperty("hibernate.jdbc.batch_size","500")
                    .setProperty("hibernate.cache.use_second_level_cache","false")
                    .buildSessionFactory();
            ServiceRegistry serviceRegistry
                    = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);




        }

        return sessionFactory;
    }
}
