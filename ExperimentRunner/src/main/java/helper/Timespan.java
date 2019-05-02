package helper;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 17.07.2017.
 */
public class Timespan {

  private LocalDateTime startTimestamp;
  private LocalDateTime endTimestamp;

  public Timespan(LocalDateTime startTime, LocalDateTime endTime) {
    if(!startTime.isBefore(endTime)){
      throw new IllegalArgumentException("starttime has to be before endtime");
    }
    this.startTimestamp= startTime;
    this.endTimestamp = endTime;
  }

  public Duration getOverlappingDuration(Timespan timespan){

    if(timespan.endTimestamp.isBefore(startTimestamp)){
      return Duration.ofSeconds(0);
    }else if(timespan.startTimestamp.isAfter(endTimestamp)){
      return Duration.ofSeconds(0);
    }

    LocalDateTime minTime = timespan.startTimestamp;
    if (timespan.startTimestamp.isBefore(startTimestamp)) {
      minTime = startTimestamp;
    }
    LocalDateTime maxTime = timespan.endTimestamp;
    if(timespan.endTimestamp.isAfter(endTimestamp)){
      maxTime = endTimestamp;
    }

    Duration overlappping = Duration.between(minTime, maxTime);
    return overlappping;
  }

  public double getOverlappingAmount(Timespan timespan){
    if(timespan.endTimestamp.isBefore(startTimestamp)){
      return 0;
    }else if(timespan.startTimestamp.isAfter(endTimestamp)){
      return 0;
    }

    Duration full = Duration.between(startTimestamp,endTimestamp);
    Duration overlapping = getOverlappingDuration(timespan);

    double fraction = (overlapping.toMillis()/(1.0*full.toMillis()));
    return fraction;

  }

  public LocalDateTime getStartTimestamp() {
    return startTimestamp;
  }

  public LocalDateTime getEndTimestamp() {
    return endTimestamp;
  }

  public Duration getDuration(){
    return Duration.between(startTimestamp,endTimestamp);
  }

  public boolean checkIfArgumentIsBetween(LocalDateTime timeOfMiddlePoint) {

    boolean startOk =
        startTimestamp.isEqual(timeOfMiddlePoint) || startTimestamp.isBefore(timeOfMiddlePoint);
    boolean endOk = endTimestamp.isEqual(timeOfMiddlePoint)|| endTimestamp.isAfter(timeOfMiddlePoint);
    return startOk && endOk;

  }
}
