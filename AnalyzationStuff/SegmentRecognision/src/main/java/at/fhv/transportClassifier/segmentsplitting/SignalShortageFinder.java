package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 02.05.2017.
 */
public class SignalShortageFinder {


  ConfigService configService;

  public SignalShortageFinder(ConfigService configService) {
    this.configService = configService;
  }



  private void updateConfigSettings(){
    long signalShortageThreshold = (long) configService.getValue("signalShortageThresholdInSeconds");
    long minDurationBetweenCSignalShortageBelow20KmH = (long)configService.getValue("minDurationBetweenCSignalShortageBelow20KmH");
    long minDurationBetweenCSignalShortageAbove20KmH = (long)configService.getValue("minDurationBetweenCSignalShortageAbove20KmH");
    double speedThreshold = configService.getValue("signalShortageFinder_speedThreshold");
    double walkingNonWalkingSpeedThreshold = configService.getValue("walkingNonWalkingSpeedThreshold");

    this.signalShortageThreshold =  Duration.ofSeconds(signalShortageThreshold);
    this.minDurationBetweenCSignalShortageBelow20KmH =  Duration.ofSeconds(minDurationBetweenCSignalShortageBelow20KmH);
    this.minDurationBetweenCSignalShortageAbove20KmH =  Duration.ofSeconds(minDurationBetweenCSignalShortageAbove20KmH);
    this.speedThreshold =  new Speed(speedThreshold);
    this.walkingNonWalkingSpeedThreshold = new Speed(walkingNonWalkingSpeedThreshold);

  }


  public Duration signalShortageThreshold = Duration.ofSeconds(30);  // a singal shortage has to be at least that long to be considered
  public Duration minDurationBetweenCSignalShortageBelow20KmH = Duration.ofSeconds(30);
  public Duration minDurationBetweenCSignalShortageAbove20KmH = Duration.ofSeconds(120);
  public Speed speedThreshold = new Speed(0.2);
  public Speed walkingNonWalkingSpeedThreshold = new Speed(20);



  public List<Segment>  find(List<IGpsPoint> coordinates,LocalDateTime startTime,LocalDateTime endTime){
    int size = coordinates.size();
    if( size< 50){
      throw new IllegalArgumentException("Not enough Data");
    }

    updateConfigSettings();

    List<SignalShortageInfo> signalShortages =    collectSignalShortages( coordinates, startTime, endTime);


    //merge signalShortages which are too Close to each other
    mergeSignalShortages(coordinates, signalShortages);



    // divide into moving semgnets and stationy
    List<Segment> segments = new LinkedList<>();

    Segment segmentBetweenSignalShortage  = null;
    for (SignalShortageInfo signalShortage : signalShortages) {
      IGpsPoint startPoint = signalShortage.getStartPoint();
      IGpsPoint endPoint = signalShortage.getEndPoint();
      LocalDateTime signalShortageStartTime = signalShortage.getStartTime();

      if(segmentBetweenSignalShortage != null){
        boolean  segmentBetweenWithZeroDuration = segmentBetweenSignalShortage.getStartTime().compareTo(signalShortageStartTime)== 0;
        if(segmentBetweenWithZeroDuration){
          //do nothing
        }else{
          segmentBetweenSignalShortage.setEndTime(signalShortageStartTime);
          segments.add(segmentBetweenSignalShortage);
        }
      }

      if(signalShortage.isUnknown()){
        Segment segment = new Segment();
        segment.setOrigin("SignalShortageFinder");
        segment.setStartTime(signalShortageStartTime);
        segment.setEndTime(signalShortage.getEndTime());
        segment.setPreType(SegmentPreType.NonClassifiable);
        segments.add(segment);
      }else{

        Speed speed = CoordinateUtil.calcSpeedBetween(startPoint,endPoint);

        if(speed.getKmPerHour() >= speedThreshold.getKmPerHour()){
          // moving segment
          Segment segment = new Segment();
          segment.setOrigin("SignalShortageFinder");
          segment.setStartTime(signalShortageStartTime);
          segment.setEndTime(signalShortage.getEndTime());
          segment.setPreType(SegmentPreType.movingSignalShortage);
          segments.add(segment);
        }else{
          // stationary
          Segment segment = new Segment();
          segment.setOrigin("SignalShortageFinder");
          segment.setStartTime(signalShortageStartTime);
          segment.setEndTime(signalShortage.getEndTime());
          segment.setPreType(SegmentPreType.stationarySignalShortage);
          segments.add(segment);
        }

      }

      segmentBetweenSignalShortage = new Segment();
      segmentBetweenSignalShortage.setOrigin("SignalShortageFinder");
      segmentBetweenSignalShortage.setStartTime(signalShortage.getEndTime());
      segmentBetweenSignalShortage.setPreType(SegmentPreType.NotClassifiedYet);
    }




    if(signalShortages.size() == 0){
      Segment segment = new Segment();
      segment.setStartTime(startTime);
      segment.setEndTime(endTime);
      segment.setPreType(SegmentPreType.NotClassifiedYet);
      segment.setOrigin("SignalShortageFinder");
      segments.add(segment);
    }

    return segments;

  }

