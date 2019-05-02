package at.fhv.gis;


import at.fhv.gis.Overpass.GisDataCreationException;
import at.fhv.gis.Overpass.GisDataException;
import at.fhv.gis.Overpass.GisDataGateway;
import at.fhv.gis.Overpass.OverpassReturnValue;
import at.fhv.gis.entities.db.GisDataUpdate;
import at.fhv.gis.entities.db.GisDataUpdateEntity;
import at.fhv.gis.entities.OverpassEntities.Node;
import at.fhv.gis.entities.OverpassEntities.Way;
import at.fhv.gis.entities.db.GisArea;
import at.fhv.gis.entities.db.GisDataUpdateStatusEntity;
import at.fhv.gis.entities.db.GisPoint;
import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.ICoordinate;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 26.05.2017.
 */
public class GisDataCreationService {

  private static Logger logger = LoggerFactory.getLogger(GisDataCreationService.class);
  CurrentUpdateStatusProvider currentUpdateStatusProvider = new CurrentUpdateStatusProvider();

  private double latitudeTileSize = 0.5;
  private double longitudeTileSize = 0.5;
  private boolean stopped = false;
  boolean stopNotExecuted;

  private volatile State state = State.uninitialized;

  public GisDataCreationService(GisDataDao gisDataDao) {
    this.gisDataDao = gisDataDao;
  }

  GisDataDao gisDataDao =null;
  OverpassRequestService overpassDataService = new OverpassRequestService();

  public void create(BoundingBox boundingBox) throws GisDataCreationException {
    while(stopped && stopNotExecuted){

    }
    stopped = false;
    stopNotExecuted= false;
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

    update(gisDataUpdateEntity);
  }




  public void resumeLastFailed() throws GisDataCreationException {
    while(stopped && stopNotExecuted){

    }
    stopped = false;
    stopNotExecuted= false;
    List<GisDataUpdateEntity> lastDataUpdates = null;
    try {
      lastDataUpdates = gisDataDao.getLastDataUpdates(1, false);
    } catch (TransactionException e) {
      throw new GisDataCreationException(e);
    }
    if(lastDataUpdates.size()== 0){
      logger.info("nothing to resume");
      return;
    }

    GisDataUpdateEntity gisDataUpdateEntity = lastDataUpdates.get(0);

    if(gisDataUpdateEntity.getStatus()!= GisDataUpdateStatusEntity.running){
      logger.info("nothing to resume");
      return;
    }

    update(gisDataUpdateEntity);
  }


  public List<? extends GisDataUpdate> get10LastUpdates() throws GisDataCreationException {

    List<GisDataUpdateEntity> lastDataUpdates = null;
    try {
      lastDataUpdates = gisDataDao.getLastDataUpdates(10, false);
    } catch (TransactionException e) {
      throw new GisDataCreationException(e);
    }

    return lastDataUpdates;

  }

