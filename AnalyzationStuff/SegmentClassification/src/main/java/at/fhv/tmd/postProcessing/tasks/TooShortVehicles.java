package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.ConfigServiceUpdateable;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 03.08.2017.
 */
public class TooShortVehicles implements PostprocessTask ,ConfigServiceUpdateable {

  @Override
  public int getPriorityLevel() {
    return 400;
  }

  private Classifier classifier;
  private FeatureCalculationService featureCalculationService;
  private ConfigService configService;

  public TooShortVehicles(Classifier classifier,
      FeatureCalculationService featureCalculationService) {
    this.classifier = classifier;
    this.featureCalculationService = featureCalculationService;
    thresholdDistances.put(TransportType.BIKE,new Distance(0.2));
    thresholdDistances.put(TransportType.BUS,new Distance(0.5));
    thresholdDistances.put(TransportType.CAR,new Distance(0.5));
    thresholdDistances.put(TransportType.TRAIN,new Distance(1));
  }

  private HashMap<TransportType, Distance> thresholdDistances = new HashMap<>();



  @Override
  public void process(TrackingContext trackingContext) {

    List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

    int size = segmentContextList.size();
    for (int i = 0; i < size;i++) {

      SegmentContext segmentContext = segmentContextList.get(i);
      LocalDateTime startTime = segmentContext.getStartTime();
      LocalDateTime endTime = segmentContext.getEndTime();


      CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);

      List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
          .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

      Distance distance = calcDistance(interpolatedCoordinatesExact);

      ClassificationResult classificationResult = PostprocessHelper
          .getClassificationResult(segmentContext);
      TransportType mostLikeliestResult = classificationResult.getMostLikeliestResult();
      double likelyHoodFor = classificationResult.getLikelyHoodFor(mostLikeliestResult);

      Distance distanceThreshold = thresholdDistances.get(mostLikeliestResult);
      if(distanceThreshold == null){
        continue;
      }

      double distanceInKM = distance.getKm() * likelyHoodFor;
      Distance distanceOfSegment = new Distance(distanceInKM);
      boolean tooShort = distanceThreshold.isLongerThan(distanceOfSegment);
      if(tooShort){
        // find best merge
        int decrementAmount = MergeHelper
            .findBestMerge(trackingContext, segmentContext, "toShortVehicle", classifier,
                featureCalculationService);
        i = i-decrementAmount;
        size = segmentContextList.size();
      }


    }


  }

  private Distance calcDistance(List<IGpsPoint> iGpsPoints){
    Distance distance = new Distance(0);
    IGpsPoint lastpoint =null;
    for (IGpsPoint iGpsPoint : iGpsPoints) {
      if(lastpoint != null){
        Distance distance1 = CoordinateUtil.haversineDistance(lastpoint, iGpsPoint);
        distance  = distance.plus(distance1);
      }
      lastpoint = iGpsPoint;
    }
    return distance;
  }

  @Override
  public void updateConfigService(ConfigService configService) {
    this.configService =configService;
    updateConfigSettings();
  }

  private void updateConfigSettings(){
    HashMap<TransportType, Distance> thresholdDistances = new HashMap<>();

    double bikeKm = configService.getValue(ConfigServiceDefaultCache.pp_toShortVehicle_bike);
    double busKm = configService.getValue(ConfigServiceDefaultCache.pp_toShortVehicle_bus);
    double trainKm = configService.getValue(ConfigServiceDefaultCache.pp_toShortVehicle_train);
    double carKm = configService.getValue(ConfigServiceDefaultCache.pp_toShortVehicle_car);
    thresholdDistances.put(TransportType.BIKE,new Distance(bikeKm));
    thresholdDistances.put(TransportType.BUS,new Distance(busKm));
    thresholdDistances.put(TransportType.CAR,new Distance(carKm));
    thresholdDistances.put(TransportType.TRAIN,new Distance(trainKm));
    this.thresholdDistances =thresholdDistances;

  }
}
