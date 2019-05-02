package at.fhv.tmd.postProcessing.tasks;

import static at.fhv.tmd.postProcessing.PostprocessHelper.addPostProcessResult;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.featureCalculation.RailwayClosestDistanceCalculator;
import at.fhv.tmd.postProcessing.ConfigServiceUpdateable;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 19.07.2017.
 */
public class TrainStationMovingSignalShortage implements PostprocessTask, ConfigServiceUpdateable {

  public static String source = "TrainStationMovingSignalShortage";

  private static Distance maxDistance = new Distance(0.1);


  public int priorityLevel= 0;
  private ConfigService configService;

  @Override
  public int getPriorityLevel() {
    return priorityLevel;
  }

  RailwayClosestDistanceCalculator gisPointClosesnessCalculator;

  public TrainStationMovingSignalShortage(RailwayClosestDistanceCalculator gisDataQueryService) {
    this.gisPointClosesnessCalculator = gisDataQueryService;
  }

  @Override
  public void process(TrackingContext trackingContext){
    List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

    for (SegmentContext segmentContext : segmentContextList) {

      SegmentPreType segmentPreType = segmentContext.getData(SegmentContext.PRE_TYPE);
      if(segmentPreType == SegmentPreType.movingSignalShortage){

        CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);

        LocalDateTime startTime = segmentContext.getStartTime();
        LocalDateTime endTime = segmentContext.getEndTime();

        IGpsPoint startCoordinate = coordinateInterpolator.getCoordinate(startTime);
        IGpsPoint endCoordinate = coordinateInterpolator.getCoordinate(endTime);
        List<IGpsPoint> coordinates = new ArrayList<>();
        coordinates.add(startCoordinate);
        coordinates.add(endCoordinate);



        Distance distance1 = gisPointClosesnessCalculator
            .calcDistance(startCoordinate);

        Distance distance2 = gisPointClosesnessCalculator
            .calcDistance(endCoordinate);

        if(maxDistance.isLongerThan(distance1) && maxDistance.isLongerThan(distance2)){
          List<Tuple<TransportType,Double>> tuples = new ArrayList<>();
          tuples.add(new Tuple<>(TransportType.TRAIN,1.0));
          addPostProcessResult(segmentContext,new ClassificationResult(tuples),source);
        }

        if(maxDistance.isLongerThan(distance1) || maxDistance.isLongerThan(distance2)){

          List<Tuple<TransportType,Double>> tuples = new ArrayList<>();
          tuples.add(new Tuple<>(TransportType.TRAIN,0.7));
          tuples.add(new Tuple<>(TransportType.BUS,0.20));
          tuples.add(new Tuple<>(TransportType.CAR,0.10));
          addPostProcessResult(segmentContext,new ClassificationResult(tuples),source);
        }

      }

    }

  }


  @Override
  public void updateConfigService(ConfigService configService) {
    this.configService = configService;
    updateConfigService();
  }

  private void updateConfigService(){
    double maxDistance = configService
        .getValue(ConfigServiceDefaultCache.pp_signalShortage_train_distance);
    this.maxDistance = new Distance(maxDistance);
  }
}
