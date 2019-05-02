//package at.fhv.transportClassifier.mainserver.bean;
//
//import at.fhv.gis.GisDataCreationService;
//import at.fhv.gis.GisDataDao;
//import at.fhv.gis.Overpass.GisDataCreationException;
//import at.fhv.gis.entities.db.GisDataUpdate;
//import at.fhv.transportClassifier.mainserver.api.GisDataUpdaterLocal;
//import at.fhv.transportClassifier.mainserver.transaction.BeanTransaction;
//import at.fhv.transportdetector.trackingtypes.BoundingBox;
//import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.ejb.Schedule;
//import javax.ejb.Singleton;
//import javax.ejb.Timer;
//import javax.ejb.TimerConfig;
//import javax.ejb.TimerService;
//import javax.ejb.TransactionManagement;
//import javax.ejb.TransactionManagementType;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.transaction.SystemException;
//import javax.transaction.UserTransaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Created by Johannes on 11.08.2017.
// */
//@Singleton
//@TransactionManagement(value= TransactionManagementType.BEAN)
//public class GisDataUpdaterBean implements GisDataUpdaterLocal {
//
//  private static Logger logger = LoggerFactory.getLogger(GisDataUpdaterBean.class);
//
//
//  GisDataCreationService gisDataCreationService ;
//  GisDataDao gisDataDao = new GisDataDao();
//
//
//
//
//  @Resource
//  TimerService timerService;
//
//
//  @PostConstruct
//  private void init(){
//    gisDataCreationService= new GisDataCreationService(gisDataDao);
//    logger.info("GisDataUpdater loaded");
//    resume();
//  }
//
//  @PersistenceContext(unitName = "persistence_context_mysql")
//  private EntityManager em;
//
//
//  @Resource
//  private UserTransaction ut;
//
//
//
//
//
//
//  @Override
//  @Schedule(timezone="Europe/Berlin", dayOfMonth="1" ,hour="3")
//  public void updateGisData(Timer timer){
//
//    timer.getInfo()
//    logger.info("Starting GIS update process");
//    gisDataDao.init(em,new BeanTransaction(ut));
//    try{
////      BoundingBox boundingBox = new SimpleBoundingBox(46.5,6.5,48.5,15.5);
//      BoundingBox boundingBox = new SimpleBoundingBox(47.5,7.0,48.5,9);
//      gisDataCreationService.create(boundingBox);
//      logger.info("Finished GIS update process");
//    }catch (Exception ex){
//     logger.error("There was an exception during the gis update process",ex);
//      try {
//        ut.rollback();
//      } catch (SystemException e) {
//        logger.error("There was an exception during the gis update process",e);
//      }
//    }
//
//  }
//
//  @Override
//  public void start(){
//
//    gisDataDao.init(em,new BeanTransaction(ut));
//    updateGisData(null);
//  }
//
//  @Override
//  public void stop()
//  {
//    gisDataDao.init(em,new BeanTransaction(ut));
//    gisDataCreationService.stop();
//  }
//
//
//  @Override
//  public void resume(){
//    try {
//      gisDataDao.init(em,new BeanTransaction(ut));
//      gisDataCreationService.resumeLastFailed();
//    } catch (GisDataCreationException e) {
//      logger.error("There was an exception during the resume gis update process",e);
//
//    }
//
//  }
//
//  @Override
//  public List<? extends GisDataUpdate> get10LastStatus(){
//      List<? extends GisDataUpdate> lastUpdates  = null;
//
//    try{
//      gisDataDao.init(em,new BeanTransaction(ut));
//      lastUpdates = gisDataCreationService.get10LastUpdates();
//
//    }catch (GisDataCreationException ex){
//      logger.error("There was an exception during the gis update process",ex);
//    }
//    return lastUpdates;
//
//  }
//
//
//
//
//
//
//
//}
