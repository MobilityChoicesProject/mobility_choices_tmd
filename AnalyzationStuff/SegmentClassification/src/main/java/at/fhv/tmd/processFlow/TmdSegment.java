package at.fhv.tmd.processFlow;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 23.07.2017.
 */
public class TmdSegment {


  private List<TransportTypeProbability> transportTypeProbabilities;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private List<IGpsPoint> coordinates;

  public void setTransportTypeProbabilities(
      List<TransportTypeProbability> transportTypeProbabilities) {
    this.transportTypeProbabilities = transportTypeProbabilities;
  }

  public List<IGpsPoint> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<IGpsPoint> coordinates) {
    this.coordinates = coordinates;
  }

  public TmdSegment(List<TransportTypeProbability> transportTypeProbabilities, LocalDateTime startTime, LocalDateTime endTime) {
    this.transportTypeProbabilities = transportTypeProbabilities;
    this.startTime = startTime;
    this.endTime = endTime;
  }


  public List<TransportTypeProbability> getTransportTypeProbabilities() {
    return transportTypeProbabilities;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public TransportType getMostLikeliest() {
    TransportTypeProbability mostLikeliest = null;
    double probability = -1;
    for (TransportTypeProbability transportTypeProbability : transportTypeProbabilities) {
      if (transportTypeProbability.getProbability() > probability) {
        mostLikeliest = transportTypeProbability;
        probability = transportTypeProbability.getProbability();
      }
    }
    return mostLikeliest.getTransportType();
  }
}
