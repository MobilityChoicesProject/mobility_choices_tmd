package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 02.05.2017.
 */
public class SegmentationServiceImp implements SegmentationService {



  SignalShortageFinder signalShortageFinder ;
  private DensityClusterFinder densityClusterFinder;
  private WalkingSplitter walkingSplitter;

  private Duration densityClusterMinLenghtThreshold = Duration.ofSeconds(120);
  private Duration walkingMinLengthThreshold = Duration.ofSeconds(60);


  @Override
  public void updateConfigService(ConfigService configService){
    signalShortageFinder=new SignalShortageFinder(configService);
    densityClusterFinder = new DensityClusterFinder(configService);
    walkingSplitter = new WalkingSplitter(configService);
  }


  @Override
  public List<Segment> splitIntoSegments(CoordinateInterpolator coordinateInterpolator,
      LocalDateTime startTime, LocalDateTime endTime){

    startTime = TimeUtil.removeMs(startTime);
    endTime = TimeUtil.removeMs(endTime);

    SegmentContainer segmentContainer;
    segmentContainer= new SegmentContainer(startTime,endTime);

    Segment defaultSegment = segmentContainer.getSegmentList().get(0);
    List<IGpsPoint> coordinates = getCoordinates(coordinateInterpolator,defaultSegment.getStartTime(),
        defaultSegment.getEndTime());

    List<Segment> segments =  signalShortageFinder.find(coordinates,startTime,endTime);
    segmentContainer.changeSegment(defaultSegment,segments);


   // DensityCluster
    List<Segment> segmentsToSplit = new LinkedList<>(segmentContainer.getSegmentList());
    for (Segment segment : segmentsToSplit) {
      SegmentPreType preType = segment.getPreType();
      Duration duration = segment.getDuration();
      boolean longerThanThreshold = densityClusterMinLenghtThreshold.compareTo(duration) < 0;
      if(( preType== SegmentPreType.WalkingSegment ||preType== SegmentPreType.NotClassifiedYet ||  preType == SegmentPreType.NonWalkingSegment) && longerThanThreshold){

        List<IGpsPoint> interpolatedCoordinates = getInterpolatedCoordinates(
            coordinateInterpolator, segment.getStartTime(), segment.getEndTime());
        List<Segment> densityClusters = densityClusterFinder.find(interpolatedCoordinates);
        segmentContainer.changeSegment(segment,densityClusters);
      }
    }


    // walking / Non walking
    segmentsToSplit = new LinkedList<>(segmentContainer.getSegmentList());
    for (Segment segment : segmentsToSplit) {
      SegmentPreType preType = segment.getPreType();
      Duration duration = segment.getDuration();
      boolean longerThanThreshold = walkingMinLengthThreshold.compareTo(duration) < 0;
      if(( preType== SegmentPreType.WalkingSegment ||preType== SegmentPreType.NotClassifiedYet ||  preType == SegmentPreType.NonWalkingSegment) && longerThanThreshold){

        List<IGpsPoint> interpolatedCoordinates = getInterpolatedCoordinates(
            coordinateInterpolator, segment.getStartTime(), segment.getEndTime());
        List<Segment> segments1 = walkingSplitter.find(interpolatedCoordinates, segment);

        segmentContainer.changeSegment(segment,segments1);
      }
    }

    List<Segment> segmentList = segmentContainer.getSegmentList();

    return segmentList;

  }


  private List<IGpsPoint> getCoordinates(CoordinateInterpolator coordinateInterpolator,LocalDateTime startTime, LocalDateTime endtime){
    List<IGpsPoint> coordinates = coordinateInterpolator.getCoordinates();
    List<IGpsPoint> coordinatesInRange = new LinkedList<>();
    for (IGpsPoint coordinate : coordinates) {
      LocalDateTime time = coordinate.getTime();
      boolean afterEqualStart = time.isAfter(startTime)|| time.equals(startTime);
      boolean beforeEqualend = time.isBefore(endtime)|| time.equals(endtime);
      if(afterEqualStart && beforeEqualend){
        coordinatesInRange.add(coordinate);
      }
    }

    return coordinatesInRange;
  }

  private List<IGpsPoint> getInterpolatedCoordinates(CoordinateInterpolator coordinateInterpolator,LocalDateTime startTime, LocalDateTime endtime){

    return coordinateInterpolator.getInterpolatedCoordinatesExact(startTime,endtime, Duration.ofSeconds(1));


  }


}


