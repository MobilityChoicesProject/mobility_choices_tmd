package at.fhv.tmd.processFlow;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.filters.PositionJumpSpeedFilter;
import at.fhv.filters.SamePositionWorseAccuracyFilter;
import at.fhv.filters.WrongTimeGpsFilter;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.ConfigServiceUpdateable;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessingService;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.tmd.smoothing.CoordinateInterpolatorFactory;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.segmentsplitting.Segment;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportClassifier.segmentsplitting.SegmentationService;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Johannes on 21.07.2017.
 */
public class TmdService implements ConfigServiceUpdateable{

  public static final String PRE_PROCESSING = "PreProcessing";
  public static final String FEATURE_CALCULATION = "FeatureCalculation";
  public static final String CLASSIFICATION = "Classification";
  public static final String POST_PROCESSING = "PostProcessing";
  public static final String  SEGMENTATION = "Segmentation";


  private SegmentationService segmentationService ;
  private FeatureCalculationService featureCalculationService ;
  private Classifier classifier;
  private PostprocessingService postprocessingService;
  private ConfigService configService;


  public TmdService(
      SegmentationService segmentationService,
      FeatureCalculationService featureCalculationService,
      Classifier classifier, PostprocessingService postprocessingService) {
    this.segmentationService = segmentationService;
    this.featureCalculationService = featureCalculationService;
    this.classifier = classifier;
    this.postprocessingService = postprocessingService;

  }



  List<Tuple<String,Consumer<TrackingContext>>> stages = new ArrayList<>();

  public void init(){
    stages.add(new Tuple<>(PRE_PROCESSING,trackingContext -> doPreProcessing(trackingContext)));
    stages.add(new Tuple<>(SEGMENTATION,trackingContext -> doSegmentation(trackingContext)));
    stages.add(new Tuple<>(FEATURE_CALCULATION,trackingContext -> doFeatureCalculation(trackingContext)));
    stages.add(new Tuple<>(CLASSIFICATION,trackingContext -> doClassification(trackingContext)));
    stages.add(new Tuple<>(POST_PROCESSING,trackingContext -> doPostProcessing(trackingContext)));

  }

  public TmdResult process(TrackingContextProvider trackingContextProvider) {
    return process(trackingContextProvider, new EarlyReturnCondition() {
      @Override
      public boolean isReached(String stage) {
        return false;
      }

      @Override
      public void setTrackingContext(TrackingContext trackingContext) {
      }
    });
  }


    public TmdResult process(TrackingContextProvider trackingContextProvider, EarlyReturnCondition earlyReturnCondition){

    TrackingContext trackingContext = trackingContextProvider.getTrackingContext();

    for (Tuple<String, Consumer<TrackingContext>> stage : stages) {
      boolean returnFlag =doStageIfNecessaryAndReturn(trackingContext,earlyReturnCondition,stage.getItem1(),stage.getItem2());
      if(returnFlag){
        earlyReturnCondition.setTrackingContext(trackingContext);
        return null;
      }
    }

      CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);
      TmdResult tmdResult = new TmdResult();
    for (SegmentContext segmentContext : trackingContext.getSegmentContextList()) {
      List<TransportTypeProbability> transportTypeProbabilities = PostprocessHelper
          .getTransportTypeProbabilities(segmentContext);
      TmdSegment segment = new TmdSegment(transportTypeProbabilities,segmentContext.getStartTime(),segmentContext.getEndTime());

      List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
          .getInterpolatedCoordinatesExact(segmentContext.getStartTime(),
              segmentContext.getEndTime(), Duration.ofSeconds(1));
      segment.setCoordinates(interpolatedCoordinatesExact);
      tmdResult.add(segment);
    }

