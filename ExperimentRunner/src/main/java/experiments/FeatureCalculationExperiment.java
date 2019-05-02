package experiments;

import at.fhv.features.LabeledFeature;
import at.fhv.filters.PositionJumpSpeedFilter;
import at.fhv.filters.SamePositionWorseAccuracyFilter;
import at.fhv.filters.WrongTimeGpsFilter;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.segmentClassification.classifier.ArfFileWrapper;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import helper.FeatureCalculationHelper;
import helper.PropertyHelper;
import helper.TrackingIdNamePairIterator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 17.05.2017.
 */
public class FeatureCalculationExperiment {

//  static Logger  root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//
  private static org.slf4j.Logger logger = LoggerFactory.getLogger(FeatureCalculationExperiment.class);


  private LeightweightTrackingDao leightweightTrackingDao;
  private final EntityManagerFactory emf;
  PositionJumpSpeedFilter positionJumpSpeedFilter ;
  WrongTimeGpsFilter wrongTimeGpsFilter = new WrongTimeGpsFilter();
  SamePositionWorseAccuracyFilter samePositionWorseAccuracyFilter = new SamePositionWorseAccuracyFilter();
  DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:s");


  private static  Duration minDuration = Duration.ofMinutes(2);
  private static int minGpsPoints = 60;

  static BoundingBox maxBoundingBox = new SimpleBoundingBox(46.5,6.5,48.5,15.5);
  private ConfigService configService;


  public FeatureCalculationExperiment(LeightweightTrackingDao leightweightTrackingDao,
      EntityManagerFactory emf){
    this.leightweightTrackingDao = leightweightTrackingDao;
    this.emf = emf;
  }






