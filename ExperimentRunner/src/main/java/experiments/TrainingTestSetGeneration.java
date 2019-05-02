package experiments;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import helper.PropertyHelper;
import helper.TrackingIdNamePairIterator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Johannes on 26.06.2017.
 */
public class TrainingTestSetGeneration {

  LeightweightTrackingDao leightweightTrackingDao;

  public TrainingTestSetGeneration(
      LeightweightTrackingDao leightweightTrackingDao) {
    this.leightweightTrackingDao = leightweightTrackingDao;
  }

  public void doIt(){

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);
    String filePath = dataFolder+"validTrackingIdNamePair.json";

    List<TrackingIdNamePair> load = TrackingIdNamePairFileReaderHelper.load(filePath);
    TrackingIdNamePairIterator trackingIdNamePairIterator = new TrackingIdNamePairIterator(load,leightweightTrackingDao);

    List<Tracking> otherOnlyTrackings = new ArrayList<>();
    List<Tracking> busOnlyTrackings = new ArrayList<>();
    List<Tracking> bikeOnlyTrackings = new ArrayList<>();
    List<Tracking> carOnlyTrackings = new ArrayList<>();
    List<Tracking> trainOnlyTrackings = new ArrayList<>();
    List<Tracking> walkingOnlyTrackings = new ArrayList<>();

    List<Tracking> multipleTransportTypesTrackings = new ArrayList<>();

    int counter = 0;
    while (trackingIdNamePairIterator.hasNext()) {

      Tracking tracking = trackingIdNamePairIterator.next();


      if(tracking.hasTrackingInfo(Constants.ManualyEdited)){

        int size = tracking.getGpsPoints().size();
        if(size< 60){
          int b  = 4;
        }

        TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();

        Set<TransportType> transportTypesWithoutOther = getTransportTypesWithoutOther(
            latestTrackingSegmentBag.getSegments());


        if(transportTypesWithoutOther.size()==0){
          otherOnlyTrackings.add(tracking);

        }else if(transportTypesWithoutOther.size()<2){
          TransportType transportType = transportTypesWithoutOther.iterator().next();
          switch (transportType){
            case BUS:{
              busOnlyTrackings.add(tracking);
              break;
            }case CAR:{
              carOnlyTrackings.add(tracking);
              break;
            }case BIKE:{
              bikeOnlyTrackings.add(tracking);
              break;
            }case WALK:{
              walkingOnlyTrackings.add(tracking);
              break;
            }case TRAIN:{
              trainOnlyTrackings.add(tracking);
              break;
            }
          }
        }else{
          multipleTransportTypesTrackings.add(tracking);
        }

      }

      counter++;
      System.out.println(counter);
    }


    long seed = 17;
    double trainSetPercentage= 0.7;
    Tuple<List<Tracking>, List<Tracking>> otherSplit = split(otherOnlyTrackings, trainSetPercentage, seed);
    Tuple<List<Tracking>, List<Tracking>> busSplit = split(busOnlyTrackings, trainSetPercentage, seed);
    Tuple<List<Tracking>, List<Tracking>> carSplit = split(carOnlyTrackings, trainSetPercentage, seed);
    Tuple<List<Tracking>, List<Tracking>> bikeSplit = split(bikeOnlyTrackings, trainSetPercentage, seed);
    Tuple<List<Tracking>, List<Tracking>> walkingSplit = split(walkingOnlyTrackings, trainSetPercentage, seed);
    Tuple<List<Tracking>, List<Tracking>> trainSplit = split(trainOnlyTrackings, trainSetPercentage, seed);

    Tuple<List<Tracking>, List<Tracking>> multipleTransportTypeSplit = split(multipleTransportTypesTrackings,
        trainSetPercentage, seed);

    List<Tracking> trainingSet = new ArrayList<>();
    List<Tracking> testSet = new ArrayList<>();

    trainingSet.addAll(otherSplit.getItem1());
    trainingSet.addAll(busSplit.getItem1());
    trainingSet.addAll(carSplit.getItem1());
    trainingSet.addAll(bikeSplit.getItem1());
    trainingSet.addAll(walkingSplit.getItem1());
    trainingSet.addAll(trainSplit.getItem1());
    trainingSet.addAll(multipleTransportTypeSplit.getItem1());

    testSet.addAll(otherSplit.getItem2());
    testSet.addAll(busSplit.getItem2());
    testSet.addAll(carSplit.getItem2());
    testSet.addAll(bikeSplit.getItem2());
    testSet.addAll(walkingSplit.getItem2());
    testSet.addAll(trainSplit.getItem2());
    testSet.addAll(multipleTransportTypeSplit.getItem2());


    List<TrackingIdNamePair> trainingSetIds = new ArrayList<>(trainingSet.size());
    List<TrackingIdNamePair> testSetIds = new ArrayList<>(testSet.size());
    for (Tracking tracking : trainingSet) {
      String fileName = tracking.getTrackingInfo(Constants.FILENAME);
      trainingSetIds.add(new TrackingIdNamePair(tracking.getId(),fileName));
    }
    for (Tracking tracking : testSet) {
      String fileName = tracking.getTrackingInfo(Constants.FILENAME);
      testSetIds.add(new TrackingIdNamePair(tracking.getId(),fileName));
    }


    saveFile(trainingSetIds,testSetIds);


    int b= 4;

  }


  private void saveFile(  List<TrackingIdNamePair> trainingSetIds,   List<TrackingIdNamePair>  testSetIds){
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String dataFolderPath = PropertyHelper.getValue(Constants.dataFolder);

    File trainingSet = new File(dataFolderPath,Constants.trainingSet);
    File testSet = new File(dataFolderPath,Constants.testSet);


    try (Writer writer = new FileWriter(trainingSet.getAbsolutePath())) {
     gson.toJson(trainingSetIds,writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try (Writer writer = new FileWriter(testSet.getAbsolutePath())) {
      gson.toJson(testSetIds,writer);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private Tuple<List<Tracking>,List<Tracking>> split(List<Tracking> trackingList, double trainingSetPercentage, long seed) {

    trackingList = new ArrayList<>(trackingList);
    int size = trackingList.size();
    double trainSetSizeWithFraction = size * trainingSetPercentage;
    int trainsetSize = (int)Math.round(trainSetSizeWithFraction);

    Random random = new Random(seed);

    List<Tracking> trainingSet = new ArrayList<>();

    Set<Integer> usedIndexes = new HashSet<>();
  while(trainingSet.size()<trainsetSize){

      int randomIndex = random.nextInt(size);
      if(!usedIndexes.contains(randomIndex)){

        Tracking tracking = trackingList.get(randomIndex);
        trainingSet.add(tracking);
      }
      usedIndexes.add(randomIndex);
    }

    for (Tracking tracking : trainingSet) {
      trackingList.remove(tracking);
    }

    return new Tuple<>(trainingSet,trackingList);

  }

  private Set<TransportType> getTransportTypesWithoutOther(List<TrackingSegment> segmentList){

    Set<TransportType> transportTypes = new HashSet<TransportType>();
    for (TrackingSegment segment : segmentList) {
      TransportType transportType = segment.getTransportType();
      if(transportType != TransportType.OTHER){
        transportTypes.add(transportType);
      }
    }
    return transportTypes;



  }


}