    return tmdResult;
  }


  private boolean doStageIfNecessaryAndReturn(TrackingContext trackingContext,EarlyReturnCondition earlyReturnCondition, String stage,Consumer<TrackingContext> consumer){
    if(!trackingContext.hasCompletedStage(stage)){
      consumer.accept(trackingContext);
      trackingContext.addCompletedStage(stage);
    }

    return earlyReturnCondition.isReached( stage);
  }


  private void doPreProcessing(TrackingContext trackingContext) {
    List<IGpsPoint> rawGpsPoints = trackingContext.getData(TrackingContext.RAW_GPS_INPUT);

    PositionJumpSpeedFilter positionJumpSpeedFilter = new PositionJumpSpeedFilter(configService);
    WrongTimeGpsFilter wrongTimeGpsFilter = new WrongTimeGpsFilter();
    SamePositionWorseAccuracyFilter samePositionWorseAccuracyFilter = new SamePositionWorseAccuracyFilter();

    List<IGpsPoint> filterGpsPoints = wrongTimeGpsFilter.filter(rawGpsPoints);
    filterGpsPoints = positionJumpSpeedFilter.filter(filterGpsPoints);
    ArrayList<IGpsPoint> filteredGpsPoints = (ArrayList<IGpsPoint>) samePositionWorseAccuracyFilter.filter(filterGpsPoints);

    CoordinateInterpolator coordinateInterpolator = CoordinateInterpolatorFactory
        .create(CoordinateInterpolatorFactory.Optimized,filterGpsPoints, configService);

    trackingContext.addData(TrackingContext.COORDINATE_INTERPOLATOR,coordinateInterpolator);
    trackingContext.addData(TrackingContext.FILTERED_GPS_INPUT,filteredGpsPoints);

  }


  private void doSegmentation(TrackingContext trackingContext) {
    segmentationService.updateConfigService(configService);

    CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);

    List<Segment> segments = segmentationService.splitIntoSegments(coordinateInterpolator,trackingContext.getTrackingStartTime(),trackingContext.getTrackingEndTime());

    SegmentContext lastSegmentContext = null;
    for (Segment segment : segments) {
      if(segment.getPreType() == SegmentPreType.NonClassifiable){
        continue;
      }
      SegmentContext segmentContext = new SegmentContext();
      segmentContext.setStartTime(segment.getStartTime());
      segmentContext.setEndTime(segment.getEndTime());
      segmentContext.addData(SegmentContext.PRE_TYPE,segment.getPreType());
      segmentContext.setPreviousContext(lastSegmentContext);
      if(lastSegmentContext != null){
        lastSegmentContext.setNextContext(segmentContext);
      }
      lastSegmentContext = segmentContext;
      trackingContext.getSegmentContextList().add(segmentContext);
    }
  }


  private void doFeatureCalculation(TrackingContext trackingContext) {

   CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);

    List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

    for (SegmentContext segmentContext : segmentContextList) {
      SegmentPreType  segmentPreType = segmentContext.getData(SegmentContext.PRE_TYPE);

      if(segmentPreType  == SegmentPreType.WalkingSegment || segmentPreType == SegmentPreType.NonWalkingSegment || segmentPreType == SegmentPreType.stationaryCluster){

        List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
            .getInterpolatedCoordinatesExact(segmentContext.getStartTime(),
                segmentContext.getEndTime(),
                Duration.ofSeconds(1));

        ArrayList<FeatureResult> featureResults = ( ArrayList<FeatureResult>)  featureCalculationService.calcFeatures(interpolatedCoordinatesExact,coordinateInterpolator);

        segmentContext.addData(SegmentContext.FEATURES,featureResults);
      }
    }

  }


  private void doClassification(TrackingContext trackingContext) {
    List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();
    for (SegmentContext segmentContext : segmentContextList) {
      SegmentPreType  segmentPreType = segmentContext.getData(SegmentContext.PRE_TYPE);

        if(segmentPreType  == SegmentPreType.WalkingSegment || segmentPreType == SegmentPreType.NonWalkingSegment || segmentPreType == SegmentPreType.stationaryCluster) {
          List<FeatureResult> featureResults = segmentContext.getData(SegmentContext.FEATURES);
          ClassificationResult classificationResult = classifier.classify(featureResults);
          segmentContext.addData(SegmentContext.CLASSIFICATION_RESULT,classificationResult);
        }
      }
  }


  private void doPostProcessing(TrackingContext trackingContext) {
    postprocessingService.updateConfigService(configService);
    postprocessingService.process(trackingContext);
  }


  @Override
  public void updateConfigService(ConfigService configService) {
    this.configService = configService;

  }
}
