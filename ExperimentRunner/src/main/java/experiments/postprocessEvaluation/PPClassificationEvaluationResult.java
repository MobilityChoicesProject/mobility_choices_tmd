package experiments.postprocessEvaluation;

import at.fhv.tmd.common.Distance;
import at.fhv.transportdetector.trackingtypes.TransportType;
import experiments.classificationEvaluation.ClassificationEvaluationResult;
import experiments.classificationEvaluation.ClassificationProbabilityMargin;
import java.time.Duration;

/**
 * Created by Johannes on 03.08.2017.
 */
public class PPClassificationEvaluationResult extends ClassificationEvaluationResult {

  public PPClassificationEvaluationResult(TransportType actual,
      TransportType classifiedAs,
      Distance item2, Duration item1,
      ClassificationProbabilityMargin classificationProbabilityMargin) {
    super(actual, classifiedAs, item2, item1, classificationProbabilityMargin);
  }

  private boolean unambigiousActualTransportType;

  public void setUnambigiousActualTransportType(boolean unambigiousActualTransportType) {
    this.unambigiousActualTransportType = unambigiousActualTransportType;
  }

  public  boolean hasUnambigiousActualTransportType(){
    return unambigiousActualTransportType;
  }
}
