package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportClassifier.common.BinaryCollectionSearcher;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 21.05.2017.
 */
public class OptimizedGaussKernelSmoother implements GaussKernelSmoother {

  private BinaryCollectionSearcher<IGpsPoint,LocalDateTime> binaryCollectionSearcher = new BinaryCollectionSearcher<>();
  private CoordinateFactory factory;

  public OptimizedGaussKernelSmoother(CoordinateFactory factory){
    this.factory = factory;
  }
  double maxRangeFactor =4.417;

  public double getMaxRangeFactor() {
    return maxRangeFactor;
  }

  public void setMaxRangeFactor(double maxRangeFactor) {
    this.maxRangeFactor = maxRangeFactor;
  }

  @Override
  public IGpsPoint calcPoint(LocalDateTime time, List<IGpsPoint> points, double sigma){

    LocalDateTime inputTime = time;

    int size = points.size();
    time = fixTimeBeforeOrAfterPoints(time,points);

    long maxRangeRadiusMs = (long) (sigma*1000*maxRangeFactor );
    double sigmaMillis = sigma*1000;
    Duration maxRange = Duration.ofMillis(maxRangeRadiusMs);

    int index = binaryCollectionSearcher
        .find(points, time, (item, localDateTime) -> item.getTime().compareTo(localDateTime));

    if(index <0 ){
      int insertionPoint = -1*(index+1);
      if(insertionPoint>= size){
        index = size -1;
      }else{
        index = insertionPoint;
      }
    }

    IGpsPoint firstPoint = points.get(0);
    LocalDateTime zeroTime = firstPoint.getTime();

    Duration between = Duration.between(zeroTime, time);
    long timeInMillis = between.toMillis();

    boolean nextDataPointUsed=false;
    boolean  previousDataPointsUsed = false;
    GaussContext context = new GaussContext();

    for(int i = index-1; i>= 0;i--){
      IGpsPoint coordinate = points.get(i);
      boolean thresholdReached = isThresholdReached(time, maxRange, coordinate);
      if(thresholdReached){
        // now 99,7  percent of the data for the smoothing are used.
        break;
      }
      doCalculations(sigmaMillis, zeroTime, timeInMillis, context, coordinate);
      previousDataPointsUsed = true;
    }

    for(int i = index; i <points.size();i++){
      IGpsPoint coordinate = points.get(i);
      boolean thresholdReached = isThresholdReached(time, maxRange, coordinate);
      if(thresholdReached){
        break;
      }
      doCalculations(sigmaMillis, zeroTime, timeInMillis, context, coordinate);
      nextDataPointUsed = true;
    }


    IGpsPoint  coordinate;
    if(!(nextDataPointUsed || previousDataPointsUsed)){
         coordinate= handleBigSignalShortage(time,points,sigma);
    }else{
      double x = 0;
      double y = 0;
      double accuracy = 0;

      if(context.getXsum()!= 0){
        x = context.getXsum()/context.getSumOfGausianKernelFunction();
      }
      if(context.getYsum()!= 0){
        y = context.getYsum()/context.getSumOfGausianKernelFunction();
      }
      if(context.getAccuracySum()!= 0){
        accuracy = context.getAccuracySum()/context.getSumOfGausianKernelFunction();
      }
      coordinate = factory.createCoordinate(x, y, accuracy, time);
    }

    if(!coordinate.getTime().equals(inputTime)){
      coordinate = factory.createCoordinate(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getAccuracy(), inputTime);
    }

    return coordinate;
  }

  private LocalDateTime fixTimeBeforeOrAfterPoints(LocalDateTime time,List<IGpsPoint> points ){
    int size = points.size();
    IGpsPoint fistPoint = points.get(0);
    IGpsPoint lastPoint = points.get(size-1);

    if(time.isBefore(fistPoint.getTime())){
      time  = fistPoint.getTime();
    }else if(time.isAfter(lastPoint.getTime())){
      time = lastPoint.getTime();
    }
    return time;
  }


