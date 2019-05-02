package experiments;

import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.OutputHelper;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Johannes on 24.06.2017.
 */
public class AreGpsPointTimeSequentalExperiment {

  OutputHelper outputHelper = OutputHelper.getOutputHelper("GpsPoint_SensorVsDeviceTime_comparison.txt");

  public AreGpsPointTimeSequentalExperiment() throws IOException {
  }

  public void doExperiment(Iterator<Tracking> trackingIterator) throws IOException {


    int fhGpsLoggerV4Counter = 0;
    int fhGpsLoggerCounter = 0;
    int otherTrackingCounter = 0;


    int gpsPointDeviceTimeNotInRightSequence = 0;
    int allgpsPointSensorTimeNotInRightSequence = 0;
    int gpsPointSensorTimeNotInRightSequencev4 = 0;

    int deviceTimeNotSequelTrackingCounter =0;
    int sensorTimeNotSequelTrackingCounter =0;

    int v4Counter=0;
    int counter = 0;

    int _10secDurationSensor = 0;
    int _10secDurationDvice = 0;

    int startDiffBiggerThan20sv4=0;
    int startDiffBiggerThan20sDevicev4=0;

    int startDiffBiggerThan20s=0;
    int startDiffBiggerThan20sDevice=0;


    int sensorTimeDeviceTimeDifferenceBiggerThan5s=0;
    int sensorTimeDeviceTimeDifferenceBiggerThan10s=0;
    int sensorTimeDeviceTimeDifferenceBiggerThan20s=0;

    int trackingSegmentWhereStartTimeIsAfterEndTime = 0;
    int trackingSegmentWherePreviousTrackingSegmentEndsAfterStart = 0;

    Set<String> phoneIds = new TreeSet<>();
    Set<String> phoneIdsnotv4butFhGpslogger = new TreeSet<>();

    while (trackingIterator.hasNext()) {
      Tracking next = trackingIterator.next();


      IExtendedGpsPoint lastGpsPoint= null;
      int counter3 = 0;
      boolean trackingCounted=false;
      boolean trackingCountedWithDeviceTimeNotInSequence=false;
      boolean trackingCountedWithSensorTimeNotInSequence=false;
      for (IExtendedGpsPoint gpsPoint : next.getGpsPoints()) {



        boolean v4File = gpsPoint.getDeviceSavingSystemTime() != null;
        if(v4File){
          Duration diffBetweenTimes = Duration
              .between(gpsPoint.getSensorTime(), gpsPoint.getDeviceSavingSystemTime()).abs();
          if(diffBetweenTimes.toMillis()>5000){
            sensorTimeDeviceTimeDifferenceBiggerThan5s++;
          }else if(diffBetweenTimes.toMillis()>10000){
            sensorTimeDeviceTimeDifferenceBiggerThan10s++;
          }else if(diffBetweenTimes.toMillis()>20000){
            sensorTimeDeviceTimeDifferenceBiggerThan20s++;
          }

        }

        if(lastGpsPoint != null) {



          if(v4File){



            if(!lastGpsPoint.getDeviceSavingSystemTime().isBefore(gpsPoint.getDeviceSavingSystemTime())){
              gpsPointDeviceTimeNotInRightSequence++;
              System.out.println("deviceTime");

              if(!trackingCountedWithDeviceTimeNotInSequence){
                deviceTimeNotSequelTrackingCounter++;
                trackingCountedWithDeviceTimeNotInSequence = true;
              }

            }

              Duration between = Duration
                  .between(lastGpsPoint.getDeviceSavingSystemTime(), gpsPoint.getDeviceSavingSystemTime());
              if(between.abs().toMillis()>30000) {
                _10secDurationDvice++;
              }

            if(next.getTrackingInfo(Constants.ORIGIN).equals(Constants.ORIGIN_FHGPSLOGGER)) {
               between = Duration
                  .between(lastGpsPoint.getSensorTime(), gpsPoint.getSensorTime());
              if (between.abs().toMillis() > 30000) {
                _10secDurationSensor++;
              }
            }


            if(!lastGpsPoint.getSensorTime().isBefore(gpsPoint.getSensorTime())){
              gpsPointSensorTimeNotInRightSequencev4++;


            }

          }


          if(!lastGpsPoint.getSensorTime().isBefore(gpsPoint.getSensorTime())){
            allgpsPointSensorTimeNotInRightSequence++;
            System.out.println("sensorTime");
            if(!trackingCountedWithSensorTimeNotInSequence){
              sensorTimeNotSequelTrackingCounter++;
              trackingCountedWithSensorTimeNotInSequence = true;
            }
        }




        }

        lastGpsPoint = gpsPoint;
        counter3++;

      }

      boolean fhGpsloggerTracking = next.getTrackingInfo(Constants.ORIGIN).equals(Constants.ORIGIN_FHGPSLOGGER);

      if (next.getTrackingSegmentBags().size()==0) {
        int b= 5;
      }

      boolean isV4Version = false;
     if(next.hasTrackingInfo(Constants.FH_GPS_LOGGER_VERSION)){
        isV4Version = next.getTrackingInfo(Constants.FH_GPS_LOGGER_VERSION).equals(Constants.FH_GPS_LOGGER_VERSION_4);
     }

      if(fhGpsloggerTracking && isV4Version){

        fhGpsLoggerV4Counter++;
        v4Counter++;
        List<IExtendedGpsPoint> gpsPoints = next.getGpsPoints();
        LocalDateTime deviceSavingSystemTime = gpsPoints.get(0).getDeviceSavingSystemTime();
        LocalDateTime sensorSavingTime = gpsPoints.get(0).getSensorTime();

        Duration deviceBetween = Duration.between(next.getStartTimestamp(), deviceSavingSystemTime);
        Duration sensorBetween = Duration.between(next.getStartTimestamp(), sensorSavingTime);

        if(Math.abs(deviceBetween.toMillis())>20000){
          startDiffBiggerThan20sDevicev4++;
        }
        if(Math.abs(sensorBetween.toMillis())>20000){
          startDiffBiggerThan20sv4++;
        }

      } else if(fhGpsloggerTracking){

        fhGpsLoggerCounter++;
        List<IExtendedGpsPoint> gpsPoints = next.getGpsPoints();
        LocalDateTime deviceSavingSystemTime = gpsPoints.get(0).getDeviceSavingSystemTime();
        LocalDateTime sensorSavingTime = gpsPoints.get(0).getSensorTime();

        Duration sensorBetween = Duration.between(next.getStartTimestamp(), sensorSavingTime);

        if(Math.abs(sensorBetween.toMillis())>20000){
          startDiffBiggerThan20s++;
          phoneIdsnotv4butFhGpslogger.add(next.getTrackingInfo(Constants.PHONE_ID));
        }
      }else{
        otherTrackingCounter++;
      }

      for (TrackingSegmentBag trackingSegmentBag : next.getTrackingSegmentBags()) {

        TrackingSegment lastTrackingSegment= null;
        for (TrackingSegment trackingSegment : trackingSegmentBag.getSegments()) {
          LocalDateTime startTime = trackingSegment.getStartTime();
          LocalDateTime endTime = trackingSegment.getEndTime();
          if (Duration.between(startTime,endTime).toMillis() <= 0) {
            trackingSegmentWhereStartTimeIsAfterEndTime++;
          }

          if(lastTrackingSegment != null){
            LocalDateTime endTime1 = lastTrackingSegment.getEndTime();

            if (Duration.between(endTime1,startTime).toMillis()< 0) {
              trackingSegmentWherePreviousTrackingSegmentEndsAfterStart++;
            }


          }

          lastTrackingSegment=trackingSegment;
        }
      }



      System.out.println(counter++);


    }

    outputHelper.writeLine("Trackings: ");
    outputHelper.writeLine("FHGpsLogger v4:  "+fhGpsLoggerV4Counter);
    outputHelper.writeLine("FHGpsLogger :  "+fhGpsLoggerCounter);
    outputHelper.writeLine("other trackings:  "+otherTrackingCounter);
    outputHelper.writeLine("");
    outputHelper.writeLine("FhGPSLogger v4 GPS points where device time is not in right order: "+gpsPointDeviceTimeNotInRightSequence);
    outputHelper.writeLine("FhGPSLogger v4 GPS points where sensor time is not in right order: "+gpsPointSensorTimeNotInRightSequencev4);
    outputHelper.writeLine("All Trackings: GPS points where sensor time is not in right order: "+allgpsPointSensorTimeNotInRightSequence);
    outputHelper.writeLine("");
    outputHelper.writeLine("Number of FhGPSLogger v4 Trackings: "+v4Counter);
    outputHelper.writeLine("Number of FhGPSLogger v4 Trackings with device time not in right order: "+deviceTimeNotSequelTrackingCounter);
    outputHelper.writeLine("Number of FhGPSLogger v4 Trackings with sensor time not in right order: "+sensorTimeNotSequelTrackingCounter);
    outputHelper.writeLine("");
    outputHelper.writeLine("FhGPSLogger v4 tracking where tracking starttime and first gps sensor time difference is bigger than 20secs: "+startDiffBiggerThan20sv4);
    outputHelper.writeLine("FhGPSLogger v4 tracking where tracking starttime and first gps device time difference is bigger than 20secs: "+startDiffBiggerThan20sDevicev4);
    outputHelper.writeLine("FhGPSLogger tracking where tracking starttime and first gps device time difference is bigger than 20secs: "+startDiffBiggerThan20s);
    outputHelper.writeLine("");
    outputHelper.writeLine("FhGPSLogger v4 tracking: GPS points where sensor time and device time have a diff above 5s: "+sensorTimeDeviceTimeDifferenceBiggerThan5s);
    outputHelper.writeLine("FhGPSLogger v4 tracking: GPS points where sensor time and device time have a diff above 10s: "+sensorTimeDeviceTimeDifferenceBiggerThan10s);
    outputHelper.writeLine("FhGPSLogger v4 tracking: GPS points where sensor time and device time have a diff above 20s: "+sensorTimeDeviceTimeDifferenceBiggerThan20s);


    outputHelper.saveAndClose();
    int size = phoneIds.size();

  }






}
