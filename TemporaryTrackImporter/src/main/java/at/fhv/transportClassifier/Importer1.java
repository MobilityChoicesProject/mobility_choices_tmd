package at.fhv.transportClassifier;

import at.fhv.transportClassifier.proto1.InvalidFileException;
import at.fhv.transportClassifier.proto1.TimeUtil;
import at.fhv.transportClassifier.proto1.TrackingDataProtos;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedType;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
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
public class Importer1 implements FhGpsLoggerImporter {


        public static void main(String args[]){
//            File file = new File("D:\\Studium\\Master\\Masterarbeit\\fhgpsLoggerTracks\\1479652100733.gpsTrack");
            File file = new File("D:\\Studium\\Master\\Masterarbeit\\fhgpsLoggerTracks\\1486482333186.gpsTrack");
            File folder = new File("D:\\Studium\\Master\\Masterarbeit\\fhgpsLoggerTracks");
            File[] files = folder.listFiles();

            Importer1 loggerImporter = new Importer1();

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
            FileInputStream stream = new FileInputStream(file);
            TrackingDataProtos.TrackingPr trackingPr = TrackingDataProtos.TrackingPr.parseFrom(stream);

            Tracking tracking = createTracking(file,trackingPr);

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


    private Tracking createTracking(File file, TrackingDataProtos.TrackingPr trackingPr) throws InvalidFileException {
        SimpleTrackingBuilder trackingBuilder = new SimpleTrackingBuilder();
        String filename = file.getName();
        String phoneId = trackingPr.getPhoneId();
        String phoneType = trackingPr.getPhoneType();

        trackingBuilder.addTrackingInfo(Constants.ORIGIN,Constants.ORIGIN_FHGPSLOGGER);
        trackingBuilder.addTrackingInfo(Constants.PHONE_ID,phoneId);
        trackingBuilder.addTrackingInfo(Constants.PHONE_TYPE,phoneType);
        trackingBuilder.addTrackingInfo(Constants.FILENAME,filename);

        boolean firstVersion = isFirstVersion(trackingPr);
        if(firstVersion){
           trackingBuilder.addTrackingInfo(Constants.FH_GPS_LOGGER_VERSION,Constants.FH_GPS_LOGGER_VERSION_0);
        }else{
            trackingBuilder.addTrackingInfo(Constants.FH_GPS_LOGGER_VERSION,Constants.FH_GPS_LOGGER_VERSION_1);

            for (TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr accelerometerTrackingPointPr : trackingPr.getAccelerometerTrackingPointsList()) {
                long utcTimestamp = accelerometerTrackingPointPr.getTimestamp();
                LocalDateTime date = TimeUtil.convertToLocalDatetime(utcTimestamp);
                trackingBuilder.addAcceleratorState(date,accelerometerTrackingPointPr.getXAxis(),accelerometerTrackingPointPr.getYAxis(),accelerometerTrackingPointPr.getZAxis());
            }

            for (TrackingDataProtos.TrackingPr.DisplayOnOffTrackingPointPr displayOnOffTrackingPointPr : trackingPr.getDisplayOnOffTrackingPointsList()) {
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
        List<TrackingDataProtos.TrackingPr.GpsTrackingPointPr> gpsTrackingPointsList = trackingPr.getGpsTrackingPointsList();
        for (TrackingDataProtos.TrackingPr.GpsTrackingPointPr pointPr : gpsTrackingPointsList) {

            long utcTimestamp = pointPr.getTimestamp();
            LocalDateTime timestamp = TimeUtil.convertToLocalDatetime(utcTimestamp);
//            TimeUtil.convertToLocalDatetime(pointPr.get)
            LocalDateTime deviceSystemSavingTime= null;
            Double altitude = null;
            Double accuracy = (double) pointPr.getAccuracy();
            Double speed = null;
           trackingBuilder.addGpsPoint(pointPr.getLatitude(),pointPr.getLongitude(),altitude,accuracy,speed,timestamp,deviceSystemSavingTime);

            TransportType transportType = getTransportType(pointPr.getTransportType());
            if(transportType != null ){
                addSegment(lastTransportType,lastSegmentTimestamp,utcTimestamp,trackingBuilder);
                lastSegmentTimestamp = utcTimestamp;
                lastTransportType = transportType;
            }
        }
        if(gpsTrackingPointsList.size() == 0){
            return null;
        }
        TrackingDataProtos.TrackingPr.GpsTrackingPointPr lastGpsPoint = gpsTrackingPointsList.get(gpsTrackingPointsList.size() - 1);
        addSegment(lastTransportType,lastSegmentTimestamp,lastGpsPoint.getTimestamp(),trackingBuilder);

        LocalDateTime startTimestamp = TimeUtil.convertToLocalDatetime(trackingPr.getStartTime());
        LocalDateTime endTimestamp = TimeUtil.convertToLocalDatetime(trackingPr.getEndTime());
        trackingBuilder.setStartTimestamp(startTimestamp);




        trackingBuilder.setEndTimestamp(endTimestamp);
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

    private TransportType getTransportType(TrackingDataProtos.TrackingPr.TransportTypePr transportType ) throws InvalidFileException {
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



    private boolean isFirstVersion(TrackingDataProtos.TrackingPr trackingPr) throws InvalidFileException {

        List<TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr> accelerometerTrackingPointsList = trackingPr.getAccelerometerTrackingPointsList();
        int size = accelerometerTrackingPointsList.size();
        if(size < 10){
            throw new InvalidFileException("invalid file, doesnt contain more than 10 accelerator values");

        }
        if(trackingPr.getFileVersion() == 0){
            return true;
        }

        TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr trackingPointPr = accelerometerTrackingPointsList.get(0);
        TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr trackingPointPr1 = accelerometerTrackingPointsList.get(1);

        long timestamp = trackingPointPr.getTimestamp();
        long timestamp1 = trackingPointPr1.getTimestamp();

        long difference = timestamp1 - timestamp;

      return difference > 1000;

    }


    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean isThisVersion(File file) {
        return true;
    }

    @Override
    public Tracking createTracking(File file) throws IOException {
        return loadTracking(file);
    }
}
