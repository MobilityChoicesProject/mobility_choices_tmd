package at.fhv.tmd.processFlow;

import at.fhv.transportdetector.trackingtypes.TransportType;

/**
 * Created by Johannes on 24.07.2017.
 */
public class TransportTypeProbability {

  private TransportType transportType;
  private double probability;

  public TransportTypeProbability(TransportType transportType, double probability) {
    this.transportType = transportType;
    this.probability = probability;
  }

  public TransportType getTransportType() {
    return transportType;
  }

  public double getProbability() {
    return probability;
  }
}