  public void doIt(boolean calcForWithoutSegmentation,boolean calcForWithSegmentation) throws IOException {
//    root.setLevel(Level.WARN);
    positionJumpSpeedFilter = new PositionJumpSpeedFilter(configService);

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

    File trainingSetFile = new File(dataFolder,Constants.trainingSet);
    File testSetFile = new File(dataFolder,Constants.testSet);

    List<TrackingIdNamePair> trainingSet = TrackingIdNamePairFileReaderHelper.load(trainingSetFile.getPath());
    List<TrackingIdNamePair> testSet = TrackingIdNamePairFileReaderHelper.load(testSetFile.getPath());
//
//    trainingSet.clear();
//    trainingSet.setTasks(new TrackingIdNamePair(411,"1481781807515.gpsTrack"));


    long beforeCalculationTime = System.currentTimeMillis();
    FeatureCalculationHelper featureCalculationHelper = new FeatureCalculationHelper();

    List<LabeledFeature> trainingSetlabeledFeatureList = null;
    List<LabeledFeature> testSetlabeledFeatureList = null;
    if(calcForWithoutSegmentation){
      trainingSetlabeledFeatureList = featureCalculationHelper
          .calculate(new TrackingIdNamePairIterator(trainingSet,leightweightTrackingDao), emf, -1);


      testSetlabeledFeatureList = featureCalculationHelper
          .calculate(new TrackingIdNamePairIterator(testSet,leightweightTrackingDao), emf, -1);

    }
    List<LabeledFeature> testSetlabeledFeatureList_segm = null;
    List<LabeledFeature> trainingSetlabeledFeatureList_segm = null;

    if(calcForWithSegmentation) {
      trainingSetlabeledFeatureList_segm = featureCalculationHelper
          .calculateWithSegmentation(
              new TrackingIdNamePairIterator(trainingSet, leightweightTrackingDao), emf, -1);

      testSetlabeledFeatureList_segm = featureCalculationHelper
          .calculateWithSegmentation(
              new TrackingIdNamePairIterator(testSet, leightweightTrackingDao), emf, -1);

      System.out.println("needed time: " + (System.currentTimeMillis() - beforeCalculationTime));
    }

    if(calcForWithoutSegmentation) {
      unifyOtherAndWalkingToOther(trainingSetlabeledFeatureList);
      unifyOtherAndWalkingToOther(testSetlabeledFeatureList);
    }
    if(calcForWithSegmentation){
      unifyOtherAndWalkingToOther(trainingSetlabeledFeatureList_segm);
      unifyOtherAndWalkingToOther(testSetlabeledFeatureList_segm);
    }



    String trainingSetWithIds = PropertyHelper.getValue(Constants.trainingSetWithIds);
    String testSetWithIds = PropertyHelper.getValue(Constants.testSetWithIds);
    String trainingSetWithoutIds = PropertyHelper.getValue(Constants.trainingSetWithoutIds);
    String testSetWithoutIds = PropertyHelper.getValue(Constants.testSetWithoutIds);


//    writeToCsvFile(dataFolder+trainingSetWithIds,trainingSetlabeledFeatureList,true);
//     writeToCsvFile(dataFolder+testSetWithIds,testSetlabeledFeatureList,true);
//     writeToCsvFile(dataFolder+trainingSetWithoutIds,trainingSetlabeledFeatureList,false);
//     writeToCsvFile(dataFolder+testSetWithoutIds,testSetlabeledFeatureList,false);

    if(calcForWithoutSegmentation) {
      saveArfFile(trainingSetWithIds, trainingSetlabeledFeatureList, true);
      saveArfFile(testSetWithIds, testSetlabeledFeatureList, true);
      saveArfFile(trainingSetWithoutIds, trainingSetlabeledFeatureList, false);
      saveArfFile(testSetWithoutIds, testSetlabeledFeatureList, false);

    }

    if(calcForWithSegmentation){
    saveArfFile(trainingSetWithIds+"_segm",trainingSetlabeledFeatureList_segm,true);
    saveArfFile(testSetWithIds+"_segm",testSetlabeledFeatureList_segm,true);
    saveArfFile( trainingSetWithoutIds+"_segm",trainingSetlabeledFeatureList_segm,false);
    saveArfFile(testSetWithoutIds+"_segm",testSetlabeledFeatureList_segm,false);

      saveJsonFile(trainingSetWithIds+"_segm",trainingSetlabeledFeatureList_segm);
      saveJsonFile(testSetWithIds+"_segm",testSetlabeledFeatureList_segm);

    }

    if(calcForWithoutSegmentation) {
      saveJsonFile(trainingSetWithIds, trainingSetlabeledFeatureList);
      saveJsonFile(testSetWithIds, testSetlabeledFeatureList);
//    saveJsonFile( trainingSetWithoutIds,trainingSetlabeledFeatureList);
//    saveJsonFile(testSetWithoutIds,testSetlabeledFeatureList);

      trainingSetlabeledFeatureList
          .removeIf(labeledFeature -> labeledFeature.getTransportType() == TransportType.OTHER);
      testSetlabeledFeatureList
          .removeIf(labeledFeature -> labeledFeature.getTransportType() == TransportType.OTHER);
      trainingSetlabeledFeatureList
          .removeIf(labeledFeature -> labeledFeature.getTransportType() == TransportType.OTHER);
      testSetlabeledFeatureList
          .removeIf(labeledFeature -> labeledFeature.getTransportType() == TransportType.OTHER);


    saveArfFile(PropertyHelper.getValue(Constants.trainingSetWithIds_withoutOther),trainingSetlabeledFeatureList,true);
    saveArfFile(PropertyHelper.getValue(Constants.testSetWithIds_withoutOther),testSetlabeledFeatureList,true);
    saveArfFile(PropertyHelper.getValue(Constants.trainingSetWithoutIds_withoutOther),trainingSetlabeledFeatureList,false);
    saveArfFile(PropertyHelper.getValue(Constants.testSetWithoutIds_withoutOther),testSetlabeledFeatureList,false);

    saveJsonFile(PropertyHelper.getValue(Constants.trainingSetWithIds_withoutOther),trainingSetlabeledFeatureList);
    saveJsonFile(PropertyHelper.getValue(Constants.testSetWithIds_withoutOther),testSetlabeledFeatureList);
//    saveJsonFile(PropertyHelper.getValue(Constants.trainingSetWithoutIds_withoutOther),trainingSetlabeledFeatureList);
//    saveJsonFile(PropertyHelper.getValue(Constants.testSetWithoutIds_withoutOther),testSetlabeledFeatureList);
    }

    int b= 4;
  }

  private void unifyOtherAndWalkingToOther(List<LabeledFeature> testSetlabeledFeatureList) {

    for (LabeledFeature labeledFeature : testSetlabeledFeatureList) {
     if(labeledFeature.getTransportType()== TransportType.WALK){
       labeledFeature.setTransportType(TransportType.OTHER);
     }
    }



  }

