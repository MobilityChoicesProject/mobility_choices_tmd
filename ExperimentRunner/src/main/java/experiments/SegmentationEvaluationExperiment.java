package experiments;

import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.segmentClassification.util.Helper;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportClassifier.segmentsplitting.Segment;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportClassifier.segmentsplitting.SegmentationService;
import at.fhv.transportClassifier.segmentsplitting.SegmentationServiceImp;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.OutputHelper;
import helper.PropertyHelper;
import helper.TrackingIdNamePairIterator;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 10.07.2017.
 */
public class SegmentationEvaluationExperiment {


  public static Distance maxDistance =new Distance(0.1);

  OutputHelper outputHelper = OutputHelper.getOutputHelper("Segmentation_Evaluation.txt");
  private ConfigService configService;


  public SegmentationEvaluationExperiment() throws IOException {
  }


  public void doIt(LeightweightTrackingDao leightweightTrackingDao) throws IOException {

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

    File trainingSetFile = new File(dataFolder, Constants.trainingSet);
    File testSetFile = new File(dataFolder,Constants.testSet);

    List<TrackingIdNamePair> trainingSet = TrackingIdNamePairFileReaderHelper.load(trainingSetFile.getPath());
    List<TrackingIdNamePair> testSet = TrackingIdNamePairFileReaderHelper.load(testSetFile.getPath());

    TrackingIdNamePairIterator idNamePairIterator = new TrackingIdNamePairIterator(trainingSet,leightweightTrackingDao);
    List<SegmentEvaluationResult> train_segmentEvaluationResults = new ArrayList<>();
    while (idNamePairIterator.hasNext()) {
      Tracking next = idNamePairIterator.next();
      SegmentEvaluationResult evaluate = evaluate(next);
      train_segmentEvaluationResults.add(evaluate);
    }

    System.out.println("--------------------------");

    int train_totalLabeledChangePoints = 0;
    int train_totalSplittedChangePoints = 0;
    int train_totalUsedLabeledChangePoints =0;
    int train_totalUsedSplittedChangePoints =0;
    for (SegmentEvaluationResult segmentEvaluationResult : train_segmentEvaluationResults) {
      train_totalLabeledChangePoints += segmentEvaluationResult.getLabeledChangePoints();
      train_totalSplittedChangePoints += segmentEvaluationResult.getSplittedChangePoints();
      train_totalUsedLabeledChangePoints += segmentEvaluationResult.getUsedLabeledChangePoints();
      train_totalUsedSplittedChangePoints += segmentEvaluationResult.getUsedSplittedChangePoints();
      System.out.println(segmentEvaluationResult.getSplittedChangePoints()-segmentEvaluationResult.getLabeledChangePoints());

    }

    System.out.println("--------------------------");


    idNamePairIterator = new TrackingIdNamePairIterator(testSet,leightweightTrackingDao);
    List<SegmentEvaluationResult> test_segmentEvaluationResults = new ArrayList<>();
    while (idNamePairIterator.hasNext()) {
      Tracking next = idNamePairIterator.next();
      SegmentEvaluationResult evaluate = evaluate(next);
      test_segmentEvaluationResults.add(evaluate);
    }

    int test_totalLabeledChangePoints = 0;
    int test_totalSplittedChangePoints = 0;
    int test_totalUsedLabeledChangePoints =0;
    int test_totalUsedSplittedChangePoints =0;
    for (SegmentEvaluationResult segmentEvaluationResult : test_segmentEvaluationResults) {
      test_totalLabeledChangePoints += segmentEvaluationResult.getLabeledChangePoints();
      test_totalSplittedChangePoints += segmentEvaluationResult.getSplittedChangePoints();
      test_totalUsedLabeledChangePoints += segmentEvaluationResult.getUsedLabeledChangePoints();
      test_totalUsedSplittedChangePoints += segmentEvaluationResult.getUsedSplittedChangePoints();
      System.out.println(segmentEvaluationResult.getSplittedChangePoints()-segmentEvaluationResult.getLabeledChangePoints());
    }
    System.out.println("--------------------------");



    test_segmentEvaluationResults.sort((o1, o2) -> (o1.getSplittedChangePoints()-o1.getLabeledChangePoints())-(o2.getSplittedChangePoints()-o2.getLabeledChangePoints()));
    SegmentEvaluationResult test_medianValue = test_segmentEvaluationResults
        .get(test_segmentEvaluationResults.size() / 2);

    double train_foundChangePointsPercentage = train_totalUsedLabeledChangePoints/(1.0*train_totalLabeledChangePoints);
    double test_foundChangePointsPercentage = test_totalUsedLabeledChangePoints/(1.0*test_totalLabeledChangePoints);



    outputHelper.writeLine("training_data: Number of change points in labeled Trackings: "+train_totalLabeledChangePoints);
    outputHelper.writeLine("training_data: Number of change points found with Segmentation: "+train_totalSplittedChangePoints);
    outputHelper.writeLine("training_data: Number of change points overlapping: "+train_totalUsedLabeledChangePoints);
    outputHelper.writeLine("training_data: percentage of found Points: "+train_foundChangePointsPercentage);

    outputHelper.writeLine("");
    outputHelper.writeLine("test_data: Number of change points overlapping: "+test_totalLabeledChangePoints);
    outputHelper.writeLine("test_data: Number of change points overlapping: "+test_totalSplittedChangePoints);
    outputHelper.writeLine("test_data: Number of change points overlapping: "+test_totalUsedLabeledChangePoints);
    outputHelper.writeLine("test_data: percentage of found Points: "+test_foundChangePointsPercentage);



    outputHelper.saveAndClose();

    int b = 4;



  }


