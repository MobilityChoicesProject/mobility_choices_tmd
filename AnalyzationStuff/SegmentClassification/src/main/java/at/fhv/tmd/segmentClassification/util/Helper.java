package at.fhv.tmd.segmentClassification.util;

import at.fhv.filters.PositionJumpSpeedFilter;
import at.fhv.filters.SamePositionWorseAccuracyFilter;
import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.filters.WrongTimeGpsFilter;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.tmd.smoothing.CoordinateInterpolatorFactory;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import java.time.Duration;
import java.util.List;

/**
 * Created by Johannes on 20.05.2017.
 */
public class Helper {

  static SameSequelTransportModeMergeFilter sameSequelTransportModeMergeFilter = new SameSequelTransportModeMergeFilter();
  private static ConfigService configService;


  public static Tracking removeSameSequelTransportModes(Tracking tracking){
    tracking=sameSequelTransportModeMergeFilter.filter(tracking);
    return tracking;
  }

  public static CoordinateInterpolator filterAndCreateCoordinateInterpolator(Tracking tracking, ConfigService configService){

    List<IExtendedGpsPoint> gpsPoints = tracking.getGpsPoints();
    List<IGpsPoint> coordinates = (List<IGpsPoint>)(List<?>) gpsPoints;
    coordinates =filterCoordinates(coordinates);

    CoordinateInterpolator coordinateInterpolator = CoordinateInterpolatorFactory
        .create(CoordinateInterpolatorFactory.Optimized,coordinates,configService);
    return coordinateInterpolator;
  }



  public static  List<IGpsPoint> filterCoordinates(List<IGpsPoint> coordinates){
    PositionJumpSpeedFilter positionJumpSpeedFilter = new PositionJumpSpeedFilter(configService);
    WrongTimeGpsFilter wrongTimeGpsFilter = new WrongTimeGpsFilter();
    SamePositionWorseAccuracyFilter samePositionWorseAccuracyFilter = new SamePositionWorseAccuracyFilter();

    List<IGpsPoint> filter = wrongTimeGpsFilter.filter(coordinates);
    List<IGpsPoint> filter1 = positionJumpSpeedFilter.filter(filter);
    List<IGpsPoint> filter2 = samePositionWorseAccuracyFilter.filter(filter1);
    return filter2;
  }



  private static  Duration minDuration = Duration.ofMinutes(2);
  private static Duration minDuration5 = Duration.ofMinutes(5);
  private static int minGpsPoints = 60;
  static BoundingBox maxBoundingBox = new SimpleBoundingBox(46.817984, 8.403123,48.372000, 10.882714);


  public static boolean isValid(Tracking tracking){
    Tracking next = tracking;

    BoundingBox boundingBox = next.getBoundingBox();
    boolean isInsideMaxBoundary = maxBoundingBox.contains(boundingBox);

    Duration durationOfTracking = Duration.between(next.getStartTimestamp(), next.getEndTimestamp());

    boolean isLongerOrEqualThanMinDuration = minDuration.compareTo(durationOfTracking) <= 0;
    boolean isLongerOrEqualThanMinDuration5 = minDuration5.compareTo(durationOfTracking) <= 0;

    int gpsPointsSize = next.getGpsPoints().size();

    boolean hasMoreOrEqualMinGpsPoints = gpsPointsSize - minGpsPoints >= 0;



    return isInsideMaxBoundary&& isLongerOrEqualThanMinDuration &&hasMoreOrEqualMinGpsPoints;
  }


}
