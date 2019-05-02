package at.fhv.transportClassifier;

import static at.fhv.transportClassifier.proto1.TimeUtil.convertToLocalDatetimeOrNull;

import at.fhv.transportClassifier.proto1.InvalidFileException;
import at.fhv.transportClassifier.proto1.TimeUtil;
import at.fhv.transportClassifier.proto4.TrackingDataProtos;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedType;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 11.03.2017.
 */
public class ImporterV4 implements FhGpsLoggerImporter {

    @Override
    public int getPriority(){
        return 4;
    }

    @Override
    public boolean isThisVersion(File file){
        return file.getName().contains("_v4");
    }



    @Override
    public Tracking createTracking(File file) throws IOException {

        SimpleTrackingBuilder trackingBuilder = new SimpleTrackingBuilder();

        BufferedInputStream bufferedInputStream = null;
        try {

            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

            TrackingDataProtos.TrackingPr.FileVersion fileVersionProto = TrackingDataProtos.TrackingPr.FileVersion.parseDelimitedFrom(bufferedInputStream);
            int fileVersion = fileVersionProto.getFileVersion();

            TrackingDataProtos.TrackingPr trackingPr = TrackingDataProtos.TrackingPr.parseDelimitedFrom(bufferedInputStream);

            if(fileVersion != 4){
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

                at.fhv.transportClassifier.proto2.TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr accelerometerTrackingPointPr = at.fhv.transportClassifier.proto2.TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr.parseDelimitedFrom(bufferedInputStream);
                long timestamp = accelerometerTrackingPointPr.getTimestamp();
                LocalDateTime localDateTime = TimeUtil.convertToLocalDatetimeOrNull(timestamp);
                trackingBuilder.addAcceleratorState(localDateTime,accelerometerTrackingPointPr.getXAxis(),accelerometerTrackingPointPr.getYAxis(),accelerometerTrackingPointPr.getZAxis());

            }

            for(int i = 0; i < displayOnofTrackingPoints;i++){

                at.fhv.transportClassifier.proto2.TrackingDataProtos.TrackingPr.DisplayOnOffTrackingPointPr displayOnOffTrackingPointPr = at.fhv.transportClassifier.proto2.TrackingDataProtos.TrackingPr.DisplayOnOffTrackingPointPr.parseDelimitedFrom(bufferedInputStream);
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

    private void addSegment(SimpleTrackingBuilder trackingBuilder, TrackingDataProtos.TrackingPr.GpsTrackingPointPr startGpsPoint,TrackingDataProtos.TrackingPr.GpsTrackingPointPr endGpsPoint) throws InvalidFileException {
        int enumValue = startGpsPoint.getTransportType().getNumber();
        TrackingDataProtos.TrackingPr.TransportTypePr transportTypePr = TrackingDataProtos.TrackingPr.TransportTypePr.forNumber(enumValue);
        TransportType transportType = getTransportType(transportTypePr);
        long systemSavingTimestamp = startGpsPoint.getSystemSavingTimestamp();
        LocalDateTime startDateTime = TimeUtil.convertToLocalDatetimeOrNull(systemSavingTimestamp);
        LocalDateTime endDateTime = TimeUtil.convertToLocalDatetimeOrNull(endGpsPoint.getSystemSavingTimestamp());
        trackingBuilder.addTrackingSegment(startDateTime,endDateTime,transportType,0);
    }


    private TransportType getTransportType(at.fhv.transportClassifier.proto4.TrackingDataProtos.TrackingPr.TransportTypePr transportType ) throws InvalidFileException {
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




}
