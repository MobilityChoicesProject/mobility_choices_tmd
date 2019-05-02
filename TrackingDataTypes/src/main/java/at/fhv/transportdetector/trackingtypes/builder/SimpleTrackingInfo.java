package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.TrackingInfo;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleTrackingInfo implements TrackingInfo {
    private String infoValue;
    private String infoName;

    public SimpleTrackingInfo(String infoName, String infoValue) {
        this.infoValue = infoValue;
        this.infoName = infoName;
    }

    public SimpleTrackingInfo() {
    }

    public void setInfoValue(String infoValue) {
        this.infoValue = infoValue;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    @Override
    public String getInfoValue() {
        return infoValue;
    }

    @Override
    public String getInfoName() {
        return infoName;
    }
}
