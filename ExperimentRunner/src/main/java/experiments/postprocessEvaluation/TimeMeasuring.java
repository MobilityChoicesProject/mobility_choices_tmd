package experiments.postprocessEvaluation;

/**
 * Created by Johannes on 13.08.2017.
 */
public class TimeMeasuring {

  private long durionBeforePostProcessing;
  private long durationAfterPostProcessing;
  private int numberOfPoints;
  private long id;

  public TimeMeasuring(Long id, long durionBeforePostProcessing,
      long durationAfterPostProcessing,
      int numberOfPoints) {
    this.durionBeforePostProcessing = durionBeforePostProcessing;
    this.durationAfterPostProcessing = durationAfterPostProcessing;
    this.numberOfPoints = numberOfPoints;
    this.id =id;
  }


  public long getId() {
    return id;
  }

  public long getDurionBeforePostProcessing() {
    return durionBeforePostProcessing;
  }

  public long getDurationAfterPostProcessing() {
    return durationAfterPostProcessing;
  }

  public int getNumberOfPoints() {
    return numberOfPoints;
  }
}