  private SegmentEvaluationResult evaluate(Tracking tracking){

    TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();

    List<TrackingSegment> segments = latestTrackingSegmentBag.getSegments();

    SegmentationService segmentationService = new SegmentationServiceImp();


    SameSequelTransportModeMergeFilter sameSequelTransportModeMergeFilter = new SameSequelTransportModeMergeFilter();
    tracking = sameSequelTransportModeMergeFilter.filter(tracking);
    CoordinateInterpolator coordinateInterpolator = Helper.filterAndCreateCoordinateInterpolator(tracking,configService);

    List<Segment> segmentedSegments = segmentationService
        .splitIntoSegments(coordinateInterpolator, tracking.getStartTimestamp(),
            tracking.getEndTimestamp());



    List<SegmentPoint> segmentPoints = new ArrayList<>();
    TrackingSegment lastTrackingSegment = null;
    for (TrackingSegment segment : segments) {

      LocalDateTime endTime = segment.getEndTime();
      if(lastTrackingSegment != null && lastTrackingSegment.getEndTime() != segment.getStartTime()){
        throw new IllegalStateException("Should not happen");
      }

      segmentPoints.add(new SegmentPoint(endTime));
    }


    List<SegmentPoint> splittedSegments = new ArrayList<>();
    Segment lastSegment = null;
    for (Segment segment : segmentedSegments) {

      if(segment.getPreType() == SegmentPreType.NonClassifiable){
        continue;
      }

      LocalDateTime endTime = segment.getEndTime();
      if(lastSegment != null && lastSegment.getEndTime() != segment.getStartTime()){
        throw new IllegalStateException("Should not happen");
      }
      splittedSegments.add(new SegmentPoint(endTime));
      lastSegment= segment;
    }



   //evaluate

    for (SegmentPoint segmentPoint : segmentPoints) {

      check(segmentPoint,splittedSegments,coordinateInterpolator);
    }




    SegmentEvaluationResult segmentEvaluationResult = new SegmentEvaluationResult();

    for (SegmentPoint segmentPoint : segmentPoints) {

      segmentEvaluationResult.IncrementlabeledChangePoints();
      if(segmentPoint.isUsed()){
        segmentEvaluationResult.IncrementUsedLabeledChangePoints();
      }

    }

    for (SegmentPoint splittedSegment : splittedSegments) {
      segmentEvaluationResult.IncrementSplittedChangePoints();
      if(splittedSegment.isUsed()){
        segmentEvaluationResult.IncrementUsedSplittedChangePoints();
      }
    }

    return segmentEvaluationResult;
  }


  private void check(SegmentPoint segmentPoint, List<SegmentPoint> splittedSegments,
      CoordinateInterpolator coordinateInterpolator) {

    SegmentPoint closestSegmgnetPoint=null;
    Duration minDuration = Duration.ofDays(9999999);
    for (SegmentPoint splittedSegment : splittedSegments) {

      Duration duration = Duration.between(segmentPoint.getTime(), splittedSegment.getTime()).abs();
      if(minDuration.compareTo(duration)>0){
        if(!splittedSegment.isUsed()){
          minDuration =duration;
          closestSegmgnetPoint = splittedSegment;
        }

      }

    }

    if(closestSegmgnetPoint == null){
      return;
    }

    IGpsPoint labeldGpsPoint=coordinateInterpolator.getCoordinate(segmentPoint.getTime());
    IGpsPoint closestSplittedGpsPoint=coordinateInterpolator.getCoordinate(closestSegmgnetPoint.getTime());

    Distance distance = CoordinateUtil.haversineDistance(labeldGpsPoint, closestSplittedGpsPoint);
    if(maxDistance.isLongerThan(distance)){
      segmentPoint.setUsed(true);
      closestSegmgnetPoint.setUsed(true);
    }
  }


  private static class SegmentPoint{


    private LocalDateTime time;
    private boolean used;


    public SegmentPoint(LocalDateTime time) {
      this.time = time;
    }

    public LocalDateTime getTime() {
      return time;
    }

    public void setTime(LocalDateTime time) {
      this.time = time;
    }

    public boolean isUsed() {
      return used;
    }

    public void setUsed(boolean used) {
      this.used = used;
    }
  }

  public static class SegmentEvaluationResult{

    private int labeledChangePoints = 0;
    private int splittedChangePoints = 0;

    private int usedLabeledChangePoints = 0;
    private int usedSplittedChangePoints = 0;


    public void IncrementlabeledChangePoints(){
      labeledChangePoints++;
    }
    public void IncrementSplittedChangePoints(){
      splittedChangePoints++;
    }
    public void IncrementUsedLabeledChangePoints(){
      usedLabeledChangePoints++;
    }
    public void IncrementUsedSplittedChangePoints(){
      usedSplittedChangePoints++;
    }


    public int getLabeledChangePoints() {
      return labeledChangePoints;
    }

    public void setLabeledChangePoints(int labeledChangePoints) {
      this.labeledChangePoints = labeledChangePoints;
    }

    public int getSplittedChangePoints() {
      return splittedChangePoints;
    }

    public void setSplittedChangePoints(int splittedChangePoints) {
      this.splittedChangePoints = splittedChangePoints;
    }

    public int getUsedLabeledChangePoints() {
      return usedLabeledChangePoints;
    }

    public void setUsedLabeledChangePoints(int usedLabeledChangePoints) {
      this.usedLabeledChangePoints = usedLabeledChangePoints;
    }

    public int getUsedSplittedChangePoints() {
      return usedSplittedChangePoints;
    }

    public void setUsedSplittedChangePoints(int usedSplittedChangePoints) {
      this.usedSplittedChangePoints = usedSplittedChangePoints;
    }
  }


}
