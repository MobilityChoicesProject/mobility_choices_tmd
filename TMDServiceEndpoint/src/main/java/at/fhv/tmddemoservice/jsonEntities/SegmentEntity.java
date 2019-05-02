package at.fhv.tmddemoservice.jsonEntities;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 20.05.2017.
 */
public class SegmentEntity {

    private String startTime;
    private String endtime;
    private List<ProbabilityEntity> probabilities;
    private List<CoordinateEntity> coordinates = new LinkedList<>();
    private double distance;
    private boolean endpoint;
    private boolean waypoint;


    private String transportMode;
    private LocationCoordinateEntity start;
    private LocationCoordinateEntity end;
    private double duration;

    public List<CoordinateEntity> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinateEntity> coordinates) {
        this.coordinates = coordinates;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public List<ProbabilityEntity> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<ProbabilityEntity> probabilities) {
        this.probabilities = probabilities;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ProbabilityEntity getMostLikliestTransportType() {
        probabilities.sort(Comparator.comparing(ProbabilityEntity::getProbability).reversed());
        return probabilities.get(0);
    }


    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public LocationCoordinateEntity getStart() {
        return start;
    }

    public void setStart(LocationCoordinateEntity start) {
        this.start = start;
    }

    public LocationCoordinateEntity getEnd() {
        return end;
    }

    public void setEnd(LocationCoordinateEntity end) {
        this.end = end;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public boolean isEndpoint() {
        return endpoint;
    }

    public void setEndpoint(boolean endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isWaypoint() {
        return waypoint;
    }

    public void setWaypoint(boolean waypoint) {
        this.waypoint = waypoint;
    }
}
