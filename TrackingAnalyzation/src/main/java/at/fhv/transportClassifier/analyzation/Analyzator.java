package at.fhv.transportClassifier.analyzation;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleAllTracking;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 09.03.2017.
 */
public class Analyzator {


    public TrackingAnalyzationResult analyze(SimpleAllTracking tracking){


        TrackingAnalyzationResult result = new TrackingAnalyzationResult();



        result.phoneId= getPhoneId(tracking);
        result.phoneType= getPhoneType(tracking);
        result.trackingVersion = getVersion(tracking);

        LocalDateTime startTimestamp = tracking.getStartTimestamp();
        LocalDateTime endTimestamp = tracking.getEndTimestamp();

        result.boundingbox =tracking.getBoundingBox();

        result.startimestamp = startTimestamp.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
        LocalDateTime firstGpsSensorSavingTime = tracking.getGpsPoints().get(0).getSensorTime();
        LocalDateTime lastGpsSensorSavingTime = tracking.getGpsPoints().get(tracking.getGpsPoints().size()-1).getSensorTime();

        Duration durationStartAndGpsSensorStart = Duration.between(startTimestamp,firstGpsSensorSavingTime);
        Duration durationEndAndGpsSensorEnd = Duration.between(endTimestamp, lastGpsSensorSavingTime);

        result.durationStartAndGpsSensorStart = durationStartAndGpsSensorStart;
        result.durationEndAndGpsSensorEnd = durationEndAndGpsSensorEnd;

        LocalDateTime firstAcSensorSavingTime = tracking.getAcceleratorStates().get(0).getTime();
        LocalDateTime lastAcSensorSavingTime = tracking.getAcceleratorStates().get(tracking.getAcceleratorStates().size()-1).getTime();

        result.durationStartAndAcSensorStart = Duration.between(startTimestamp,firstAcSensorSavingTime);
        result.durationEndAndAcSensorEnd = Duration.between(endTimestamp,lastAcSensorSavingTime);


        if (tracking.getGpsPoints().get(0).getDeviceSavingSystemTime() != null) {
            LocalDateTime firstGpsSystemSavingTime = tracking.getGpsPoints().get(0).getDeviceSavingSystemTime();
            LocalDateTime lastGpsSystemSavingTime = tracking.getGpsPoints().get(tracking.getGpsPoints().size()-1).getDeviceSavingSystemTime();

            result.durationStartAndGpsSystemStart = Duration.between(startTimestamp,firstGpsSystemSavingTime);
            result.durationEndAndGpsSystemEnd= Duration.between(endTimestamp,lastGpsSystemSavingTime);
        }else{
            result.durationStartAndGpsSystemStart=null;
            result.durationEndAndGpsSystemEnd=null;
        }


        List<Tuple<LocalDateTime,LocalDateTime>> noAccelerationTimespans = new ArrayList<>();
        LocalDateTime lastAcState = startTimestamp;
        Duration totalNoAcState = Duration.ofMillis(0);
        for(AcceleratorState acceleratorState :tracking.getAcceleratorStates()){
            AcceleratorState next = acceleratorState;
            Duration between = Duration.between(lastAcState, next.getTime());

            if(between.toMillis() > 1000){
                noAccelerationTimespans.add(new Tuple<>(lastAcState,next.getTime()));
                totalNoAcState = totalNoAcState.plus(between);
            }
            lastAcState = next.getTime();
        }

        Duration between = Duration.between(lastAcState, endTimestamp);
        if(between.toMillis() > 1000){
            noAccelerationTimespans.add(new Tuple<>(lastAcState,endTimestamp));
            totalNoAcState = totalNoAcState.plus(between);
        }

        result.totalNoAcStateData = totalNoAcState;
        double percentage =((double) totalNoAcState.toMillis()) / Duration.between(startTimestamp, endTimestamp).toMillis();
        result.totalNoAcStateDataPercentage =percentage;


        List<Tuple<LocalDateTime, LocalDateTime>> noGpsTimespans = analyzeGpsData(tracking);


        List<TrackingSegment> segments = tracking.getTrackingSegmentBags().get(0).getSegments();
        TrackingSegment lastTrackingSemgnet = null;
        for (TrackingSegment segment : segments) {
            LocalDateTime startTime = segment.getStartTime();

            int round =0;
            boolean dataAvailable = true;
            for (Tuple<LocalDateTime, LocalDateTime> noAccelerationTimespan : noAccelerationTimespans) {

                Duration durationNoDataBeginnAndSegmentStart = Duration.between(noAccelerationTimespan.getItem1(),startTime);
                Duration durationNoDataEndAndSegmentStart = Duration.between(startTime,noAccelerationTimespan.getItem2());

                if(durationNoDataBeginnAndSegmentStart.toMillis() >= 0 && durationNoDataEndAndSegmentStart.toMillis() >= 0 ){
                    result.noAccAtSegmentChange.add(startTime);
                    dataAvailable=false;
                }else if(Math.abs(durationNoDataBeginnAndSegmentStart.toMillis()) <= 1000*10 && round != 0){
                    result.noAccAtSegmentChange.add(startTime);
                    dataAvailable=false;
                }else if(Math.abs(durationNoDataEndAndSegmentStart.toMillis()) <= 1000*10){
                    result.noAccAtSegmentChange.add(startTime);
                    dataAvailable=false;
                }
                round ++;
            }
            if(dataAvailable) {

                TransportType transportType = lastTrackingSemgnet != null ? lastTrackingSemgnet.getTransportType() : null;
                SegmentChange segmentChange = new SegmentChange(segment.getStartTime(),transportType,segment.getTransportType());
                result.gpsSegmentChanges.add(segmentChange);
            }

            round =0;
            dataAvailable = true;
            for (Tuple<LocalDateTime, LocalDateTime> noGpsTimespan : noGpsTimespans) {
                Duration durationNoDataBeginnAndSegmentStart = Duration.between(noGpsTimespan.getItem1(),startTime);
                Duration durationNoDataEndAndSegmentStart = Duration.between(startTime,noGpsTimespan.getItem2());

                if(durationNoDataBeginnAndSegmentStart.toMillis() >= 0 && durationNoDataEndAndSegmentStart.toMillis() >= 0 ){
                    result.noGpsAtSegmentChange.add(startTime);
                    dataAvailable=false;
                }else if(Math.abs(durationNoDataBeginnAndSegmentStart.toMillis()) <= 1000*10 && round != 0){
                    result.noGpsAtSegmentChange.add(startTime);
                    dataAvailable=false;
                }else if(Math.abs(durationNoDataEndAndSegmentStart.toMillis()) <= 1000*10){
                    result.noGpsAtSegmentChange.add(startTime);
                    dataAvailable=false;
                }
                round++;
            }
            if(dataAvailable) {
                TransportType transportType = lastTrackingSemgnet != null ? lastTrackingSemgnet.getTransportType() : null;
                SegmentChange segmentChange = new SegmentChange(segment.getStartTime(),transportType,segment.getTransportType());
                result.acSegmentChanges.add(segmentChange);
            }

            lastTrackingSemgnet = segment;
        }


        return result;
    }

