package experiments;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.DroolsClassificationService;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.postProcessing.PostprocessingService;
import at.fhv.tmd.postProcessing.tasks.MergeSameSequentielSegments;
import at.fhv.tmd.postProcessing.tasks.MovingSignalShortage;
import at.fhv.tmd.postProcessing.tasks.OtherBetweenSameTransportModes;
import at.fhv.tmd.postProcessing.tasks.OtherEvaluation;
import at.fhv.tmd.postProcessing.tasks.RemoveNotClassifiedYetSegments;
import at.fhv.tmd.postProcessing.tasks.ThreeVehiclesInARow;
import at.fhv.tmd.postProcessing.tasks.TooShortVehicles;
import at.fhv.tmd.postProcessing.tasks.TrainStationMovingSignalShortage;
import at.fhv.tmd.processFlow.EarlyReturnCondition;
import at.fhv.tmd.processFlow.TmdResult;
import at.fhv.tmd.processFlow.TmdSegment;
import at.fhv.tmd.processFlow.TmdService;
import at.fhv.tmd.processFlow.TrackingContextProvider;
import at.fhv.tmd.processFlow.TransportTypeProbability;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.ClassifierException;
import at.fhv.tmd.segmentClassification.classifier.ClassifierNew;
import at.fhv.tmd.segmentClassification.classifier.ClassifierParameters;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceImp;
import at.fhv.transportClassifier.common.transaction.NoTransaction;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportClassifier.mainserver.impl.ConfigSettingDaoImp;
import at.fhv.transportClassifier.mainserver.impl.GisFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.LocationFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.RailwayClosestDistanceCalculatorImp;
import at.fhv.transportClassifier.mainserver.impl.SpeedCalculatorService;
import at.fhv.transportClassifier.mainserver.transaction.EntityManagerTransaction;
import at.fhv.transportClassifier.segmentsplitting.SegmentationServiceImp;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import experiments.classificationEvaluation.ConfusionMatrix;
import experiments.postprocessEvaluation.PPClassificationEvaluationResult;
import experiments.postprocessEvaluation.Stopwatch;
import experiments.postprocessEvaluation.TimeMeasuring;
import experiments.postprocessEvaluation.TransportTypeGroundTruthFinder;
import helper.OverlappingCalcualtor;
import helper.OverlappingResult;
import helper.PropertyHelper;
import helper.Section;
import helper.StringSavingHelper;
import helper.TmdServiceCache;
import helper.TrackingContextProviderSimple;
import helper.TrackingIdNamePairIterator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
/**
 * Created by Johannes on 22.07.2017.
 */
public class TmdServiceExperiment {

 private EntityManagerFactory emf;
  private TmdService ramdomForestTmdService;
  private TmdService droolsTmdService;

  private TmdServiceCache afterFeatureCalculationCache = new TmdServiceCache("calculatedFeatures/");
  private TmdServiceCache afterClassificationCacheRf = new TmdServiceCache("classificationRF/");
  private TmdServiceCache afterClassificationCacheDrools = new TmdServiceCache("classificationDrools/");
  private TmdServiceCache afterPostProcessingCacheRf = new TmdServiceCache("postProcessingRf/");
  private TmdServiceCache afterPostProcessingCacheDrools = new TmdServiceCache("postProcessingDrools/");
  DecimalFormat decimalFormat;
  private ConfigServiceImp configService;

  public void doIt(EntityManagerFactory emf, LeightweightTrackingDao leightweightTrackingDao)
      throws IOException, ClassNotFoundException {

    this.emf = emf;

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);
    File trainingSetFile = new File(dataFolder, Constants.trainingSet);
    File testSetFile = new File(dataFolder,Constants.testSet);
    List<TrackingIdNamePair> trainingSet = TrackingIdNamePairFileReaderHelper.load(trainingSetFile.getPath());
    List<TrackingIdNamePair> testSet = TrackingIdNamePairFileReaderHelper.load(testSetFile.getPath());

    TrackingIdNamePairIterator testSetIterator = new TrackingIdNamePairIterator(testSet,leightweightTrackingDao);
    TrackingIdNamePairIterator trainSetIterator = new TrackingIdNamePairIterator(trainingSet,leightweightTrackingDao);

    try {
      initTmdService();
    } catch (ClassifierException e) {
      e.printStackTrace();
    }

    afterFeatureCalculationCache.setEnabled(true);
    afterClassificationCacheRf.setEnabled(true);
    afterClassificationCacheDrools.setEnabled(true);
    afterPostProcessingCacheRf.setEnabled(true);
    afterPostProcessingCacheDrools.setEnabled(true);

