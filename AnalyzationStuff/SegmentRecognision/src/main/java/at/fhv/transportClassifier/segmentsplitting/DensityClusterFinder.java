package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceImp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 02.05.2017.
 */
public class DensityClusterFinder {



  private double distanceThreshold= 5; // max distance (+accuracy of points) a point has to have to another point to be a cluster point
  private double distanceThresholdFactor = 0.80;
  private int pointsRadius= 30;

  private int frameSize = 30;
  private double frameSizeThresholdFactor = 0.66;
  private int minimalPointsBetween;
  private Duration minimalDurationOfCluster;


  ConfigService configService;


  public DensityClusterFinder(ConfigService configService) {
    this.configService = configService;
  }

  private void updateConfigSettings(){

    double pointsRadius = configService.getValue(ConfigServiceDefaultCache.pointsRadius);
    double frameSize = configService.getValue(ConfigServiceDefaultCache.frameSize);
    double minimalPointsBetweenCluster = configService.getValue(ConfigServiceDefaultCache.minimalPointsBetweenCluster);
    long minimalClusterDuration = (long) configService.getValue(ConfigServiceDefaultCache.minimalClusterDuration);

    this.frameSizeThresholdFactor = configService.getValue(ConfigServiceDefaultCache.frameSizeThresholdFactor);
    this.distanceThreshold = configService.getValue(ConfigServiceDefaultCache.distanceThreshold);
    this.distanceThresholdFactor = configService.getValue(ConfigServiceDefaultCache.distanceThresholdFactor);
    this.pointsRadius = (int)pointsRadius;
    this.frameSize = (int) frameSize;
    this.minimalPointsBetween = (int)minimalPointsBetweenCluster;
    this.minimalDurationOfCluster = Duration.ofSeconds(minimalClusterDuration);
  }



  public  List<Segment> find(List<IGpsPoint> points){

    updateConfigSettings();
    List list = calculateDensityThresholds(points);
    List<Tuple<Integer, Integer>> startEndClusterPoints = putDesnityThresholdPointsTogether(list);
    mergeClusterPoints(startEndClusterPoints,60);
    extendAtEdgesWhenNeccesary(startEndClusterPoints,60,points);
    removeToShortClusterPoints(startEndClusterPoints, Duration.ofSeconds(60));

    LocalDateTime lastFirstTime = points.get(0).getTime();
    // create Segments
    List<Segment> clusters = new ArrayList<>();
    for (Tuple<Integer, Integer> startEndClusterPoint : startEndClusterPoints) {
      Integer index1 = startEndClusterPoint.getItem1();
      Integer index2 = startEndClusterPoint.getItem2();
      IGpsPoint startPoint = points.get(index1);
      IGpsPoint endPoint = points.get(index2);

      if(lastFirstTime.compareTo(startPoint.getTime()) <0){
        Segment segment = new Segment();
        segment.setStartTime(lastFirstTime);
        segment.setEndTime(startPoint.getTime());
        segment.setPreType(SegmentPreType.NotClassifiedYet);
        segment.setOrigin("DensityClusterFinder");
        clusters.add(segment);
      }

      Segment segment = new Segment();
      segment.setStartTime(startPoint.getTime());
      segment.setEndTime(endPoint.getTime());
      segment.setPreType(SegmentPreType.stationaryCluster);
      segment.setOrigin("DensityClusterFinder");
      clusters.add(segment);

      lastFirstTime = endPoint.getTime();
    }

    IGpsPoint lastPoint = points.get(points.size() - 1);
    if(lastFirstTime.compareTo(lastPoint.getTime()) != 0){
      Segment segment = new Segment();
      segment.setStartTime(lastFirstTime);
      segment.setEndTime(lastPoint.getTime());
      segment.setPreType(SegmentPreType.NotClassifiedYet);
      segment.setOrigin("DensityClusterFinder");
      clusters.add(segment);
    }

    return clusters;

  }

  private List<Tuple<Integer,Integer>> putDesnityThresholdPointsTogether(List list) {
    int clusterSize = list.size();
    boolean clusterStartFound= false;
    int clusterStartIndex= 0;
    int clusterEndIndex= 0;

    List<Tuple<Integer,Integer>> startEndClusterPoints = new ArrayList<>();
    for(int i = 0; i < clusterSize-frameSize;i++){

      boolean clusterPart = isClusterPart(list, i, frameSize);

      if(clusterPart && !clusterStartFound){
        clusterStartFound = true;
        clusterStartIndex= i;
      }

      if(!clusterPart && clusterStartFound){
//        clusterEndIndex = i-1+frameSize;
        clusterEndIndex = getClusterEndIndex(list,i,frameSize);
        clusterStartFound= false;
        startEndClusterPoints.add(new Tuple<>(clusterStartIndex,clusterEndIndex));
      }

    }

    if(clusterStartFound){
      clusterEndIndex = clusterSize-1;
      startEndClusterPoints.add(new Tuple<>(clusterStartIndex,clusterEndIndex));
    }

    return startEndClusterPoints;


  }

