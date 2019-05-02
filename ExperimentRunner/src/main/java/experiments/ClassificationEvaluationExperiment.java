package experiments;

import at.fhv.features.LabeledFeature;
import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.tmd.DroolsClassificationService;
import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.segmentClassification.classifier.ClassifierException;
import at.fhv.tmd.segmentClassification.classifier.ClassifierNew;
import at.fhv.tmd.segmentClassification.classifier.ClassifierParameters;
import at.fhv.tmd.segmentClassification.util.Helper;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportClassifier.mainserver.impl.GisFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.LocationFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.SpeedCalculatorService;
import at.fhv.transportClassifier.mainserver.transaction.EntityManagerTransaction;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import com.google.gson.Gson;
import experiments.classificationEvaluation.ClassificationEvaluationResult;
import experiments.classificationEvaluation.ClassificationProbabilityMargin;
import experiments.classificationEvaluation.ConfusionMatrix;
import experiments.classificationEvaluation.Evaluation;
import experiments.classificationEvaluation.MarginThresholdCounter;
import helper.GpsTrackingDaoFileNameIterator;
import helper.OutputHelper;
import helper.OverlappingCalcualtor;
import helper.OverlappingResult;
import helper.PropertyHelper;
import helper.Section;
import helper.StringSavingHelper;
import helper.ThreadSafeIterator;
import helper.TrackingIdNamePairIterator;
import helper.segmentation.SegmentationHelper;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Created by Johannes on 11.07.2017.
 */
public class ClassificationEvaluationExperiment {


  DecimalFormat decimalFormat;

  String dot_line = "---------------------------------------------";
  OutputHelper outputHelper = OutputHelper.getOutputHelper("Classification_Evaluation.txt");


  LeightweightTrackingDao leightweightTrackingDao ;


  public ClassificationEvaluationExperiment(
      LeightweightTrackingDao leightweightTrackingDao) throws IOException {
    this.leightweightTrackingDao = leightweightTrackingDao;
  }
  EntityManagerFactory emf;

  ClassifierNew classifierNew = new ClassifierNew();
  DroolsClassificationService droolsClassificationService = new DroolsClassificationService();

  public void doIt(EntityManagerFactory emf) throws IOException {

    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
    otherSymbols.setDecimalSeparator(',');
    otherSymbols.setGroupingSeparator(' ');
    decimalFormat = new DecimalFormat("#.####", otherSymbols);

  this.emf = emf;

    try {
      classifierNew.init(ClassifierParameters.DEFAULT());
    } catch (ClassifierException e) {
      e.printStackTrace();
    }

//    calcOverlapping();

    Gson gson = new Gson();
    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

    FileReader fileReader = new FileReader(dataFolder+"testTrainingSets\\trainingSetWithIds.json");
    LabeledFeature[] trainingSetLabeledFeatures = gson.fromJson(fileReader, LabeledFeature[].class);

    fileReader = new FileReader(dataFolder+"testTrainingSets\\testSetWithIds.json");
    LabeledFeature[] testSetLabeledFeatures = gson.fromJson(fileReader, LabeledFeature[].class);

    ClassifierNew classifierNew1 = new ClassifierNew();
    try {
      classifierNew1.init(ClassifierParameters.WITHOUT_SEGMENTATION());
    } catch (ClassifierException e) {
      e.printStackTrace();
    }

    outputHelper.writeLine(dot_line);
    outputHelper.writeLine("Random Forest with TrainingSet");
    List<ClassificationEvaluationResult> resultsRf_Train = classify(
        trainingSetLabeledFeatures, classifierNew1);
    writeStatisticData(resultsRf_Train);
    writeThresholdAndMarginCurve(resultsRf_Train,"RandomForest_trainingSet");
    writeMargins(resultsRf_Train,"RandomForest_trainingSet");


    outputHelper.writeLine("");
    outputHelper.writeLine(dot_line);
    outputHelper.writeLine("Random Forest with TestSet");
    List<ClassificationEvaluationResult> results_rf_test = classify(testSetLabeledFeatures,classifierNew1);
    writeStatisticData(results_rf_test);
    writeThresholdAndMarginCurve(results_rf_test,"RandomForest_testSet");
    writeMargins(results_rf_test,"RandomForest_testSet");


    outputHelper.writeLine("");
    outputHelper.writeLine(dot_line);
    outputHelper.writeLine("Drools with TrainingSet");
    List<ClassificationEvaluationResult> results_drools_train = classify(trainingSetLabeledFeatures,droolsClassificationService);
    writeStatisticData(results_drools_train);
    writeThresholdAndMarginCurve(results_drools_train,"Drools_trainingSet");
    writeMargins(results_drools_train,"Drools_trainingSet");


    outputHelper.writeLine("");
    outputHelper.writeLine(dot_line);
    outputHelper.writeLine("Drools with TestSet");
    List<ClassificationEvaluationResult> results_drools_test = classify(testSetLabeledFeatures,droolsClassificationService);
    writeStatisticData(results_drools_test);
    writeThresholdAndMarginCurve(results_drools_test,"Drools_testSet");
    writeMargins(results_drools_test,"Drools_testSet");

    writeOverlappingData(resultsRf_Train,results_rf_test,results_drools_train,results_drools_test);

    outputHelper.saveAndClose();
    int B= 4;
  }

