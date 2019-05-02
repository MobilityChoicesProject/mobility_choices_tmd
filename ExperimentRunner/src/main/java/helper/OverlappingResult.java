package helper;

import at.fhv.tmd.common.Distance;
import java.time.Duration;

/**
 * Created by Johannes on 22.06.2017.
 */
public class OverlappingResult {

  private double relativeDistanceOverlapping;
  private double relativeDurationOveralapping;
  private Distance distanceOverlapping;
  private Duration durationOveralapping;
  private Distance totalDistance;
  private Duration totalDuration;


  public double getRelativeDistanceOverlapping() {
    return distanceOverlapping.getKm()/totalDistance.getKm();
  }

  public double getRelativeDurationOveralapping() {
    return durationOveralapping.toMillis()/(totalDuration.toMillis()*1.0);
  }

  public Distance getDistanceOverlapping() {
    return distanceOverlapping;
  }

  public Duration getDurationOveralapping() {
    return durationOveralapping;
  }

  public Distance getTotalDistance() {
    return totalDistance;
  }

  public Duration getTotalDuration() {
    return totalDuration;
  }

  public OverlappingResult(Distance distanceOverlapping, Duration durationOveralapping,
      Distance totalDistance, Duration totalDuration) {
    this.distanceOverlapping = distanceOverlapping;
    this.durationOveralapping = durationOveralapping;
    this.totalDistance = totalDistance;
    this.totalDuration = totalDuration;
  }
}
