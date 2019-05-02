package at.fhv.transportClassifier.mainserver.bean.gis;


import at.fhv.gis.CurrentUpdateStatus;
import at.fhv.gis.CurrentUpdateStatusProvider;
import at.fhv.gis.Overpass.GisDataCreationException;
import at.fhv.gis.Overpass.GisDataException;
import at.fhv.gis.Overpass.GisDataGateway;
import at.fhv.gis.Overpass.OverpassReturnValue;
import at.fhv.gis.OverpassDataType;
import at.fhv.gis.entities.OverpassEntities.Node;
import at.fhv.gis.entities.db.GisArea;
import at.fhv.gis.entities.db.GisDataUpdate;
import at.fhv.gis.entities.db.GisDataUpdateEntity;
import at.fhv.gis.entities.db.GisDataUpdateStatusEntity;
import at.fhv.gis.entities.db.GisPoint;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import at.fhv.transportClassifier.mainserver.bean.OverpassRequestServiceLocal;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
@ConcurrencyManagement(value = javax.ejb.ConcurrencyManagementType.BEAN)
@TransactionManagement(value= TransactionManagementType.BEAN)
public class GisService implements GisServiceLocal {

  private static Logger logger = LoggerFactory.getLogger(GisService.class);
  private CurrentUpdateStatusProvider currentUpdateStatusProvider = new CurrentUpdateStatusProvider();

  private final Object gisStatusUpdateLock = new Object();
  private final Object accessLock  =new Object();
  private final Object stopLock = new Object();

  private Future activeFuture = null;

  private ArrayList<GisDataUpdateEntity> last10GisStatus = new ArrayList();

  private double latitudeTileSize = 1;
  private double longitudeTileSize = 1;
//  private BoundingBox boundingBox =  new SimpleBoundingBox(47,9,48.0,10.0);
  private BoundingBox boundingBox =  new SimpleBoundingBox(46.5,6.5,48.5,15.5);
  private boolean stopTriggered = false;

  @Resource
  private TimerService timerService;

  @PersistenceContext(unitName = "persistence_context_mysql")
  private EntityManager em;

  @Resource
  private UserTransaction ut;

  @Resource
  ManagedExecutorService scheduler;

  @EJB
  private GisDataDaoLocal gisDao;

  @EJB
  OverpassRequestServiceLocal overpassRequestServiceLocal;

  @PostConstruct
  private void init(){
    SchedulePlan schedulePlan = new SchedulePlan();
    schedulePlan.setDayOfMonth("3");
    schedulePlan.setHour("3");
    schedulePlan.setMinute("15");
    ScheduleExpression expression = schedulePlan.generateScheduleExpression();
    logger.info("initializing calendar Timer for gis data update process");
    timerService.createCalendarTimer(expression);
    logger.info("initialized calendar Timer for gis data update process");

  }

  @Timeout
  private void calendarTimerHandler(){
    start();
  }

  @Override
  public void start(){
     synchronized(accessLock){
       if(activeFuture== null){
         activeFuture = scheduler.submit(() -> {internalStart();
        });

       }else{
         logger.info("Service already active. Nothing to start.");
       }
     }
  }

   @Override
   public void cancel() {
     synchronized (accessLock) {
       if(activeFuture != null){
         activeFuture = scheduler.submit(() -> {internalStop();
         });
       }else{
         logger.info("No service active. Nothing to stop.");
       }
     }
   }


  @Override
  synchronized public void resume(){
    synchronized (accessLock) {
      if(activeFuture== null){
        activeFuture = scheduler.submit(() -> {internalResume();
        });

      }else{
        logger.info("Service already active. Nothing to resume.");
      }
    }
  }


  @Override
  synchronized public CurrentUpdateStatus getCurrentStatus(){
    synchronized (accessLock) {
      return currentUpdateStatusProvider.getCurrentUpdateStatus();
    }
  }


  @Override
  synchronized public List<? extends GisDataUpdate> get10LastStatus(){
    synchronized (accessLock) {
      synchronized (gisStatusUpdateLock){
        if(activeFuture == null){
          updateGisStatus();
        }
        return new ArrayList<>(last10GisStatus);
      }

    }
  }

