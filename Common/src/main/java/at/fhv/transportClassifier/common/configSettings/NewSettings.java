package at.fhv.transportClassifier.common.configSettings;

/**
 * Created by Johannes on 10.08.2017.
 */
public class NewSettings {

  private String key;
  private double value;

  public NewSettings(String key, double value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public double getValue() {
    return value;
  }
}
