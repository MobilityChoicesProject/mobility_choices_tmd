package experiments;

import at.fhv.tmd.segmentClassification.util.Helper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import helper.OutputHelper;
import helper.PropertyHelper;
import helper.StringSavingHelper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 19.05.2017.
 *
 * This experiment seperates valid Trackings from Trackings which are not valid for this Work.
 *
 */
public class ValidTrackingExperiment {
  OutputHelper outputHelper = OutputHelper.getOutputHelper("Valid Trackings.txt");


  private static  Duration minDuration = Duration.ofMinutes(2);
  private static Duration minDuration5 = Duration.ofMinutes(5);
  private static int minGpsPoints = 60;

  static BoundingBox maxBoundingBox = new SimpleBoundingBox(46.5,6.5,48.5,15.5);

  public ValidTrackingExperiment() throws IOException {
  }

  public void doExperiment(Iterator<Tracking> trackingIterator) throws IOException {


    int countergood = 0;
    int counterBad =0;

    int bike = 0,car =0,walk=0,train=0,bus=0,other=0;
    int goodbike = 0,goodcar =0,goodwalk=0,goodtrain=0,goodbus=0,goodother=0;

    long currentTimeMillis = System.currentTimeMillis();

    int notInsideBoundary = 0, notEnoughtPoints = 0, notLongEnough = 0,notLongEnough5 = 0,tooBigTimeDiffCounter = 0;

    List<LocalDateTime> goodOneStartLocalDateTimes = new ArrayList();
    List<TrackingIdNamePair> goodTrackingIdNamePairs = new ArrayList();

    int trackingCounter = 0;
    while (trackingIterator.hasNext()) {
      Tracking next = trackingIterator.next();

      trackingCounter++;
      next = Helper.removeSameSequelTransportModes(next);

      for (TrackingSegment trackingSegment : next.getTrackingSegmentBagWithVersion(0).getSegments()) {
        if(trackingSegment.getTransportType()== TransportType.BIKE){
          bike++;
        }else if(trackingSegment.getTransportType()== TransportType.BUS){
          bus++;
        }else if(trackingSegment.getTransportType()== TransportType.CAR){
          car++;
        }else if(trackingSegment.getTransportType()== TransportType.WALK){
          walk++;
        }else if(trackingSegment.getTransportType()== TransportType.TRAIN){
          train++;
        }else{
          other++;
        }
      }


      List<Duration> durations = new ArrayList<>();
      IExtendedGpsPoint lastPoint = null;
      for (IExtendedGpsPoint gpsPoint : next.getGpsPoints()) {
        if(lastPoint != null){

          Duration between = Duration.between(lastPoint.getMostAccurateTime(), gpsPoint.getMostAccurateTime());

          durations.add(between);

        }
        lastPoint= gpsPoint;

      }

      boolean tooBigTimeDiffBetweenGpsPoints = false;
      Duration duration = Duration.ofSeconds(6);
      durations.sort((o1, o2) -> o1.compareTo(o2));
      int halfSize = durations.size()/2;
     for(int i = 0; i < halfSize;i++){
       if(durations.get(i).compareTo(duration)>0){
         tooBigTimeDiffBetweenGpsPoints=true;
         break;
       }
     }
      BoundingBox boundingBox = next.getBoundingBox();
      boolean isInsideMaxBoundary = maxBoundingBox.contains(boundingBox);

      Duration durationOfTracking = Duration.between(next.getStartTimestamp(), next.getEndTimestamp());

      boolean isLongerOrEqualThanMinDuration = minDuration.compareTo(durationOfTracking) <= 0;
      boolean isLongerOrEqualThanMinDuration5 = minDuration5.compareTo(durationOfTracking) <= 0;

      int gpsPointsSize = next.getGpsPoints().size();

      boolean hasMoreOrEqualMinGpsPoints = gpsPointsSize - minGpsPoints >= 0;

      if(!isLongerOrEqualThanMinDuration5){
        notLongEnough5++;
      }

      if(isInsideMaxBoundary && isLongerOrEqualThanMinDuration && hasMoreOrEqualMinGpsPoints &&!tooBigTimeDiffBetweenGpsPoints){
        countergood++;
        goodOneStartLocalDateTimes.add(next.getStartTimestamp());
        String filenName = next.getTrackingInfo(Constants.FILENAME);
        Long id = next.getId();
        goodTrackingIdNamePairs.add(new TrackingIdNamePair(id,filenName));


        for (TrackingSegment trackingSegment : next.getTrackingSegmentBagWithVersion(0).getSegments()) {
          if(trackingSegment.getTransportType()== TransportType.BIKE){
            goodbike++;
          }else if(trackingSegment.getTransportType()== TransportType.BUS){
            goodbus++;
          }else if(trackingSegment.getTransportType()== TransportType.CAR){
            goodcar++;
          }else if(trackingSegment.getTransportType()== TransportType.WALK){
            goodwalk++;
          }else if(trackingSegment.getTransportType()== TransportType.TRAIN){
            goodtrain++;
          }else{
            goodother++;
          }
        }


      }else{
        counterBad++;

        if(tooBigTimeDiffBetweenGpsPoints){
          tooBigTimeDiffCounter++;
        }

        if(!isInsideMaxBoundary)
        {
          notInsideBoundary++;
        }
        if(!hasMoreOrEqualMinGpsPoints)
        {
          notEnoughtPoints++;
        }
        if(!isLongerOrEqualThanMinDuration)
        {
          notLongEnough++;
        }
        System.out.println(next.getStartTimestamp());
      }

    }


   outputHelper.writeLine("Car Segments: "+car);
   outputHelper.writeLine("Bike Segments: "+bike);
   outputHelper.writeLine("Train Segments: "+train);
   outputHelper.writeLine("Bus Segments: "+bus);
   outputHelper.writeLine("Walk Segments: "+walk);
   outputHelper.writeLine("other Segments: "+other);


   outputHelper.writeLine("Good Car Segments: "+goodcar);
   outputHelper.writeLine("Good Bike Segments: "+goodbike);
   outputHelper.writeLine("Good Train Segments: "+goodtrain);
   outputHelper.writeLine("Good Bus Segments: "+goodbus);
   outputHelper.writeLine("Good Walk Segments: "+goodwalk);
   outputHelper.writeLine("Good other Segments: "+goodother);

   outputHelper.writeLine("All Trackings "+trackingCounter);
   outputHelper.writeLine("Good Trackings "+countergood);
   outputHelper.writeLine("bad Trackings "+counterBad);


    Gson gSon = new GsonBuilder().setPrettyPrinting().create();
    String json = gSon.toJson(goodTrackingIdNamePairs);
    String msg = json.toString();
    try {

      String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

      Path path = Paths.get(dataFolder,"validTrackingIdNamePair.json");
      StringSavingHelper.save(path.toString(),msg.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }


  }





}
