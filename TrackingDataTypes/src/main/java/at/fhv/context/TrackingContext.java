package at.fhv.context;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by Johannes on 19.07.2017.
 */
public class TrackingContext implements Serializable {



  private long trackingId;
  private Set<String> completedStages= new HashSet<>();

  public static final String RAW_GPS_INPUT="raw_gps_input";
  public static final String COORDINATE_INTERPOLATOR ="coordinateInterpolator";
  public static final String FILTERED_GPS_INPUT ="filtered_gps_input";

  private HashMap<String,Serializable> data = new HashMap<>();

  private List<SegmentContext> segmentContextList = new ArrayList<>();

  private LocalDateTime trackingStartTime;
  private LocalDateTime trackingEndTime;

  public List<SegmentContext> getSegmentContextList() {
    return segmentContextList;
  }

  public boolean hasCompletedStage(String stage){
    return completedStages.contains(stage);
  }

  public void addCompletedStage(String stage){
    completedStages.add(stage);
  }

  public void addData(String dataName,Serializable data){
    this.data.put(dataName,data);
  }

  public boolean hasData(String dataName){
    return data.get(dataName) != null;
  }

  public <T> T getData(String dataName){
    Object o = data.get(dataName);
    T tReturn;
    try{
      tReturn = (T)o;
    }catch (Exception ex){
      throw new NoSuchElementException("No element for dataName:'"+dataName+"' and the generic type");
    }
    return tReturn;
  }

  public long getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(long trackingId) {
    this.trackingId = trackingId;
  }

  public LocalDateTime getTrackingStartTime() {
    return trackingStartTime;
  }

  public void setTrackingStartTime(LocalDateTime trackingStartTime) {
    this.trackingStartTime = trackingStartTime;
  }

  public LocalDateTime getTrackingEndTime() {
    return trackingEndTime;
  }

  public void setTrackingEndTime(LocalDateTime trackingEndTime) {
    this.trackingEndTime = trackingEndTime;
  }
}
