package at.fhv.tmddemoservice.jsonEntities;
/**
 * Created by Johannes on 20.05.2017.
 */
public class ProbabilityEntity {

  private String transportMode;
  private String probability;

  public ProbabilityEntity() {
  }

  public ProbabilityEntity(String transportMode, String probability) {
    this.transportMode = transportMode;
    this.probability = probability;
  }

  public String getTransportMode() {
    return transportMode;
  }

  public void setTransportMode(String transportMode) {
    this.transportMode = transportMode;
  }

  public String getProbability() {
    return probability;
  }

  public void setProbability(String probability) {
    this.probability = probability;
  }
}
