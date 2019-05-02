package experiments.classificationEvaluation;

import at.fhv.tmd.common.Distance;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.Duration;

/**
 * Created by Johannes on 26.07.2017.
 */
public class ClassificationEvaluationResult {

  private TransportType actual;
  private TransportType classifiedAs;
  private Duration duration;
  private Distance distance;
  private ClassificationProbabilityMargin classificationProbabilityMargin;

  public ClassificationEvaluationResult(TransportType actual,
      TransportType classifiedAs, Distance item2, Duration item1,
      ClassificationProbabilityMargin classificationProbabilityMargin) {
    this.actual = actual;
    this.classifiedAs = classifiedAs;
    this.distance = item2;
    this.duration = item1;
    this.classificationProbabilityMargin = classificationProbabilityMargin;
  }

  public ClassificationProbabilityMargin getClassificationProbabilityMargin() {
    return classificationProbabilityMargin;
  }

  public Duration getDuration() {
    return duration;
  }

  public Distance getDistance() {
    return distance;
  }

  public TransportType getActual() {
    return actual;
  }

  public TransportType getClassifiedAs() {
    return classifiedAs;
  }

  private String fileName;
  //optional
  public void setFileName(String filename) {
    this.fileName = filename;
  }

  public String getFileName() {
    return fileName;
  }
}
