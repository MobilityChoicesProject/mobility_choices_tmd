package at.fhv.tmd.postProcessing.tasks;

import static at.fhv.tmd.postProcessing.PostprocessHelper.addPostProcessResult;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 21.07.2017.
 */
public class MovingSignalShortage implements PostprocessTask {

  private static final Speed WalkingSpeedThreshold = new Speed(20);
  private static final String source = "walkingSignalShortage";


  public MovingSignalShortage(Classifier classifier,
      FeatureCalculationService featureCalculationService) {
    this.classifier = classifier;
    this.featureCalculationService = featureCalculationService;
  }

  private Classifier classifier;
  private FeatureCalculationService featureCalculationService;

  @Override
  public int getPriorityLevel() {
    return 100;
  }

  @Override
  public void process(TrackingContext trackingContext) {
    List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();
    int size = segmentContextList.size();
    for (int i = 0; i < size;i++) {

      SegmentContext segmentContext = segmentContextList.get(i);

      if(segmentContext.hasData(SegmentContext.POST_PROCESS_RESULT)){
        continue;
      }

      SegmentPreType preType = segmentContext.getData(SegmentContext.PRE_TYPE);
      if(preType != SegmentPreType.movingSignalShortage){
        continue;
      }

     int decrementAmount =  MergeHelper.findBestMerge(trackingContext,segmentContext,source,classifier,featureCalculationService);
      i = i-decrementAmount;

      size = segmentContextList.size();
    }
  }

  private int processAboveWalkingThreshold(TrackingContext trackingContext,
      SegmentContext segmentContext) {

    return MergeHelper.findBestMerge(trackingContext,segmentContext,source,classifier,featureCalculationService);
  }






  public static enum BestMelting{
    meltWithPrevious,
    meltWithNext,
    meltWithPreviousAndNext;
  }

  private int processBelowWalkingThreshold(TrackingContext trackingContext,SegmentContext segmentContext) {

    boolean previousWalking= false;
    boolean nextWalking = false;
    if(segmentContext.hasPreviousContext()){
      SegmentContext previousContext = segmentContext.getPreviousContext();
    }

    if(segmentContext.hasPreviousContext()){
      SegmentContext previousContext = segmentContext.getPreviousContext();
      TransportType transportType = PostprocessHelper.getTransportType(previousContext);
      if(transportType == TransportType.WALK || transportType == TransportType.OTHER){
        previousWalking = true;
      }
    }


    if(segmentContext.hasNextContext()){
      SegmentContext nextContext = segmentContext.getNextContext();
      TransportType transportType = PostprocessHelper.getTransportType(nextContext);
      if(transportType == TransportType.WALK || transportType == TransportType.OTHER){
        nextWalking = true;
      }
    }

    if(previousWalking || nextWalking){

      List<Tuple<TransportType,Double>> tuples = new ArrayList<>();
      tuples.add(new Tuple<>(TransportType.WALK,1.0));

      addPostProcessResult(segmentContext,new ClassificationResult(tuples),source);
      return 0;
    }else{
      return MergeHelper.findBestMerge(trackingContext,segmentContext,source,classifier,featureCalculationService);
    }




  }


}
