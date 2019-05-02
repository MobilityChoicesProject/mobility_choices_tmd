package at.fhv.transportClassifier;

import static at.fhv.transportClassifier.proto1.TimeUtil.convertToLocalDatetimeOrNull;

import at.fhv.transportClassifier.proto1.InvalidFileException;
import at.fhv.transportClassifier.proto1.TimeUtil;
import at.fhv.transportClassifier.proto2.TrackingDataProtos;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedType;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 14.02.2017.
 */
public class ImporterV2 implements FhGpsLoggerImporter {


        public static void main(String args[]){
//            File file = new File("D:\\Studium\\Master\\Masterarbeit\\fhgpsLoggerTracks\\1479652100733.gpsTrack");
            File file = new File("D:\\Studium\\Master\\Masterarbeit\\fhgpsLoggerTracks\\1486482333186.gpsTrack");
            File folder = new File("D:\\Studium\\Master\\Masterarbeit\\fhgpsLoggerTracks");
            File[] files = folder.listFiles();

            ImporterV2 loggerImporter = new ImporterV2();

            List<Tracking> trackings = new ArrayList<>();
            for (File file1 : files) {
                trackings.add(loggerImporter.loadTracking(file1));
            }
            Tracking tracking = trackings.get(1);
            List<IExtendedGpsPoint> gpsPoints = tracking.getTrackingSegmentBags().get(0).getSegments().get(0).getGpsPoints();
            Tracking tracking1 = trackings.get(69);
            List<IExtendedGpsPoint> gpsPoints1 = tracking1.getTrackingSegmentBags().get(0).getSegments().get(0).getGpsPoints();

            BoundingBox boundingBox = tracking.getTrackingSegmentBags().get(0).getSegments().get(0).getBoundingBox();
            BoundingBox boundingBox1 = tracking.getTrackingSegmentBags().get(0).getSegments().get(1).getBoundingBox();

            int segmentCounter = 0;
            int bikeCounter = 0;
            int carCounter = 0;
            int busCounter = 0;
            int otherCounter = 0;
            int walkingCounter = 0;
            int trainCounter = 0;
            for (Tracking tracking2 : trackings) {

//                if(isVersionZero(tracking2)) {
//                    continue;
//                }
                    for (TrackingSegment trackingSegment : tracking2.getTrackingSegmentBags().get(0).getSegments()) {
                        segmentCounter++;
                        if(trackingSegment.getTransportType() == TransportType.BIKE){
                            bikeCounter++;
                        }else if(trackingSegment.getTransportType() == TransportType.BUS){
                            busCounter++;
                        }else if(trackingSegment.getTransportType() == TransportType.CAR){
                            carCounter++;
                        }else if(trackingSegment.getTransportType() == TransportType.WALK){
                            walkingCounter++;
                        }else if(trackingSegment.getTransportType() == TransportType.TRAIN){
                            trainCounter++;
                        }else if(trackingSegment.getTransportType() == TransportType.OTHER){
                            otherCounter++;
                        }
                    }


            }

            Tracking tracking3 = trackings.get(221);
        }

        private static boolean isVersionZero(Tracking tracking){
            for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {
                if(trackingInfo.getInfoName().equals(Constants.FH_GPS_LOGGER_VERSION)){
                    return trackingInfo.getInfoValue().equals(Constants.FH_GPS_LOGGER_VERSION_0) ;
                }

            }
            throw new RuntimeException("should not happen");

        }

