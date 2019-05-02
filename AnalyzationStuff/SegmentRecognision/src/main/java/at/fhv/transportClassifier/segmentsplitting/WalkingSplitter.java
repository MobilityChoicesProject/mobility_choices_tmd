package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceImp;
import at.fhv.transportdetector.trackingtypes.Tracking;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 22.04.2017.
 */
public class WalkingSplitter {



  private Duration minWalkingThreshold = Duration.ofSeconds(60);
  private Duration minNonWalkingThreshold = Duration.ofSeconds( 60);
  double walkingSpeedThrehold = 10;


  public WalkingSplitter(ConfigService configService) {
    this.configService = configService;
  }

  ConfigService configService;

  private void updateConfigSettings(){
    long minWalkingThreshold = (long) configService.getValue(ConfigServiceDefaultCache.minWalkingThreshold);
    long minNonWalkingThreshold = (long)configService.getValue(ConfigServiceDefaultCache.minNonWalkingThreshold);
    double walkingSpeedThrehold = configService.getValue(ConfigServiceDefaultCache.walkingSpeedThrehold);

    this.minWalkingThreshold= Duration.ofSeconds(minWalkingThreshold);
    this.minNonWalkingThreshold= Duration.ofSeconds(minNonWalkingThreshold);
    this.walkingSpeedThrehold = walkingSpeedThrehold;


  }

  public List<Segment> find(List<IGpsPoint> coordinates, Segment oldSegment){

    updateConfigSettings();
    LocalDateTime lastTime = coordinates.get(coordinates.size() - 1).getTime();

    Segment segment=null;

    List<Segment> segments = new ArrayList<>();

    List<Tuple<LocalDateTime, SplitType>> tuples = split(coordinates);
    for (Tuple<LocalDateTime, SplitType> tuple : tuples) {
      if(segment != null){
        segment.setEndTime(tuple.getItem1());
        segments.add(segment);
      }

      segment = new Segment();
      segment.setStartTime(tuple.getItem1());
      if(tuple.getItem2()== SplitType.SOW){
        segment.setPreType(SegmentPreType.WalkingSegment);
      }else{
        segment.setPreType(SegmentPreType.NonWalkingSegment);
      }

    }
    if(segment != null) {
      segment.setEndTime(lastTime);
      segments.add(segment);
    }


    if(tuples.size() == 0){
      segment = new Segment();
      segment.setStartTime(oldSegment.getStartTime());
      segment.setEndTime(oldSegment.getEndTime());
      segment.setPreType(SegmentPreType.NonWalkingSegment);
      segments.add(segment);
    }

    return segments;
  }


  public List<Tuple<LocalDateTime,SplitType>> splitDetail1(Tracking tracking){
    List<IGpsPoint>  coordinates = (  List<IGpsPoint>) (List<?>)tracking.getGpsPoints();

    List<Tuple<LocalDateTime, SplitType>> tuples = split(coordinates);
    return tuples;

  }



