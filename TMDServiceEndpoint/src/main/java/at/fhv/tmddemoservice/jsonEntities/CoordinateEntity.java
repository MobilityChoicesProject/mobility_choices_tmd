package at.fhv.tmddemoservice.jsonEntities;

import java.time.LocalDateTime;

/**
 * Created by Johannes on 20.05.2017.
 */
public class CoordinateEntity {

    private double lat;
    private double lng;
    private String time;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
