package at.fhv.tmddemoservice.jsonEntities;

import java.util.List;

/**
 * Created by Johannes on 20.05.2017.
 */
public class ResponseEntity {

    private String Status;
    private String UserId;
    private long requestId;
    private String tmdVersion;
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

    public String getTmdVersion() {
        return tmdVersion;
    }

    public void setTmdVersion(String tmdVersion) {
        this.tmdVersion = tmdVersion;
    }

    private List<SegmentEntity> segments;

    public List<SegmentEntity> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentEntity> segments) {
        this.segments = segments;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
