package helper;

import at.fhv.tmd.common.IGpsPoint;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 22.06.2017.
 */
public class Section {

  private String name;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private List<? extends IGpsPoint> gpsPointList;


  public String getName() {
    return name;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public List<? extends IGpsPoint> getGpsPointList() {
    return gpsPointList;
  }


  public Section(String name, LocalDateTime startTime, LocalDateTime endTime,
      List<? extends IGpsPoint> gpsPointList) {
    this.name = name;
    this.startTime = startTime;
    this.endTime = endTime;
    this.gpsPointList = gpsPointList;
  }
}
