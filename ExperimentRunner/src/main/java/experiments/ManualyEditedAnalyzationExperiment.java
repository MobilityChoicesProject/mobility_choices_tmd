package experiments;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.ManualyEditedHelper;
import helper.OutputHelper;
import helper.OverlappingCalcualtor;
import helper.OverlappingResult;
import helper.PropertyHelper;
import helper.Section;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 24.06.2017.
 */
public class ManualyEditedAnalyzationExperiment {
  OutputHelper outputHelper = OutputHelper.getOutputHelper("Manually Edited Overlapping Analyzation.txt");

  public ManualyEditedAnalyzationExperiment() throws IOException {
  }


  public void doExperiment(Iterator<Tracking> iterator) throws IOException {

    List<OverlappingResult> overlappingResults = new ArrayList<>();
    List<OverlappingResult> overlappingResultsFhGpsLogger = new ArrayList<>();
    List<OverlappingResult> overlappingResultsOldTrackings = new ArrayList<>();


    Tuple<Distance,Duration> notChangedFhTrackings = new Tuple<>(new Distance(0),Duration.ofDays(0));
    Tuple<Distance,Duration> notChangedOtherTrackings= new Tuple<>(new Distance(0),Duration.ofDays(0));
    Tuple<Distance,Duration> notChangedAllTrackings= new Tuple<>(new Distance(0),Duration.ofDays(0));


    int counter = 0;
    int goodTrackingCounter = 0;

    int editedSegmentCounter = 0;
    int checkedCounter = 0;
    int mobilityChoiceEdited = 0;

    int manualyEditedFhGpsLoggerTrackingCounter=0;
    int manualyCheckedFhGpsLoggerTrackingCounter=0;


    int manualyEditedOlderTrackingCounter=0;
    int manualyCheckedOldTrackingCounter=0;

    Duration totalGoodDuration = Duration.ofSeconds(0) ;
    Distance totalGoodDistance = new Distance(0) ;

    Duration fhGpsLoggerEditedTotalDuration = Duration.ofSeconds(0) ;
    Distance fhGpsLoggerEditedTotalDistance= new Distance(0) ;

    Duration otherEditedTotalDuration = Duration.ofSeconds(0) ;
    Distance otherEditedTotalDistance= new Distance(0) ;

    Distance allGoodFhGpsLoggerDistance =  new Distance(0) ;
    Duration allGoodFhGpsLoggerDuration =  Duration.ofSeconds(0) ;

    Distance allGoodolderTrackingDistance =  new Distance(0) ;
    Duration allGoodolderTrackingDuration =  Duration.ofSeconds(0) ;

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);
    String path = dataFolder+Constants.validTrackingIdNamePairFileName;

    List<Tuple<TransportType,Integer>> transportTypeCounterList = new ArrayList<>();

    List<TrackingIdNamePair> trackingIdNamePairs = TrackingIdNamePairFileReaderHelper.load(path);