    public List<Tuple<LocalDateTime,SplitType>> split(List<IGpsPoint> coordinates){


    List<IGpsPoint> gpsPoints = coordinates;

    Iterator<IGpsPoint> iterator = gpsPoints.iterator();
      IGpsPoint current = iterator.next();
      IGpsPoint next = iterator.next();
    Speed speed = CoordinateUtil.calcSpeedBetween(current, next);

    MiniSegment lastMiniSegment = new MiniSegment();
    lastMiniSegment.setStartTime(current.getTime());
    List<MiniSegment> segments = new ArrayList<>();

    boolean isCurrentlyBelowThreshold ;
    double kmPerHour = speed.getKmPerHour();
    if(kmPerHour < walkingSpeedThrehold){
      isCurrentlyBelowThreshold= true;
      lastMiniSegment.setType(MiniSegmentType.Walking);
    }else{
      isCurrentlyBelowThreshold =false;
      lastMiniSegment.setType(MiniSegmentType.NonWalking);
    }

    current = next;
    while (iterator.hasNext()){
      next = iterator.next();

      speed = CoordinateUtil.calcSpeedBetween(current, next);
      kmPerHour = speed.getKmPerHour();
      if(kmPerHour < walkingSpeedThrehold && !isCurrentlyBelowThreshold){
        isCurrentlyBelowThreshold= true;
        lastMiniSegment.setEndTime(current.getTime());
        segments.add(lastMiniSegment);
        lastMiniSegment = new MiniSegment();
        lastMiniSegment.setStartTime(current.getTime());
        lastMiniSegment.setType(MiniSegmentType.Walking);

      }

      if(kmPerHour >= walkingSpeedThrehold && isCurrentlyBelowThreshold ){
        isCurrentlyBelowThreshold =false;
        lastMiniSegment.setEndTime(current.getTime());
        segments.add(lastMiniSegment);
        lastMiniSegment = new MiniSegment();
        lastMiniSegment.setStartTime(current.getTime());
        lastMiniSegment.setType(MiniSegmentType.NonWalking);

      }
      current = next;
    }

    lastMiniSegment.setEndTime(current.getTime());
    segments.add(lastMiniSegment);

    // remove to short walkings
      mergeShortSegments(MiniSegmentType.Walking,miniSegment -> minWalkingThreshold.compareTo(miniSegment.getDuration())>=0,segments);

      // remove to short Non-walkings
      mergeShortSegments(MiniSegmentType.NonWalking,miniSegment -> minNonWalkingThreshold.compareTo(miniSegment.getDuration())>=0,segments);



    List<Tuple<LocalDateTime,SplitType>> times = new LinkedList<>();
    for (MiniSegment miniSegment : segments) {

      if(miniSegment.getType()== MiniSegmentType.Walking){
        Tuple<LocalDateTime, SplitType> localDateTimeSplitTypeTuple = new Tuple<LocalDateTime, SplitType>(
            miniSegment.getStartTime(),
           SplitType.SOW);
        times.add(localDateTimeSplitTypeTuple);

      }else{
        Tuple<LocalDateTime, SplitType> localDateTimeSplitTypeTuple = new Tuple<LocalDateTime, SplitType>(
            miniSegment.getStartTime(),
            SplitType.EOW);
        times.add(localDateTimeSplitTypeTuple);

      }
    }

    return times;
  }


  private void mergeShortSegments(MiniSegmentType segmentType, ThresholdReached thresholdReached,List<MiniSegment> segments){

    MiniSegment lastMiniSegment =null;

    for(int i = 0; i < segments.size();i++){

      MiniSegment miniSegment = segments.get(i);
      if(miniSegment.getType() == segmentType ){
        boolean isthresholdReached = thresholdReached.isTooShort(miniSegment);
        if(isthresholdReached){
          if(i == 0){
            MiniSegment nextMiniSegment = segments.get(i + 1);
            nextMiniSegment.setStartTime(miniSegment.getStartTime());
            segments.remove(miniSegment);
          }else if(i ==  segments.size()-1){
            MiniSegment segmentBefore = segments.get(i - 1);
            segmentBefore.setEndTime(miniSegment.getEndTime());
            segments.remove(miniSegment);
          }else{
            MiniSegment nextMiniSegment = segments.get(i + 1);
            lastMiniSegment = segments.get(i - 1);
            lastMiniSegment.setEndTime(nextMiniSegment.getEndTime());
            segments.remove(nextMiniSegment);
            segments.remove(miniSegment);
            i--;
          }
        }
      }
    }



  }

  private static interface ThresholdReached{

    public boolean isTooShort( MiniSegment miniSegment);

  }

  public enum SplitType{
    SOW,EOW
  }

  private enum MiniSegmentType{
    Walking,
    NonWalking
  }
  private class MiniSegment{
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MiniSegmentType type;

    public LocalDateTime getStartTime() {
      return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
      return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
    }

    public MiniSegmentType getType() {
      return type;
    }

    public void setType(MiniSegmentType type) {
      this.type = type;
    }

    public Duration getDuration(){
      return Duration.between(startTime,endTime);
    }
  }


}
