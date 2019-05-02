package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleTrackingSegment implements TrackingSegment {

    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected TransportType transportType;
    protected List<IExtendedGpsPoint> gpsPoints = null;
    protected List<IExtendedGpsPoint> allGpsPoints;
    protected BoundingBox boundingBox;

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }



    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public void setAllGpsPoints(List<IExtendedGpsPoint> allGpsPoints) {
        this.allGpsPoints = allGpsPoints;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }


    @Override
    public TransportType getTransportType() {
        return transportType;
    }

    @Override
    public List<IExtendedGpsPoint> getGpsPoints() {
        if(gpsPoints == null){
            initGpsPoints();
        }
        return gpsPoints;
    }

    @Override
    public BoundingBox getBoundingBox() {
        if(boundingBox == null){
            List<IExtendedGpsPoint> gpsPoints = getGpsPoints();
            boundingBox = new SimpleBoundingBox(gpsPoints);
        }
        return boundingBox;
    }

    protected void initGpsPoints() {
        gpsPoints = new ArrayList<>();
        for (IExtendedGpsPoint gpsPoint : allGpsPoints) {

            LocalDateTime gpsTime;
            if(gpsPoint.getDeviceSavingSystemTime() != null) {
                gpsTime = gpsPoint.getDeviceSavingSystemTime();
            }else {
                gpsTime = gpsPoint.getSensorTime();
            }

            boolean isAfter =gpsTime.isAfter(startTime);
            boolean isAt = gpsTime.isEqual(startTime);
            boolean minLimitValid = isAfter||isAt;
            isAt = gpsTime.isEqual(endTime);
            boolean maxLimitValid =gpsTime.isBefore(endTime)||isAt;



            if(minLimitValid && maxLimitValid){
                gpsPoints.add(gpsPoint);
            }
        }
        allGpsPoints = null;
    }

}