    while (iterator.hasNext()) {

      Tracking tracking = iterator.next();
      counter++;
      TrackingIdNamePair  trackingIdNamePair = new TrackingIdNamePair(tracking.getId(),tracking.getTrackingInfo(Constants.FILENAME));
      if(!trackingIdNamePairs.contains(trackingIdNamePair)){
        // not a good tracking
        continue;
      }

      goodTrackingCounter++;


      IExtendedGpsPoint firstPoint = tracking.getGpsPoints().get(0);
      IExtendedGpsPoint lastPoint = tracking.getGpsPoints().get(tracking.getGpsPoints().size() - 1);

      Duration duration = Duration.between(firstPoint.getMostAccurateTime(),lastPoint.getMostAccurateTime());
      totalGoodDuration =totalGoodDuration.plus(duration);
      Distance distance= calcDistance(tracking.getGpsPoints());
      totalGoodDistance = totalGoodDistance.plus(distance);

      if (ManualyEditedHelper.isManuallyEdited(tracking.getTrackingInfos())) {

        TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();

        for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
          TransportType transportType = trackingSegment.getTransportType();
          incrementList(transportTypeCounterList,transportType);
        }


        if(latestTrackingSegmentBag.getVersion() == 1){
          // manually added new segment labeling


          TrackingSegmentBag trackingSegmentBagWithVersion0 = tracking
              .getTrackingSegmentBagWithVersion(0);

          OverlappingCalcualtor overlappingCalcualtor = new OverlappingCalcualtor();

          List<Section> section_v0 = new ArrayList<>();
          List<Section> section_v1 = new ArrayList<>();

          for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {

            String name = trackingSegment.getTransportType().name();
            if(name.equals(TransportType.WALK.name())){
              name= TransportType.OTHER.name();
            }

            Section section = new Section(name,trackingSegment.getStartTime(),trackingSegment.getEndTime(),trackingSegment.getGpsPoints());
            section_v1.add(section);

          }

          for (TrackingSegment trackingSegment : trackingSegmentBagWithVersion0.getSegments()) {
            String name = trackingSegment.getTransportType().name();
            if(name.equals(TransportType.WALK.name())){
              name= TransportType.OTHER.name();
            }
            Section section = new Section(name,trackingSegment.getStartTime(),trackingSegment.getEndTime(),trackingSegment.getGpsPoints());
            section_v0.add(section);

          }

          OverlappingResult overlappingResult = overlappingCalcualtor
              .calcOverlappings(section_v0, section_v1);

          String origin = tracking.getTrackingInfo(Constants.ORIGIN);
          if(origin.equals(Constants.ORIGIN_FHGPSLOGGER)){
            fhGpsLoggerEditedTotalDistance = fhGpsLoggerEditedTotalDistance.plus(distance);
            fhGpsLoggerEditedTotalDuration = fhGpsLoggerEditedTotalDuration.plus(duration);

            allGoodFhGpsLoggerDistance = allGoodFhGpsLoggerDistance.plus(distance);
            allGoodFhGpsLoggerDuration = allGoodFhGpsLoggerDuration.plus(duration);

            overlappingResultsFhGpsLogger.add(overlappingResult);
            overlappingResults.add(overlappingResult);
            editedSegmentCounter++;
            manualyEditedFhGpsLoggerTrackingCounter++;
          }else if(!origin.equals(Constants.ORIGIN_MobilityChoices)){
            otherEditedTotalDistance = otherEditedTotalDistance.plus(distance);
            otherEditedTotalDuration = otherEditedTotalDuration.plus(duration);


            allGoodolderTrackingDistance = allGoodolderTrackingDistance.plus(distance);
            allGoodolderTrackingDuration = allGoodolderTrackingDuration.plus(duration);

            overlappingResultsOldTrackings.add(overlappingResult);
            overlappingResults.add(overlappingResult);
            manualyEditedOlderTrackingCounter++;

            editedSegmentCounter++;
          }else{
            // mobility choices had no initial labeling and there are not used for this calculation
            mobilityChoiceEdited++;
          }

        }else{



          checkedCounter++;
          String origin = tracking.getTrackingInfo(Constants.ORIGIN);
          if(tracking.getTrackingInfo(Constants.ORIGIN).equals(Constants.ORIGIN_FHGPSLOGGER)){

            allGoodFhGpsLoggerDistance = allGoodFhGpsLoggerDistance.plus(distance);
            allGoodFhGpsLoggerDuration = allGoodFhGpsLoggerDuration.plus(duration);

            manualyCheckedFhGpsLoggerTrackingCounter++;
          } else if(!origin.equals(Constants.ORIGIN_MobilityChoices)){


            allGoodolderTrackingDistance = allGoodolderTrackingDistance.plus(distance);
            allGoodolderTrackingDuration = allGoodolderTrackingDuration.plus(duration);
            manualyCheckedOldTrackingCounter++;
          }else{

          }
        }



      }
      System.out.println(counter);
    }


    Tuple<Double, Double> overlappingDistance = sumResults(overlappingResults);

    Tuple<Double, Double> fh = sumResults(
        overlappingResultsFhGpsLogger);
    Tuple<Double, Double> old = sumResults(
        overlappingResultsOldTrackings);

    Tuple<Distance, Duration> distanceDurationTuple = sumEditedDistanceDuration(overlappingResults);
    double totalDistanceRatio = distanceDurationTuple.getItem1().getKm()/totalGoodDistance.getKm();
    double totalDurationRatio = distanceDurationTuple.getItem2().toMillis()/(totalGoodDuration.toMillis()*1.0);

    Tuple<Distance, Duration> fhDistanceDurationTuple = sumEditedDistanceDuration(overlappingResultsFhGpsLogger);
    double fhDistanceRatio = fhDistanceDurationTuple.getItem1().getKm()/fhGpsLoggerEditedTotalDistance.getKm();
    double fhDurationRatio = fhDistanceDurationTuple.getItem2().toMillis()/(fhGpsLoggerEditedTotalDuration.toMillis()*1.0);

    Tuple<Distance, Duration> otherTrackingDistanceDurationTuple = sumEditedDistanceDuration(overlappingResultsOldTrackings);
    double otherDistanceRatio = otherTrackingDistanceDurationTuple.getItem1().getKm()/otherEditedTotalDistance.getKm();
    double otherDurationRatio = otherTrackingDistanceDurationTuple.getItem2().toMillis()/(otherEditedTotalDuration.toMillis()*1.0);


    double fhDistanceRatio1 = fhDistanceDurationTuple.getItem1().getKm()/allGoodFhGpsLoggerDistance.getKm();
    double fhDurationRatio1 = fhDistanceDurationTuple.getItem2().toMillis()/(allGoodFhGpsLoggerDuration.toMillis()*1.0);

    double otherDistanceRatio1 = otherTrackingDistanceDurationTuple.getItem1().getKm()/allGoodolderTrackingDistance.getKm();
    double otherDurationRatio1 = otherTrackingDistanceDurationTuple.getItem2().toMillis()/(allGoodolderTrackingDuration.toMillis()*1.0);





    Distance totalDistance = notChangedAllTrackings.getItem1().plus(distanceDurationTuple.getItem1());
    Duration totalDuration = notChangedAllTrackings.getItem2().plus(distanceDurationTuple.getItem2());


    double distanceRatio = distanceDurationTuple.getItem1().getKm()/totalDistance.getKm();
    double durationRatio = distanceDurationTuple.getItem2().toMillis()/(totalDuration.toMillis()*1.0);

    transportTypeCounterList.sort((o1, o2) -> o1.getItem1().name().charAt(0) - o2.getItem1().name().charAt(0) );

    StringBuilder stringBuilder = new StringBuilder();
    for (Tuple<TransportType, Integer> transportTypeIntegerTuple : transportTypeCounterList) {
      stringBuilder.append(transportTypeIntegerTuple.getItem1().name()+": "+transportTypeIntegerTuple.getItem2()+ "  |  ");
    }




    outputHelper.writeLine("All Trackings: "+counter);
    outputHelper.writeLine("Good Trackings (= for the analysis below used ones): "+goodTrackingCounter);
    outputHelper.writeLine("Mbbility Choice Trackings (no initial labeling available): "+mobilityChoiceEdited);
    outputHelper.writeLine("");
    outputHelper.writeLine("FhGpsLogger: edited Trackings / checked Trackings: "+manualyEditedFhGpsLoggerTrackingCounter+" / "+manualyCheckedFhGpsLoggerTrackingCounter);
    outputHelper.writeLine("older Tracking: edited Trackings / checked Trackings: "+manualyEditedOlderTrackingCounter+" / "+manualyCheckedOldTrackingCounter);
    outputHelper.writeLine("");
    outputHelper.writeLine("Trackings where labeling has manually been checked and corrected: "+editedSegmentCounter);
    outputHelper.writeLine("Trackings where labeling has manually been checked and where no correction was necessary: "+checkedCounter);
    outputHelper.writeLine("");
    outputHelper.writeLine("Distance Ratio (edited / allGood): "+totalDistanceRatio);
    outputHelper.writeLine("Duration Ratio (edited / allGood): "+totalDurationRatio);
    outputHelper.writeLine("");
    outputHelper.writeLine("Distance Ratio (edited part of edited FhGpsLogger / all parts of edited FHGpsLogger)"+ fhDistanceRatio );
    outputHelper.writeLine("Duration Ratio (edited part of edited FhGpsLogger / all parts of edited FHGpsLogger)"+ fhDurationRatio );
    outputHelper.writeLine("");
    outputHelper.writeLine("Distance Ratio (edited part of edited 'older Trackings' / all parts of edited 'older Trackings')"+ otherDistanceRatio );
    outputHelper.writeLine("Duration Ratio (edited part of edited 'older Trackings' / all parts of edited 'older Trackings')"+ otherDurationRatio );
    outputHelper.writeLine("");
    outputHelper.writeLine("Distance Ratio (edited part of edited FhGpsLogger / all parts of FHGpsLogger)"+ fhDistanceRatio1 );
    outputHelper.writeLine("Duration Ratio (edited part of edited FhGpsLogger / all parts of FHGpsLogger)"+ fhDurationRatio1 );
    outputHelper.writeLine("");
    outputHelper.writeLine("Distance Ratio (edited part of edited 'older Trackings' / all parts of 'older Trackings')"+ otherDistanceRatio1 );
    outputHelper.writeLine("Duration Ratio (edited part of edited 'older Trackings' / all parts of 'older Trackings')"+ otherDurationRatio1 );
    outputHelper.writeLine("" );
    outputHelper.writeLine(stringBuilder.toString() );

   outputHelper.saveAndClose();
    int b = 4;



  }

  private void incrementList(List<Tuple<TransportType, Integer>> transportTypeCounterList,
      TransportType transportType) {

    Tuple<TransportType, Integer> transportTypeIntegerTuple = null;
    for (Tuple<TransportType, Integer> typeIntegerTuple : transportTypeCounterList) {
      if(typeIntegerTuple.getItem1().equals(transportType)) {
        transportTypeIntegerTuple = typeIntegerTuple;
        break;

      }
    }
    if(transportTypeIntegerTuple== null){
      transportTypeIntegerTuple = new Tuple<>(transportType,0);
      transportTypeCounterList.add(transportTypeIntegerTuple);
    }

    transportTypeIntegerTuple.setItem2(transportTypeIntegerTuple.getItem2()+1);
  }

  private Tuple<Distance,Duration> sumEditedDistanceDuration(List<OverlappingResult> overlappingResults) {

    Distance distanceSum = new Distance(0);
    Duration durationSum = Duration.ofSeconds(0);
    for (OverlappingResult overlappingResult : overlappingResults) {
      distanceSum = distanceSum.plus(overlappingResult.getTotalDistance().minus(overlappingResult.getDistanceOverlapping()));
      durationSum = durationSum.plus(overlappingResult.getTotalDuration().minus(overlappingResult.getDurationOveralapping()));
    }

    return new Tuple<>(distanceSum,durationSum);

  }

  private Tuple<Distance,Duration> sumTotalDistanceDuration(List<OverlappingResult> overlappingResults) {

    Distance distanceSum = new Distance(0);
    Duration durationSum = Duration.ofSeconds(0);
    for (OverlappingResult overlappingResult : overlappingResults) {
      distanceSum = distanceSum.plus(overlappingResult.getTotalDistance());
      durationSum = durationSum.plus(overlappingResult.getTotalDuration());
    }

    return new Tuple<>(distanceSum,durationSum);

  }

  private Distance calcDistance(List<IExtendedGpsPoint> gpsPoints) {

    Distance distanceSum = new Distance(0);
    IExtendedGpsPoint lastGpsPoint =null;
    for (IExtendedGpsPoint gpsPoint : gpsPoints) {

      if(lastGpsPoint != null){

        Distance distance = CoordinateUtil.haversineDistance(lastGpsPoint, gpsPoint);
        distanceSum = distanceSum.plus(distance);


      }


      lastGpsPoint = gpsPoint;

    }
    return distanceSum;

  }


  public Tuple<Double, Double> sumResults(List<OverlappingResult> results){
    Distance distanceSum = new Distance(0);
    Distance overlappingdistanceSum = new Distance(0);
    Duration durationSum = Duration.ofSeconds(0);
    Duration overlappingdurationSum = Duration.ofSeconds(0);
    for (OverlappingResult overlappingResult : results) {
      distanceSum = distanceSum.plus(overlappingResult.getTotalDistance()) ;
      overlappingdistanceSum = overlappingdistanceSum.plus(overlappingResult.getDistanceOverlapping()) ;

      durationSum = durationSum.plus(overlappingResult.getTotalDuration());
      overlappingdurationSum = overlappingdurationSum.plus(overlappingResult.getDurationOveralapping());

    }

    double overlappingDistance = overlappingdistanceSum.getKm()/distanceSum.getKm();
    double overlappingDuration = overlappingdurationSum.toMillis()/(durationSum.toMillis()*1.0);

    return new Tuple<Double,Double>(overlappingDistance,overlappingDuration);
  }



}
