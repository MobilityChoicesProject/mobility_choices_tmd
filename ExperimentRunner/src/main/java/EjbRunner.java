import at.fhv.tmd.common.Coordinate;
import at.fhv.tmd.common.ICoordinate;
import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.HibernateUtil;
import at.fhv.transportClassifier.dal.daos.HibernateLeightweightAccelerationValueDao;
import at.fhv.transportClassifier.dal.daos.HibernateLeightweightTrackingDao;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateBoundingBoxRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateDatapointsRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingInfoRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentBagRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentTypeRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.TrackingInfoTypeRepository;
import at.fhv.transportClassifier.dal.interfaces.LeightweightAccelerationValueDao;
import at.fhv.transportClassifier.dal.interfaces.TrackingRepository;
import at.fhv.transportClassifier.mainserver.impl.GisQuerierService;
import ch.qos.logback.classic.Level;
import experiments.ClassificationEvaluationExperiment;
import experiments.ExportExperiment;
import experiments.FeatureCalculationExperiment;
import experiments.GisDataCreationExperiment;
import experiments.ManualyEditedAnalyzationExperiment;
import experiments.SegmentationEvaluationExperiment;
import experiments.TmdServiceExperiment;
import helper.GpsTrackingDaoIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Johannes on 13.06.2017.
 */
public class EjbRunner {
  private static Logger logger = LoggerFactory.getLogger(EjbRunner.class);

  private Session session;
  private HibernateLeightweightTrackingDao leightweightTrackingDao;
  TrackingRepository trackingRepository;

  private void initHibernate() {

    org.hibernate.SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    session = sessionFactory.openSession();
    HibernateSessionMananger hibernateSessionMananger = new HibernateSessionMananger(session);
    LeightweightAccelerationValueDao leightweightAccelerationValueDao = new HibernateLeightweightAccelerationValueDao(
        hibernateSessionMananger);

    TrackingInfoTypeRepository trackingInfoTypeRepository = new TrackingInfoTypeRepository(
        hibernateSessionMananger);
    HibernateTrackingInfoRepository trackingInfoRepository = new HibernateTrackingInfoRepository(
        hibernateSessionMananger, trackingInfoTypeRepository);
    HibernateDatapointsRepository hibernateDatapointsRepository = new HibernateDatapointsRepository(
        hibernateSessionMananger);
    HibernateTrackingSegmentTypeRepository hibernateTrackingSegmentTypeRepository = new HibernateTrackingSegmentTypeRepository(
        hibernateSessionMananger);
    HibernateBoundingBoxRepository hibernateBoundingBoxRepository = new HibernateBoundingBoxRepository(
        hibernateSessionMananger);
    HibernateTrackingSegmentRepository hibernateTrackingSegmentRepository = new HibernateTrackingSegmentRepository(
        hibernateSessionMananger, hibernateBoundingBoxRepository,
        hibernateTrackingSegmentTypeRepository);
    HibernateTrackingSegmentBagRepository hibernateTrackingSegmentBagRepository = new HibernateTrackingSegmentBagRepository(
        hibernateSessionMananger, hibernateTrackingSegmentRepository);
    HibernateTrackingRepository hibernateTrackingRepository = new HibernateTrackingRepository(
        hibernateSessionMananger, trackingInfoRepository, hibernateTrackingSegmentBagRepository,
        hibernateBoundingBoxRepository, hibernateDatapointsRepository);

    leightweightTrackingDao = new HibernateLeightweightTrackingDao(hibernateSessionMananger,
        leightweightAccelerationValueDao, hibernateTrackingRepository);

     trackingRepository = new HibernateTrackingRepository(hibernateSessionMananger,trackingInfoRepository,hibernateTrackingSegmentBagRepository,hibernateBoundingBoxRepository,hibernateDatapointsRepository);

  }

  public static void main(String[] args) throws NamingException, IOException, ClassNotFoundException {

    EjbRunner ejbRunner = new EjbRunner();
    ejbRunner.initHibernate();

    EntityManagerFactory emf =
        Persistence.
            createEntityManagerFactory(
                "persistence_context_mysql");
    EntityManager em = emf.createEntityManager();

    ExportExperiment exportExperiment = new ExportExperiment();
    exportExperiment.doit(ejbRunner.leightweightTrackingDao);


    TmdServiceExperiment tmdServiceExperiment = new TmdServiceExperiment();
    tmdServiceExperiment.doIt(emf,ejbRunner.leightweightTrackingDao);


    ManualyEditedAnalyzationExperiment manualyEditedAnalyzationExperiment = new ManualyEditedAnalyzationExperiment();
    manualyEditedAnalyzationExperiment.doExperiment(new GpsTrackingDaoIterator(ejbRunner.leightweightTrackingDao));

    FeatureCalculationExperiment featureCalculationExperiment = new FeatureCalculationExperiment( ejbRunner.leightweightTrackingDao,emf);
    featureCalculationExperiment.doIt(true,false);

    ClassificationEvaluationExperiment classificationEvaluationExperiment = new ClassificationEvaluationExperiment(ejbRunner.leightweightTrackingDao);
    classificationEvaluationExperiment.doIt(emf);




    SegmentationEvaluationExperiment segmentationEvaluationExperiment = new SegmentationEvaluationExperiment();
    segmentationEvaluationExperiment.doIt(ejbRunner.leightweightTrackingDao);



    System.exit(3);


    }



}
