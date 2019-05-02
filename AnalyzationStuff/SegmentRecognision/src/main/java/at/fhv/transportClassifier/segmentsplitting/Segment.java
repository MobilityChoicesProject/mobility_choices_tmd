package at.fhv.transportClassifier.segmentsplitting;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 03.05.2017.
 */
public class Segment {


  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private SegmentPreType preType;
  private String origin;





  public void setPreType(SegmentPreType preType) {
    this.preType = preType;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public Duration getDuration(){
    return Duration.between(startTime,endTime);
  }

  public SegmentPreType getPreType() {
    return preType;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getOrigin() {
      return origin;
  }
}


