package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 03.08.2017.
 */
public class OtherBetweenSameTransportModes implements PostprocessTask{


  public static Duration duratonThreshold = Duration.ofMinutes(5);

  private Classifier classifier;
  private FeatureCalculationService featureCalculationService;

  public OtherBetweenSameTransportModes(
      Classifier classifier,
      FeatureCalculationService featureCalculationService) {
    this.classifier = classifier;
    this.featureCalculationService = featureCalculationService;
  }

  @Override
  public int getPriorityLevel() {
    return 200;
  }

  @Override
  public void process(TrackingContext trackingContext) {

    List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();
    int size = segmentContextList.size();

    for(int i = 0; i < size;i++){
      SegmentContext segmentContext = segmentContextList.get(i);




      TransportType transportType =PostprocessHelper.getTransportType(segmentContext);


      if(transportType != TransportType.OTHER){
        continue;
      }
      if(!(segmentContext.hasPreviousContext() && segmentContext.hasNextContext())){
        continue;
      }

      SegmentContext previousContext = segmentContext.getPreviousContext();
      SegmentContext nextContext = segmentContext.getNextContext();

      TransportType previousTransportType = PostprocessHelper.getTransportType(previousContext);
      TransportType nextTransportType = PostprocessHelper.getTransportType(nextContext);

      Duration between = Duration
          .between(segmentContext.getStartTime(), segmentContext.getEndTime());


      if (previousTransportType == null) {
        continue;
      }
      boolean vehicle = previousTransportType != TransportType.WALK
          && previousTransportType != TransportType.OTHER && previousTransportType != TransportType.STATIONARY;
      if( vehicle && previousTransportType == nextTransportType ){
        // merge

        ClassificationResult classificationResult = PostprocessHelper
            .getClassificationResult(segmentContext);
        TransportType mostLikeliestResult = classificationResult.getMostLikeliestResult();
        double likelyHoodFor = classificationResult.getLikelyHoodFor(mostLikeliestResult);

        long thresholdMillis= duratonThreshold.toMillis();
        long betweenMillis =(long) (between.toMillis() * likelyHoodFor);
        boolean tooLongOtherSegment = (thresholdMillis- betweenMillis) <0;
      if(tooLongOtherSegment){
        continue;
      }

        CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);

        LocalDateTime startTime = previousContext.getStartTime();
        LocalDateTime endTime = nextContext.getEndTime();

        List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
            .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

        List<FeatureResult> featureResults = featureCalculationService
            .calcFeatures(interpolatedCoordinatesExact, coordinateInterpolator);
        ClassificationResult classify = classifier.classify(featureResults);

        PostprocessHelper.meltThreeSegments(trackingContext,previousContext,segmentContext,nextContext,classify,"TooShortOther");
        i = i-1;
        size = trackingContext.getSegmentContextList().size();
      }


    }


  }
}