  protected void mergeSignalShortages(List<IGpsPoint> coordinates,
      List<SignalShortageInfo> signalShortages) {

    Iterator<SignalShortageInfo> sigShortIterator = signalShortages.iterator();
    if(sigShortIterator.hasNext()){
      SignalShortageInfo currentSignalShortage = sigShortIterator.next();

      while (sigShortIterator.hasNext()){
        SignalShortageInfo nextSignalShortage = sigShortIterator.next();

        LocalDateTime lastTimeOfCurrentSignalShortage = currentSignalShortage.getEndTime();
        LocalDateTime startTimeOfNextSignalShortage = nextSignalShortage.getStartTime();

        Duration diffBetweenSignalShortage = Duration.between(lastTimeOfCurrentSignalShortage, startTimeOfNextSignalShortage);

        Speed speedBetween = calcSpeedBetween(currentSignalShortage.getEndIndex(),nextSignalShortage.getStartIndex(),coordinates);

        boolean belowMinDurationForBelow20KmH =
            minDurationBetweenCSignalShortageBelow20KmH.compareTo(diffBetweenSignalShortage) > 0;

        boolean belowMinDurationForAbove20KmH =  minDurationBetweenCSignalShortageAbove20KmH.compareTo(diffBetweenSignalShortage) > 0;



        if(belowMinDurationForBelow20KmH) {
          // merge those two
          currentSignalShortage.setEnd(nextSignalShortage.getEndIndex(),nextSignalShortage.getEndPoint(),nextSignalShortage.getEndTime());
          sigShortIterator.remove();

        }else if(!belowMinDurationForBelow20KmH && belowMinDurationForAbove20KmH){
            if(speedBetween.getKmPerHour() > walkingNonWalkingSpeedThreshold.getKmPerHour() ){

              // merge those two
              currentSignalShortage.setEnd(nextSignalShortage.getEndIndex(),nextSignalShortage.getEndPoint(),nextSignalShortage.getEndTime());
              sigShortIterator.remove();
            }else{
              currentSignalShortage = nextSignalShortage;
              // do nothing
            }

        }else{
          currentSignalShortage = nextSignalShortage;
          // do nothing
        }

      }

    }
  }


  private List<SignalShortageInfo> collectSignalShortages(List<IGpsPoint> coordinates, LocalDateTime startTime,
      LocalDateTime endTime){

    List<SignalShortageInfo> signalShortages = new ArrayList<>();
    int size = coordinates.size();

    Iterator<IGpsPoint> iterator = coordinates.iterator();
    IGpsPoint current = iterator.next();

    LocalDateTime currentTime = current.getTime();
    Duration between = Duration.between(startTime, currentTime);


    // segment between start of tracking and first gps point
    signalShortages.add(new SignalShortageInfo(startTime,currentTime,null,current,-1,0));


    int index = 0;
    while (iterator.hasNext()){
      IGpsPoint next = iterator.next();
      index++;

      currentTime = current.getTime();
      LocalDateTime nextTime = next.getTime();
      Duration duration = Duration.between(currentTime, nextTime);

      //  if signalshortageThreshold is smaller than duration, the threshold is reached and the signal shortage is long enough
      boolean durationBiggerThanThreshold = signalShortageThreshold.compareTo(duration) <= 0;
      if(durationBiggerThanThreshold){
        signalShortages.add(new SignalShortageInfo(currentTime,nextTime,current,next,index-1,index));
      }
      current = next;
    }

    signalShortages.add(new SignalShortageInfo(current.getTime(),endTime,current,null,size-1,-1));

    return signalShortages;
  }


  private Speed calcSpeedBetween(int lastTimeOfCurrentSignalShortage,
      int startTimeOfNextSignalShortage, List<IGpsPoint> coordinates) {


    List<Speed> speeds = new ArrayList<>();

    IGpsPoint lastCoordinate = null;
    for(int i  = lastTimeOfCurrentSignalShortage; i <= startTimeOfNextSignalShortage; i++){

      IGpsPoint coordinate = coordinates.get(i);
      if(lastCoordinate == null){
        lastCoordinate = coordinate;

      }else{

        Speed speed = CoordinateUtil.calcSpeedBetween(lastCoordinate, coordinate);
        speeds.add(speed);

        lastCoordinate = coordinate;
      }
    }


    double speedSum = 0;

    for (Speed speed : speeds) {

      speedSum += speed.getKmPerHour();
    }
    int size = speeds.size();
    if(size != 0){
      double averageSpeed = speedSum / size;
      return new Speed(averageSpeed);

    }else{
      return new Speed(0);
    }


  }


  private class SignalShortageInfo {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private IGpsPoint startPoint;
    private IGpsPoint endPoint;
    private int startIndex = -1;
    private int endIndex = -1;

    public SignalShortageInfo(LocalDateTime startTime, LocalDateTime endTime,
        IGpsPoint startPoint, IGpsPoint endPoint, int startIndex, int endIndex) {
      this.startTime = startTime;
      this.endTime = endTime;
      this.startPoint = startPoint;
      this.endPoint = endPoint;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }


    public boolean isUnknown() {
      return startPoint == null || endPoint == null;
    }


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

    public IGpsPoint getStartPoint() {
      return startPoint;
    }

    public void setStartPoint(IGpsPoint startPoint) {
      this.startPoint = startPoint;
    }

    public IGpsPoint getEndPoint() {
      return endPoint;
    }

    public void setEndPoint(IGpsPoint endPoint) {
      this.endPoint = endPoint;
    }

    public int getStartIndex() {
      return startIndex;
    }

    public int getEndIndex() {
      return endIndex;
    }


    public void setStart(int index, IGpsPoint startPoint, LocalDateTime startTime) {
      this.startIndex = index;
      this.startPoint = startPoint;
      this.startTime = startTime;
    }

    public void setEnd(int endIndex, IGpsPoint endPoint, LocalDateTime endTime) {
      this.endIndex = endIndex;
      this.endPoint = endPoint;
      this.endTime = endTime;
    }

  }


}