  private void writeOverlappingData(List<ClassificationEvaluationResult> resultsRf_train,
      List<ClassificationEvaluationResult> results_rf_test,
      List<ClassificationEvaluationResult> results_drools_train,
      List<ClassificationEvaluationResult> results_drools_test) throws IOException {

    List<OverlappingSum> rf_train = calcOverlappingSum(resultsRf_train);
    List<OverlappingSum> rf_test = calcOverlappingSum(results_rf_test);
    List<OverlappingSum>  drools_train= calcOverlappingSum(results_drools_train);
    List<OverlappingSum>  drools_test= calcOverlappingSum(results_drools_test);


    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("rf_train; rf_test; drools_train; drools_test; ; rf_train; rf_test; drools_train; drools_test"+System.lineSeparator() );

    boolean hasData = true;
    for(int i = 0; hasData;i++){


      stringBuilder.append(getDistanceString(i,rf_train)+"; ");
      stringBuilder.append(getDistanceString(i,rf_test)+"; ");
      stringBuilder.append(getDistanceString(i,drools_train)+"; ");
      stringBuilder.append(getDistanceString(i,drools_test)+"; ");

      stringBuilder.append(" ; ");

      stringBuilder.append(getDurationString(i,rf_train)+"; ");
      stringBuilder.append(getDurationString(i,rf_test)+"; ");
      stringBuilder.append(getDurationString(i,drools_train)+"; ");
      stringBuilder.append(getDurationString(i,drools_test)+System.lineSeparator());

      hasData = false;
      hasData |= rf_train.size() >i;
      hasData |= rf_test.size() >i;
      hasData |= drools_train.size() >i;
      hasData |= drools_test.size() >i;
    }

    String path = PropertyHelper.getValue(Constants.dataFolder)+"Classification_Only_DistanceDurationOverlappings.csv";
    StringSavingHelper.save(path,stringBuilder.toString().getBytes());
  }

  private String getDistanceString(int i, List<OverlappingSum> overlappingSums){
    if(overlappingSums.size()>i){
      OverlappingSum overlappingSum = overlappingSums.get(i);
      double overlapping = overlappingSum.getOverlappingDistance().getKm() / overlappingSum.getDistanceSum().getKm();
      return overlapping+"";
    }
    return " ";
  }

  private String getDurationString(int i, List<OverlappingSum> overlappingSums){
    if(overlappingSums.size()>i){
      OverlappingSum overlappingSum = overlappingSums.get(i);
      double overlapping = overlappingSum.getOverlappingDuration().toMillis() / (overlappingSum.getDurationSum().toMillis()*1.0);
      return overlapping+"";
    }
    return " ";
  }

  private List<OverlappingSum> calcOverlappingSum(List<ClassificationEvaluationResult> list ){

    HashMap<String,OverlappingSum> map = new HashMap<>();
    for (ClassificationEvaluationResult classificationEvaluationResult : list) {
      String fileName = classificationEvaluationResult.getFileName();
      OverlappingSum overlappingSum = map.get(fileName);
      if(overlappingSum == null){
        overlappingSum = new OverlappingSum();
        map.put(fileName,overlappingSum);
      }
      overlappingSum.addDistanceSum(classificationEvaluationResult.getDistance());
      overlappingSum.addDurationSum(classificationEvaluationResult.getDuration());
      if(classificationEvaluationResult.getClassifiedAs() == classificationEvaluationResult.getActual()){
        overlappingSum.addOverlappingDistance(classificationEvaluationResult.getDistance());
        overlappingSum.addOverlappingDuration(classificationEvaluationResult.getDuration());
      }
    }

    List<OverlappingSum> overlappingSums = new ArrayList<>();
    for (String s : map.keySet()) {
      overlappingSums.add(map.get(s));
    }
    return overlappingSums;
  }


  public static class OverlappingSum{
    private Distance distanceSum = new Distance(0);
    private Distance overlappingDistance = new Distance(0);
    private Duration durationSum = Duration.ofSeconds(0);
    private Duration overlappingDuration = Duration.ofSeconds(0);

    public void addDistanceSum(Distance distance){
      distanceSum = distanceSum.plus(distance);
    }
    public void addOverlappingDistance(Distance distance){
      overlappingDistance = overlappingDistance.plus(distance);
    }
    public void addDurationSum(Duration duration){
      durationSum = durationSum.plus(duration);
    }

    public void addOverlappingDuration(Duration duration){
      overlappingDuration = overlappingDuration.plus(duration);
    }