  private IGpsPoint handleBigSignalShortage(LocalDateTime time, List<IGpsPoint> points, double sigma) {

    int index = binaryCollectionSearcher
        .find(points, time, (item, localDateTime) -> item.getTime().compareTo(localDateTime));

    int size = points.size();
    if(index <0 ){
      int insertionPoint = -1*(index+1);
      if(insertionPoint>= size){
        throw new IllegalStateException("OptimizedGaussKernelSmoother seems to have a bug. index cant be >= size");
      }else if(insertionPoint==0) {
        throw new IllegalStateException("OptimizedGaussKernelSmoother seems to have a bug. index cant be == 0");
      } else{
          index = insertionPoint;
        }
      }else {
      throw new IllegalStateException("OptimizedGaussKernelSmoother seems to have a bug. it should not be possible to find point at exact time");
    }


    int indexOfPreviousPoint = index-1;
    int indexOfNextPoint = index;

    IGpsPoint previousPoint = points.get(indexOfPreviousPoint);
    IGpsPoint nextPoint = points.get(indexOfNextPoint);

    LocalDateTime previousTime =  previousTime = previousPoint.getTime();
    IGpsPoint previousMaxInterpolatedPoint = calcPoint(previousTime,
        points, sigma);

    LocalDateTime nextTime = nextPoint.getTime();
    IGpsPoint nextMaxInterpolatedPoint = calcPoint(nextTime,
        points, sigma);

    Duration duration = Duration
        .between(previousMaxInterpolatedPoint.getTime(), nextMaxInterpolatedPoint.getTime());

    Duration previousDuration = Duration.between(previousMaxInterpolatedPoint.getTime(),time);


    double previousFraction = previousDuration.toMillis()/((double)duration.toMillis());
    double nextFraction  = 1 - previousFraction;


    double latitude = previousMaxInterpolatedPoint.getLatitude()*previousFraction + nextMaxInterpolatedPoint.getLatitude()*nextFraction;
    double longitude = previousMaxInterpolatedPoint.getLongitude()*previousFraction + nextMaxInterpolatedPoint.getLongitude()*nextFraction;
    double accuracy = previousMaxInterpolatedPoint.getAccuracy()*previousFraction + nextMaxInterpolatedPoint.getAccuracy()*nextFraction;
    IGpsPoint coordinate = factory.createCoordinate(latitude, longitude, accuracy, time);

    return coordinate;
  }



  protected boolean isThresholdReached(LocalDateTime time, Duration sigmaFastDuration,
      IGpsPoint coordinate) {
    LocalDateTime currentTime = coordinate.getTime();

    Duration abs = Duration.between(time, currentTime).abs();
    return sigmaFastDuration.compareTo(abs) < 0;
  }

  protected void doCalculations(double sigma, LocalDateTime zeroTime, long timeInSeconds,
      GaussContext context, IGpsPoint coordinate) {
    double x_c_t = coordinate.getLatitude();
    double y_c_t = coordinate.getLongitude();
    double accuracy_c_t = coordinate.getAccuracy();

    long millis = Duration.between(zeroTime, coordinate.getTime()).toMillis();
    double tj = millis;

    double  w_t = GaussKernelHelper.gausianKernelFunction(timeInSeconds,tj,sigma);

    context.addXSum(x_c_t*w_t);
    context.addYSum(y_c_t*w_t);
    context.addAccuracy(accuracy_c_t*w_t);
    context.addGausianKernelFunction(w_t);
    context.incrementCounter();
  }





  public class GaussContext{
    private double xsum = 0;
    private double ysum = 0;
    private double accuracySum = 0;
    private double sumOfGausianKernelFunction = 0;
    private int counter = 0;

    public void incrementCounter(){
      counter++;
    }

    public int getCounter() {
      return counter;
    }

    public void addXSum(double x){
      xsum+=x;
    }
    public void addYSum(double y){
      ysum+=y;
    }
    public void addAccuracy(double accuracy){
      accuracySum+=accuracy;
    }

    public void addGausianKernelFunction(double gausianKernelFunction){
      sumOfGausianKernelFunction+=gausianKernelFunction;
    }

    public double getXsum() {
      return xsum;
    }

    public void setXsum(double xsum) {
      this.xsum = xsum;
    }

    public double getYsum() {
      return ysum;
    }

    public void setYsum(double ysum) {
      this.ysum = ysum;
    }

    public double getAccuracySum() {
      return accuracySum;
    }

    public void setAccuracySum(double accuracySum) {
      this.accuracySum = accuracySum;
    }

    public double getSumOfGausianKernelFunction() {
      return sumOfGausianKernelFunction;
    }

    public void setSumOfGausianKernelFunction(double sumOfGausianKernelFunction) {
      this.sumOfGausianKernelFunction = sumOfGausianKernelFunction;
    }

  }




}