    public Tracking loadTracking(File file){
        try {
            if(isThirdVersion(file)){
                return createTracking(file);
            }

            FileInputStream stream = new FileInputStream(file);
            at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr trackingPr = at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.parseFrom(stream);

            Tracking tracking = createTrackingV1(file,trackingPr);

            stream.close();

            return tracking;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFileException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Tracking createTrackingV1(File file, at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr trackingPr) throws InvalidFileException {
        SimpleTrackingBuilder trackingBuilder = new SimpleTrackingBuilder();
        String filename = file.getName();

        long startTime = trackingPr.getStartTime();
        long endTime = trackingPr.getEndTime();
        LocalDateTime startDateTime = TimeUtil.convertToLocalDatetimeOrNull(startTime);
        LocalDateTime endDateTime = TimeUtil.convertToLocalDatetimeOrNull(endTime);

        trackingBuilder.setStartTimestamp(startDateTime);
        trackingBuilder.setEndTimestamp(endDateTime);

        trackingBuilder.addTrackingInfo(Constants.FILENAME,filename);


        boolean firstVersion = isFirstVersion(trackingPr);
        if(firstVersion){
           trackingBuilder.addTrackingInfo(Constants.FH_GPS_LOGGER_VERSION,Constants.FH_GPS_LOGGER_VERSION_0);
        }else{
            trackingBuilder.addTrackingInfo(Constants.FH_GPS_LOGGER_VERSION,Constants.FH_GPS_LOGGER_VERSION_1);

            for (at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr accelerometerTrackingPointPr : trackingPr.getAccelerometerTrackingPointsList()) {
                long utcTimestamp = accelerometerTrackingPointPr.getTimestamp();
                LocalDateTime date = TimeUtil.convertToLocalDatetime(utcTimestamp);
                trackingBuilder.addAcceleratorState(date,accelerometerTrackingPointPr.getXAxis(),accelerometerTrackingPointPr.getYAxis(),accelerometerTrackingPointPr.getZAxis());
            }

            for (at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.DisplayOnOffTrackingPointPr displayOnOffTrackingPointPr : trackingPr.getDisplayOnOffTrackingPointsList()) {
                long utcTimestamp = displayOnOffTrackingPointPr.getTimestamp();
                LocalDateTime date = TimeUtil.convertToLocalDatetime(utcTimestamp);

                DisplayStateChangedType displayStateChangedType;
                if (displayOnOffTrackingPointPr.getTurnedOn()) {
                    displayStateChangedType = DisplayStateChangedType.TURNED_ON;
                }else{
                    displayStateChangedType = DisplayStateChangedType.TURNED_OFF;
                }
                trackingBuilder.addDisplayStateChangedEvent(date,displayStateChangedType);
            }
        }

        Long lastSegmentTimestamp = null;
        TransportType lastTransportType = null;
        List<at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.GpsTrackingPointPr> gpsTrackingPointsList = trackingPr.getGpsTrackingPointsList();

        boolean accuracyNull = true;
        boolean altitudeNull = true;
        boolean speedNull = true;
        for(at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.GpsTrackingPointPr pointPr : gpsTrackingPointsList){
            if (pointPr.getAccuracy() != 0) {
                accuracyNull = false;
            }
            if(pointPr.getAltitude() != 0){
                altitudeNull = false;
            }
            if(pointPr.getSpeed() != 0){
                speedNull = false;
            }
        }


        Double speed,accuracy,altitude;
        for (at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.GpsTrackingPointPr pointPr : gpsTrackingPointsList) {

            if(speedNull){
                speed= null;
            }else{
                speed = (double)pointPr.getSpeed();
            }
            if(accuracyNull){
                accuracy= null;
            }else{
                accuracy = (double)pointPr.getAccuracy();
            }
            if(altitudeNull){
                altitude= null;
            }else{
                altitude = pointPr.getAltitude();
            }

            long utcTimestamp = pointPr.getTimestamp();
            LocalDateTime sensorTime = TimeUtil.convertToLocalDatetime(utcTimestamp);
            trackingBuilder.addGpsPoint(pointPr.getLatitude(),pointPr.getLongitude(),altitude, accuracy,speed,sensorTime,null);

            TransportType transportType = getTransportType(pointPr.getTransportType());
            if(transportType != null ){
                addSegment(lastTransportType,lastSegmentTimestamp,utcTimestamp,trackingBuilder);
                lastSegmentTimestamp = utcTimestamp;
                lastTransportType = transportType;
            }
        }
        at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.GpsTrackingPointPr lastGpsPoint = gpsTrackingPointsList.get(gpsTrackingPointsList.size() - 1);
        addSegment(lastTransportType,lastSegmentTimestamp,lastGpsPoint.getTimestamp(),trackingBuilder);

        return trackingBuilder.build();
    }




    private void addSegment(TransportType lastTransportType, Long lastSegmentTimestamp, Long timestamp, SimpleTrackingBuilder trackingBuilder) throws InvalidFileException {
        if(lastTransportType == null){
            return;
        }
        if(lastSegmentTimestamp.equals(timestamp) ){
            return ;
        }

        LocalDateTime startTimetstamp = TimeUtil.convertToLocalDatetime(lastSegmentTimestamp);
        LocalDateTime endTimetstamp = TimeUtil.convertToLocalDatetime(timestamp);

        trackingBuilder.addTrackingSegment(startTimetstamp,endTimetstamp,lastTransportType,0);

    }



    private TransportType getTransportType(at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.TransportTypePr transportType ) throws InvalidFileException {
        switch (transportType) {
            case BIKE:{
                return TransportType.BIKE;

            }
            case BUS:{

                return TransportType.BUS;
            }
            case CAR:{

                return TransportType.CAR;
            }
            case OTHER:{

                return TransportType.OTHER;
            }
            case TRAIN:{

                return TransportType.TRAIN;
            }
            case WALKING:{

                return TransportType.WALK;
            }
            case UNKNOWN:{
                //do nothing
                return null;
            }default:{
                throw new InvalidFileException("File contains illegal transportmode: {"+transportType.name()+"}");
            }
        }

    }



    private boolean isFirstVersion(at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr trackingPr) throws InvalidFileException {

        List<at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr> accelerometerTrackingPointsList = trackingPr.getAccelerometerTrackingPointsList();
        int size = accelerometerTrackingPointsList.size();
        if(size < 10){
            throw new InvalidFileException("invalid file, doesnt contain more than 10 accelerator values");

        }
        if(trackingPr.getFileVersion() == 0){
            return true;
        }

        at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr trackingPointPr = accelerometerTrackingPointsList.get(0);
        at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr trackingPointPr1 = accelerometerTrackingPointsList.get(1);

        long timestamp = trackingPointPr.getTimestamp();
        long timestamp1 = trackingPointPr1.getTimestamp();

        long difference = timestamp1 - timestamp;

      return difference > 1000;

    }


    private boolean isThirdVersion(File file){
        return file.getName().contains("v2");
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isThisVersion(File file) {
        return file.getName().contains("_v2");
    }


    @Override
    public Tracking createTracking(File file) throws IOException {

        SimpleTrackingBuilder trackingBuilder = new SimpleTrackingBuilder();

        BufferedInputStream bufferedInputStream = null;
        try {

            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

            TrackingDataProtos.TrackingPr trackingPr = TrackingDataProtos.TrackingPr.parseDelimitedFrom(bufferedInputStream);

            int fileVersion = trackingPr.getFileVersion();

            if(fileVersion != 2){
                return null;
            }

            int gpsPoints = trackingPr.getGpsPoints();
            int accelerationPoints = trackingPr.getAccelerationPoints();
            int displayOnofTrackingPoints = trackingPr.getDisplayOnofTrackingPoints();


            LocalDateTime startTime = convertToLocalDatetimeOrNull(trackingPr.getStartTime());
            LocalDateTime endTime = convertToLocalDatetimeOrNull(trackingPr.getEndTime());

            String phoneId = trackingPr.getPhoneId();
            String phoneType = trackingPr.getPhoneType();
            String name = file.getName();

            trackingBuilder.addTrackingInfo(Constants.ORIGIN,Constants.ORIGIN_FHGPSLOGGER);
            trackingBuilder.addTrackingInfo(Constants.FH_GPS_LOGGER_VERSION,fileVersion+"");
            trackingBuilder.addTrackingInfo(Constants.FILENAME,name);
            trackingBuilder.addTrackingInfo(Constants.PHONE_ID,phoneId);
            trackingBuilder.addTrackingInfo(Constants.PHONE_TYPE,phoneType);


            trackingBuilder.setStartTimestamp(startTime);
            trackingBuilder.setEndTimestamp(endTime);

            TrackingDataProtos.TrackingPr.GpsTrackingPointPr lastGpsTrackingPoint = null;
            TrackingDataProtos.TrackingPr.GpsTrackingPointPr lastLoadedGpsTrackingPointPr = null;
            for(int i = 0; i < gpsPoints;i++){

                TrackingDataProtos.TrackingPr.GpsTrackingPointPr gpsTrackingPointPr = TrackingDataProtos.TrackingPr.GpsTrackingPointPr.parseDelimitedFrom(bufferedInputStream);
                lastLoadedGpsTrackingPointPr = gpsTrackingPointPr;
                if(gpsTrackingPointPr.getSystemSavingTimestamp() == 0)
                {
                    return null;
                }
                if(gpsTrackingPointPr.getTransportType() != TrackingDataProtos.TrackingPr.TransportTypePr.UNKNOWN &&gpsTrackingPointPr.getTransportType() != TrackingDataProtos.TrackingPr.TransportTypePr.UNRECOGNIZED){
                    if(lastGpsTrackingPoint != null){
                        addSegment(trackingBuilder, lastGpsTrackingPoint, gpsTrackingPointPr);
                    }

                    lastGpsTrackingPoint =gpsTrackingPointPr;
                }



                LocalDateTime sensorTime = convertToLocalDatetimeOrNull(gpsTrackingPointPr.getTimestamp());
                LocalDateTime systemSavingTime = convertToLocalDatetimeOrNull(gpsTrackingPointPr.getSystemSavingTimestamp());
                double accuracy = gpsTrackingPointPr.getAccuracy();
                double altitude = gpsTrackingPointPr.getAltitude();
                double speed = gpsTrackingPointPr.getSpeed();

                trackingBuilder.addGpsPoint(gpsTrackingPointPr.getLatitude(),gpsTrackingPointPr.getLongitude(),null,accuracy,null,sensorTime,systemSavingTime);
            }

            if(lastGpsTrackingPoint != lastLoadedGpsTrackingPointPr){
                addSegment(trackingBuilder, lastGpsTrackingPoint, lastLoadedGpsTrackingPointPr);
            }


            for(int i = 0; i < accelerationPoints;i++){

                TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr accelerometerTrackingPointPr = TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr.parseDelimitedFrom(bufferedInputStream);
                long timestamp = accelerometerTrackingPointPr.getTimestamp();
                LocalDateTime localDateTime = TimeUtil.convertToLocalDatetimeOrNull(timestamp);
                trackingBuilder.addAcceleratorState(localDateTime,accelerometerTrackingPointPr.getXAxis(),accelerometerTrackingPointPr.getYAxis(),accelerometerTrackingPointPr.getZAxis());

            }

            for(int i = 0; i < displayOnofTrackingPoints;i++){

                TrackingDataProtos.TrackingPr.DisplayOnOffTrackingPointPr displayOnOffTrackingPointPr = TrackingDataProtos.TrackingPr.DisplayOnOffTrackingPointPr.parseDelimitedFrom(bufferedInputStream);
                long timestamp = displayOnOffTrackingPointPr.getTimestamp();
                LocalDateTime localDateTime = TimeUtil.convertToLocalDatetimeOrNull(timestamp);
                DisplayStateChangedType displayStateChangedType = displayOnOffTrackingPointPr.getTurnedOn() ? DisplayStateChangedType.TURNED_ON : DisplayStateChangedType.TURNED_OFF;
                trackingBuilder.addDisplayStateChangedEvent(localDateTime,displayStateChangedType);

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (InvalidFileException e) {
            e.printStackTrace();
        } finally {
            if(bufferedInputStream != null ){
              bufferedInputStream.close();
            }
        }


        return trackingBuilder.build();


    }

    private void addSegment(SimpleTrackingBuilder trackingBuilder, TrackingDataProtos.TrackingPr.GpsTrackingPointPr lastGpsTrackingPoint, TrackingDataProtos.TrackingPr.GpsTrackingPointPr lastLoadedGpsTrackingPointPr) throws InvalidFileException {
        int enumValue = lastGpsTrackingPoint.getTransportType().getNumber();
        at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.TransportTypePr transportTypePr = at.fhv.transportClassifier.proto1.TrackingDataProtos.TrackingPr.TransportTypePr.forNumber(enumValue);
        TransportType transportType = getTransportType(transportTypePr);
        long systemSavingTimestamp = lastGpsTrackingPoint.getSystemSavingTimestamp();
        LocalDateTime startDateTime = TimeUtil.convertToLocalDatetimeOrNull(systemSavingTimestamp);
        LocalDateTime endDateTime = TimeUtil.convertToLocalDatetimeOrNull(lastLoadedGpsTrackingPointPr.getSystemSavingTimestamp());
        trackingBuilder.addTrackingSegment(startDateTime,endDateTime,transportType,0);
    }


}