  private int getClusterEndIndex(List<Tuple<Boolean, IGpsPoint>> list, int i, int frameSize) {

    for (; i < i + frameSize; i++) {
      boolean noDensityPoint = !list.get(i).getItem1();
      if (noDensityPoint) {
        return i;
      }
    }
    return i+frameSize-1;
  }



  private List<Tuple<Boolean, IGpsPoint>> calculateDensityThresholds(List<IGpsPoint> points) {
    List<Tuple<Boolean, IGpsPoint>> list = new ArrayList<>();

    int size = points.size();
    for(int i = 0; i <size;i++){

      IGpsPoint gpsPoint = points.get(i);

      int inThresholdCounter= 0;
      int totalCounter= 0;


      int min  = i -pointsRadius;
      if(min < 0){
        min = 0;
      }
      for(int j = i-1; j >=min;j--){
        IGpsPoint previousGpsPoint = points.get(j);
        totalCounter++;
        double inaccuracyBuffer = (previousGpsPoint.getAccuracy()+gpsPoint.getAccuracy())/2;
        Distance distance = CoordinateUtil.haversineDistance(previousGpsPoint, gpsPoint);
        if(distance.getMeter() <= distanceThreshold+inaccuracyBuffer){
          inThresholdCounter++;
        }
      }

      int max = i+pointsRadius;
      if(max >size ){
        max = size;
      }

      for(int j = i+1; j  <max;j++){
        IGpsPoint nextGpsPoint = points.get(j);
        totalCounter++;
        double inaccuracyBuffer = nextGpsPoint.getAccuracy()+gpsPoint.getAccuracy();

        Distance distance = CoordinateUtil.haversineDistance(gpsPoint, nextGpsPoint);
        if(distance.getMeter() <= distanceThreshold+inaccuracyBuffer){
          inThresholdCounter++;
        }
      }

      double fraction = inThresholdCounter / (totalCounter*1.0);
      if(fraction>distanceThresholdFactor){
        // high density
        list.add(new Tuple<Boolean, IGpsPoint>(true,gpsPoint));
      }else{
        list.add(new Tuple<Boolean, IGpsPoint>(false,gpsPoint));
      }

    }
    return list;
  }

  private void removeToShortClusterPoints(List<Tuple<Integer, Integer>> startEndClusterPoints,
      Duration duration) {
    Iterator<Tuple<Integer, Integer>> iterator = startEndClusterPoints.iterator();

    while (iterator.hasNext()){
      Tuple<Integer, Integer> next = iterator.next();
      int diffInSeconds = next.getItem2() - next.getItem1();

      long seconds = duration.toMillis() / 1000;
      if(diffInSeconds< seconds){
        iterator.remove();
      }


    }


  }

  private void mergeClusterPoints(List<Tuple<Integer, Integer>> startEndClusterPoints,
      int minimalPointsBetween) {

    if(startEndClusterPoints.size()<2){
      return;
    }

    Iterator<Tuple<Integer, Integer>> iterator = startEndClusterPoints.iterator();
    Tuple<Integer, Integer> current = iterator.next();
    while (iterator.hasNext()){
      Tuple<Integer, Integer> next = iterator.next();

      int diff = next.getItem1() - current.getItem2();
      if(diff< minimalPointsBetween){
        iterator.remove();
        current.setItem2(next.getItem2());
      }else{
        current = next;
      }
    }

  }

  private void extendAtEdgesWhenNeccesary(List<Tuple<Integer, Integer>> startEndClusterPoints, int minimalPointsBetween,List<IGpsPoint> coordinates){

    if(startEndClusterPoints.size() == 0){
      return;
    }
    IGpsPoint coordinate = coordinates.get(0);
    Tuple<Integer, Integer> firstCluster = startEndClusterPoints.get(0);

    Integer item1 = firstCluster.getItem1();
    int diff =    item1- minimalPointsBetween;
    if(diff<0){
      firstCluster.setItem1(0);
    }

    int clusterSize = startEndClusterPoints.size();
    Tuple<Integer, Integer> lastCluster = startEndClusterPoints.get(clusterSize-1);
    int lastCoordinatesIndex = coordinates.size()-1;
    diff = lastCoordinatesIndex - lastCluster.getItem2();
    if(diff < minimalPointsBetween){
      lastCluster.setItem2(lastCoordinatesIndex);
    }

  }

  private boolean isClusterPart(List<Tuple<Boolean, IGpsPoint>> list, int index, int frameSize ){

    int counter = 0;
    for(int i = index; i <index+frameSize;i++){
      Tuple<Boolean, IGpsPoint> booleanGpsPointTuple = list.get(i);
      if (booleanGpsPointTuple.getItem1()) {
        counter++;
      }
    }

    double fraction = counter / (frameSize * 1.0);
    return fraction>=frameSizeThresholdFactor;


  }












}