    public Distance getDistanceSum() {
      return distanceSum;
    }

    public Distance getOverlappingDistance() {
      return overlappingDistance;
    }

    public Duration getDurationSum() {
      return durationSum;
    }

    public Duration getOverlappingDuration() {
      return overlappingDuration;
    }
  }

  private void writeMargins(List<ClassificationEvaluationResult> results,
      String dataSet) throws IOException {


    List<Double> bikes = new ArrayList<>();
    List<Double> bus = new ArrayList<>();
    List<Double> car = new ArrayList<>();
    List<Double> other = new ArrayList<>();
    List<Double> train = new ArrayList<>();
    List<Double> walking = new ArrayList<>();
    List<Double> all = new ArrayList<>();


    for (ClassificationEvaluationResult result : results) {

      ClassificationProbabilityMargin classificationProbabilityMargin = result
          .getClassificationProbabilityMargin();

      TransportType actualTransportType = classificationProbabilityMargin.getActualTransportType();

      switch (actualTransportType) {
        case CAR:
          car.add(classificationProbabilityMargin.getMargin());
          break;
        case BIKE:
          bikes.add(classificationProbabilityMargin.getMargin());
          break;
        case BUS:
          bus.add(classificationProbabilityMargin.getMargin());
          break;
        case TRAIN:
          train.add(classificationProbabilityMargin.getMargin());
          break;
        case WALK:
          walking.add(classificationProbabilityMargin.getMargin());
          break;
        case OTHER:
          other.add(classificationProbabilityMargin.getMargin());
          break;
      }

      all.add(classificationProbabilityMargin.getMargin());

    }


    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);
    String folder = dataFolder+"margin/";
    File file = new File(folder);
    if(!file.exists()){
      file.mkdir();
    }

    String filePathStr = folder+"margin_"+dataSet+".csv";
    File filePath = new File(filePathStr);
    if(filePath.exists()){
      filePath.delete();
    }



    StringBuilder margins = new StringBuilder();
    margins.append("All; Bus; Bike; Car; Other; Train; Walking "+System.lineSeparator());

    for(int i = 0; i< all.size();i++){

      margins.append(getString(all,i)+"; ");
      margins.append(getString(bus,i)+"; ");
      margins.append(getString(bikes,i)+"; ");
      margins.append(getString(car,i)+"; ");
      margins.append(getString(other,i)+"; ");
      margins.append(getString(train,i)+"; ");
      margins.append(getString(walking,i)+" "+System.lineSeparator());

    }