  private void writeToCsvFile(String path, List<LabeledFeature> labeledFeatureList,
      boolean withIdInfos) throws IOException {

    StringBuilder stringBuilder = new StringBuilder();
    boolean firstRound = true;
    for (LabeledFeature labeledFeature : labeledFeatureList) {
      List<FeatureResult> resultList = new ArrayList<>(labeledFeature.getFeatureResultList());
      resultList.sort((o1, o2) -> o1.getFeatureName().charAt(0) - o2.getFeatureName().charAt(0));

      if(firstRound){
        for (FeatureResult featureResult : resultList) {
          stringBuilder.append(featureResult.getFeatureName()+", ");
        }
        if(withIdInfos){
          stringBuilder.append("trackingStartTime, ");
          stringBuilder.append("segmentStartTime, ");
          stringBuilder.append("fileName, ");
        }

        stringBuilder.append("transportType ");
        stringBuilder.append(System.lineSeparator());
      }


      for (FeatureResult featureResult : resultList) {
        stringBuilder.append(featureResult.getFeatureValue()+", ");
      }
      if(withIdInfos) {
        stringBuilder
            .append(labeledFeature.getTrackingStartTime().format(dateTimeFormatter) + ", ");
        stringBuilder.append(labeledFeature.getSegmentStartTime().format(dateTimeFormatter) + ", ");
        stringBuilder.append(labeledFeature.getTrackingFileName() + ", ");
      }
      stringBuilder.append(labeledFeature.getTransportType().name());
      stringBuilder.append(System.lineSeparator());

      firstRound= false;
    }

    File file = new File(path);
    if(file.exists()){
      file.delete();
    }


    Files.write(Paths.get(path), stringBuilder.toString().getBytes());


  }

  void saveJsonFile(String name,List<LabeledFeature> labeledFeatureList )throws  IOException{

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String str = gson.toJson(labeledFeatureList);

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

    String path = dataFolder+"testTrainingSets/"+name+".json";
    File file = new File(path);
    String folder = file.getParent();

    boolean exists = Files.exists( Paths.get(folder));
    if(!exists){
      Files.createDirectories(Paths.get(folder));
    }

    Files.write(Paths.get(path), str.getBytes());


  }

  void saveArfFile(  String name, List<LabeledFeature> labeledFeatureList,
      boolean withIdInfos) throws IOException {

    ArfFileWrapper arfFileWrapper = new ArfFileWrapper();

    List<FeatureResult> featureResultList = labeledFeatureList.get(0).getFeatureResultList();
    featureResultList
        .sort((o1, o2) -> o1.getFeatureName().charAt(0)-o2.getFeatureName().charAt(0));

    for (FeatureResult featureResult : featureResultList) {
      arfFileWrapper.addNumericAttribute(featureResult.getFeatureName());
    }

    if(withIdInfos){
      arfFileWrapper.addStringAttribute("trackingStartTime");
      arfFileWrapper.addStringAttribute("segmentStartTime");
      arfFileWrapper.addStringAttribute("fileName");
    }

    List<String> transportTypes = new ArrayList<>();
    transportTypes.add("'"+TransportType.BIKE+"'");
    transportTypes.add("'"+TransportType.BUS+"'");
    transportTypes.add("'"+TransportType.CAR+"'");
    transportTypes.add("'"+TransportType.OTHER+"'");
    transportTypes.add("'"+TransportType.TRAIN+"'");
//    transportTypes.add("'"+TransportType.WALK+"'");

      arfFileWrapper.setLabelAttribute("TransportType",transportTypes);

    // data

    for (LabeledFeature labeledFeature : labeledFeatureList) {

      featureResultList = labeledFeature.getFeatureResultList();
      featureResultList
        .sort((o1, o2) -> o1.getFeatureName().charAt(0)-o2.getFeatureName().charAt(0));

      List<String> data = new ArrayList<>();
      for (FeatureResult featureResult : featureResultList) {
        data.add(featureResult.getFeatureValue()+"");
      }
      if(withIdInfos){

        data.add("\""+labeledFeature.getTrackingStartTime().format(dateTimeFormatter)+"\"");
        data.add("\""+labeledFeature.getSegmentStartTime().format(dateTimeFormatter)+"\"");
        data.add("\""+labeledFeature.getTrackingFileName()+"\"");
      }
        data.add("'"+labeledFeature.getTransportType()+"'");

      arfFileWrapper.addData(data);
    }

    String arfFileStr = arfFileWrapper.getFileAsText(name);

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

    String path = dataFolder+"testTrainingSets/"+name+".arff";
    File file = new File(path);
    String folder = file.getParent();

    boolean exists = Files.exists( Paths.get(folder));
    if(!exists){
      Files.createDirectories(Paths.get(folder));
    }


    if(file.exists()){
      file.delete();
    }

    Files.write(Paths.get(path), arfFileStr.getBytes());

  }




  public List<IGpsPoint> filterCoordinates(List<IGpsPoint> coordinates){
    List<IGpsPoint> filter = wrongTimeGpsFilter.filter(coordinates);
    List<IGpsPoint> filter1 = positionJumpSpeedFilter.filter(filter);
    List<IGpsPoint> filter2 = samePositionWorseAccuracyFilter.filter(filter1);
    return filter2;
  }






}