  // internal - internal - internal - internal - internal - internal - internal - internal - internal

  protected void internalStart(){
    try{
      GisDataUpdateEntity gisDataUpdateEntity = new GisDataUpdateEntity();
      gisDataUpdateEntity.setTimestamp(LocalDateTime.now());
      boundingBox = expandBoundingboxToFrameSize(boundingBox);
      double westLongitude = boundingBox.getWestLongitude();
      double eastLongitude = boundingBox.getEastLongitude();

      gisDataUpdateEntity.setWestLongitude(westLongitude);
      gisDataUpdateEntity.setEastLongitude(eastLongitude);
      gisDataUpdateEntity.setSouthLatitude(boundingBox.getSouthLatitude());
      gisDataUpdateEntity.setNorthLatitude(boundingBox.getNorthLatitude());
      gisDataUpdateEntity.setLatitudeTileSize(latitudeTileSize);
      gisDataUpdateEntity.setLongitudeTileSize(longitudeTileSize);

      double longitudeDiff = eastLongitude - westLongitude;
      int longitudeSteps = (int)( longitudeDiff / longitudeTileSize);

      double latitudeDiff = boundingBox.getNorthLatitude() - boundingBox.getSouthLatitude();
      int latitudeSteps = (int)(latitudeDiff/ latitudeTileSize);

      int numberOfBoxes = longitudeSteps * latitudeSteps;
      gisDataUpdateEntity.setNumberOfBoxes(numberOfBoxes*4);
      logger.info("has to update {} boxes",numberOfBoxes*4);

      gisDao.initGisData(gisDataUpdateEntity);
      update(gisDataUpdateEntity);


    }catch (Exception |  Error ex){
      logger.error("There was an unexpected exception during the start",ex);
    }finally {
      synchronized (stopLock){
        activeFuture = null;
        deactivateStop();
      }
    }

  }
  protected void internalResume(){
    try{
      updateGisStatus();
      if (last10GisStatus.size() >0) {
        GisDataUpdateEntity gisDataUpdateEntity  = last10GisStatus.get(0);
        if(gisDataUpdateEntity.getStatus()== GisDataUpdateStatusEntity.running){
          update(gisDataUpdateEntity);
        }
      }


    }catch (Exception |  Error ex){
      logger.error("There was an unexpected exception during the resume",ex);
    }finally {
      synchronized (stopLock){
        activeFuture = null;
        deactivateStop();
      }
    }
  }

  protected void internalStop() {
    try{
      synchronized (stopLock){
        if(activeFuture != null){
          stop();
        }
      }
    }catch (Exception |  Error ex){
      logger.error("There was an unexpected exception during the stop",ex);
    }
  }