    Path  path = Paths.get(filePath.getAbsolutePath());
    byte[] bytes = margins.toString().getBytes();
    Files.write(path,bytes);



  }

  private String getString(List<Double> doubles, int index){

    if(doubles.size()<=index){
      return "  " ;
    }

    String format = df2.format(doubles.get(index));
    return format;

  }




  private void writeThresholdAndMarginCurve(List<ClassificationEvaluationResult> results, String dataSet)
      throws IOException {


    List<MarginThresholdCounter> thresholdCounters = new ArrayList<>();
    List<MarginThresholdCounter> marginCounters = new ArrayList<>();
    for(int i = 0; i <= 100; i++){

      MarginThresholdCounter marginCounter = new MarginThresholdCounter();
      MarginThresholdCounter thresholdCounter = new MarginThresholdCounter();
      marginCounters.add(marginCounter);
      thresholdCounters.add(thresholdCounter);
      for (ClassificationEvaluationResult result : results) {
        ClassificationProbabilityMargin classificationProbabilityMargin = result
            .getClassificationProbabilityMargin();


        TransportType actualTransportType = result.getActual();
        marginCounter.incrementTotal(actualTransportType);
        thresholdCounter.incrementTotal(actualTransportType);

        boolean correctPrediction=result.getActual()== result.getClassifiedAs();

        boolean correctPrediction1= true;
        double bestResult = classificationProbabilityMargin.getActualTransportTypeProbability();
        if(bestResult < classificationProbabilityMargin.getBestNotActualTransportTypeProbability()){
          bestResult = classificationProbabilityMargin.getBestNotActualTransportTypeProbability();
          correctPrediction1 =false;
        }

        if(correctPrediction != correctPrediction1){
//          throw new RuntimeException("There must be a logic error. Both have to be the same");
        }

          bestResult = bestResult*100;
        if(bestResult>=i){
          if(bestResult<18){
            int debug= 3;
          }
          if(correctPrediction){
            thresholdCounter.incrementCorrect(actualTransportType);
          }
          thresholdCounter.incrementActual(actualTransportType);
        }

        double margin = classificationProbabilityMargin.getMargin();
        double absMargin = Math.abs(margin)*100;
        if(absMargin>=i){

          if(correctPrediction){
            marginCounter.incrementCorrect(actualTransportType);
          }
          marginCounter.incrementActual(actualTransportType);
        }
      }
    }

    // build csv file

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);
    String folder = dataFolder+"marginThresholdCurves/";
    File file = new File(folder);
    if(!file.exists()){
      file.mkdir();
    }

    String filePathStr = folder+"margin_"+dataSet+".csv";
    File filePath = new File(filePathStr);
    if(filePath.exists()){
      filePath.delete();
    }

    StringBuilder marginCsv = getCsv(marginCounters);
    Path path = Paths.get(filePath.getAbsolutePath());
    byte[] bytes = marginCsv.toString().getBytes();
    Files.write(path,bytes);


    filePathStr = folder+"threshold_"+dataSet+".csv";
    filePath = new File(filePathStr);
    if(filePath.exists()){
      filePath.delete();
    }
    path = Paths.get(filePath.getAbsolutePath());

    StringBuilder thresholdCsv = getCsv(thresholdCounters);
    bytes = thresholdCsv.toString().getBytes();
    Files.write(path,bytes);

  }

  private StringBuilder getCsv(List<MarginThresholdCounter> marginThresholdCounters){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Threshold; Bike_Probability; Bus_Probability; Car_Probability; Other_Probability; Train_Probability; Walking_Probability; All_Probability;   ; Threshold; ");
    stringBuilder.append("Bike_Actual; Bus_Actual; Car_Actual; Other_Actual; Train_Actual; Walking_Actual; All_Actual;    ; Threshold; Bike_actual_percentage; Bus_actual_percentage; Car_actual_percentage; Other_actual_percentage; Train_actual_percentage; Walking_actual_percentage; All_actual_percentage "+System.lineSeparator());

    for(int i = 0; i<=100;i++){
      MarginThresholdCounter marginCounter = marginThresholdCounters.get(i);
      stringBuilder.append(i+"; " );
      stringBuilder.append(getAccuracyProbabilty(marginCounter,TransportType.BIKE)+"; " );
      stringBuilder.append(getAccuracyProbabilty(marginCounter,TransportType.BUS)+"; " );
      stringBuilder.append(getAccuracyProbabilty(marginCounter,TransportType.CAR)+"; " );
      stringBuilder.append(getAccuracyProbabilty(marginCounter,TransportType.OTHER)+"; " );
      stringBuilder.append(getAccuracyProbabilty(marginCounter,TransportType.TRAIN)+"; " );
      stringBuilder.append(getAccuracyProbabilty(marginCounter,TransportType.WALK)+"; " );
      stringBuilder.append(getAccuracyProbabilty(marginCounter,null)+"; " );
      stringBuilder.append(  "   ; " );
      stringBuilder.append(i+"; " );
      stringBuilder.append(marginCounter.getActual(TransportType.BIKE)+"; " );
      stringBuilder.append(marginCounter.getActual(TransportType.BUS)+"; " );
      stringBuilder.append(marginCounter.getActual(TransportType.CAR)+"; " );
      stringBuilder.append(marginCounter.getActual(TransportType.OTHER)+"; " );
      stringBuilder.append(marginCounter.getActual(TransportType.TRAIN)+"; " );
      stringBuilder.append(marginCounter.getActual(TransportType.WALK)+"; " );
      stringBuilder.append(marginCounter.getActual(null)+"; ");
      stringBuilder.append(  "   ; " );
      stringBuilder.append(i+"; " );
      stringBuilder.append(getActualyProbabilty(marginCounter,TransportType.BIKE)+"; " );
      stringBuilder.append(getActualyProbabilty(marginCounter,TransportType.BUS)+"; " );
      stringBuilder.append(getActualyProbabilty(marginCounter,TransportType.CAR)+"; " );
      stringBuilder.append(getActualyProbabilty(marginCounter,TransportType.OTHER)+"; " );
      stringBuilder.append(getActualyProbabilty(marginCounter,TransportType.TRAIN)+"; " );
      stringBuilder.append(getActualyProbabilty(marginCounter,TransportType.WALK)+"; " );
      stringBuilder.append(getActualyProbabilty(marginCounter,null)+" "+System.lineSeparator() );

    }
    return stringBuilder;
  }

  public static final DecimalFormat df2 = new DecimalFormat( "#.####" );
  private String getAccuracyProbabilty(MarginThresholdCounter marginThresholdCounter,TransportType transportType){
    int actual = marginThresholdCounter.getActual(transportType);
    if(actual== 0){
      return " null ";
    }
    double accuracy = marginThresholdCounter.getAccuracy(transportType);
    String accuracyStr = df2.format(accuracy);
    return accuracyStr;
  }

  private String getActualyProbabilty(MarginThresholdCounter marginThresholdCounter,TransportType transportType){
    int actual = marginThresholdCounter.getActual(transportType);
    if(actual== 0){
      return " null ";
    }
    double accuracy = marginThresholdCounter.getActualPercentageOfTotal(transportType);
    String accuracyStr = df2.format(accuracy);
    return accuracyStr;
  }


  static final AtomicInteger atomicInteger = new AtomicInteger(0);

  private void calcOverlapping(){

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

    File trainingSetFile = new File(dataFolder, Constants.trainingSet);
    File testSetFile = new File(dataFolder,Constants.testSet);

    List<TrackingIdNamePair> trainingSet = TrackingIdNamePairFileReaderHelper.load(trainingSetFile.getPath());
    List<TrackingIdNamePair> testSet = TrackingIdNamePairFileReaderHelper.load(testSetFile.getPath());

    List<String>  trackings = new ArrayList<>();
    trackings.add("1481094815203.gpsTrack");
    trackings.add("1481459553133.gpsTrack");
//    trackings.setTasks("7_bike_columbus_20110829_rc.gpx");
//    trackings.setTasks("1486217145739.gpsTrack");

    GpsTrackingDaoFileNameIterator gpsTrackingDaoFileNameIterator = new GpsTrackingDaoFileNameIterator(
        leightweightTrackingDao, trackings);

    TrackingIdNamePairIterator trackingIdNamePairIterator = new TrackingIdNamePairIterator(testSet,leightweightTrackingDao);
    ThreadSafeIterator<Tracking> trackingThreadSafeIterator = new ThreadSafeIterator<>(trackingIdNamePairIterator);
    int npools =8;
    Evaluation evaluation = new Evaluation();
    ExecutorService executorService1 = Executors.newFixedThreadPool(npools);
    for(int i = 0; i < npools;i++){
      OverlappingEvaluationRunnable evaluationRunnable = new OverlappingEvaluationRunnable(trackingThreadSafeIterator,
          evaluation, emf.createEntityManager());
      executorService1.submit(evaluationRunnable);
    }

    while(trackingThreadSafeIterator.hasNext()){

      double distanceAccuracy = evaluation.getDistanceOverlapping()/evaluation.getDistanceSum();
      double durationAccuracy = evaluation.getDurationOverlapping()/(evaluation.getDurationSum()*1.0);

      double droolsdistanceAccuracy = evaluation.getDrools_distanceOverlapping()/evaluation.getDrools_distanceSum();
      double  droolsdurationAccuracy = evaluation.getDrools_durationOverlapping()/(evaluation.getDrools_durationSum()*1.0);

      System.out.println(atomicInteger.get()+"  forest: da: "+distanceAccuracy+" ta: "+durationAccuracy +"  drools "+droolsdistanceAccuracy+"  druation:"+droolsdurationAccuracy);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("     --------------------- ");
    double distanceAccuracy = evaluation.getDistanceOverlapping()/evaluation.getDistanceSum();
    double durationAccuracy = evaluation.getDurationOverlapping()/(evaluation.getDurationSum()*1.0);

    double droolsdistanceAccuracy = evaluation.getDrools_distanceOverlapping()/evaluation.getDrools_distanceSum();
    double  droolsdurationAccuracy = evaluation.getDrools_durationOverlapping()/(evaluation.getDrools_durationSum()*1.0);
    double signalShortageDistancePercentage = evaluation.getSignalShortage_distanceSum()/evaluation.getDistanceSum();
    double signalShortageDurationPercentage = evaluation.getSignalShortage_durationSum()/(evaluation.getDurationSum()*1.0);
    System.out.println(atomicInteger.get()+"  forest: da: "+distanceAccuracy+" ta: "+durationAccuracy +"  drools "+droolsdistanceAccuracy+"  druation:"+droolsdurationAccuracy);
    System.out.println("distance percentage: "+signalShortageDistancePercentage+ "   duration percentage  "+signalShortageDurationPercentage);


    int debug = 4;





  }

  public static class OverlappingEvaluationRunnable implements Runnable{

    Iterator<Tracking> trackingIterator;
    Evaluation evaluation;
    DroolsClassificationService droolsClassificationService = new DroolsClassificationService();
    private ConfigService configService;

    public OverlappingEvaluationRunnable(
        Iterator<Tracking> trackingIterator,
        Evaluation evaluation, EntityManager em) {
      this.trackingIterator = trackingIterator;
      this.evaluation = evaluation;
      this.em = em;
    }

    ClassifierNew classifierNew = new ClassifierNew();
    private EntityManager em;

    private FeatureCalculationService featureCalculationService = new FeatureCalculationService();

    @Override
    public void run() {

      try {
        classifierNew.init(ClassifierParameters.DEFAULT());
      } catch (ClassifierException e) {
        e.printStackTrace();
      }
      EntityTransaction entityTransaction = em.getTransaction();
      ITransaction transaction = new EntityManagerTransaction(entityTransaction);
      GisFeatureCalculator gisFeatureCalculator = new GisFeatureCalculator();
      gisFeatureCalculator.init(em,transaction);
      featureCalculationService = new FeatureCalculationService();
      featureCalculationService.addFeatureCalculator(new SpeedCalculatorService());
      featureCalculationService.addFeatureCalculator(new LocationFeatureCalculator());
      featureCalculationService.addFeatureCalculator(gisFeatureCalculator);
      SegmentationHelper segmentationHelper = new SegmentationHelper();


      while (trackingIterator.hasNext()) {
try{


        Tracking tracking = trackingIterator.next();

        SameSequelTransportModeMergeFilter sameSequelTransportModeMergeFilter = new SameSequelTransportModeMergeFilter();
        Tracking tracking1 = sameSequelTransportModeMergeFilter.filter(tracking);

        CoordinateInterpolator coordinateInterpolator = Helper.filterAndCreateCoordinateInterpolator(tracking1,configService);

        List<SegmentationResultInfo> segmentationResultInfos = segmentationHelper.calcSegmentsAndFeatureResults1(
            tracking1,coordinateInterpolator);



        atomicInteger.addAndGet(segmentationResultInfos.size());

        List<Section> sectionList1 = new ArrayList<>();
        List<TrackingSegment> segments = tracking.getLatestTrackingSegmentBag().getSegments();
        for (TrackingSegment segment : segments) {

          List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
              .getInterpolatedCoordinatesExact(segment.getStartTime(), segment.getEndTime(),
                  Duration.ofSeconds(1));
          sectionList1.add(new Section(segment.getTransportType().name(),segment.getStartTime(),segment.getEndTime(),interpolatedCoordinatesExact));
        }

        List<Section> sectionList2 = new ArrayList<>();

  List<Section> sectionList3 = new ArrayList<>();


  Duration signalShortageDurationSum = Duration.ofSeconds(0);
  Distance signalShortageDistanceSum= new Distance(0);

  for (SegmentationResultInfo segmentationResultInfo : segmentationResultInfos) {

          if(segmentationResultInfo.getPreType() == SegmentPreType.movingSignalShortage || segmentationResultInfo.getPreType() == SegmentPreType.stationarySignalShortage || segmentationResultInfo.getPreType() == SegmentPreType.NotClassifiedYet){

            LocalDateTime startTime = segmentationResultInfo.getStartTime();
            LocalDateTime endTime = segmentationResultInfo.getEndTime();
            Duration between = Duration.between(startTime,endTime);
            signalShortageDurationSum = signalShortageDurationSum.plus(between);

            List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
                .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

            IGpsPoint lastPoint = null;
            for (IGpsPoint iGpsPoint : interpolatedCoordinatesExact) {
              if(lastPoint != null){
                Distance distance1 = CoordinateUtil.haversineDistance(lastPoint, iGpsPoint);
                signalShortageDistanceSum = signalShortageDistanceSum.plus(distance1);

              }
              lastPoint    = iGpsPoint;
            }


          }else {

            List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
                .getInterpolatedCoordinatesExact(segmentationResultInfo.getStartTime(),
                    segmentationResultInfo.getEndTime(), Duration.ofSeconds(1));

            List<FeatureResult> results = featureCalculationService
                .calcFeatures(interpolatedCoordinatesExact, coordinateInterpolator);

            ClassificationResult classify = classifierNew
                .classify(results);

            TransportType transportType = classify.getMostLikeliestResult();
            String transportTypeStr = transportType.name();

            LocalDateTime startTime = segmentationResultInfo.getStartTime();
            LocalDateTime endTime = segmentationResultInfo.getEndTime();
            List<? extends IGpsPoint> gpsPoints = segmentationResultInfo.getGpsPoints();
            SectionWithProbability sectionWithProbability = new SectionWithProbability(
                transportTypeStr,
                startTime, endTime, gpsPoints);

            sectionWithProbability.setProbability(classify.getLikelyHoodFor(transportType));
            sectionList2.add(sectionWithProbability);

            // drools

            {
              ClassificationResult classify1 = droolsClassificationService
                  .classify(results);
              TransportType transportType1 = classify1.getMostLikeliestResult();
              String transportTypeStr1 = transportType1.name();

              LocalDateTime startTime1 = segmentationResultInfo.getStartTime();
              LocalDateTime endTime1 = segmentationResultInfo.getEndTime();
              List<? extends IGpsPoint> gpsPoints1 = segmentationResultInfo.getGpsPoints();

              SectionWithProbability sectionWithProbability1 = new SectionWithProbability(
                  transportTypeStr1,
                  startTime1, endTime1, gpsPoints1);

              sectionWithProbability.setProbability(classify1.getLikelyHoodFor(transportType1));

              sectionList3.add(sectionWithProbability1);

            }

          }
        }




//        sectionList1=otherToWalk(sectionList1);
//        sectionList2=otherToWalk(sectionList2);


        OverlappingCalcualtor overlappingCalcualtor = new OverlappingCalcualtor();
        OverlappingResult overlappingResult = overlappingCalcualtor
            .calcOverlappings(sectionList1, sectionList2);
        double relativeDistanceOverlapping = overlappingResult.getRelativeDistanceOverlapping();
        double relativeDurationOveralapping = overlappingResult.getRelativeDurationOveralapping();
        System.out.println("overlapping:"+  relativeDistanceOverlapping + "  "+relativeDurationOveralapping+ "   fileName: "+tracking.getTrackingInfo(Constants.FILENAME));
        printSections("11111: ",sectionList1);
        printSections("22222: ",sectionList2);
        printSections("33333: ",sectionList3);

        OverlappingResult overlappingResult1 = overlappingCalcualtor
            .calcOverlappings(sectionList1, sectionList3);

        evaluation.addDistanceOverlapping(overlappingResult.getDistanceOverlapping().getKm());
        evaluation.addDistanceSum(overlappingResult.getTotalDistance().getKm());
        evaluation.addDurationOverlapping(overlappingResult.getDurationOveralapping().toMillis());
        evaluation.addDurationSum(overlappingResult.getTotalDuration().toMillis());


        evaluation.drools_addDistanceOverlapping(overlappingResult1.getDistanceOverlapping().getKm());
        evaluation.drools_addDistanceSum(overlappingResult1.getTotalDistance().getKm());
        evaluation.drools_addDurationOverlapping(overlappingResult1.getDurationOveralapping().toMillis());
        evaluation.drools_addDurationSum(overlappingResult1.getTotalDuration().toMillis());

        evaluation.signalShortage_addDurationSum(signalShortageDurationSum.toMillis());
        evaluation.signalShortage_addDistanceSum(signalShortageDistanceSum.getKm());



      }catch (Exception ex){
        ex.printStackTrace();
      }
      }



    }

    private void printSections(String sectionName ,List<Section> sectionList1) {

      StringBuilder stringBuilder = new StringBuilder();
      for (Section section : sectionList1) {
        if(section instanceof SectionWithProbability){
          SectionWithProbability sectionWithProbability = (SectionWithProbability)section;
          stringBuilder.append(" ("+sectionWithProbability.getProbability()+")"+section.getName() + "; ");

        }else {
          stringBuilder.append(section.getName() + "; ");
        }
      }
      System.out.println(sectionName+ "   " +stringBuilder.toString());
    }


  }

  public static class SectionWithProbability extends Section{

    public SectionWithProbability(String name, LocalDateTime startTime, LocalDateTime endTime,
        List<? extends IGpsPoint> gpsPointList) {
      super(name, startTime, endTime, gpsPointList);
    }

    private double probability;

    public double getProbability() {
      return probability;
    }

    public void setProbability(double probability) {
      this.probability = probability;
    }
  }


  private List<ClassificationEvaluationResult> classify( LabeledFeature[] trainingSetLabeledFeatures,Classifier classifier){
    List<ClassificationEvaluationResult> results = new ArrayList<>();
    for (LabeledFeature labeledFeature : trainingSetLabeledFeatures) {



      String trackingFileName = labeledFeature.getTrackingFileName();


      List<FeatureResult> featureResultList = labeledFeature.getFeatureResultList();
      TransportType transportType = labeledFeature.getTransportType();
      transportType =unifyWalkingAndOtherToOther(transportType);
      Tuple<Duration, Distance> distanceAndDuration = getDistanceAndDuration(labeledFeature);

      featureResultList = new ArrayList<>(featureResultList);
      ClassificationResult result = classifier.classify(featureResultList);

      TransportType mostLikeliestResult = result.getMostLikeliestResult();

      ClassificationProbabilityMargin classificationProbabilityMargin= calcProbabilityMargin(
          transportType,result);

      ClassificationEvaluationResult classificationEvaluationResult = new ClassificationEvaluationResult(
          transportType, mostLikeliestResult, distanceAndDuration.getItem2(),
          distanceAndDuration.getItem1(),classificationProbabilityMargin);

      classificationEvaluationResult.setFileName(trackingFileName);

      results.add(classificationEvaluationResult);
    }
    return results;
  }

  private TransportType unifyWalkingAndOtherToOther(TransportType transportType) {
    if(transportType== TransportType.WALK){
      return TransportType.OTHER;
    }
    return transportType;
  }

  private ClassificationProbabilityMargin calcProbabilityMargin(TransportType transportType, ClassificationResult result) {

    List<Tuple<TransportType, Double>> results = result.getResults();
    results = new ArrayList<>(results);

    Tuple<TransportType, Double> bestNotActualTransportTypeResult = null;
    Iterator<Tuple<TransportType, Double>> iterator = results.iterator();
    while (iterator.hasNext()) {
      Tuple<TransportType, Double> next = iterator.next();
      if(next.getItem1() == transportType){
        // do nothing
      }else{
        if(bestNotActualTransportTypeResult == null || bestNotActualTransportTypeResult.getItem2() < next.getItem2()){
          bestNotActualTransportTypeResult = next;
        }
      }
    }

    double bestNotActualTransportTypeProbability =0;
    if(bestNotActualTransportTypeResult !=  null){
      bestNotActualTransportTypeProbability= bestNotActualTransportTypeResult.getItem2();
    }

    double probabilityForActualClass = result.getLikelyHoodFor(transportType);
    ClassificationProbabilityMargin classificationProbabilityMargin = new ClassificationProbabilityMargin(transportType,bestNotActualTransportTypeResult.getItem1(),probabilityForActualClass,bestNotActualTransportTypeProbability);
    return classificationProbabilityMargin;
  }


  private Tuple<Duration,Distance>getDistanceAndDuration(LabeledFeature labeledFeature){
    double distanceInKm = 0;
    double durationInSeconds = 0;
    for (FeatureResult featureResult : labeledFeature.getFeatureResultList()) {
      if(featureResult.getFeatureName().equals("distance_in_km")){
        distanceInKm = featureResult.getFeatureValue();
      }
      if(featureResult.getFeatureName().equals("duration_in_seconds")){
        durationInSeconds = featureResult.getFeatureValue();
      }
    }
    Distance distance = new Distance(distanceInKm);
    Duration duration = Duration.ofSeconds((long)durationInSeconds);
    return new Tuple<>(duration,distance);
  }


  public static class SegmentationResultInfo{
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<FeatureResult> featureResults;
    private List<? extends IGpsPoint> gpsPoints;
    private SegmentPreType preType;

    public List<? extends IGpsPoint> getGpsPoints() {
      return gpsPoints;
    }

    public void setGpsPoints(List<? extends IGpsPoint> gpsPoints) {
      this.gpsPoints = gpsPoints;
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

    public List<FeatureResult> getFeatureResults() {
      return featureResults;
    }

    public void setFeatureResults(
        List<FeatureResult> featureResults) {
      this.featureResults = featureResults;
    }

    public void setPreType(SegmentPreType preType) {
      this.preType = preType;
    }

    public SegmentPreType getPreType() {
      return preType;
    }
  }


  public void writeStatisticData(List<ClassificationEvaluationResult> results ) throws IOException {

    int correcltyClassifiedInstances = 0;
    int incorrecltyClassifiedInstances = 0;

    Distance correcDistance= new Distance(0);
    Duration correctDuration = Duration.ofSeconds(0);
    Distance distanceSum= new Distance(0);
    Duration durationSum = Duration.ofSeconds(0);
    for (ClassificationEvaluationResult result : results) {
      distanceSum=distanceSum.plus(result.getDistance());
      durationSum=durationSum.plus(result.getDuration());
      if(result.getClassifiedAs()== result.getActual()){
        correcltyClassifiedInstances++;
        correcDistance = correcDistance.plus(result.getDistance());
        correctDuration = correctDuration.plus(result.getDuration());
      }else{
        incorrecltyClassifiedInstances++;
      }
    }

    double durationAccuracy = correctDuration.toMillis()/(durationSum.toMillis()*1.0);
    double distanceAccuracy = correcDistance.getKm()/(distanceSum.getKm());

    double accuracy= correcltyClassifiedInstances / (correcltyClassifiedInstances+incorrecltyClassifiedInstances*1.0);

    ConfusionMatrix confusionMatrix = new ConfusionMatrix();
    TransportType[] values = TransportType.values();
    List<TransportType> transportTypes = new ArrayList<>();
    for (TransportType value : values) {
      if(value!= TransportType.WALK){
        transportTypes.add(value);
      }
    }
    confusionMatrix.sortAndInit(transportTypes);
    String confusionMatrixStr = confusionMatrix.calculate(results);

    StatisValueCalculator statisValues = new StatisValueCalculator();
    String statisMeasurementValues = statisValues.calculate(results);

    outputHelper.writeLine(System.lineSeparator()+System.lineSeparator()+"Correctly Classified Instances: "+ correcltyClassifiedInstances);
    outputHelper.writeLine("Correctly Classified Instance Percentage: "+ accuracy);
    outputHelper.writeLine("Incorrectly Classified Instances: "+ incorrecltyClassifiedInstances);
    outputHelper.writeLine("Incorrectly Classified Instance Percentage: "+ (1.0-accuracy));
    outputHelper.writeLine("Incorrectly Classified Instance Percentage: "+ (1.0-accuracy));
    outputHelper.writeLine("");
    outputHelper.writeLine("Correctly Classified Distance "+ distanceAccuracy);
    outputHelper.writeLine("Correctly Classified Duration "+ durationAccuracy);
    outputHelper.writeLine(System.lineSeparator()+"Confusion Matrix"+System.lineSeparator());
    outputHelper.writeLine(confusionMatrixStr);
    outputHelper.writeLine(System.lineSeparator()+"Detailed Accuracy By Class"+System.lineSeparator());
    outputHelper.writeLine(statisMeasurementValues);

  }



}
