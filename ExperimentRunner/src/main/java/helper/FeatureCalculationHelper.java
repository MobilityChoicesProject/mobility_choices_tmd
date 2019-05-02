package helper;

import at.fhv.features.LabeledFeature;
import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.segmentClassification.util.Helper;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.mainserver.impl.GisFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.LocationFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.SpeedCalculatorService;
import at.fhv.transportClassifier.mainserver.transaction.EntityManagerTransaction;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.NotAvailableExcepion;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import experiments.ClassificationEvaluationExperiment.SegmentationResultInfo;
import helper.segmentation.SegmentationHelper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Created by Johannes on 21.06.2017.
 */
public class FeatureCalculationHelper {

  private static int minGpsPoints;
  private static BoundingBox maxBoundingBox;
  private static Duration minDuration;


  public  List<LabeledFeature> calculate(Iterator<Tracking> trackingIterator, EntityManagerFactory em, int version){

    initBoundaries();
    int numberOfThreads = PropertyHelper
        .getInt("feature_calculation_used_threads");
    ExecutorService executorService  = Executors.newFixedThreadPool(numberOfThreads);

    List<LabeledFeature> labeledFeatures = Collections.synchronizedList(new ArrayList<LabeledFeature>());


    trackingIterator = new ThreadSafeIterator<>(trackingIterator);
    AtomicInteger currentlyWorkingThreads = new AtomicInteger(0);

    for(int i = 0; i < numberOfThreads;i++){

      WorkingTask workingTask = new WorkingTask(trackingIterator, em.createEntityManager(),
          currentlyWorkingThreads,labeledFeatures, version);
      executorService.submit(workingTask);
    }


    while (trackingIterator.hasNext() || currentlyWorkingThreads.get()!= 0){
      try {
        Thread.sleep(1000);
        System.out.println("number of Calculated trackings: "+labeledFeatures.size());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return labeledFeatures;

  }



  public  List<LabeledFeature> calculateWithSegmentation(Iterator<Tracking> trackingIterator, EntityManagerFactory em, int version){

    initBoundaries();
    int numberOfThreads = PropertyHelper
        .getInt("feature_calculation_used_threads");
    ExecutorService executorService  = Executors.newFixedThreadPool(numberOfThreads);

    List<LabeledFeature> labeledFeatures = Collections.synchronizedList(new ArrayList<LabeledFeature>());


    trackingIterator = new ThreadSafeIterator<>(trackingIterator);
    AtomicInteger currentlyWorkingThreads = new AtomicInteger(0);

    for(int i = 0; i < numberOfThreads;i++){

      SegmentationWorkingTask workingTask = new SegmentationWorkingTask(trackingIterator, em.createEntityManager(),
          currentlyWorkingThreads,labeledFeatures, version);
      executorService.submit(workingTask);
    }


    while (trackingIterator.hasNext() || currentlyWorkingThreads.get()!= 0){
      try {
        Thread.sleep(1000);
        System.out.println("number of Calculated trackings: "+labeledFeatures.size());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return labeledFeatures;

  }



  public static class WorkingTask implements Runnable{


    private Iterator<Tracking> trackingIterator;
    private EntityManager em;
    private AtomicInteger currentlyWorkingThreads;
    private List<LabeledFeature> labeledFeatures;
    SameSequelTransportModeMergeFilter sameSequelTransportModeMergeFilter = new SameSequelTransportModeMergeFilter();
    private int version;
    FeatureCalculationService featureCalculationService = new FeatureCalculationService();
    private ConfigService configService;

    public WorkingTask(
        Iterator<Tracking> trackingIterator, EntityManager em,
        AtomicInteger currentlyWorkingThreads,
        List<LabeledFeature> labeledFeatures, int version) {
      this.trackingIterator = trackingIterator;
      this.em = em;
      this.currentlyWorkingThreads = currentlyWorkingThreads;
      this.labeledFeatures =labeledFeatures;
      this.version = version;
    }

    @Override
    public void run() {

      try{

      EntityTransaction entityTransaction = em.getTransaction();
      ITransaction transaction = new EntityManagerTransaction(entityTransaction);
      GisFeatureCalculator gisFeatureCalculator = new GisFeatureCalculator();
      gisFeatureCalculator.init(em,transaction);
      featureCalculationService.addFeatureCalculator(new SpeedCalculatorService());
      featureCalculationService.addFeatureCalculator(new LocationFeatureCalculator());
      featureCalculationService.addFeatureCalculator(gisFeatureCalculator);
        List<LabeledFeature> labeledFeaturesTemp;
        while (trackingIterator.hasNext()){
        labeledFeaturesTemp = null;
        try{
          currentlyWorkingThreads.incrementAndGet();
          boolean reachedEnd = false;
          Tracking tracking=null;
          try{
            tracking = trackingIterator.next();

          }catch (NoSuchElementException elementException){
            reachedEnd = true;
          }

          if(reachedEnd){
            return;
          }

          labeledFeaturesTemp = calcFeature(tracking, em,version);
          labeledFeatures.addAll(labeledFeaturesTemp);

        }catch (Error ex){
          ex.printStackTrace();
        }finally {
          currentlyWorkingThreads.decrementAndGet();
        }
      }
      }catch (Exception ex){
        ex.printStackTrace();
      }
    }

    protected List<LabeledFeature> calcFeature(Tracking tracking, EntityManager em, int version) {


      List<LabeledFeature> labeledFeatures  = new ArrayList<>();
      boolean checkIfValid = checkIfValid(tracking,version);
      if(!checkIfValid){
        return new ArrayList<>();
      }


      tracking = sameSequelTransportModeMergeFilter.filter(tracking);
      CoordinateInterpolator coordinateInterpolator = Helper.filterAndCreateCoordinateInterpolator(tracking,configService);
      TrackingSegmentBag trackingSegmentBagWithVersion =null;
      if(version ==-1){
        trackingSegmentBagWithVersion  = tracking
            .getLatestTrackingSegmentBag();
      }else{
        trackingSegmentBagWithVersion  = tracking
            .getTrackingSegmentBagWithVersion(version);
      }



      for (TrackingSegment trackingSegment : trackingSegmentBagWithVersion.getSegments()) {

        List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
            .getInterpolatedCoordinatesExact(trackingSegment.getStartTime(),
                trackingSegment.getEndTime(), Duration.ofSeconds(1));

        List<FeatureResult> results = featureCalculationService
            .calcFeatures(interpolatedCoordinatesExact, coordinateInterpolator);
        LabeledFeature labeledFeature = new LabeledFeature();
        labeledFeature.setFeatureResultList(results);
        labeledFeature.setTrackingFileName(tracking.getTrackingInfo(Constants.FILENAME));
        labeledFeature.setSegmentStartTime(trackingSegment.getStartTime());
        labeledFeature.setTrackingStartTime(tracking.getStartTimestamp());
        labeledFeature.setTransportType(trackingSegment.getTransportType());
        labeledFeatures.add(labeledFeature);
      }

      return labeledFeatures;
    }



  }

  public static class SegmentationWorkingTask extends WorkingTask{

    private ConfigService configService;

    public SegmentationWorkingTask(
        Iterator<Tracking> trackingIterator, EntityManager em,
        AtomicInteger currentlyWorkingThreads,
        List<LabeledFeature> labeledFeatures, int version) {
      super(trackingIterator, em, currentlyWorkingThreads, labeledFeatures, version);
    }


    @Override
    public void run() {


      super.run();
    }




    @Override
    protected List<LabeledFeature> calcFeature(Tracking tracking, EntityManager em, int version) {


      SegmentationHelper segmentationHelper = new SegmentationHelper();


      List<LabeledFeature> labeledFeatures  = new ArrayList<>();
      boolean checkIfValid = checkIfValid(tracking,version);
      if(!checkIfValid){
        return new ArrayList<>();
      }


      tracking = sameSequelTransportModeMergeFilter.filter(tracking);
      CoordinateInterpolator coordinateInterpolator = Helper.filterAndCreateCoordinateInterpolator(tracking,configService);
      TrackingSegmentBag trackingSegmentBagWithVersion =null;

      List<SegmentationResultInfo> segmentationResultInfos = segmentationHelper.calcSegmentsAndFeatureResults(
          tracking);

      if(version ==-1){
        trackingSegmentBagWithVersion  = tracking
            .getLatestTrackingSegmentBag();
      }else{
        trackingSegmentBagWithVersion  = tracking
            .getTrackingSegmentBagWithVersion(version);
      }



      for (SegmentationResultInfo segmentationResultInfo : segmentationResultInfos) {

        List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
            .getInterpolatedCoordinatesExact(segmentationResultInfo.getStartTime(),
                segmentationResultInfo.getEndTime(), Duration.ofSeconds(1));

        List<FeatureResult> results = featureCalculationService
            .calcFeatures(interpolatedCoordinatesExact, coordinateInterpolator);
        LabeledFeature labeledFeature = new LabeledFeature();
        labeledFeature.setFeatureResultList(results);
        labeledFeature.setTrackingFileName(tracking.getTrackingInfo(Constants.FILENAME));
        labeledFeature.setSegmentStartTime(segmentationResultInfo.getStartTime());
        labeledFeature.setTrackingStartTime(tracking.getStartTimestamp());


        TransportType transportType =segmentationHelper.getDominantTransportType(segmentationResultInfo,tracking,version);
        if(transportType == null){
          int debug= 3;
        }
        labeledFeature.setTransportType(transportType);
        labeledFeatures.add(labeledFeature);
      }

      return labeledFeatures;
    }



  }

  private void initBoundaries(){



    minGpsPoints = PropertyHelper.getInt("minGpsPointsForValidTracking");
    double maxBoundingBox_soutLatitude = PropertyHelper.getDouble("maxBoundingBox_soutLatitude");
    double maxBoundingBox_northLatitude = PropertyHelper.getDouble("maxBoundingBox_northLatitude");
    double maxBoundingBox_westLongitude = PropertyHelper.getDouble("maxBoundingBox_westLongitude");
    double maxBoundingBox_eastLongitude = PropertyHelper.getDouble("maxBoundingBox_eastLongitude");
    maxBoundingBox = new SimpleBoundingBox(maxBoundingBox_soutLatitude,maxBoundingBox_westLongitude,maxBoundingBox_northLatitude,maxBoundingBox_eastLongitude);
    int minDurationInSeconds = PropertyHelper.getInt("minDurationInSeconds");
    minDuration = Duration.ofSeconds(minDurationInSeconds);

  }


  protected static boolean checkIfValid(Tracking fullTracking, int version){

    if(version!= -1){
      try{

        TrackingSegmentBag trackingSegmentBagWithVersion = fullTracking
            .getTrackingSegmentBagWithVersion(version);

      }catch (NotAvailableExcepion notAvailableExcepion){
        return false;
      }
    }


    if (fullTracking.getGpsPoints().size() < minGpsPoints) {
      return false;
    }

    if(!maxBoundingBox.contains(fullTracking.getBoundingBox())){
      return false;

    }
    IExtendedGpsPoint gpsPoint = fullTracking.getGpsPoints().get(0);
    IExtendedGpsPoint lastGpsPoint = fullTracking.getGpsPoints().get(fullTracking.getGpsPoints().size() - 1);
    Duration between = Duration
        .between(gpsPoint.getMostAccurateTime(), lastGpsPoint.getMostAccurateTime());
    return between.compareTo(minDuration) >= 0;
  }






}