    private String getPhoneId(SimpleAllTracking tracking) {
        for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {

            if(trackingInfo.getInfoName().equals(Constants.PHONE_ID) ){
                return trackingInfo.getInfoValue();
            }
        }
    return null;
    }
    private String getPhoneType(SimpleAllTracking tracking) {
        for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {

            if(trackingInfo.getInfoName() .equals(Constants.PHONE_TYPE)){
                return trackingInfo.getInfoValue();
            }
        }
        return null;
    }

    private int getVersion(Tracking tracking){
        for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {

            if(trackingInfo.getInfoName().equals(Constants.FH_GPS_LOGGER_VERSION)){
                String infoValue = trackingInfo.getInfoValue();
                int version = Integer.parseInt(infoValue);
                return version;
            }
        }
        return -1;
    }


    private List<Tuple<LocalDateTime,LocalDateTime>> analyzeGpsData(SimpleAllTracking tracking){
        LocalDateTime startTimestamp = tracking.getStartTimestamp();
        LocalDateTime endTimestamp = tracking.getEndTimestamp();

        List<Tuple<LocalDateTime,LocalDateTime>> noGpsTimespans = new ArrayList<>();
        LocalDateTime lastAcState = startTimestamp;
        Duration totalNoGpsData = Duration.ofMillis(0);
        for(IExtendedGpsPoint gpsPoint :tracking.getGpsPoints()){
            IExtendedGpsPoint next = gpsPoint;
            Duration between = Duration.between(lastAcState, next.getSensorTime());

            if(between.toMillis() > 8000){
                noGpsTimespans.add(new Tuple<>(lastAcState,next.getSensorTime()));
                totalNoGpsData = totalNoGpsData.plus(between);
            }
            lastAcState = next.getSensorTime();
        }

        Duration between = Duration.between(lastAcState, endTimestamp);
        if(between.toMillis() > 1000){
            noGpsTimespans.add(new Tuple<>(lastAcState,endTimestamp));
            totalNoGpsData = totalNoGpsData.plus(between);
        }
        return noGpsTimespans;
    }



}