  private void update(GisDataUpdateEntity gisDataUpdateEntity) throws GisDataCreationException {

    try{
      double latitudeFrameSize = gisDataUpdateEntity.getLatitudeTileSize();
      double longitudeFrameSize = gisDataUpdateEntity.getLongitudeTileSize();
      double southLatitude = gisDataUpdateEntity.getSouthLatitude();
      double westLongitude = gisDataUpdateEntity.getWestLongitude();

      double longitudeDiff = gisDataUpdateEntity.getEastLongitude() - westLongitude;
      int longitudeSteps = (int)( longitudeDiff / longitudeFrameSize);

      double latitudeDiff = gisDataUpdateEntity.getNorthLatitude() - southLatitude;
      int latitudeSteps = (int)(latitudeDiff/latitudeFrameSize);
      File file;

      currentUpdateStatusProvider.setAllTiles(latitudeSteps*longitudeSteps);
      currentUpdateStatusProvider.setBoundingBox(new SimpleBoundingBox(southLatitude,westLongitude,gisDataUpdateEntity.getNorthLatitude(),gisDataUpdateEntity.getEastLongitude()));
      currentUpdateStatusProvider.setLatitudeTileSize(latitudeFrameSize);
      currentUpdateStatusProvider.setLongitudeTileSize(longitudeFrameSize);
      currentUpdateStatusProvider.setUpdatedTiles(gisDataUpdateEntity.getNumberOfUpdatedTiles());
      currentUpdateStatusProvider.setRunning(true);

  //    int updatedBoxNumber = gisDataUpdateEntity.getNumberOfUpdatedTiles();
      int updatedBoxNumber =0;
      for(int i = 0; i < latitudeSteps;i++){
        for (int j = 0; j < longitudeSteps;j++){

          double frameSoutLatitude = southLatitude + i * latitudeFrameSize;
          double frameNorthLatitude = southLatitude + i * latitudeFrameSize+latitudeFrameSize;

          double frameWestLongitude = westLongitude + j * longitudeFrameSize;
          double frameEastLongitude = westLongitude +j * longitudeFrameSize+longitudeFrameSize;

          BoundingBox frameBoundingBox = new SimpleBoundingBox(frameSoutLatitude,frameWestLongitude,frameNorthLatitude,frameEastLongitude);
          try {

            if (isStopped(gisDataUpdateEntity)) {
              return;
            }
            if(updatedBoxNumber==gisDataUpdateEntity.getNumberOfUpdatedTiles()){
              file = overpassDataService.getFile(frameBoundingBox, OverpassDataType.BusRoutes);
              saveWayFileToDb(file,OverpassDataType.BusRoutes,frameBoundingBox, gisDataUpdateEntity);

            }
            updatedBoxNumber++;

            if (isStopped(gisDataUpdateEntity)) {
              return;
            }
            if(updatedBoxNumber==gisDataUpdateEntity.getNumberOfUpdatedTiles()){
              file = overpassDataService.getFile(frameBoundingBox, OverpassDataType.BusStops);
              savePointFileToDb(file,OverpassDataType.BusStops,frameBoundingBox,gisDataUpdateEntity);
            }
            updatedBoxNumber++;

            if (isStopped(gisDataUpdateEntity)) {
              return;
            }
            if(updatedBoxNumber==gisDataUpdateEntity.getNumberOfUpdatedTiles()){
              file = overpassDataService.getFile(frameBoundingBox, OverpassDataType.RailwayRoutes);
              saveWayFileToDb(file,OverpassDataType.RailwayRoutes,frameBoundingBox,gisDataUpdateEntity);
            }
            updatedBoxNumber++;

            if (isStopped(gisDataUpdateEntity)) {
              return;
            }
            if(updatedBoxNumber==gisDataUpdateEntity.getNumberOfUpdatedTiles()){
              file = overpassDataService.getFile(frameBoundingBox, OverpassDataType.RailwayStops);
              savePointFileToDb(file,OverpassDataType.RailwayStops,frameBoundingBox,gisDataUpdateEntity);
            }
            updatedBoxNumber++;

          } catch (OverpassDataException e) {
            gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.failed);
            throw new GisDataCreationException(e);
          } catch (FileNotFoundException e) {
            gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.failed);
            throw new GisDataCreationException(e);
          } catch (GisDataException e) {
            gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.failed);
            throw new GisDataCreationException(e);
          } catch (TransactionException e) {
            gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.failed);
            throw new GisDataCreationException(e);
          }
        }
      }
      try {
        deactivateOldGisDataUpdateStatus();
        gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.ok);
        gisDataDao.saveGisDataUpdate(gisDataUpdateEntity);
      } catch (TransactionException e) {
          logger.error("Could not persist gisDataUpdateStatus",e);
      }

    }finally {
      currentUpdateStatusProvider.setRunning(false);
    }

  }

  private boolean isStopped(GisDataUpdateEntity gisDataUpdateEntity) throws TransactionException {
    if(stopped){
      gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.failed);
      gisDataDao.saveGisDataUpdate(gisDataUpdateEntity);
      stopNotExecuted= false;
      return true;
    }
    return false;
  }

  private void deactivateOldGisDataUpdateStatus() throws TransactionException {
    gisDataDao.setLatGisUpdateToOldAndRemoveAreasAndPoints();
  }


  private void savePointFileToDb(File file, OverpassDataType overpassDataType,BoundingBox boundingBox,GisDataUpdateEntity gisDataUpdateEntity)
      throws FileNotFoundException, GisDataException, TransactionException {

    FileInputStream fileInputStream = new FileInputStream(file);
    GisDataGateway gIsDataGateway = new GisDataGateway();

    OverpassReturnValue overpassData = gIsDataGateway
        .importData(fileInputStream);

    List<GisPoint> gisPoints = new ArrayList<GisPoint>();
    for (Node node : overpassData.getNodes()) {
      if(boundingBox.containsWithLowerBorders(node.getLocation().getLatitude(),node.getLocation().getLongitude())){
        gisPoints.add(craeteGisPoint(node.getLocation().getLatitude(),node.getLocation().getLongitude(),overpassDataType));
      }
    }
    GisArea gisArea = new GisArea();
    gisArea.setType(overpassDataType.name());
    gisArea.setUpdateTime(LocalDateTime.now());
    gisArea.setBoundingbox(boundingBox.getSouthLatitude(),boundingBox.getNorthLatitude(),boundingBox.getWestLongitude(),boundingBox.getEastLongitude());

    gisDataUpdateEntity.setNumberOfUpdatedFrames(gisDataUpdateEntity.getNumberOfUpdatedTiles()+1);

    gisArea.setGisUpdate(gisDataUpdateEntity);
    gisDataDao.save(gisArea,gisPoints,gisDataUpdateEntity);
  }



  private void saveWayFileToDb(File file, OverpassDataType overpassDataType,
      BoundingBox boundingBox, GisDataUpdateEntity gisDataUpdateEntity)
      throws FileNotFoundException, GisDataException, TransactionException {
    GisDataGateway gIsDataGateway = new GisDataGateway();
    FileInputStream fileInputStream = new FileInputStream(file);

    OverpassReturnValue overpassData = gIsDataGateway
        .importData(fileInputStream);

    Collection<Way> ways = overpassData.getWays();

    Set<GisPoint> allNodes = new HashSet<GisPoint>();
    Node lastNode=null;
    for (Way way : ways) {
      List<Node> subnodes = way.getSubnodes();
      lastNode = null;
      for (Node subnode : subnodes) {
        if(lastNode != null){
          List<GisPoint> nodesBetween = createNodesBetween(lastNode, subnode,overpassDataType);
          for (GisPoint gisPoint : nodesBetween) {
            if(boundingBox.containsWithLowerBorders(gisPoint.getLatitude(),gisPoint.getLongitude())){
              allNodes.add(gisPoint);
            }
          }
        }

        if(boundingBox.containsWithLowerBorders(subnode.getLocation().getLatitude(),subnode.getLocation().getLongitude())){
          allNodes.add(craeteGisPoint(subnode.getLocation().getLatitude(),subnode.getLocation().getLongitude(),overpassDataType));
        }
        lastNode = subnode;
      }

    }

    GisArea gisArea = new GisArea();
    gisArea.setType(overpassDataType.name());
    gisArea.setUpdateTime(LocalDateTime.now());
    gisArea.setBoundingbox(boundingBox.getSouthLatitude(),boundingBox.getNorthLatitude(),boundingBox.getWestLongitude(),boundingBox.getEastLongitude());
    gisArea.setGisUpdate(gisDataUpdateEntity);
    gisDataUpdateEntity.setNumberOfUpdatedFrames(gisDataUpdateEntity.getNumberOfUpdatedTiles()+1);
    gisDataDao.save(gisArea,allNodes, gisDataUpdateEntity);
    logger.info("updated tile number: {} of {}",gisDataUpdateEntity.getNumberOfUpdatedTiles(),gisDataUpdateEntity.getNumberOfTiles());
    currentUpdateStatusProvider.incrementUpdatedTiles();
  }



  private GisPoint craeteGisPoint(double latitude, double longitude,OverpassDataType overpassDataType){
    GisPoint gisPoint = new GisPoint();
    gisPoint.setPointType(overpassDataType.getValue());
    gisPoint.setPositionByDouble(latitude,longitude);
    return gisPoint;
  }

  private List<GisPoint> createNodesBetween(Node node1, Node node2,OverpassDataType overpassDataType){

    ICoordinate location = node1.getLocation();
    ICoordinate location1 = node2.getLocation();
    List<GisPoint> gisPoints =null;

    Distance distance = CoordinateUtil
        .haversineDistance(location.getLatitude(),location.getLongitude(), location1.getLatitude(),location1.getLongitude());

      double meter = distance.getMeter();
      if(meter <= 25){
        gisPoints = new ArrayList<GisPoint>(0);
       return gisPoints;
      }else{

        double parts =  meter / 25;
        double latitudeVector = location1.getLatitude() - location.getLatitude();
        double longitudeVector = location1.getLongitude() - location.getLongitude();

        double latitudeStep = latitudeVector / parts;
        double longitudeStep = longitudeVector / parts;

        int maxSteps = (int) Math.floor(parts);
        double modulo = meter % 25;
        if(modulo<0.01){
          maxSteps--;
        }
        gisPoints= new ArrayList<GisPoint>(maxSteps);
        for(int i= 1; i <= maxSteps;i++){
          GisPoint gisPoint = new GisPoint();
          gisPoint.setPointType(overpassDataType.getValue());
          gisPoint.setPositionByDouble(location.getLatitude()+i*latitudeStep,location.getLongitude()+i*longitudeStep);
          gisPoints.add(gisPoint);
        }
      }
    return gisPoints;
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

  /**
   * This method checks if the gis data cache is filled with valid data. If there is no valid data
   * available, the cache updating process is tarted
   */
  protected void checkAndUpdateGisData(){

  }


  public void init() {
    if(State.uninitialized.equals(state)){
      checkAndUpdateGisData();
      state= State.ready;
    }
  }

  public void start() {
    if(State.ready.equals(state)){
      state = State.running;
      try {
        create(new SimpleBoundingBox(7.0,47.0,8.0,48.0));
      } catch (GisDataCreationException e) {
        e.printStackTrace();
      }
      state = State.ready;
      currentUpdateStatusProvider.setRunning(false);
    }
  }

  public void stop() {
    if(State.running.equals(state)){
      stopped = true;
      stopNotExecuted=true;

    }
  }


  public void resume() {
    if(State.ready.equals(state)){
      state = State.running;
      try {
        resumeLastFailed();
      } catch (GisDataCreationException e) {
        e.printStackTrace();
      }
      state = State.ready;
      currentUpdateStatusProvider.setRunning(false);
    }
  }

  public CurrentUpdateStatus getCurrentUpdateStatus(){
    return currentUpdateStatusProvider.getCurrentUpdateStatus();
  }

  protected static enum State{
    uninitialized,
    ready,
    running
  }


}