    calc(testSetIterator,"testSet");
    calc(trainSetIterator,"trainSet");


    int debg =3;
  }

  private  void initTmdService() throws ClassifierException {
    EntityManager em = emf.createEntityManager();


    ConfigSettingDaoImp configSettingDaoImp = new ConfigSettingDaoImp();
    configSettingDaoImp.init(em,new NoTransaction() );
    configService= new ConfigServiceImp(configSettingDaoImp);
    configService.init();

    EntityManagerTransaction transaction = new EntityManagerTransaction(em.getTransaction());
    FeatureCalculationService featureCalculationService = new FeatureCalculationService();
    GisFeatureCalculator gisFeatureCalculator = new GisFeatureCalculator();
    gisFeatureCalculator.init(em,transaction);
    featureCalculationService = new FeatureCalculationService();
    featureCalculationService.addFeatureCalculator(new SpeedCalculatorService());
    featureCalculationService.addFeatureCalculator(new LocationFeatureCalculator());
    featureCalculationService.addFeatureCalculator(gisFeatureCalculator);


    ClassifierNew classifier1  = new ClassifierNew();
    classifier1.init(ClassifierParameters.WITHOUT_SEGMENTATION());
    DroolsClassificationService droolsClassificationService = new DroolsClassificationService();
    droolsClassificationService.init();
    RailwayClosestDistanceCalculatorImp gisPointClosesnessCalculator = new RailwayClosestDistanceCalculatorImp();
    gisPointClosesnessCalculator.updateEntityManager(em);
    gisFeatureCalculator.init(em,new NoTransaction());
    PostprocessingService postprocessingService = new PostprocessingService();
    List<PostprocessTask> postprocessTasks = new ArrayList<>();
    postprocessTasks.add(new OtherEvaluation());
    postprocessTasks.add(new RemoveNotClassifiedYetSegments());
    postprocessTasks.add(new TrainStationMovingSignalShortage(gisPointClosesnessCalculator));
    postprocessTasks.add(new MovingSignalShortage(classifier1,featureCalculationService));
    postprocessTasks.add(new MergeSameSequentielSegments(classifier1,featureCalculationService));
    postprocessTasks.add(new OtherBetweenSameTransportModes(classifier1,featureCalculationService));
    postprocessTasks.add(new ThreeVehiclesInARow(classifier1,featureCalculationService));
    postprocessTasks.add(new TooShortVehicles(classifier1,featureCalculationService));
    postprocessingService.setTasks(postprocessTasks);

    SegmentationServiceImp segmentationServiceImp = new SegmentationServiceImp();
    segmentationServiceImp.updateConfigService(configService);
    ramdomForestTmdService = new TmdService(segmentationServiceImp,featureCalculationService,classifier1,postprocessingService);
    ramdomForestTmdService.init();
    ramdomForestTmdService.updateConfigService(configService);

    PostprocessingService droolspostprocessingService = new PostprocessingService();
    List<PostprocessTask> droolspostprocessTasks = new ArrayList<>();
    droolspostprocessTasks.add(new OtherEvaluation());
    droolspostprocessTasks.add(new RemoveNotClassifiedYetSegments());
    droolspostprocessTasks.add(new TrainStationMovingSignalShortage(gisPointClosesnessCalculator));
    droolspostprocessTasks.add(new MovingSignalShortage(droolsClassificationService,featureCalculationService));
    droolspostprocessTasks.add(new MergeSameSequentielSegments(droolsClassificationService,featureCalculationService));
    droolspostprocessTasks.add(new OtherBetweenSameTransportModes(droolsClassificationService,featureCalculationService));
    droolspostprocessTasks.add(new ThreeVehiclesInARow(droolsClassificationService,featureCalculationService));
    droolspostprocessTasks.add(new TooShortVehicles(droolsClassificationService,featureCalculationService));
    droolspostprocessingService.setTasks(droolspostprocessTasks);

    droolsTmdService = new TmdService(segmentationServiceImp,featureCalculationService,droolsClassificationService,droolspostprocessingService);
    droolsTmdService.init();
    droolsTmdService.updateConfigService(configService);

    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
    otherSymbols.setDecimalSeparator(',');
    otherSymbols.setGroupingSeparator(' ');
    decimalFormat = new DecimalFormat("#.####", otherSymbols);

    afterFeatureCalculationCache.initCache();
        afterClassificationCacheRf.initCache();
    afterClassificationCacheDrools.initCache();
        afterPostProcessingCacheRf.initCache();
    afterPostProcessingCacheDrools.initCache();

  }

  private void calc(Iterator<Tracking> trackingIdNamePairIterator,String fileNameSuffix)
      throws IOException {
    OverlappingCalcualtor overlappingCalcualtor = new OverlappingCalcualtor();

    List<TimeMeasuring> rfTimeMeasurings = new ArrayList<>();
    List<TimeMeasuring> droolsTimeMeasurings = new ArrayList<>();

    allSum_rf =0;
    allSum_drools =0;
    correctSum_drools =0;
    correctSum_rf =0;

    List<Tuple<Integer,Duration>> pointNumberDuration = new ArrayList<>();
    List<Tuple<Double,Double>> durationDistanceAccuracy= new ArrayList<>();
    List<PPClassificationEvaluationResult> rfClassificationEvaluationResults = new ArrayList<>();
    List<PPClassificationEvaluationResult> droolsClassificationEvaluationResults = new ArrayList<>();

    boolean firstRound = true;

    List<OverlappingResults> overlappingResultsList = new ArrayList<>();
    while (trackingIdNamePairIterator.hasNext()) {
      try {

        Tracking tracking = trackingIdNamePairIterator.next();
        int numberOfGpsPoints = tracking.getGpsPoints().size();

        if (index >= 0) {

          if(firstRound){

            TrackingContext temp = calculateAfterFeature(tracking);
            firstRound = false;
          }

          Stopwatch rfStopwatch = new Stopwatch();
          Stopwatch droolsStopwatch = new Stopwatch();

          // features

          rfStopwatch.start();
          TrackingContext featureTrackingContext0 = calculateAfterFeature(tracking);
          rfStopwatch.stop();

          droolsStopwatch.start();
          TrackingContext featureTrackingContext1 = calculateAfterFeature(tracking);
          droolsStopwatch.stop();


          // classification

          rfStopwatch.start();
          TrackingContext classificationTrackingContextRf = calculateClassification(tracking,featureTrackingContext0,afterClassificationCacheRf,
              ramdomForestTmdService);
          rfStopwatch.stop();

          CoordinateInterpolator coordinateInterpolator = classificationTrackingContextRf.getData(TrackingContext.COORDINATE_INTERPOLATOR);
          List<Section> classificationSectionsRf = calcClassificationSections(classificationTrackingContextRf,coordinateInterpolator);

          droolsStopwatch.start();
          TrackingContext classificationTrackingContextDrools = calculateClassification(tracking,featureTrackingContext1,afterClassificationCacheDrools,droolsTmdService);
          droolsStopwatch.stop();

          List<Section> classificationSectionsDrools = calcClassificationSections(classificationTrackingContextDrools,coordinateInterpolator);

          long rfMillis = rfStopwatch.millis();
          long droolsMillis = droolsStopwatch.millis();

          // post process
          rfStopwatch.start();
          TmdResult tmdResultRf = calculatePostProcess(tracking,classificationTrackingContextRf,afterPostProcessingCacheRf,ramdomForestTmdService);
          rfStopwatch.stop();

          List<Section> tmdResultSectionsRf = calcTmdResultSections(tmdResultRf,classificationTrackingContextRf);

          droolsStopwatch.start();
          TmdResult tmdResultDrools = calculatePostProcess(tracking,classificationTrackingContextDrools,afterPostProcessingCacheDrools,droolsTmdService);
          droolsStopwatch.stop();

          List<Section> tmdResultSectionsDrools = calcTmdResultSections(tmdResultDrools,classificationTrackingContextDrools);


          long rfMillisAfterPP = rfStopwatch.millis();
          long droolsMillisAfterPP = droolsStopwatch.millis();

          TimeMeasuring rfTimeMeasuring = new TimeMeasuring(tracking.getId(),rfMillis,rfMillisAfterPP,numberOfGpsPoints);
          TimeMeasuring droolsTimeMeasuring = new TimeMeasuring(tracking.getId(),droolsMillis,droolsMillisAfterPP,numberOfGpsPoints);

          rfTimeMeasurings.add(rfTimeMeasuring);
          droolsTimeMeasurings.add(droolsTimeMeasuring);

          List<Section> actualSections = calcActualSections(tracking,coordinateInterpolator);

          List<PPClassificationEvaluationResult> classificationEvaluationResults_rf = calcStatisticData(
              tmdResultRf, tracking, coordinateInterpolator);



          rfClassificationEvaluationResults.addAll(classificationEvaluationResults_rf);
          List<PPClassificationEvaluationResult> classificationEvaluationResults_drools = calcStatisticData(
              tmdResultDrools, tracking, coordinateInterpolator);
          droolsClassificationEvaluationResults.addAll(classificationEvaluationResults_drools);



          OverlappingResult overlappingRandomForest = overlappingCalcualtor
              .calcOverlappings(actualSections, classificationSectionsRf);

          OverlappingResult overlappingDrools = overlappingCalcualtor
              .calcOverlappings(actualSections, classificationSectionsDrools);

          OverlappingResult overlappingRandomForestPostProcess = overlappingCalcualtor
              .calcOverlappings(actualSections, tmdResultSectionsRf);

          OverlappingResult overlappingDroolsPostProcess = overlappingCalcualtor
              .calcOverlappings(actualSections, tmdResultSectionsDrools);

          printSTuff(classificationEvaluationResults_rf,    tmdResultRf    ,overlappingRandomForestPostProcess,tracking,classificationSectionsRf,
              true);
          printSTuff(classificationEvaluationResults_drools,tmdResultDrools,overlappingDroolsPostProcess      ,tracking,classificationSectionsDrools,
              false);



          OverlappingResults overlappingResults = new OverlappingResults(overlappingRandomForest,overlappingDrools,overlappingRandomForestPostProcess,overlappingDroolsPostProcess,tracking.getId());
          overlappingResultsList.add(overlappingResults);

        }

        index++;
        System.out.println(index);

      }catch (Exception ex){
        ex.printStackTrace();
      }
    }

    saveTimeMeasurings(fileNameSuffix,rfTimeMeasurings,droolsTimeMeasurings);

    ConfusionMatrix confusionMatrix = new ConfusionMatrix();
    TransportType[] values = TransportType.values();
    List<TransportType> transportTypes = new ArrayList<>();
    for (TransportType value : values) {
      if(value!= TransportType.WALK){
        transportTypes.add(value);
      }
    }
    confusionMatrix.sortAndInit(transportTypes);
    String confusionMatrixDrools = confusionMatrix.calculate(droolsClassificationEvaluationResults);
    String confusionMatrixRF = confusionMatrix.calculate(rfClassificationEvaluationResults);
    StatisValueCalculator statisValues = new StatisValueCalculator();
    String statisticValuesDrools = statisValues.calculate(droolsClassificationEvaluationResults);
    String statisticValuesRF = statisValues.calculate(rfClassificationEvaluationResults);


    droolsClassificationEvaluationResults.removeIf(classificationEvaluationResult -> !classificationEvaluationResult.hasUnambigiousActualTransportType());
    rfClassificationEvaluationResults.removeIf(classificationEvaluationResult -> !classificationEvaluationResult.hasUnambigiousActualTransportType());
    String confusionMatrixStr_onlyUnambigious = confusionMatrix.calculate(droolsClassificationEvaluationResults);
    String confusionMatrixStr1_onlyUnambigious = confusionMatrix.calculate(rfClassificationEvaluationResults);
    String statisMeasurementValues_onlyUnambigious = statisValues.calculate(droolsClassificationEvaluationResults);
    String statisMeasurementValues1_onlyUnambigious = statisValues.calculate(rfClassificationEvaluationResults);

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Confusion Matrix with Random Forest"+System.lineSeparator()+System.lineSeparator());
    stringBuilder.append(getBasicResult(rfClassificationEvaluationResults));
    stringBuilder.append(confusionMatrixRF+System.lineSeparator());
    stringBuilder.append(statisticValuesRF);
    stringBuilder.append(System.lineSeparator());
    stringBuilder.append("Confusion Matrix with Drools"+System.lineSeparator()+System.lineSeparator());
    stringBuilder.append(getBasicResult(droolsClassificationEvaluationResults));
    stringBuilder.append(confusionMatrixDrools+System.lineSeparator());
    stringBuilder.append(statisticValuesDrools);
    stringBuilder.append(System.lineSeparator());

    String path = PropertyHelper.getValue(Constants.dataFolder)+"TmdService_"+fileNameSuffix+".txt";
    StringSavingHelper.save(path,stringBuilder.toString().getBytes());

    stringBuilder = new StringBuilder();
    stringBuilder.append("Tracking_ID; Random_Forest_Overlapping_dist; Drools_Overlapping_dist; Random_Forest_Post_Processing_Overlapping_dist; Drools_Post_Processing_Overlapping_dist; "+"Random_Forest_Overlapping_dur; Drools_Overlapping_dur; Random_Forest_Post_Processing_Overlapping_dur; Drools_Post_Processing_Overlapping_dur"+"; total_dist_rF; totalMillis; total_dist_rf_pp "+System.lineSeparator());
    for (OverlappingResults overlappingResults : overlappingResultsList) {

      stringBuilder.append(overlappingResults.getId()+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestOverlappingResult().getRelativeDistanceOverlapping())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getDroolsOverlappingResult().getRelativeDistanceOverlapping())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestPostProcessOverlappingResult().getRelativeDistanceOverlapping())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getDroolsPostProcessOverlappingResult().getRelativeDistanceOverlapping())+"; ");

      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestOverlappingResult().getRelativeDurationOveralapping())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getDroolsOverlappingResult().getRelativeDurationOveralapping())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestPostProcessOverlappingResult().getRelativeDurationOveralapping())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getDroolsPostProcessOverlappingResult().getRelativeDurationOveralapping())+"; ");

      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestOverlappingResult().getTotalDistance().getKm())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestOverlappingResult().getTotalDuration().toMillis())+"; ");
      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestPostProcessOverlappingResult().getTotalDistance().getKm())+" "+System.lineSeparator());
      stringBuilder.append(decimalFormat.format(overlappingResults.getRandomForestPostProcessOverlappingResult().getTotalDuration().toMillis())+" "+System.lineSeparator());

    }
     path = PropertyHelper.getValue(Constants.dataFolder)+"DistanceDurationOverlappings"+fileNameSuffix+".csv";
    StringSavingHelper.save(path,stringBuilder.toString().getBytes());

  }

  private void saveTimeMeasurings(String fileNameSuffix, List<TimeMeasuring> rfTimeMeasurings,List<TimeMeasuring> droolsTimeMeasurings)
      throws IOException {


    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Random Forest; Id; durationBeforePP_millis; durationAFterPP_millis; numberOfGpsPoints; ; Drools; Id; durationBeforePP_millis; durationAFterPP_millis; numberOfGpsPoints"+System.lineSeparator());



    for(int i = 0; i < rfTimeMeasurings.size();i++){
      TimeMeasuring rfTimeMeasuring = rfTimeMeasurings.get(i);
      TimeMeasuring droolsTimeMeasuring = droolsTimeMeasurings.get(i);

      stringBuilder.append(" ; ");
      stringBuilder.append(rfTimeMeasuring.getId()+"; ");
      stringBuilder.append(rfTimeMeasuring.getDurionBeforePostProcessing()+"; ");
      stringBuilder.append(rfTimeMeasuring.getDurationAfterPostProcessing()+"; ");
      stringBuilder.append(rfTimeMeasuring.getNumberOfPoints()+";");
      stringBuilder.append(" ; ");
      stringBuilder.append(" ; ");
      stringBuilder.append(droolsTimeMeasuring.getId()+"; ");
      stringBuilder.append(droolsTimeMeasuring.getDurionBeforePostProcessing()+"; ");
      stringBuilder.append(droolsTimeMeasuring.getDurationAfterPostProcessing()+"; ");
      stringBuilder.append(droolsTimeMeasuring.getNumberOfPoints()+System.lineSeparator());

    }

    String path = PropertyHelper.getValue(Constants.dataFolder)+"TmdService_neededTime_"+fileNameSuffix+".csv";
    StringSavingHelper.save(path,stringBuilder.toString().getBytes());


  }

  static int allSum_rf;
  static int correctSum_rf;

  static int allSum_drools;
  static int correctSum_drools;

  private String getBasicResult(List<PPClassificationEvaluationResult> results){

    int correct = 0;
    int all= 0;
    for (PPClassificationEvaluationResult result : results) {
      TransportType actual = result.getActual();
      TransportType classifiedAs = result.getClassifiedAs();
      if(actual == classifiedAs){
        correct++;
      }
      all++;
    }

    double accuracy = correct/(all*1.0);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("correct:"+correct +System.lineSeparator());
    stringBuilder.append("all:"+all +System.lineSeparator());
    stringBuilder.append("accuracy:"+accuracy +System.lineSeparator());
    return stringBuilder.toString();
  }

  private void printSTuff(List<PPClassificationEvaluationResult> classificationEvaluationResults,
      TmdResult tmdResultRf, OverlappingResult overlappingResult,
      Tracking tracking, List<Section> classificationSections, boolean isRandomForest) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(overlappingResult.getRelativeDistanceOverlapping()+System.lineSeparator());
    stringBuilder.append("Actual:  ");
    for (TrackingSegment trackingSegment : tracking.getLatestTrackingSegmentBag().getSegments()) {
      stringBuilder.append(trackingSegment.getTransportType().name()+" | ");

    }
    stringBuilder.append(System.lineSeparator());

    stringBuilder.append("Actual guess: ");
    for (PPClassificationEvaluationResult classificationEvaluationResult : classificationEvaluationResults) {

      String am = classificationEvaluationResult.hasUnambigiousActualTransportType()?"+":"";
      stringBuilder.append(classificationEvaluationResult.getActual().name()+" " +am+"|  " );
    }
    stringBuilder.append(System.lineSeparator());
    stringBuilder.append("prediction : ");
    for (TmdSegment tmdSegment : tmdResultRf) {
      stringBuilder.append(tmdSegment.getMostLikeliest().name()+"  |  ");
    }
    stringBuilder.append(System.lineSeparator()+"Classification Sections without PP: ");
    for (Section section : classificationSections) {
      stringBuilder.append(section.getName()+"  |  ");
    }
    stringBuilder.append(System.lineSeparator());


    int correct=0;
    int all =0;
    for (int i = 0; i < classificationEvaluationResults.size();i++) {
      PPClassificationEvaluationResult ppClassificationEvaluationResult = classificationEvaluationResults
          .get(i);
      TmdSegment tmdSegment = tmdResultRf.get(i);
      if (ppClassificationEvaluationResult.getActual() == tmdSegment.getMostLikeliest()) {
        correct++;
      }
      all++;
    }
    double ratioThisRound = correct/(1.0*all);
    double ratio ;
    double correctSum;
    double allSum ;
    if(isRandomForest){
      allSum_rf +=all;
      correctSum_rf+=correct;
      correctSum = correctSum_rf;
      allSum = allSum_rf;
      ratio= correctSum_rf/(1.0*allSum_rf);
    }else{
      allSum_drools +=all;
      correctSum_drools+=correct;
      correctSum = correctSum_drools;
      allSum = allSum_drools;
      ratio= correctSum_drools/(1.0*allSum_drools);

    }

    stringBuilder.append("correct: "+correct+" all: "+all+" | ratio: "+ ratioThisRound+" | correct: "+correctSum+ " all: "+allSum+"  |  accuracy: "+ratio);

    stringBuilder.append(System.lineSeparator());

    System.out.println(stringBuilder.toString());;


  }

  TransportTypeGroundTruthFinder transportTypeGroundTruthFinder = new TransportTypeGroundTruthFinder();

  private List<PPClassificationEvaluationResult> calcStatisticData(TmdResult tmdResult, Tracking next,
      CoordinateInterpolator coordinateInterpolator) {

    List<PPClassificationEvaluationResult> classificationEvaluationResults = new ArrayList<>();

      for (TmdSegment tmdSegment : tmdResult) {

        TransportType actualTransportType = transportTypeGroundTruthFinder
            .getTransportType(tmdSegment, next, coordinateInterpolator);

        if(actualTransportType == TransportType.WALK){
          actualTransportType = TransportType.OTHER;
        }

        TransportTypeProbability bestNotActualTransportType = getBestNotActualTransportType(
            actualTransportType, tmdSegment.getTransportTypeProbabilities());

        double bestnotActualTransportTypeProability = 0;
        if(bestNotActualTransportType != null){
          bestnotActualTransportTypeProability = bestNotActualTransportType.getProbability();
        }

        PPClassificationEvaluationResult classificationEvaluationResult = new PPClassificationEvaluationResult(actualTransportType,tmdSegment.getMostLikeliest(),null,null,null);
        classificationEvaluationResults.add(classificationEvaluationResult);
      }
    return classificationEvaluationResults;


  }







  private TransportTypeProbability getBestNotActualTransportType(TransportType actualTransportType,List<TransportTypeProbability> transportTypeProbabilities){

    transportTypeProbabilities = new ArrayList<>(transportTypeProbabilities);
    transportTypeProbabilities.sort((o1, o2) -> -Double.compare(o1.getProbability(),o2.getProbability()));
    if(transportTypeProbabilities.get(0).getTransportType() == actualTransportType){
      if(transportTypeProbabilities.size()>1){
        return transportTypeProbabilities.get(1);
      }else{
        return null;
      }
    }else{
      return transportTypeProbabilities.get(0);
    }

  }




  private List<Section> calcActualSections(Tracking next,
      CoordinateInterpolator coordinateInterpolator) {

    List<Section> sectionList1 = new ArrayList<>();
    for (TrackingSegment trackingSegment : next.getLatestTrackingSegmentBag().getSegments()) {
      LocalDateTime startTme = trackingSegment.getStartTime();
      LocalDateTime endTime = trackingSegment.getEndTime();
      List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
          .getInterpolatedCoordinatesExact(startTme, endTime,
              Duration.ofSeconds(1));
      TransportType transportType = trackingSegment.getTransportType();

      if(transportType == TransportType.WALK){
        transportType= TransportType.OTHER;
      }

      Section section = new Section(transportType.name(), startTme,
          endTime, interpolatedCoordinatesExact);
      sectionList1.add(section);
    }
    return sectionList1;
  }

  private List<Section> calcTmdResultSections(TmdResult tmdResult,TrackingContext context) {

    CoordinateInterpolator coordinateInterpolator = context.getData(TrackingContext.COORDINATE_INTERPOLATOR);
    List<Section> sectionList = new ArrayList<>();

    for (TmdSegment tmdSegment : tmdResult) {
      TransportType mostLikeliest = tmdSegment.getMostLikeliest();

      LocalDateTime startTime = tmdSegment.getStartTime();
      LocalDateTime endTime = tmdSegment.getEndTime();

      List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
          .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

      Section section = new Section(mostLikeliest.name(), startTime,endTime,interpolatedCoordinatesExact);
      sectionList.add(section);
    }

    return sectionList;

  }

  private List<Section> calcClassificationSections(TrackingContext classificationTrackingContext,
      CoordinateInterpolator coordinateInterpolator) {

    List<Section> sectionList = new ArrayList<>();

    List<SegmentContext> segmentContextList = classificationTrackingContext.getSegmentContextList();
    for (SegmentContext segmentContext : segmentContextList) {
      if (segmentContext.hasData(SegmentContext.CLASSIFICATION_RESULT)) {
        ClassificationResult classificationResult = segmentContext.getData(SegmentContext.CLASSIFICATION_RESULT);
        TransportType transportType = classificationResult.getMostLikeliestResult();
        double transportTypeProbability = classificationResult.getLikelyHoodFor(transportType);

        LocalDateTime startTime = segmentContext.getStartTime();
        LocalDateTime endTime = segmentContext.getEndTime();

        List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
            .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

        Section section = new Section(transportType.name(), startTime, endTime,interpolatedCoordinatesExact);
        sectionList.add(section);
      }
    }
    return sectionList;
  }

  private TmdResult calculatePostProcess(Tracking tracking,
      TrackingContext classificationTrackingContext, TmdServiceCache cache,TmdService tmdService) throws IOException, ClassNotFoundException {

    TrackingContext trackingContext = cache.getFromCache(tracking.getId());
    if(trackingContext == null){

      TrackingContextProvider trackingContextProvider = new TrackingContextProviderSimple(classificationTrackingContext);
      EarlyReturnConditionImp earlyReturnConditionImp = new EarlyReturnConditionImp(
          TmdService.POST_PROCESSING);
      TmdResult tmdResult = tmdService
          .process(trackingContextProvider,earlyReturnConditionImp);
      trackingContext = earlyReturnConditionImp.getTrackingContext();
      cache.saveToCache(trackingContext);
    }

    TrackingContextProvider contextProvider =new TrackingContextProviderSimple(trackingContext);
    TmdResult tmdResult = tmdService.process(contextProvider);
    return tmdResult;

  }

  private TrackingContext calculateClassification(Tracking tracking,
      TrackingContext featureTrackingContext, TmdServiceCache cache, TmdService tmdService)
      throws IOException, ClassNotFoundException {

    TrackingContext trackingContext = cache.getFromCache(tracking.getId());
    if(trackingContext != null){
      return trackingContext;
    }

   TrackingContextProvider trackingContextProvider = new TrackingContextProviderSimple(featureTrackingContext);


    EarlyReturnConditionImp earlyReturnConditionImp = new EarlyReturnConditionImp(
        TmdService.CLASSIFICATION);

    TmdResult tmdResult = tmdService.process(trackingContextProvider,earlyReturnConditionImp);
    TrackingContext temporaryTrackingContext = earlyReturnConditionImp.getTrackingContext();

    cache.saveToCache(temporaryTrackingContext);
    return temporaryTrackingContext;

  }

  private TrackingContext calculateAfterFeature(Tracking tracking) throws IOException, ClassNotFoundException {

    Long id = tracking.getId();
    TrackingContext trackingContext = afterFeatureCalculationCache.getFromCache(id);
    if(trackingContext != null){
      return trackingContext;
    }
    TrackingContextProvider trackingContextProvider;
    trackingContextProvider = new TrackingTrackingContextProvider(tracking);

    EarlyReturnConditionImp earlyReturnConditionImp = new EarlyReturnConditionImp(
        TmdService.FEATURE_CALCULATION);

    TmdResult tmdResult = ramdomForestTmdService
        .process(trackingContextProvider,earlyReturnConditionImp);
    TrackingContext temporaryTrackingContext = earlyReturnConditionImp.getTrackingContext();

    afterFeatureCalculationCache.saveToCache(temporaryTrackingContext);
    return temporaryTrackingContext;

  }




  private void saveNeededTime(List<Tuple<Integer, Duration>> neededTime,
      String fileNameSuffix){
    String dataFolderPath = PropertyHelper.getValue(Constants.dataFolder);
    String dataFolderConstants = PropertyHelper.getValue(Constants.dataFolder_tmdOverlapping);

    String path = Paths.get(dataFolderPath,dataFolderConstants).toString();
    File file= new File(path);
    if(!file.exists()){
      file.mkdir();
    }

    String fileName = fileNameSuffix+"_neededTime.csv";
    Path filePath = Paths.get(path,fileName);

    File csvFile = new File(filePath.toString());
    if (csvFile.exists()) {
      csvFile.delete();
    }

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Number_of_gps_points; duration_in_millis"+System.lineSeparator());
    for (Tuple<Integer, Duration> integerDurationTuple : neededTime) {
      stringBuilder.append(integerDurationTuple.getItem1()+"; "+integerDurationTuple.getItem2().toMillis()+System.lineSeparator());
    }

    try {
      BufferedWriter writer = Files.newBufferedWriter(filePath,StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
      writer.write(stringBuilder.toString());
      writer.flush();
    }catch(Exception ex) {
      ex.printStackTrace();
    }


  }

  private void saveCsv(List<Tuple<Double, Double>> durationDistanceAccuracy,
      String fileNameSuffix){
    String dataFolderPath = PropertyHelper.getValue(Constants.dataFolder);
    String dataFolderConstants = PropertyHelper.getValue(Constants.dataFolder_tmdOverlapping);

    String path = Paths.get(dataFolderPath,dataFolderConstants).toString();
    File file= new File(path);
    if(!file.exists()){
      file.mkdir();
    }



    StringBuilder durationAccuracyCsv = new StringBuilder();
    durationAccuracyCsv.append("DurationOverlapping; DistanceOveralapping"+System.lineSeparator());
    for (Tuple<Double, Double> doubleDoubleTuple : durationDistanceAccuracy) {
      Double duration = doubleDoubleTuple.getItem1();
      Double distance = doubleDoubleTuple.getItem2();

      String durationOverlappingStr = decimalFormat.format(duration);
      String distanceOverlappingStr = decimalFormat.format(distance);
      durationAccuracyCsv.append(durationOverlappingStr+"; "+distanceOverlappingStr+System.lineSeparator());
    }
    String fileName = fileNameSuffix+"_overlapping.csv";
    Path filePath = Paths.get(path,fileName);

    File csvFile = new File(filePath.toString());
    if (csvFile.exists()) {
      csvFile.delete();
    }


    try {
         BufferedWriter writer = Files.newBufferedWriter(filePath,StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        writer.write(durationAccuracyCsv.toString());
        writer.flush();
        }catch(Exception ex) {
      ex.printStackTrace();
    }
  }

static int index= 0;




  public static class EarlyReturnConditionImp implements EarlyReturnCondition{

    public EarlyReturnConditionImp(String stageToReach) {
      this.stageToReach = stageToReach;
    }

    private String stageToReach;

    private TrackingContext trackingContext;

    public TrackingContext getTrackingContext() {
      return trackingContext;
    }

    @Override
    public boolean isReached(String stage) {
      if(stageToReach== null){
        return false;
      }
      return(stageToReach.equals(stage));
    }

    @Override
    public void setTrackingContext(TrackingContext trackingContext) {
      this.trackingContext= trackingContext;
    }
  }


  public static class TrackingTrackingContextProvider implements TrackingContextProvider{

    Tracking next;

    public TrackingTrackingContextProvider(Tracking tracking) {
      this.next = tracking;
    }

    TrackingContext trackingContext;

    @Override
    public TrackingContext getTrackingContext() {
      if(trackingContext == null){
        trackingContext = new TrackingContext();
        trackingContext.setTrackingId(next.getId());
        List<IExtendedGpsPoint> gpsPoints = next.getGpsPoints();
        ArrayList<IExtendedGpsPoint> gpsPointsArray = new ArrayList<>(gpsPoints);
        LocalDateTime startTime = gpsPointsArray.get(0).getTime();
        LocalDateTime endTime = gpsPoints.get(gpsPoints.size() - 1).getTime();
        if(startTime.isAfter(next.getStartTimestamp())){
          startTime = next.getStartTimestamp();
        }
        if(endTime.isBefore(next.getEndTimestamp())){
          endTime  = next.getEndTimestamp();
        }
        if(endTime.isBefore(startTime)){
          int debug = 3;
        }
        trackingContext.setTrackingEndTime(endTime);
        trackingContext.setTrackingStartTime(startTime);
        trackingContext.addData(TrackingContext.RAW_GPS_INPUT,gpsPointsArray );
      }
      return trackingContext;
    }
  }

}
