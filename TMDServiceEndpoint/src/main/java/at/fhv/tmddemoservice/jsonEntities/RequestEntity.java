package at.fhv.tmddemoservice.jsonEntities;

import java.util.List;

/**
 * Created by Johannes on 20.05.2017.
 */
public class RequestEntity {

    private String UserId;

    private List<GpsPointEntity> trajectory;

    private String pushToken;
    private String accessToken;
    private String date;

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public List<GpsPointEntity> getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(List<GpsPointEntity> trajectory) {
        this.trajectory = trajectory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