  private void update(GisDataUpdateEntity gisDataUpdateEntity)
      throws GisDataCreationException, GisPersistenceException, OverpassFileRequestFailedException {

    try{
      LatitudeLongitudeSteps latitudeLongitudeSteps = initStatusProvider(gisDataUpdateEntity);
      int latitudeSteps = latitudeLongitudeSteps.getLatitudeSteps();
      int longitudeSteps = latitudeLongitudeSteps.getLongitudeSteps();

      OverpassDataType[] overpassDataTypes = OverpassDataType.values();
      int numberOfTiles = gisDataUpdateEntity.getNumberOfTiles();
      currentUpdateStatusProvider.setAllTiles(numberOfTiles);
      currentUpdateStatusProvider.setUpdatedTiles(gisDataUpdateEntity.getNumberOfUpdatedTiles());
      currentUpdateStatusProvider.setBoundingBox(boundingBox);
      currentUpdateStatusProvider.setLatitudeTileSize(latitudeTileSize);
      currentUpdateStatusProvider.setLongitudeTileSize(longitudeTileSize);
      currentUpdateStatusProvider.setRunning(true);

      //    int updatedBoxNumber = gisDataUpdateEntity.getNumberOfUpdatedTiles();
      int updatedBoxNumber =0;
      for(int i = 0; i < latitudeSteps;i++){
        for (int j = 0; j < longitudeSteps;j++){

          BoundingBox frameBoundingBox = calcFrame(i,j,gisDataUpdateEntity);

          for (OverpassDataType overpassDataType : overpassDataTypes) {

            if(isStopped())return;
            if(updatedBoxNumber==gisDataUpdateEntity.getNumberOfUpdatedTiles()) {
              File file = getFile(frameBoundingBox, overpassDataType);
              saveGisFileToDb(file, overpassDataType, frameBoundingBox, gisDataUpdateEntity);
              updateGisStatus();
              currentUpdateStatusProvider.setUpdatedTiles(updatedBoxNumber+1);
              logger.info("updated {} of {}",updatedBoxNumber+1, numberOfTiles);
            }
            updatedBoxNumber++;
          }
        }
      }

      gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.ok);
      gisDao.activateStatus(gisDataUpdateEntity);
      updateGisStatus();

    } finally {
      currentUpdateStatusProvider.setRunning(false);
    }

  }

  private void saveGisFileToDb(File file, OverpassDataType overpassDataType, BoundingBox frame,
      GisDataUpdateEntity gisDataUpdateEntity) throws GisPersistenceException {
    try {

      // might throw a FileNotFoundException
      FileInputStream fileInputStream = new FileInputStream(file);

      GisDataGateway gIsDataGateway = new GisDataGateway();
      // might throw a GisDataException
      OverpassReturnValue overpassData = gIsDataGateway.importData(fileInputStream);


      List<GisPoint> gisPoints = new ArrayList<GisPoint>();
      for (Node node : overpassData.getNodes()) {
        if(frame.containsWithLowerBorders(node.getLocation().getLatitude(),node.getLocation().getLongitude())){
          gisPoints.add(craeteGisPoint(node.getLocation().getLatitude(),node.getLocation().getLongitude(),overpassDataType));
        }
      }

      GisArea gisArea = new GisArea();
      gisArea.setType(overpassDataType.name());
      gisArea.setUpdateTime(LocalDateTime.now());
      gisArea.setBoundingbox(frame.getSouthLatitude(),frame.getNorthLatitude(),frame.getWestLongitude(),frame.getEastLongitude());

      gisDataUpdateEntity.setNumberOfUpdatedFrames(gisDataUpdateEntity.getNumberOfUpdatedTiles()+1);

      gisArea.setGisUpdate(gisDataUpdateEntity);

        //might throw a TransactionException
        gisDao.updateGisData(gisDataUpdateEntity,gisArea,gisPoints);

    } catch (FileNotFoundException|GisDataException e) {
      if(e instanceof TransactionException){
        logger.error("Failed to persist overpass file in db",e);
        throw new GisPersistenceException("Failed to persist overpass file in db",e);
      }
      if(e instanceof GisDataException){
        logger.error("Downloaded Overpass file cannot be parsed",e);
        throw new GisPersistenceException("Downloaded Overpass file cannot be parsed",e);
      }
      if(e instanceof FileNotFoundException){
        logger.error("downloaded Overpass File not found",e);
        throw new GisPersistenceException("Downloaded Overpass file not found",e);
      }
    }
  }



  private GisPoint craeteGisPoint(double latitude, double longitude,OverpassDataType overpassDataType){
    GisPoint gisPoint = new GisPoint();
    gisPoint.setPointType(overpassDataType.getValue());
    gisPoint.setPositionByDouble(latitude,longitude);
    return gisPoint;
  }

  private void stop(){
    stopTriggered=true;
  }
  private void deactivateStop(){
    stopTriggered = false;
  }

  private boolean isStopped(){
    return stopTriggered;
  }

  private File getFile(BoundingBox frameBoundingBox, OverpassDataType busRoutes) throws OverpassFileRequestFailedException{
    Future<File> futureFile = overpassRequestServiceLocal.getFile(frameBoundingBox, busRoutes);
    try {
      logger.info("getting file from overpass");
      File file = futureFile.get(10, TimeUnit.MINUTES);
      logger.info("got file from overpass");

      return file;

    } catch (InterruptedException e) {
      throw new OverpassFileRequestFailedException("There was an interrupt",e);
    } catch (ExecutionException e) {
      throw new OverpassFileRequestFailedException("There was an exception during the overpass request",e.getCause());
    } catch (TimeoutException e) {
      throw new OverpassFileRequestFailedException("Download took to long",e);
    }


  }

  private BoundingBox calcFrame(int i, int j, GisDataUpdateEntity gisDataUpdateEntity) {
    double latitudeTileSize = gisDataUpdateEntity.getLatitudeTileSize();
    double longitudeTileSize = gisDataUpdateEntity.getLongitudeTileSize();
    double frameSoutLatitude = gisDataUpdateEntity.getSouthLatitude() + i * latitudeTileSize;
    double frameNorthLatitude = gisDataUpdateEntity.getSouthLatitude()+ i * latitudeTileSize+latitudeTileSize;

    double frameWestLongitude = gisDataUpdateEntity.getWestLongitude() + j * longitudeTileSize;
    double frameEastLongitude = gisDataUpdateEntity.getWestLongitude() +j * longitudeTileSize+longitudeTileSize;
    return new SimpleBoundingBox(frameSoutLatitude,frameWestLongitude,frameNorthLatitude,frameEastLongitude);
  }

  private LatitudeLongitudeSteps initStatusProvider(GisDataUpdateEntity gisDataUpdateEntity){
    double latitudeFrameSize = gisDataUpdateEntity.getLatitudeTileSize();
    double longitudeFrameSize = gisDataUpdateEntity.getLongitudeTileSize();
    double southLatitude = gisDataUpdateEntity.getSouthLatitude();
    double westLongitude = gisDataUpdateEntity.getWestLongitude();

    double longitudeDiff = gisDataUpdateEntity.getEastLongitude() - westLongitude;
    int longitudeSteps = (int)( longitudeDiff / longitudeFrameSize);

    double latitudeDiff = gisDataUpdateEntity.getNorthLatitude() - southLatitude;
    int latitudeSteps = (int)(latitudeDiff/latitudeFrameSize);

    currentUpdateStatusProvider.setAllTiles(latitudeSteps*longitudeSteps*4);
    currentUpdateStatusProvider.setBoundingBox(new SimpleBoundingBox(southLatitude,westLongitude,gisDataUpdateEntity.getNorthLatitude(),gisDataUpdateEntity.getEastLongitude()));
    currentUpdateStatusProvider.setLatitudeTileSize(latitudeFrameSize);
    currentUpdateStatusProvider.setLongitudeTileSize(longitudeFrameSize);
    currentUpdateStatusProvider.setUpdatedTiles(gisDataUpdateEntity.getNumberOfUpdatedTiles());
    currentUpdateStatusProvider.setRunning(true);
    return new LatitudeLongitudeSteps(latitudeSteps,longitudeSteps);
  }

  private void updateGisStatus() {
    List<GisDataUpdateEntity> last10DataUpdates = gisDao.getLast10DataUpdates();

    synchronized (gisStatusUpdateLock){
      this.last10GisStatus.clear();
      for (GisDataUpdateEntity gisStatus : last10DataUpdates) {
        this.last10GisStatus.add(gisStatus);
      }
    }

  }

  private BoundingBox expandBoundingboxToFrameSize(BoundingBox boundingBox)
  {

    double diff = boundingBox.getWestLongitude() % longitudeTileSize;
    double westLongitude =  boundingBox.getWestLongitude()-diff;

    diff = boundingBox.getSouthLatitude() % latitudeTileSize;
    double southLatitude =  boundingBox.getSouthLatitude()-diff;

    double eastLongitude;
    diff = boundingBox.getEastLongitude() % longitudeTileSize;
    if(diff != 0){
      eastLongitude =  boundingBox.getEastLongitude()-diff+ longitudeTileSize;
    }else{
      eastLongitude = boundingBox.getEastLongitude();
    }

    double northLatitude;
    diff = boundingBox.getNorthLatitude() % latitudeTileSize;
    if(diff != 0){
      northLatitude =  boundingBox.getNorthLatitude()-diff+ latitudeTileSize;
    }else{
      northLatitude = boundingBox.getNorthLatitude();
    }

    return new SimpleBoundingBox(southLatitude,westLongitude,northLatitude,eastLongitude);

  }

}
