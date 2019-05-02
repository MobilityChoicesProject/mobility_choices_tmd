package at.fhv.tmddemoservice.jsonEntities;

import java.sql.Date;

public class LocationCoordinateEntity {
    private CoordinateEntity coordinates;
    private long timestamp;
    private String name;

    public CoordinateEntity getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinateEntity coordinates) {
        this.coordinates = coordinates;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
