package at.fhv.tmd.segmentClassification.classifier;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 21.05.2017.
 */
public class ClassifiedSegment {

 private LocalDateTime starttime;
 private LocalDateTime endTime;
 private List<Tuple<TransportType,Double>> probabilities;
 private List<IGpsPoint> coordinates;

  public LocalDateTime getStarttime() {
    return starttime;
  }

  public void setStarttime(LocalDateTime starttime) {
    this.starttime = starttime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public List<Tuple<TransportType, Double>> getProbabilities() {
    return probabilities;
  }

  public void setProbabilities(
      List<Tuple<TransportType, Double>> probabilities) {
    this.probabilities = probabilities;
  }

  public List<IGpsPoint> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<IGpsPoint> coordinates) {
    this.coordinates = coordinates;
  }
}
