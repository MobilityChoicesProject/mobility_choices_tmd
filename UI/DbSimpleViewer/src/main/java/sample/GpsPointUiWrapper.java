package sample;

import at.fhv.tmd.common.Speed;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 02.03.2017.
 */
public class GpsPointUiWrapper {

    private Speed speed;

    public static List<GpsPointUiWrapper> generateList(TrackingSegmentBag trackingSegmentBag,
        LocalDateTime startTimestamp) {

        ArrayList<GpsPointUiWrapper> gpsPointUiWrappers = new ArrayList<>();
        int index = 0;
        boolean evenColor = false;
        IExtendedGpsPoint lastGpsPoint = null;
        for (TrackingSegment trackingSegment : trackingSegmentBag.getSegments()) {
            evenColor = !evenColor;

            for (IExtendedGpsPoint point : trackingSegment.getGpsPoints()) {
                GpsPointUiWrapper gpsPointUiWrapper = new GpsPointUiWrapper(point);

                if (lastGpsPoint == null) {
                    lastGpsPoint = point;
                    gpsPointUiWrapper.setSpeed(new Speed(0));

                } else {

                    Speed speed = CoordinateUtil.calcSpeedBetween1(lastGpsPoint, point);
                    gpsPointUiWrapper.setSpeed(speed);

                }
                gpsPointUiWrapper.setIndex(index++);
                gpsPointUiWrapper.setEvenColor(evenColor);
                gpsPointUiWrapper.setTransportType(trackingSegment.getTransportType());
                gpsPointUiWrapper.setTrackingSegment(trackingSegment);
                gpsPointUiWrapper.setStartTimestamp(startTimestamp);
                gpsPointUiWrappers.add(gpsPointUiWrapper);
                lastGpsPoint = point;
            }
        }
        return gpsPointUiWrappers;
    }

    public static List<GpsPointUiWrapper> generateSmoothedList(TrackingSegmentBag trackingSegmentBag,
        LocalDateTime startTimestamp) {

        ArrayList<GpsPointUiWrapper> gpsPointUiWrappers = new ArrayList<>();
        int index = 0;
        boolean evenColor = false;
        IExtendedGpsPoint lastGpsPoint = null;
        for (TrackingSegment trackingSegment : trackingSegmentBag.getSegments()) {
            evenColor = !evenColor;


//            List<GpsPoint> calc = kernelSmother.calc(trackingSegment.getGpsPoints());
            List<IExtendedGpsPoint> calc = null;


            for (IExtendedGpsPoint point : calc) {
                GpsPointUiWrapper gpsPointUiWrapper = new GpsPointUiWrapper(point);

                if (lastGpsPoint == null) {
                    lastGpsPoint = point;
                    gpsPointUiWrapper.setSpeed(new Speed(0));

                } else {

                    Speed speed = CoordinateUtil.calcSpeedBetween1(lastGpsPoint, point);
                    gpsPointUiWrapper.setSpeed(speed);

                }
                gpsPointUiWrapper.setIndex(index++);
                gpsPointUiWrapper.setEvenColor(evenColor);
                gpsPointUiWrapper.setTransportType(trackingSegment.getTransportType());
                gpsPointUiWrapper.setTrackingSegment(trackingSegment);
                gpsPointUiWrapper.setStartTimestamp(startTimestamp);
                gpsPointUiWrappers.add(gpsPointUiWrapper);
                lastGpsPoint = point;
            }
        }
        return gpsPointUiWrappers;
    }




    private String newTransportType;

    private LocalDateTime startTimestamp;
    private IExtendedGpsPoint gpsPoint;
    private boolean evenColor;
    private int index;
    private TransportType transportType;
    private TrackingSegment trackingSegment;

    public String getNewTransportType() {
        return newTransportType;
    }

    public void setNewTransportType(String newTransportType) {
        this.newTransportType = newTransportType;
    }

    public TrackingSegment getTrackingSegment() {
        return trackingSegment;
    }

    public void setTrackingSegment(TrackingSegment trackingSegment) {
        this.trackingSegment = trackingSegment;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public GpsPointUiWrapper(IExtendedGpsPoint point){
        this.gpsPoint = point;
    }

    public IExtendedGpsPoint getGpsPoint() {
        return gpsPoint;
    }

    public void setGpsPoint(IExtendedGpsPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }

    public boolean isEvenColor() {
        return evenColor;
    }

    public void setEvenColor(boolean evenColor) {
        this.evenColor = evenColor;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(LocalDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Duration durationSinceStartAndDeviceSystemTime(){
        if(gpsPoint.getDeviceSavingSystemTime()!= null)
        return Duration.between(startTimestamp,gpsPoint.getDeviceSavingSystemTime());
        return Duration.between(startTimestamp,gpsPoint.getSensorTime());

    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public Speed getSpeed() {
        return speed;
    }
}
