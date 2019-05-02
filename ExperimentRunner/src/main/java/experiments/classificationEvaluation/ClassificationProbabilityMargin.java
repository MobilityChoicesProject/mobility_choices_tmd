package experiments.classificationEvaluation;

import at.fhv.transportdetector.trackingtypes.TransportType;

/**
 * Created by Johannes on 26.07.2017.
 */
public class ClassificationProbabilityMargin {

  private TransportType actualTransportType;
  private TransportType bestNotActualTransportType;

  private double actualTransportTypeProbability;
  private double bestNotActualTransportTypeProbability;

  public double getMargin(){
    return actualTransportTypeProbability-bestNotActualTransportTypeProbability;
  }

  public TransportType getActualTransportType() {
    return actualTransportType;
  }

  public TransportType getBestNotActualTransportType() {
    return bestNotActualTransportType;
  }

  public double getActualTransportTypeProbability() {
    return actualTransportTypeProbability;
  }

  public double getBestNotActualTransportTypeProbability() {
    return bestNotActualTransportTypeProbability;
  }

  public ClassificationProbabilityMargin(
      TransportType actualTransportType,
      TransportType bestNotActualTransportType, double actualTransportTypeProbability,
      double bestNotActualTransportTypeProbability) {
    this.actualTransportType = actualTransportType;
    this.bestNotActualTransportType = bestNotActualTransportType;
    this.actualTransportTypeProbability = actualTransportTypeProbability;
    this.bestNotActualTransportTypeProbability = bestNotActualTransportTypeProbability;
  }
}
